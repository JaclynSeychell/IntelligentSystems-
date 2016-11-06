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
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.Codec.CodecException;
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
	
	public int updateUnit = 1000; // seconds;
	
	// Run parameters
	private int updateTicks = 60000;
	private boolean updateTickRange = false;
	private int updateTickMin;
	private int updateTickMax;
	
	private boolean sendNotification = true;
	
	public int randomRangeTimer(int min, int max) {
		float result = (rnd.nextFloat() * (max - min) + min) * updateUnit;
		return (int)result;
	}
	
	public float randomRangeFloat(float min, float max) {
		float result = (rnd.nextFloat() * (max - min) + min);
		return result;
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
			updateTickMin = (int)updateData[1];
			updateTickMax = (int)updateData[2];
			
			updateTicks = updateTickRange ? randomRangeTimer(updateTickMin, updateTickMax) : (int)(updateTickMax * updateUnit);
		} else {
			retailer = new Retailer();
			updateTicks = 60000 * 5;
		}
		//TODO Confirm
//		ProgramGUI.getInstance().printToLog(retailer.hashCode(), retailer.toString(), Color.GREEN.darker());
		ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName(),
				"created", Color.GREEN.darker());
		updateRetailer.reset(updateTicks);
		addBehaviour(updateRetailer);
		
		// Register in the DF
		DFRegistry.register(this, RETAILER_AGENT);
		subscriptionHandler();
		
		// Run agent
		process();
	}
	
	// Deregister this agent
	protected void takeDown() {
		ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName(),
				"shutdown", Color.RED);
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(RETAILER_AGENT);
			dfd.addServices(sd);
			
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			String msg = getLocalName() + ": ";
			if(dfds.length > 0) {
				msg += "remaining retailers:";
			} else {
				msg += "no remaining retailers.";
			}
			
			for(int i = 0; i < dfds.length; i++) {
				msg += "\t" + dfds[i].getName();
			}
			
			//TODO Confirm
//			ProgramGUI.getInstance().printToLog(retailer.hashCode(), msg, Color.RED);
			ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName(), msg, Color.RED);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	void process() {
		ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName(),
				"awaitaing requests...", Color.GREEN.darker());
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
					case BUY:
						retailer.setSupply(retailer.getSupply() - action.getUnits());
						//TODO Confirm
//						ProgramGUI.getInstance().printToLog(retailer.hashCode(), "Agent " + getLocalName(),
//								": Action successfully performed" + "\n\tSuppy sold: " + action.getUnits(), Color.GREEN.darker());
						ProgramGUI.getInstance().printToLog(retailer.hashCode(), "Agent " + getLocalName(),
								"<< " + action.getUnits() + " units sold", Color.GREEN.darker());
						
						System.out.println("Agent " + getLocalName() + ": Action successfully performed" + 
								"\n\tSuppy sold: " + action.getUnits());
						
						break;
					case SELL:
						retailer.setSupply(retailer.getSupply() + action.getUnits());
						
						//TODO Confirm
