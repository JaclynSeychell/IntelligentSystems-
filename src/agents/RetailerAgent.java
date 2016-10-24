package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionResponder;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.Action;

import java.util.Random;

import ontologies.*;
import ontologies.Retailer.retailerType;
import utility.*;

@SuppressWarnings("serial")
public class RetailerAgent extends Agent implements SupplierVocabulary {
	private Retailer retailer = new Retailer();
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());
	
	private static final int TICK_TIME = (60000 * 5);
	
	retailerType determineTypeByName(String str) {
		switch (str) {
			case "R1":
				return retailerType.typeA;
			case "R2":
				return retailerType.typeB;
			case "R3":
				return retailerType.typeC;
			default:
				return retailerType.typeD;
		}
	}
	
	void setPriceFromType() {
		switch ( retailer.getRetailerType() ) {
			case typeA: //price set to random between 3-5
				retailer.setPricePerUnit(rnd.nextInt(2)+3);
				break;
			case typeB: //reduces 5% after each transaction
				retailer.setPricePerUnit(4);
				break;
			case typeC: //based on demand... (need to figure out how we determine this)
				//analyse previous unit prices from sales?
				retailer.setPricePerUnit(rnd.nextInt(5));
				break;
			case typeD: //price fixed at $5 per unit
				retailer.setPricePerUnit(5);
				break;
		}
	}
	
	void setupRetailer() {
		//sets the retailer type based on the agent's name (e.g. R1)
		retailer.setRetailerType(determineTypeByName(this.getLocalName())); 
		setPriceFromType(); //initiates price based on the type of retailer
		retailer.setGenerationRate(rnd.nextInt(10)); //random int between 0-10
		retailer.setSupply(rnd.nextInt(2000)); //initial supply random 0-2000
	}
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Setup retailer state
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			retailer = (Retailer)args[0];
		} else {
			setupRetailer();
		}
		
		System.out.println(retailer.toString());
		
		addBehaviour(updateRetailer);
		
		
		// Register in the DF
		DFRegistry.register(this, RETAILER_AGENT);
		subscriptionHandler();
		
		// Run agent
		process();
	}
	
	// Deregister this agent
	protected void takeDown() {
		System.out.println("Shutting down " + this.getName() + ".");
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(RETAILER_AGENT);
			dfd.addServices(sd);
			
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			
			if(dfds.length > 0) {
				System.out.println("\tRemaining retailers:");
			} else {
				System.out.println("\tNo remaining retailers.");
			}
			
			for(int i = 0; i < dfds.length; i++) {
				System.out.print("\t" + dfds[i].getName());
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	void process() {
		System.out.println("Agent " + getLocalName() + " of " + retailer.getRetailerType() + " waiting for requests...");
		MessageTemplate template = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
		
		// Handle purchase confirmation
		addBehaviour(new AchieveREResponder(this, template) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": REQUEST received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
		
				System.out.println("Agent " + getLocalName() + ": Agree");
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
			}
			
			@Override
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				request.setLanguage(codec.getName());
				request.setOntology(ontology.getName());
				
				try {
					ContentElement content = getContentManager().extractContent(request);
					Exchange action = (Exchange)((Action)content).getAction();
					
					switch(action.getType()) {
					case SupplierVocabulary.BUY:
						int updatedSupply = retailer.getSupply() - action.getUnits();
						System.out.println("Agent " + getLocalName() + ": Action successfully performed" + 
								"\nSuppy sold: " + retailer.getSupply() + "\nRemaining supply: " + updatedSupply);
						retailer.setSupply(updatedSupply);
						
						break;
					case SupplierVocabulary.SELL:
					}
					
				} catch(Exception e) {
					inform.setPerformative(ACLMessage.FAILURE);
					e.printStackTrace();
				}
				
				System.out.println(inform.getContent());
				return inform;
			}
		} );
		
		template = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF) );
		
		// Handles quote requests
		addBehaviour(new AchieveREResponder(this, template) {
			@Override
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": QUERY_REF received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
				
				System.out.println("Agent " + getLocalName() + ": Agree");
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
			}
			
			@Override
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				System.out.println("Agent " + getLocalName() + ": Quote generated");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				inform.setContent("" + retailer.getPricePerUnit());
				return inform;
			}
		} );
	}
	
	TickerBehaviour updateRetailer = new TickerBehaviour(this, rnd.nextInt(TICK_TIME)) {
		@Override
		public void onTick() {
			//Update supply based on generation rate
			retailer.setSupply(retailer.getSupply() + retailer.getGenerationRate());
		
			System.out.println(myAgent.getLocalName() + " updating supply...\n Change = " + 
					retailer.getGenerationRate() + "\n Supply = " + retailer.getSupply());
			
			//Update price based on retailer type
			setPriceFromType();
			
			reset(rnd.nextInt(TICK_TIME));
		}
	};
	
	// -- Utility Methods -- 
	void subscriptionHandler() {
		MessageTemplate mt = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE)
		);
		
		SubscriptionResponder sr = new SubscriptionResponder(this, mt) {
			ACLMessage notification;
			Subscription sub;
			int lastPrice = 0;
			
			@Override
			protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
				super.handleSubscription(subscription);
				System.out.println("Subscription: \n\t" + 
						subscription.getSender().getName() + " successfully subscribed to " + myAgent.getName());
				
				sub = getSubscription(subscription);
				notification = subscription.createReply();
				notification.setPerformative(ACLMessage.INFORM);
				
				addBehaviour(new CyclicBehaviour() {
					@Override
					public void action() {
						if(lastPrice == 0 || retailer.getPricePerUnit() != lastPrice) {
							notification.setContent(Integer.toString(retailer.getPricePerUnit()));
							lastPrice = retailer.getPricePerUnit();
							sub.notify(notification);
						}
					}
				});
				
				return null;
			}	
		};
		
		addBehaviour(sr);
	}
}
