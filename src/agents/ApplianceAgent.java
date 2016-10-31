package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import java.util.Random;

import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class ApplianceAgent extends Agent implements SupplierVocabulary{
	private Appliance appliance = new Appliance();
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());
	
	private static final int TICK_TIME = (60000 * 5);
	
	void setupAppliance() {
		appliance.setGenerationRate(rnd.nextInt(10)); 	//random int between 0-10
		appliance.setUsageRate(rnd.nextInt(30));    	//random int between 0-30
		
		System.out.println(appliance.toString());
	}
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Setup retailer state
		setupAppliance();
		
		// Register in the DF
		DFRegistry.register(this, APPLIANCE_AGENT);
		subscriptionHandler();
	}
	
	// Deregister this agent
	protected void takeDown() {
		System.out.println("Shutting down " + this.getName() + ".");
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(APPLIANCE_AGENT);
			dfd.addServices(sd);
			
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			
			if(dfds.length > 0) {
				System.out.println("\tRemaining appliances:");
			} else {
				System.out.println("\tNo remaining appliances.");
			}
			
			for(int i = 0; i < dfds.length; i++) {
				System.out.print("\t" + dfds[i].getName());
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	// -- Utility Methods -- 
	void subscriptionHandler() {
		MessageTemplate mt = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE)
		);
		
		SubscriptionResponder sr = new SubscriptionResponder(this, mt) {
			ACLMessage notification;
			Subscription sub;
			
			@Override
			protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
				super.handleSubscription(subscription);
				System.out.println("Subscription: \n\t" + 
						subscription.getSender().getName() + " successfully subscribed to " + myAgent.getName());
				
				sub = getSubscription(subscription);
				notification = subscription.createReply();
				notification.setPerformative(ACLMessage.INFORM);
				
				
				// Send rate data out to subscribers
				addBehaviour(new TickerBehaviour(myAgent, rnd.nextInt(TICK_TIME)) {
					@Override
					public void onTick() {
						System.out.println(myAgent.getLocalName() + " rate: " 
								+ appliance.getRate() + " units.");
						
						notification.setContent(Integer.toString(appliance.getRate()));
						sub.notify(notification);
					}
				});
				
				return null;
			}	
		};
		
		addBehaviour(sr);
	}
}
