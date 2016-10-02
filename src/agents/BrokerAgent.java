package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import java.util.*;

import ontologies.*;
import behaviours.*;
import utility.*;

@SuppressWarnings("serial")
public class BrokerAgent extends Agent implements SupplierVocabulary {
	private ArrayList<AID> retailers = new ArrayList<AID>();
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Register in the DF
		addBehaviour(new RegisterInDF(this, BROKER_AGENT));
		addBehaviour(fetchRetailers);
		
		// Find retailers
		lookupRetailers();
		
		// Run agent
		process();
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
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
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
				
				// Prepare the request to forward to the responder
				ACLMessage outgoingRequest = new ACLMessage(ACLMessage.REQUEST);
				outgoingRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				outgoingRequest.setContent(incomingRequest.getContent());
				outgoingRequest.setReplyByDate(incomingRequest.getReplyByDate());
				Vector v = new Vector();

				for(AID retailer: retailers) {
					System.out.println("Agent " + getLocalName() + ": Forward the request to " + retailer.getName());
					outgoingRequest.addReceiver(retailer);
					send(outgoingRequest);
					v.addElement(outgoingRequest);
					outgoingRequest.removeReceiver(retailer);
				}
				return v;
			}
			
			protected void handleInform(ACLMessage inform) {
				storeNotification(ACLMessage.INFORM);
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
					// Timeout
					storeNotification(ACLMessage.FAILURE);
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
			}
		} );

		addBehaviour(arer);
  	}
	
	TickerBehaviour fetchRetailers = new TickerBehaviour(this, 10000) {
		@Override
		public void onTick() {
			retailers.clear();
			lookupRetailers();
		}
	};
	
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
}