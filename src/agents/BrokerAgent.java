package agents;

import jade.core.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionInitiator;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;

import java.util.*;
import java.util.Map.Entry;

import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class BrokerAgent extends Agent implements SupplierVocabulary {
	private HashMap<AID, Integer> retailers = new HashMap<AID, Integer>();
	private AID bestOffer;
	
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	@Override
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Register in the DF
		DFRegistry.register(this, BROKER_AGENT);
		
		// Find retailers
		subscribeToRetailers();
		
		// Run agent
		query();
		purchase();
	}
	
	@Override
	protected void takeDown() {
		System.out.println("Shutting down " + this.getName() + ".");
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
	}
	
	void query() {
		System.out.println("Agent " + getLocalName() + " waiting for query requests...");
		MessageTemplate template = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF) );
		
		AchieveREResponder arer = new AchieveREResponder(this, template) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": QUERY_REF received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
				
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
				System.out.println("Agent " + getLocalName() + ": returning best price");
				ACLMessage result = request.createReply();
				result.setPerformative(ACLMessage.INFORM);
				result.setContent(Integer.toString(retailers.get(bestOffer)));
				return result;
			}
		};
		
		addBehaviour(arer);
	}
	
	void purchase() {
		System.out.println("Agent " + getLocalName() + " waiting for purchase requests...");
	  	MessageTemplate template = MessageTemplate.and(
	  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
	  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
	  		
		AchieveREResponder arer = new AchieveREResponder(this, template) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": REQUEST received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
				
				// Request fails if their are no retailers
				ACLMessage response = request.createReply();
			
				if(retailers.size() > 0) {
					response.setPerformative(ACLMessage.AGREE);
				} else {
					response.setPerformative(ACLMessage.REFUSE);
				}
				
				return response;
			}
		};
			
		// Register an AchieveREInitiator in the PREPARE_RESULT_NOTIFICATION state
		arer.registerPrepareResultNotification(new AchieveREInitiator(this, null) {
			@Override
			protected Vector prepareRequests(ACLMessage request) {
				// Retrieve the incoming request from the DataStore
				String incomingRequestKey = (String) ((AchieveREResponder) parent).REQUEST_KEY;
				ACLMessage incomingRequest = (ACLMessage) getDataStore().get(incomingRequestKey);
				// Prepare the request to forward to the responder
				System.out.println("Agent "+getLocalName()+": Forward the request to "+bestOffer.getName());
				ACLMessage outgoingRequest = new ACLMessage(ACLMessage.REQUEST);
				outgoingRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				outgoingRequest.addReceiver(bestOffer);
				outgoingRequest.setContent(incomingRequest.getContent());
				outgoingRequest.setReplyByDate(incomingRequest.getReplyByDate());
				Vector v = new Vector(1);
				v.addElement(outgoingRequest);
				return v;
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
			protected void handleAllResultNotifications(Vector notifications) {
				if (notifications.size() == 0) {
					// Timeout
					storeNotification(ACLMessage.FAILURE);
				}
			}
			
			private void storeNotification(int performative) {
				if (performative == ACLMessage.INFORM) {
					System.out.println("Agent "+getLocalName()+": brokerage successful");
				}
				else {
					System.out.println("Agent "+getLocalName()+": brokerage failed");
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
  			
  			ACLMessage retailSub = new ACLMessage(ACLMessage.QUERY_REF);
  			
  			@Override
  			protected void handleInform(ACLMessage inform) {
  				try {
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					DFAgentDescription[] df = DFService.search(myAgent, dfd);
  					retailSub.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
  					
  					// Register additional price update subscription
  					SubscriptionInitiator priceChanges = new SubscriptionInitiator(myAgent, retailSub) {		
  						@Override
  						protected void handleInform(ACLMessage inform) {
  							System.out.println("Price updated: " + inform.getSender().getName() +
  									" to $" + Integer.parseInt(inform.getContent()) + " per unit");
  							retailers.put(inform.getSender(), Integer.parseInt(inform.getContent()));
  							
  							storeBestOffer();
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
							System.out.println("\tNew retailer: " + dfds[i].getName());
							if (retailers.size() == 0) { System.out.println("\tBroker listening for price changes..."); }
							
							retailers.put(dfds[i].getName(), null);
							retailSub.addReceiver(dfds[i].getName());
							
							addBehaviour(priceChanges);
						} else {
							System.out.println("\tDeleted retailer: " + dfds[i].getName());
							if(retailers.size() < 1) { System.out.println("\tBroker stopped listening for price changes."); }
							
							retailers.remove(dfds[i].getName());
							retailSub.removeReceiver(dfds[i].getName());
						}
						
						storeBestOffer();
					}
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
		});
	}
	
	private void storeBestOffer() {
		Iterator<Entry<AID, Integer>> it = retailers.entrySet().iterator();
		int bestPrice = Integer.MAX_VALUE;
		bestOffer = null;
		
		try {
			while(it.hasNext()) {
				Map.Entry<AID, Integer> pair = (Map.Entry<AID, Integer>)it.next();
				Integer price = (Integer)pair.getValue();
				
				if(price != null) {
					if(price < bestPrice) {
						bestPrice = price;
						bestOffer = (AID)pair.getKey();
						System.out.println("Best price offered by " + bestOffer.getName() + " at " + bestPrice);
					} 
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}