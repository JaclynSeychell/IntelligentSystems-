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

import ontologies.*;

public class BrokerAgent extends Agent implements SupplierVocabulary {
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		SequentialBehaviour sb = new SequentialBehaviour();
		sb.addSubBehaviour(new RegisterInDF(this));
		sb.addSubBehaviour(new ReceiveMessages(this));
		addBehaviour(sb);
	}
	
	class RegisterInDF extends OneShotBehaviour {
		RegisterInDF(Agent a) {
			super(a);
		}
		
		public void action() {
			ServiceDescription sd = new ServiceDescription();
			sd.setType(BROKER_AGENT);
			sd.setName(getName());
			sd.setOwnership("Group");
			
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			dfd.addServices(sd);
			
			try {
				DFAgentDescription[] dfds = DFService.search(myAgent, dfd);
				if (dfds.length > 0) {
					DFService.deregister(myAgent, dfd);
				}
				DFService.register(myAgent, dfd);
				System.out.println(getLocalName() + " is ready.");
			} catch (Exception e) {
				System.out.println("Failed registering with DF! Shutting down...");
				e.printStackTrace();
				doDelete();
			}
		}
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
				ContentElement content = getContentManager().extractContent(request);
		        ACLMessage reply = request.createReply();
		        reply.setPerformative(ACLMessage.INFORM);
		        getContentManager().fillContent(reply, content);
		        send(reply);
		        System.out.println("Exchange processed!");
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
}