//						ProgramGUI.getInstance().printToLog(retailer.hashCode(), "Agent " + getLocalName(), 
//								": Action successfully performed" + "\n\tSuppy purchased: " + action.getUnits(), Color.GREEN.darker());
						ProgramGUI.getInstance().printToLog(retailer.hashCode(), "Agent " + getLocalName(), 
								"<< " + action.getUnits() + " units bought", Color.GREEN.darker());
						
						System.out.println("Agent " + getLocalName() + ": Action successfully performed" + 
								"\n\tSuppy purchased: " + action.getUnits());
		
						break;
					}
					
					// Accumulate orders over trading cycle
					retailer.setOrders(retailer.getOrders() + 1);
					
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
		public void onStart() {
			super.onStart();
			System.out.println(getLocalName() + ": updating in " + updateTicks + "ms");
		}
		
		@Override
		public void onTick() {
			//Update supply based on generation rate
			float variance;
			int priceChange = 0;
			int supplyChange = 0;
			
			variance = rnd.nextFloat() <= 0.05f ? (randomRangeFloat(-50, 50) / 100) + 1 : (randomRangeFloat(-20, 20) / 100) + 1;
			supplyChange = (int) Math.round( (retailer.getGenerationRate() * variance) );
			retailer.setSupply(retailer.getSupply() + supplyChange);
			
			switch(retailer.getRetailerType()) {
			// Price is fixed
			case FIXED:
				break;
				
			// Fluctuate prices +/- 5% with a 5% chance of a larger +/- 10% change
			case RANDOM:
				variance = rnd.nextFloat() <= 0.05f ? (randomRangeFloat(-10, 10) / 100) + 1 : (randomRangeFloat(-5, 5) / 100) + 1;
				priceChange = (int) Math.round( (retailer.getPricePerUnit() * variance) );
				
				System.out.println("Variance: " + variance + " = " + retailer.getPricePerUnit() + " + " + priceChange);
				retailer.setPricePerUnit(priceChange);
				
				// min price is 1
				if(retailer.getPricePerUnit() < 1) {
					retailer.setPricePerUnit(1);
				}
				break;
			// For each order made since the last update 75% to increase by 2 & 50% to increase by 1
			case DEMAND:
				System.out.println(retailer.getOrders());
				for(int i = 0; i < retailer.getOrders(); i++) {
					if(rnd.nextFloat() > 0.75) {
						retailer.setPricePerUnit(retailer.getPricePerUnit() + 2);
					} else if (rnd.nextFloat() > 0.5) {
						retailer.setPricePerUnit(retailer.getPricePerUnit() + 1);
					}
				}
				break;
			}
			//TODO Confirm
//			ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName(), 
//					": " + retailer.getOrders() + " orders made during this update cycle\n" + 
//					"updating: \n\tSupply=" + retailer.getSupply() + "(" + supplyChange + ")" +
//					"\n\tPrice=" + retailer.getPricePerUnit() + "(" + priceChange + ")", 
//					Color.GREEN.darker());
			ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName(), 
					retailer.getOrders() + " orders made during update cycle\n" + 
					"updating: \n\tSupply=" + retailer.getSupply() + "(" + supplyChange + ")" +
					"\n\tPrice=" + retailer.getPricePerUnit() + "(" + priceChange + ")", 
					Color.GREEN.darker());
			
			if(updateTickRange) {
				updateTicks = randomRangeTimer(updateTickMin, updateTickMax);
			}
			
			// inform subscribers of changes
			sendNotification = true;
			retailer.setOrders(0);
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
			
			@Override
			protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
				super.handleSubscription(subscription);
				//TODO Confirm
				ProgramGUI.getInstance().printToLog(retailer.hashCode(), myAgent.getLocalName(),
						"new subscriber " + subscription.getSender().getLocalName(), Color.BLACK);
				
				sub = getSubscription(subscription);
				notification = subscription.createReply();
				notification.setPerformative(ACLMessage.INFORM);
				
				addBehaviour(new CyclicBehaviour() {
					@Override
					public void action() {
						if( sendNotification ) {
							
							try {
								getContentManager().fillContent(notification, new Action(subscription.getSender(), getQuote()));
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							//ProgramGUI.getInstance().printToLog(retailer.hashCode(), getLocalName() + 
							//		": notifying broker agent of price change.", Color.BLACK);
					
							sub.notify(notification);
							sendNotification = false;
							
							// sleep
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				
				return null;
			}	
		};
		
		addBehaviour(sr);
	}
	
	Quote getQuote() {
		Quote result = new Quote();
		result.setSellPrice(retailer.getPricePerUnit());
		result.setBuyPrice(retailer.getBuyPrice());
		result.setUnits(retailer.getSupply());
		return result;
	}
}
