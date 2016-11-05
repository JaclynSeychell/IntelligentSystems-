package agents;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionInitiator;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.content.AgentAction;
import jade.content.Concept;

import java.awt.Color;
import java.util.*;
import java.util.Map.Entry;

import behaviours.DelayBehaviour;
import gui.ProgramGUI;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class BrokerAgent extends Agent implements SupplierVocabulary {
	private Broker broker;
	
	// Agents
	private HashMap<AID, Tuple<Integer, Integer>> retailers = new HashMap<AID, Tuple<Integer, Integer>>();
	private AID bestOffer;
	
	// Language
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();

	@Override
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			broker = (Broker)args[0];
		} else {
			broker = new Broker();
		}
		
		ProgramGUI.getInstance().printToLog(broker.hashCode(), "Broker agent successfully created", Color.GREEN);
		
		// Register in the DF
		DFRegistry.register(this, BROKER_AGENT);
		
		subscribeToRetailers();
		query();
		purchase();
	}
	
	@Override
	protected void takeDown() {
		ProgramGUI.getInstance().printToLog(broker.hashCode(), "Broker agent shutdown", Color.RED);
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
	}
	
	void query() {
		ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + ": waiting for query requests...", Color.GREEN);
		
		MessageTemplate template = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF) );
		
		AchieveREResponder arer = new AchieveREResponder(this, template) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				ProgramGUI.getInstance().printToLog(broker.hashCode(), "Agent " + getLocalName() + ": QUERY_REF received from " + 
						request.getSender().getName() + ". Action is " + request.getContent(), Color.GREEN);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Request fails if their are no retailers
				ACLMessage response = request.createReply();
				
				if(retailers.size() > 0) {
					response.setPerformative(ACLMessage.AGREE);
				} else {
					response.setPerformative(ACLMessage.REFUSE);
				}
				
				return response;
			}
			
			@Override
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				ProgramGUI.getInstance().printToLog(broker.hashCode(), "Agent " + getLocalName() + ": returning best price", Color.ORANGE);
				ACLMessage result = request.createReply();
				result.setPerformative(ACLMessage.INFORM);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
				try {
					ContentElement content = getContentManager().extractContent(request);
					Exchange e = (Exchange)((Action)content).getAction();
					
					System.out.println(getLocalName() + ": Home wants to purchase " + e.getUnits() + " units.");
					
					AID optimal = null;
					AID subOptimal = null;
					
					int optimalPrice = 0;
					int optimalBuyPrice = Integer.MAX_VALUE;
					int optimalSellPrice = 0;
					int subOptimalPrice = 0;
					int subOptimalSupply = 0;
					int count = 1;
					
					// Try and find the best retailer based on homes wants
					for(Map.Entry<AID, Tuple<Integer, Integer>> cursor : retailers.entrySet()) {
						AID retailer = cursor.getKey();
						int price = cursor.getValue().first();
						int supply = cursor.getValue().last();
						
						// find retailer with the best price that has stock available.
						if(e.getType() == BUY) {
							if(supply >= e.getUnits()) {
								if(price < optimalPrice) {
									optimal = retailer;
									optimalBuyPrice = price;
								}
							}
							
							if(supply > subOptimalSupply) {
								subOptimalSupply = supply;
								subOptimalPrice = price;
								subOptimal = retailer;
							}
							
							optimalPrice = optimalBuyPrice;
						} else if (e.getType() == SELL) {
							if(price > optimalSellPrice) {
								optimal = retailer;
								optimalSellPrice = price;
							}
							
							optimalPrice = optimalSellPrice;
						}
					}
					
					Exchange eResult = new Exchange();
					eResult.setType(e.getType());
					
					// if no retailers have sufficient stock choose the retailer with the most stock
					if(optimal != null) {
						System.out.println("Optimal retailer: " + optimal.getLocalName());
						bestOffer = optimal;
						eResult.setPrice(optimalPrice);
						eResult.setUnits(e.getUnits());
					} else {
						bestOffer = subOptimal;
						eResult.setPrice(subOptimalPrice);
						eResult.setUnits(subOptimalSupply);
					}
					
					getContentManager().fillContent(result, new Action(request.getSender(), eResult));
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				return result;
			}
			
			@Override 
			public int onEnd() {
				System.out.println(getLocalName() + ": \"start\" state finished with exit code 1");
				return 1;
			}
		};
		
		addBehaviour(arer);
	}
	
	void purchase() {
		ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + ": waiting for purchase requests...", Color.GREEN);
		
	  	MessageTemplate template = MessageTemplate.and(
	  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
	  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
	  		
		AchieveREResponder arer = new AchieveREResponder(this, template) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				ProgramGUI.getInstance().printToLog(broker.hashCode(), "Agent " + getLocalName() + ": REQUEST received from " + 
						request.getSender().getLocalName() + ". Action is " + request.getContent(), Color.ORANGE);
			
				// Request fails if their are no retailers
				ACLMessage response = request.createReply();
			
				if(retailers.size() > 0) {
					response.setPerformative(ACLMessage.AGREE);
				} else {
					response.setPerformative(ACLMessage.REFUSE);
					throw new RefuseException("no-retailers");
				}
				
				return response;
			}
		};
			
		// Register an AchieveREInitiator in the PREPARE_RESULT_NOTIFICATION state
		arer.registerPrepareResultNotification(new AchieveREInitiator(this, null) {
			@Override
			protected Vector<ACLMessage> prepareRequests(ACLMessage request) {
				try {
					// Retrieve the incoming request from the DataStore
					String incomingRequestKey = (String) ((AchieveREResponder) parent).REQUEST_KEY;
					ACLMessage incomingRequest = (ACLMessage) getDataStore().get(incomingRequestKey);
					// Prepare the request to forward to the responder
					ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
							": Forward the request to "+ bestOffer.getLocalName(), Color.ORANGE);
					
					ACLMessage outgoingRequest = new ACLMessage(ACLMessage.REQUEST);
					outgoingRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					outgoingRequest.addReceiver(bestOffer);
					outgoingRequest.setContent(incomingRequest.getContent());
					outgoingRequest.setReplyByDate(incomingRequest.getReplyByDate());
					Vector<ACLMessage> v = new Vector<ACLMessage>(1);
					v.addElement(outgoingRequest);
					return v;
				} catch(Exception e) {
					e.printStackTrace();
					ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
							": Failed to forward request to retailer", Color.RED);
					return null;
				}
			}
			
			@Override
			protected void handleInform(ACLMessage inform) {
				storeNotification(ACLMessage.INFORM);
			}
			
			@Override
			protected void handleRefuse(ACLMessage refuse) {
				storeNotification(ACLMessage.FAILURE);
			}
			
			@Override
			protected void handleNotUnderstood(ACLMessage notUnderstood) {
				storeNotification(ACLMessage.FAILURE);
			}
			
			@Override
			protected void handleFailure(ACLMessage failure) {
				storeNotification(ACLMessage.FAILURE);
			}
			
			@Override
			protected void handleAllResultNotifications(@SuppressWarnings("rawtypes") Vector notifications) {
				if (notifications.size() == 0) {
					// Timeout
					storeNotification(ACLMessage.FAILURE);
				}
			}
			
			private void storeNotification(int performative) {
				if (performative == ACLMessage.INFORM) {
					ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
							": brokerage successful", Color.GREEN);
				}
				else {
					ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
							": brokerage failed", Color.RED);
				}
					
				// Retrieve the incoming request from the DataStore
				String incomingRequestkey = (String) ((AchieveREResponder) parent).REQUEST_KEY;
				ACLMessage incomingRequest = (ACLMessage) getDataStore().get(incomingRequestkey);
				// Prepare the notification to the request originator and store it in the DataStore
				ACLMessage notification = incomingRequest.createReply();
				notification.setPerformative(performative);
				String notificationkey = (String) ((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY;
				getDataStore().put(notificationkey, notification);
			}
		});

		addBehaviour(arer);
  	}
	
	// -- Utility Methods --
	void subscribeToRetailers() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(RETAILER_AGENT);
		dfd.addServices(sd);
	
		// Handle registration of new retailers
  		addBehaviour(new SubscriptionInitiator(this,
			DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, null)) {
  			
  			@Override
  			protected void handleInform(ACLMessage inform) {
  				try {
  					ACLMessage retailSub = new ACLMessage(ACLMessage.QUERY_REF);
  					retailSub.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
  					retailSub.setLanguage(codec.getName());
  					retailSub.setOntology(ontology.getName());
  					
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					DFAgentDescription[] df = DFService.search(myAgent, dfd);
  					
  					// Register additional price update subscription
  					SubscriptionInitiator priceChanges = new SubscriptionInitiator(myAgent, retailSub) {		
  						@Override
  						protected void handleInform(ACLMessage inform) {
  							try {
	  							ContentElement content = getContentManager().extractContent(inform);
	  							Quote quote = (Quote)((Action)content).getAction();
	  							
	  							ProgramGUI.getInstance().printToLog(broker.hashCode(), "Price updated: " + inform.getSender().getLocalName() +
	  									" to $" + quote.getPrice() + " per unit", Color.ORANGE);
	  							
	  							retailers.put( inform.getSender(), new Tuple<Integer, Integer>( quote.getPrice(), quote.getUnits() ) );
  							} catch(Exception e) {
  								e.printStackTrace();
  							}
  						}
  					};
  					
  					// Check agent in the inform message exists before adding it
					for(int i = 0; i < dfds.length; i++) {
						boolean exists = false;
						
						for(int j = 0; j < df.length; j++) {
							if(dfds[i].getName().hashCode() == df[j].getName().hashCode()) {
								exists = true;
								break;
							}
						}
						
						removeBehaviour(priceChanges);
						priceChanges.reset(retailSub);
						
						if(exists) {
							System.out.println(getLocalName() + ": new retailer " + dfds[i].getName());
							ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
									": new retailer " + dfds[i].getName(), Color.GREEN);
				
							if (retailers.size() == 0) { 
								System.out.println(getLocalName() + ": listening for price changes from " + dfds[i].getName());
								ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
										": listening for price changes from " + dfds[i].getName(), Color.BLACK);
							}
							
							retailers.put(dfds[i].getName(), null);
							retailSub.addReceiver(dfds[i].getName());
							
							addBehaviour(priceChanges);
						} else {
							ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
									": stopped listening for retailer " + dfds[i].getName(), Color.RED);

							if(retailers.size() < 1) { 
								ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + 
										": stopped listening for retailers.", Color.RED);
							}
							
							retailers.remove(dfds[i].getName());
							retailSub.removeReceiver(dfds[i].getName());
						}
					}
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
		});
	}
	
	private void storeBestOffer() {
		Iterator<Entry<AID, Tuple<Integer, Integer>>> it = retailers.entrySet().iterator();
		int bestPrice = Integer.MAX_VALUE;
		bestOffer = null;
		
		try {
			while(it.hasNext()) {
				Map.Entry<AID, Tuple<Integer, Integer>> pair = (Map.Entry<AID, Tuple<Integer, Integer>>)it.next();
				Tuple<Integer, Integer> t = (Tuple<Integer, Integer>)pair.getValue();
				int price = t.first();
				int supply = t.last();
				
				if(price < bestPrice) {
					bestPrice = price;
					bestOffer = (AID)pair.getKey();
				
					ProgramGUI.getInstance().printToLog(broker.hashCode(), getLocalName() + "Best price offered by " + 
							bestOffer.getName() + " at " + bestPrice, Color.GREEN);
				} 
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}