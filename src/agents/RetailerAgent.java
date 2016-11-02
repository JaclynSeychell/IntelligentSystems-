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

import java.awt.Color;
import java.util.Random;

import gui.ProgramGUI;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class RetailerAgent extends Agent implements SupplierVocabulary {
	private Retailer retailer;
	
	// Language
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());
	
	// Run parameters
	private int updateTicks = 60000;
	private boolean updateTickRange = false;
	private float updateTickMin;
	private float updateTickMax;
	
	public int randomRange(float min, float max) {
		float result = (rnd.nextFloat() * (max - min) + min) * 60000;
		return (int)result;
	}
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Setup retailer state
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			retailer = (Retailer)args[0];
			Object[] updateData = (Object[])args[1];
			
			updateTickRange = (boolean)updateData[0];
			updateTickMin = (float)updateData[1];
			updateTickMax = (float)updateData[2];
			
			updateTicks = updateTickRange ? randomRange(updateTickMin, updateTickMax) : (int)updateTickMax * 60000;
		} else {
			retailer = new Retailer();
			updateTicks = 60000 * 5;
		}
		
		ProgramGUI.getInstance().printToLog(retailer.hashCode(), retailer.toString(), Color.GREEN);
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
	
	TickerBehaviour updateRetailer = new TickerBehaviour(this, updateTicks) {
		@Override
		public void onTick() {
			//Update supply based on generation rate
			retailer.setSupply(retailer.getSupply() + retailer.getGenerationRate());
			
			ProgramGUI.getInstance().printToLog(retailer.hashCode(), "Updating the supply", Color.BLACK);
			if(updateTickRange) {
				updateTicks = randomRange(updateTickMin, updateTickMax);
			}
			reset(updateTicks);
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
							ProgramGUI.getInstance().printToLog(retailer.hashCode(), "Notifying broker agent of price change.", Color.BLACK);
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
