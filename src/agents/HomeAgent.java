package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.SubscriptionInitiator;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import java.awt.Color;
import java.sql.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import behaviours.*;
import gui.ProgramGUI;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class HomeAgent extends Agent implements SupplierVocabulary {
	private Home home;
	
	// Agents
	private HashMap<AID, Integer> appliances = new HashMap<AID, Integer>();
	private AID broker;
	
	// Language
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Exchange exchange;
	
	// Utility
	private Random rnd = Utility.newRandom(hashCode());	
	private int bestPrice;
	private boolean queryFinished;
	private boolean purchaseFinished;
	
	// Run parameters
	private int tradeTicks = 60000;
	private int updateTicks = 60000;
	private boolean tradeTickRange = false;
	private boolean updateTickRange = false;
	private float tradeTickMin;
	private float tradeTickMax;
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

		// Setup home state
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			home = (Home)args[0];
			Object[] tradeData = (Object[])args[1];
			Object[] updateData = (Object[])args[2];
			
			tradeTickRange = (boolean)tradeData[0];
			tradeTickMin = (float)tradeData[1];
			tradeTickMax = (float)tradeData[2];
			
			updateTickRange = (boolean)updateData[0];
			updateTickMin = (float)updateData[1];
			updateTickMax = (float)updateData[2];
			
			tradeTicks = tradeTickRange ? randomRange(tradeTickMin, tradeTickMax) : (int)tradeTickMax * 60000;
			updateTicks = updateTickRange ? randomRange(updateTickMin, updateTickMax) : (int)updateTickMax * 60000;
		} else {
			home = new Home();
			tradeTicks = 60000 * 5;
			updateTicks = 60000 * 5;
		}
		
		ProgramGUI.getInstance().printToLog(home.hashCode(), home.toString(), Color.GREEN);
		addBehaviour(update);
		
		// Find the broker agent
		lookupBroker();
		// Find appliances
		subscribeToAppliances();
		
		// Run agent
		query();
	}
	
	void query() {
		ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
				": Initiating Communication with Broker", Color.GREEN);
		
		SequentialBehaviour querySequence = new SequentialBehaviour();
		addBehaviour(querySequence);
	
		// Get Quotes
		querySequence.addSubBehaviour(new OneShotBehaviour() {
			@Override 
			public void action() { 
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Sending query request", Color.GREEN);
				sendQuery();
			}
		});
		
		querySequence.addSubBehaviour(new SimpleBehaviour() {
			@Override
			public void action() {};
			
			@Override 
			public boolean done() { 
				if (queryFinished) {
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Query request complete", Color.GREEN);
					purchase();
				}
				return queryFinished; 
			}
		});
	}
	
	void purchase() {
		SequentialBehaviour purchaseSequence = new SequentialBehaviour();
		addBehaviour(purchaseSequence);
		
		if (bestPrice < home.remainingBudget()) {
			// Make Purchases
			purchaseSequence.addSubBehaviour(new OneShotBehaviour() {
				@Override 
				public void action() { 
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Sending purchase request", Color.GREEN);
					exchange = buyUnits();
					purchaseRequest(exchange);
				}
			});
			
			purchaseSequence.addSubBehaviour(new SimpleBehaviour() {
				@Override
				public void action() {}
				
				@Override 
				public boolean done() { 
					if (purchaseFinished) {
						ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
								": Purchase request complete", Color.GREEN);
					}
					return purchaseFinished; 
				}
			});
		} 
		
		purchaseSequence.addSubBehaviour(new DelayBehaviour(this, tradeTicks) {
			@Override 
			public void handleElapsedTimeout() { 
				queryFinished = purchaseFinished = false;
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Concluding Communication with Broker", Color.GREEN);
				
				query(); 
				
				if(tradeTickRange) {
					tradeTicks = randomRange(tradeTickMin, tradeTickMax);
				}
			}
		});
	}
	
	TickerBehaviour update = new TickerBehaviour(this, updateTicks) {
		@Override
		public void onTick() {
			int supplyChange = (home.getGenerationRate() + home.getUsageRate());
			home.setSupply(home.getSupply() - supplyChange);
			
			ProgramGUI.getInstance().printToLog(home.hashCode(), myAgent.getLocalName() + 
					" updating supply: \n\tChange = " + supplyChange + "\n\tSupply = " + home.getSupply() +
					"\n\tBudget reset", Color.RED);
			
			home.setExpenditure(0);
			
			if(updateTickRange) {
				updateTicks = randomRange(updateTickMin, updateTickMax);
			}
			reset(updateTicks);
		}
	};

	// -- Utility Methods --
	void lookupBroker() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(BROKER_AGENT);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try {
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			if (dfds.length > 0) {
				broker = dfds[0].getName();
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Localized broker agent", Color.GREEN);
				
			} else {
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Couldn't localize broker agent!", Color.RED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed searching in the DF!");
		}
	}
	
	void sendQuery() {
		if (broker == null) lookupBroker();
		if (broker == null) {
			ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
					": Unable to localize broker agent! \nOperation aborted!", Color.RED);
			return;
		}
		
		// Setup REQUEST message
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
		msg.addReceiver(broker);
		
		addBehaviour(new AchieveREInitiator(this, msg) {
			@Override
			protected void handleAgree(ACLMessage agree) {	
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Agent " + agree.getSender().getName() + " agreed to find the best price.", Color.GREEN);
			}
			
			@Override
			protected void handleRefuse(ACLMessage agree) {	
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Agent " + agree.getSender().getName() + " refused to find the best price.", Color.RED);
			}
			
			@Override
			protected void handleInform(ACLMessage inform) {
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Agent " + inform.getSender().getName() + " has retrieved the best price " + Integer.parseInt(inform.getContent()), Color.ORANGE);
				bestPrice = Integer.parseInt(inform.getContent());
			}
			
			@Override
			protected void handleAllResultNotifications(@SuppressWarnings("rawtypes") Vector notifications) {
				queryFinished = true;
				
				// Some responder didn't reply within the specified timeout
				if (notifications.size() == 0) {
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Timeout expired: no response received", Color.RED);
				}
			}
		});
	}
	
	void purchaseRequest(Exchange ex) {
		if (broker == null) lookupBroker();
		if (broker == null) {
			ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
					": Unable to localize broker agent! \nOperation aborted!", Color.RED);
			return;
		}
		
		// Setup REQUEST message
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		
		try {
			getContentManager().fillContent(msg, new Action(broker, ex));
			msg.addReceiver(broker);
		
			addBehaviour(new AchieveREInitiator(this, msg) {
				@Override
				protected void handleAgree(ACLMessage agree) {
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + agree.getSender().getName() + " agreed to purchase units on our behalf.", Color.ORANGE);
				}
				
				@Override
				protected void handleInform(ACLMessage inform) {
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + inform.getSender().getName() + " has purchased some energy units.", Color.GREEN);
					
					home.setExpenditure(home.getExpenditure() + (exchange.getPrice() * exchange.getUnits()));
					
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": \n\tBudget: " + home.getBudget() + "\n\tExpenditure: " + home.getExpenditure(), Color.ORANGE);
					System.out.println();
				}
				
				@Override
				protected void handleAllResultNotifications(@SuppressWarnings("rawtypes") Vector notifications) {
					purchaseFinished = true;
					
					// Some responder didn't reply within the specified timeout
					if (notifications.size() == 0) {
						ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
								": Timeout expired: no response received", Color.ORANGE);
						System.out.println();
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	Exchange buyUnits() {
		Exchange ex = new Exchange();
		ex.setType(SupplierVocabulary.BUY);
		ex.setPrice(bestPrice); 
		
		int max = (bestPrice != 0) ? home.remainingBudget() / bestPrice : 0;
		int result = 0;
		
		// Purchase an amount if allowed when running low or purchase all units at a good price.
		if (home.getSupply() < 50) {
			if (max > 10) {
				result = 10;
			} else {
				result = max;
			}
		} else if (bestPrice <= 5) {
			result = max;
		}
		
		ex.setUnits(result);
		return ex;
	}
	
	// TO-DO: Selling of units
	Exchange sellUnits() {
		Exchange ex = new Exchange();
		ex.setType(SupplierVocabulary.SELL);
		ex.setPrice(bestPrice);
		int result = 0;
		ex.setUnits(result);
		return ex;
	}
	
	// -- Utility Methods --
	void subscribeToAppliances() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(APPLIANCE_AGENT);
		dfd.addServices(sd);
	
		// Handle registration of new appliances
  		addBehaviour(new SubscriptionInitiator(this,
			DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, null)) {
  			ACLMessage applianceSub = new ACLMessage(ACLMessage.QUERY_REF);
  			
  			@Override
  			protected void handleInform(ACLMessage inform) {
  				try {
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					DFAgentDescription[] df = DFService.search(myAgent, dfd);
  					applianceSub.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
  					
  					// Register additional energy usage subscription
  					SubscriptionInitiator rateUpdate = new SubscriptionInitiator(myAgent, applianceSub) {		
  						@Override
  						protected void handleInform(ACLMessage inform) {
  							ProgramGUI.getInstance().printToLog(home.hashCode(), "Appliance data received. Updating supply.", Color.BLACK);
  							home.setSupply(home.getSupply() - Integer.parseInt(inform.getContent()));
  							appliances.put(inform.getSender(), Integer.parseInt(inform.getContent()));
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
						
						removeBehaviour(rateUpdate);
						rateUpdate.reset(applianceSub);
						
						if(exists) {
							System.out.println("\tNew retailer: " + dfds[i].getName());
							if (appliances.size() == 0) { System.out.println("\tHome listening for rate data..."); }
							
							appliances.put(dfds[i].getName(), null);
							applianceSub.addReceiver(dfds[i].getName());
							
							addBehaviour(rateUpdate);
						} else {
							System.out.println("\tDeleted retailer: " + dfds[i].getName());
							if(appliances.size() < 1) { System.out.println("\tHome stopped listening for rate data."); }
							
							appliances.remove(dfds[i].getName());
							applianceSub.removeReceiver(dfds[i].getName());
						}
					}
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
		});
	}
}
