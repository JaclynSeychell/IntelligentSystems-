package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import java.util.*;

import ontologies.*;
import behaviours.*;
import utility.*;

public class BrokerAgent extends Agent implements SupplierVocabulary {
	private ArrayList<AID> retailers = new ArrayList<AID>();
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	private int bestPrice;
	private ACLMessage bestOffer;
	
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new RegisterInDF(this, BROKER_AGENT));
		seq.addSubBehaviour(new ReceiveMessages(this));
		addBehaviour(seq);
	}
	
	class ReceiveMessages extends CyclicBehaviour {
		public ReceiveMessages(Agent a) {
			super(a);
		}
		
		public void action() {
			ACLMessage msg = receive();
			if(msg == null) { block(); return; }
			
			try {
				ContentElement content = getContentManager().extractContent(msg);
				Concept action = ((Action)content).getAction();
				
				switch(msg.getPerformative()) {
				case (ACLMessage.REQUEST):
					System.out.println("Request from " + msg.getSender().getLocalName());
				
					if (action instanceof Exchange) {
						addBehaviour(new HandleExchange(myAgent, msg));
					} else {
						replyNotUnderstood(msg);
					}
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class HandleExchange extends OneShotBehaviour {
		private ACLMessage request;
		
		HandleExchange(Agent a, ACLMessage pRequest) {
			super(a);
			request = pRequest;
		}
		
		public void action() {
			try {
				// Update the list of available retailers
				retailers.clear();
				lookupRetailers();
				
				if (retailers.size() == 0) {
					System.out.println("Unable to localize any retailer agents! \nOperation aborted!");
					return;
				}
				
				bestPrice = 9999;
				bestOffer = null;
				
				ACLMessage query = new ACLMessage(ACLMessage.QUERY_REF);
				query.setConversationId(request.getConversationId());
				ContentElement content = getContentManager().extractContent(request);
				Exchange requestParams = (Exchange)((Action)content).getAction();
				
				SequentialBehaviour seq = new SequentialBehaviour();
				addBehaviour(seq);
				
				ParallelBehaviour par = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
				seq.addSubBehaviour(par);
				
			
				query.setLanguage(codec.getName());
				query.setOntology(ontology.getName());
				query.setConversationId(Utility.genCID(getLocalName(), hashCode()));
				
				
			
				for (AID retailer: retailers) {
					try {
						getContentManager().fillContent(query, new Action(retailer, requestParams));
						query.addReceiver(retailer);
						System.out.println("Contacting agent " + retailer.getLocalName() + "... Please wait!");
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					par.addSubBehaviour(new ReceiveResponse(myAgent, 2500, MessageTemplate.MatchSender(retailer)) {
						@Override 
						public void handle(ACLMessage m) {
							if(msg != null) {
								try {
									ContentElement content = getContentManager().extractContent(msg);
									Exchange quoteParams = (Exchange)((Action)content).getAction();
									int price = quoteParams.getPrice();
									System.out.println("Got quote $" + price + "from " + msg.getSender().getLocalName());
									if(price < bestPrice) {
										bestPrice = price;
										bestOffer = msg;
									}
								} catch(Exception e) {
									e.printStackTrace();
								}
							} else {
								System.out.println(retailer.getLocalName() + " did not respond in time");
							}
						}
					});
				}
				send(query);
				
				seq.addSubBehaviour(new DelayBehaviour(myAgent, 5000) {
					@Override
					public void handleElapsedTimeout() {
						if(bestOffer == null) {
							System.out.println("No quotes received");
						} else {
							System.out.println("Best Price $" + bestPrice + " from " + bestOffer.getSender().getLocalName());
							ACLMessage response = request.createReply();
							if (bestPrice < requestParams.getPrice()) {
								response.setPerformative(ACLMessage.INFORM);
								send(response);
							}
						}
					}
				});
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void replyNotUnderstood(ACLMessage msg) {
		try {
			ContentElement content = getContentManager().extractContent(msg);
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			getContentManager().fillContent(reply, content);
			send(reply);
			System.out.println("Not understood!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// -- Utility Methods --
	void lookupRetailers() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(RETAILER_AGENT);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try {
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			if (dfds.length > 0) {
				for (int i = 0; i < dfds.length; i++) {
					retailers.add(dfds[i].getName());
					System.out.println("Localized retailer with AID = " + dfds[i].getName());
				}
			} else {
				System.out.println("Couldn't localize any retailer agents");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed searching in the DF!");
		}
	}
	
	private void sendMessage(int performative, AgentAction action, AID agent) {
		ACLMessage msg = new ACLMessage(performative);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		msg.setConversationId(Utility.genCID(getLocalName(), hashCode()));
		
		try {
			getContentManager().fillContent(msg, new Action(agent, action));
			msg.addReceiver(agent);
			send(msg);
			System.out.println("Contacting agent " + agent.getLocalName() + "... Please wait!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}