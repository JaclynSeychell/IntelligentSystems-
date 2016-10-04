package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionInitiator;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.Action;

import java.sql.Date;
import java.util.*;

import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class BrokerAgent extends Agent implements SupplierVocabulary {
	private ArrayList<AID> retailers = new ArrayList<AID>();
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private ACLMessage request;
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Register in the DF
		DFRegistry.register(this, BROKER_AGENT);
		
		// Find retailers
		subscribeToRetailers();
		
		// Run agent
		process();
	}
	
	protected void takeDown() {
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
	}
	
	void process() {
		System.out.println("Agent "+getLocalName()+" waiting for requests...");
	  	MessageTemplate template = MessageTemplate.and(
	  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
	  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
	  		
		AchieveREResponder arer = new AchieveREResponder(this, template) {
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": REQUEST received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
				
				// Request fails if their are no retailers
				ACLMessage response = request.createReply();
				
				if(retailers.size() > 1) {
					response.setPerformative(ACLMessage.AGREE);
				} else {
					response.setPerformative(ACLMessage.REFUSE);
				}
				
				return response;
			}
		};
			
		// Register an AchieveREInitiator in the PREPARE_RESULT_NOTIFICATION state
		arer.registerPrepareResultNotification(new AchieveREInitiator(this, null) {
			// Since we don't know what message to send to the responder
			// when we construct this AchieveREInitiator, we redefine this 
			// method to build the request on the fly
			
			protected Vector prepareRequests(ACLMessage request) {
				
				// Retrieve the incoming request from the DataStore
				String incomingRequestKey = (String) ((AchieveREResponder) parent).REQUEST_KEY;
				ACLMessage incomingRequest = (ACLMessage) getDataStore().get(incomingRequestKey);
				ACLMessage outgoingRequest = new ACLMessage(ACLMessage.QUERY_REF);
				outgoingRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				
				request = incomingRequest;
				for(AID retailer: retailers){
					// Prepare the request to forward to the responder
					System.out.println("Agent " + getLocalName() + ": Forward the request to " + retailer.getName());
					outgoingRequest.addReceiver(retailer);
				}
				
				outgoingRequest.setContent(incomingRequest.getContent());
				outgoingRequest.setReplyByDate(incomingRequest.getReplyByDate());
				Vector v = new Vector(1);
				v.addElement(outgoingRequest);
				return v;
			}
			
			protected void handleInform(ACLMessage inform) {
				// storeNotification(ACLMessage.INFORM);
				
				System.out.println("Quote received from " + inform.getSender().getName() + 
						".\n\tPrice per unit: " + Integer.parseInt(inform.getContent()));
			}
			
			protected void handleRefuse(ACLMessage refuse) {
				storeNotification(ACLMessage.FAILURE);
			}
			
			protected void handleNotUnderstood(ACLMessage notUnderstood) {
				storeNotification(ACLMessage.FAILURE);
			}
			
			protected void handleFailure(ACLMessage failure) {
				storeNotification(ACLMessage.FAILURE);
			}
			
			protected void handleAllResultNotifications(Vector notifications) {
				if (notifications.size() == 0) {
					storeNotification(ACLMessage.FAILURE);
				} else {
					System.out.println("Evaluating quotes \n...");
					
					
					// Find the best quote
					try {
						Iterator i = notifications.iterator();
						ACLMessage bestOffer = (ACLMessage)i.next();
						int bestPrice = Integer.parseInt(bestOffer.getContent());
						while(i.hasNext()) {
							ACLMessage notification = (ACLMessage)i.next();
							if(Integer.parseInt(notification.getContent()) < bestPrice) {
								bestPrice = Integer.parseInt(notification.getContent());
								bestOffer = notification;
							}
						}
						
						System.out.println("\t Best quote found: \n\t\tOffered by " 
								+ bestOffer.getSender().getName() + "\n\t\tPrice" + bestPrice);
						
						ACLMessage purchase = new ACLMessage(ACLMessage.REQUEST);
						purchase.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
						purchase.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
						purchase.addReceiver(bestOffer.getSender());
						purchase.setLanguage(codec.getName());
						purchase.setOntology(ontology.getName());
						
						String incomingRequestkey = (String) ((AchieveREResponder) parent).REQUEST_KEY;
						ACLMessage incomingRequest = (ACLMessage) getDataStore().get(incomingRequestkey);
						purchase.setContent(incomingRequest.getContent());
						purchase.setReplyByDate(incomingRequest.getReplyByDate());
						
						
						addBehaviour(new AchieveREInitiator(myAgent, purchase) {
							public void handleAgree(ACLMessage agree) {
								System.out.println("Retailer has accepted our purchase");
								storeNotification(ACLMessage.INFORM);
							}
							
							public void handleRefuse(ACLMessage refuse) {
								System.out.println("Retailer has rejected our purchase");
							}
						});
						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			private void storeNotification(int performative) {
				if (performative == ACLMessage.INFORM) {
					System.out.println("Agent " + getLocalName() + ": brokerage successful");
				}
				else {
					System.out.println("Agent " + getLocalName() + ": brokerage failed");
				}
					
				// Retrieve the incoming request from the DataStore
				String incomingRequestkey = (String) ((AchieveREResponder) parent).REQUEST_KEY;
				ACLMessage incomingRequest = (ACLMessage) getDataStore().get(incomingRequestkey);
				// Prepare the notification to the request originator and store it in the DataStore
				ACLMessage notification = incomingRequest.createReply();
				notification.setPerformative(performative);
				String notificationkey = (String) ((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY;
				getDataStore().put(notificationkey, notification);
				
				// Want to send back the quote here
				/*ACLMessage notification = request.createReply();
				notification.setPerformative(performative);
				String notificationkey = (String) ((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY;
				getDataStore().put(notificationkey, notification);*/
			}
		} );

		addBehaviour(arer);
  	}
	
	// -- Utility Methods --
	void subscribeToRetailers() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(RETAILER_AGENT);
		dfd.addServices(sd);
	    
  		addBehaviour(new SubscriptionInitiator(this,
			DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, null)) {
  			
  			protected void handleInform(ACLMessage inform) {
  				try {
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					System.out.println("Found new retailer: " + dfds[0].getName());
  					retailers.add(dfds[0].getName());
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
		});
	}
}