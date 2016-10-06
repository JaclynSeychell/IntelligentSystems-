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
	private ACLMessage homeRequest;
	
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
		process();
	}
	
	@Override
	protected void takeDown() {
		System.out.println("Shutting down " + this.getName() + ".");
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
	}
	
	void process() {
		System.out.println("Agent " + getLocalName() + " waiting for requests...");
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
				
				// Store the initial request
				homeRequest = request;
				
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
			// Since we don't know what message to send to the responder
			// when we construct this AchieveREInitiator, we redefine this 
			// method to build the request on the fly
			
			@Override
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
			
			@Override
			protected void handleInform(ACLMessage inform) {
				System.out.println("Quote received from " + inform.getSender().getName() + 
						".\n\tPrice per unit: " + Integer.parseInt(inform.getContent()));
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
								+ bestOffer.getSender().getName() + "\n\t\tPrice " + bestPrice);
						
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
						
						// Retrieve the incoming request from the DataStore
						String notificationkey = (String) ((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY;
						
						ACLMessage notification = incomingRequest.createReply();
						
						addBehaviour(new AchieveREInitiator(myAgent, purchase) {
							public void handleAgree(ACLMessage agree) {
								System.out.println("Retailer has accepted our purchase");
							}
							
							public void handleRefuse(ACLMessage refuse) {
								System.out.println("Retailer has rejected our purchase");
							}
							
							public void handleInform(ACLMessage inform) {
								System.out.println("Informing home agent of succesful purchase");	
						
								notification.setPerformative(ACLMessage.INFORM);
								try {
									notification.setPerformative(ACLMessage.INFORM);
									setNotification(notificationkey, notification);
									//getDataStore().put(notificationkey, notification);
								} catch(Exception e) {
									e.printStackTrace();
								}
							}
						});
						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			private void setNotification(String nk, ACLMessage n) {
				getDataStore().put(nk, n);
			}
			
			private void storeNotification(int performative) {
				if (performative == ACLMessage.INFORM) {
					System.out.println("Agent " + getLocalName() + ": brokerage successful");
				}
				else {
					System.out.println("Agent " + getLocalName() + ": brokerage failed");
				}
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
	
		// Handle registration of new retailers
  		addBehaviour(new SubscriptionInitiator(this,
			DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, null)) {
  			
  			@Override
  			protected void handleInform(ACLMessage inform) {
  				try {
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					DFAgentDescription[] df = DFService.search(myAgent, dfd);
  					
  					if(dfds.length > 0) {
  						for(int i = 0; i < dfds.length; i++) {
  							boolean exists = false;
  							
  							for(int j = 0; j < df.length; j++) {
  								if(dfds[i].getName().hashCode() == df[j].getName().hashCode()) {
  									exists = true;
  									break;
  								}
  							}
  							
  							if(exists) {
								System.out.println("\tNew retailer: " + dfds[i].getName());
								retailers.add(dfds[i].getName());
  							} else {
  								System.out.println("\tDeleted retailer: " + dfds[i].getName());
  								retailers.remove(dfds[i].getName());
  							}
						}
  					}
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
		});
	}
}