package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.SubscriptionInitiator;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import java.awt.Color;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import behaviours.*;
import gui.ProgramGUI;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class HomeAgent extends Agent implements SupplierVocabulary {
	// Data
	private Home home;
	private Queue<Tuple<Integer, Integer>> updateOverTime = new CircularFifoQueue<Tuple<Integer, Integer>>(100);

	// Agents
	private HashMap<AID, Integer> appliances = new HashMap<AID, Integer>();
	private AID broker;
	
	// Language
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Exchange quote;
	private Exchange purchase;
	
	// Utility
	private Random rnd = Utility.newRandom(hashCode());	
	private int bestPrice;
	
	// FSM Variables
	private boolean queryFinished;
	private boolean queryError;
	private boolean purchaseFinished;
	private boolean purchaseError;
	
	// Run parameters
	private int tradeTicks = 60000;
	private int updateTicks = 60000;
	private boolean tradeTickRange = false;
	private boolean updateTickRange = false;
	private int tradeTickMin;
	private int tradeTickMax;
	private int updateTickMin;
	private int updateTickMax;
	
	public int updateUnit = 1000; // seconds;
	
	public int randomRangeTimer(int min, int max) {
		float result = (rnd.nextFloat() * (max - min) + min) * updateUnit;
		return (int)result;
	}
	
	public float randomRangeFloat(float min, float max) {
		float result = (rnd.nextFloat() * (max - min) + min);
		return result;
	}
	
	public float averageTime(float min, float max) {
		return ( (max - min) / 2 ) + min;
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
			tradeTickMin = (int)tradeData[1];
			tradeTickMax = (int)tradeData[2];
			
			updateTickRange = (boolean)updateData[0];
			updateTickMin = (int)updateData[1];
			updateTickMax = (int)updateData[2];
			
			tradeTicks = tradeTickRange ? randomRangeTimer(tradeTickMin, tradeTickMax) : (int)(tradeTickMax * updateUnit);
			updateTicks = updateTickRange ? randomRangeTimer(updateTickMin, updateTickMax) : (int)(updateTickMax * updateUnit);
			System.out.println(getLocalName() + " Trading every " + tradeTicks + "ms");
			System.out.println(getLocalName() + " Updating every " + updateTicks + "ms");
		} else {
			home = new Home();
			tradeTicks = 60000 * 5;
			updateTicks = 60000 * 5;
		}
		
		ProgramGUI.getInstance().printToLog(home.hashCode(), home.toString(), Color.GREEN);
		update.reset(updateTicks);
		addBehaviour(update);
		
		// Find the broker agent
		lookupBroker();
		
		// Find appliances
		subscribeToAppliances();
		
		process();
	}
	
	void process() {
		SequentialBehaviour seq = new SequentialBehaviour();
		
		// Time between trades
		seq.addSubBehaviour(new WakerBehaviour(this, tradeTicks) {
			@Override
			protected void onWake() {
				if (tradeTickRange) {
					tradeTicks = randomRangeTimer(tradeTickMin, tradeTickMax);
				}
			}
			
			@Override
			public int onEnd() {
				System.out.println("Ticks: " + tradeTicks);
				reset(tradeTicks);
				return 0;
			}
		});
		
		// Start query
		seq.addSubBehaviour(new OneShotBehaviour() {
			@Override 
			public void action() {
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Sending query request", Color.GREEN);
				
				sendQuery();
			}
		});
		
		seq.addSubBehaviour(new SimpleBehaviour() {
			private boolean finished;
			
			@Override
			public void action() {
				if (queryFinished) {
					finished = true;
					
					// Reset behaviour (don't run purchases) 
					if (!queryError) {
						System.out.println("Query finished with no errors");
					} else {
						System.out.println("Query finished with errors");
						seq.reset();
					}	
				}
			}
			
			@Override 
			public boolean done() {
				return finished;
			}
		});
		
		// Start purchase
		seq.addSubBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
						": Sending purchase request", Color.GREEN);
					
				purchaseRequest(purchase);
 			}
		});
		
		seq.addSubBehaviour(new SimpleBehaviour() {
			private boolean finished;
			
			@Override 
			public void action() {
				if(purchaseFinished) {
					finished = true;
					
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Purchase request complete", Color.GREEN);
					
					seq.reset();
				}
			}
			
			@Override 
			public boolean done() {
				return finished;
			}
		});
		
		addBehaviour(seq);
	}
	
	TickerBehaviour update = new TickerBehaviour(this, updateTicks) {
		@Override
		public void onTick() {
			// Fluctuate supply +/- 20% with a 5% chance of a larger +/- 50% change
			float variance;
			variance = rnd.nextFloat() <= 0.05f ? (randomRangeFloat(-50, 50) / 100) + 1 : (randomRangeFloat(-20, 20) / 100) + 1;
			int supplyChange = (int) Math.round(( ((home.getUsageRate() - home.getGenerationRate()) * variance) ));
			home.setSupply(home.getSupply() - supplyChange);
			
			// Fluctuate income +/- 20% with a 5% chance of a larger +/- 50% change
			variance = rnd.nextFloat() <= 0.05f ? (randomRangeFloat(-50, 50) / 100) + 1 : (randomRangeFloat(-20, 20) / 100) + 1;
			int budgetChange = (int) Math.round( (home.getIncome() * variance) );
			home.setBudget( home.getBudget() + budgetChange );
			
			updateOverTime.add(new Tuple<Integer, Integer>(supplyChange, budgetChange));
		
			ProgramGUI.getInstance().printToLog(home.hashCode(), myAgent.getLocalName() + 
					" updating: \n\tSupply=" + home.getSupply() + "(" + supplyChange + ")" + 
					"\n\tBudget = " + home.getBudget() + "(" + budgetChange + ")", Color.RED);
			
			if(updateTickRange) {
				updateTicks = randomRangeTimer(updateTickMin, updateTickMax);
			}
			reset(updateTicks);
		}
	};
	
	DelayBehaviour tradeDelay = new DelayBehaviour(this, tradeTicks) {
		@Override
		public void handleElapsedTimeout() {
			ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
					": checking energy requirements", Color.BLACK);
			
			if (tradeTickRange) {
				tradeTicks = randomRangeTimer(tradeTickMin, tradeTickMax);
			}
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
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		msg.addReceiver(broker);
		
		quote = buyUnits();
		
		try {
			getContentManager().fillContent(msg, new Action(broker, quote));
			
			addBehaviour(new AchieveREInitiator(this, msg) {
				@Override
				protected void handleAgree(ACLMessage agree) {	
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + agree.getSender().getName() + " agreed to perform query.", Color.GREEN);
				}
				
				@Override
				protected void handleRefuse(ACLMessage agree) {	
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + agree.getSender().getName() + " refused to perform query.", Color.RED);
				}
				
				@Override 
				protected void handleFailure(ACLMessage failure) {
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + failure.getSender().getName() + " failed to perform query.", Color.RED);
				}
				
				@Override
				protected void handleInform(ACLMessage inform) {
					try {
						ContentElement content = getContentManager().extractContent(inform);
						purchase = (Exchange)((Action)content).getAction();	
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + inform.getSender().getName() + " has returned a quote $" + 
							purchase.getPrice() * purchase.getUnits() + " for " + purchase.getUnits() + "units", 
							Color.ORANGE);
				}
				
				@Override
				protected void handleAllResultNotifications(@SuppressWarnings("rawtypes") Vector notifications) {
					queryFinished = true;
					
					// Some responder didn't reply within the specified timeout
					if (notifications.size() == 0) {
						queryError = true;
						ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
								": Timeout expired! No response received", Color.RED);
					}
					
					// sleep
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		
		try {
			getContentManager().fillContent(msg, new Action(broker, purchase));
			msg.addReceiver(broker);
		
			addBehaviour(new AchieveREInitiator(this, msg) {
				@Override
				protected void handleAgree(ACLMessage agree) {
					ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
							": Agent " + agree.getSender().getName() + " agreed to purchase units on our behalf.", Color.GREEN);
				}
				
				@Override
				protected void handleInform(ACLMessage inform) {
					switch(purchase.getType()) {
					case BUY:
						ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
								": Agent " + inform.getSender().getName() + " buying transaction complete", Color.GREEN);
						
						home.setBudget(home.getBudget() - (purchase.getPrice() * purchase.getUnits()));
						home.setSupply(home.getSupply() + purchase.getUnits());
						
						break;
					case SELL:
						ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
								": Agent " + inform.getSender().getName() + " selling transaction complete", Color.GREEN);
						
						home.setBudget(home.getBudget() + (purchase.getPrice() * purchase.getUnits()));
						home.setSupply(home.getSupply() - purchase.getUnits());
		
						break;
					}
				}
				
				@Override
				protected void handleAllResultNotifications(@SuppressWarnings("rawtypes") Vector notifications) {
					purchaseFinished = true;
					
					// Some responder didn't reply within the specified timeout
					if (notifications.size() == 0) {
						purchaseError = true; 
						
						ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
								": Timeout expired! No response received", Color.RED);
					}
					
					// sleep
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	Exchange buyUnits() {
		// Get average update times
		int avgUpdate = updateTicks;
		int avgTrade = tradeTicks;
		
		if (updateTickRange) {
			avgUpdate = (int) (averageTime(updateTickMin, updateTickMax) * updateUnit);
		}
		
		if(tradeTickRange) {
			avgTrade = (int) (averageTime(tradeTickMin, tradeTickMax) * updateUnit);
		}
		
		int updatesPerTrade = avgTrade / avgUpdate;
		updatesPerTrade = updatesPerTrade < 1 ? 1 : updatesPerTrade; 
		System.out.println(getLocalName() + ": has " + updatesPerTrade + " updates between trades");
		
		// Collect past data
		int maxUsage = home.getUsageRate() - home.getGenerationRate(); 
		int minUsage = home.getUsageRate() - home.getGenerationRate();
		int avgUsage = home.getUsageRate() - home.getGenerationRate(); 
		int totUsage = home.getUsageRate() - home.getGenerationRate();
		int maxIncome = home.getIncome();
		int minIncome = home.getIncome();
		int avgIncome = home.getIncome();
		int totIncome = home.getIncome();
		
		Iterator<Tuple<Integer, Integer>> it = updateOverTime.iterator();
		int count = 1;
		while(it.hasNext()) {
			Tuple<Integer, Integer> e = it.next();
			int usage = e.first();
			int income = e.last();
			
			System.out.println(getLocalName() + ": usage = " + usage + ", income = " + income);
			
			if(count == 1) {
				maxUsage = minUsage = avgUsage = totUsage = usage;
				maxIncome = minIncome = avgIncome = totIncome = income;
			} else {
				maxUsage = usage > maxUsage ? usage : maxUsage;
				minUsage = usage < minUsage ? usage : minUsage;
				maxIncome = income > maxIncome ? income : maxIncome;
				minIncome = income < minIncome ? income : minIncome;
				
				totUsage += usage;
				totIncome += income;
				avgUsage = totUsage / count;
				avgIncome = totIncome / count;
			}
			
			count++;
		}
		System.out.println(getLocalName() + ": average usage = " + avgUsage);
		
		int updatesLeft = ( (home.getSupply() - (avgUsage * updatesPerTrade)) / (avgUsage * updatesPerTrade) );
		System.out.println(getLocalName() + ": has " + updatesLeft + " updates remaining until out of supply");
		
		Exchange ex = new Exchange();
		ex.setType(SupplierVocabulary.BUY);
		
		int units = 0;
		float roll = 0;
		
		if (updatesLeft < 3) {
			units = (avgUsage * (updatesPerTrade + 2));
			roll = 0.75f;
			for(int i = 0; i < 3; i++) {
				if(rnd.nextFloat() > roll) {
					units += avgUsage;
				}
			}
		} else if (updatesLeft < 5) {
			units = (avgUsage * (updatesPerTrade + 1));
			roll = 0.50f;
			for(int i = 0; i < 3; i++) {
				if(rnd.nextFloat() > roll) {
					units += avgUsage;
				}
			}
		} else if (updatesLeft < 10) {
			units = (avgUsage * updatesPerTrade);
			roll = 0.25f;
			for(int i = 0; i < 3; i++) {
				if(rnd.nextFloat() > roll) {
					units += avgUsage;
				}
			}
		} else {
			ex.setType(SupplierVocabulary.SELL);
			
			units = (avgUsage * updatesPerTrade);
			roll = 0.10f;
			for(int i = 0; i < 3; i++) {
				if(rnd.nextFloat() > roll) {
					units += avgUsage;
				}
			}
		}

		ex.setPrice(0); 
		ex.setUnits(units);
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
  			
  			@Override
  			protected void handleInform(ACLMessage inform) {
  				try {
  					ACLMessage applianceSub = new ACLMessage(ACLMessage.QUERY_REF);
  					applianceSub.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
  					
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					DFAgentDescription[] df = DFService.search(myAgent, dfd);
  					
  					// Register additional energy usage subscription
  					SubscriptionInitiator rateUpdate = new SubscriptionInitiator(myAgent, applianceSub) {		
  						@Override
  						protected void handleInform(ACLMessage inform) {
  							ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + ": appliance data received. Updating supply.", Color.BLACK);
  							
  							home.setSupply(home.getSupply() + Integer.parseInt(inform.getContent()));
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
							System.out.println(getLocalName() + ": new appliance " + dfds[i].getName());
							ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() +
									": new appliance " + dfds[i].getName(), Color.GREEN);
							
							if (appliances.size() == 0) { 
								System.out.println(getLocalName() + ": listening for energy usage from " + dfds[i].getName());
								ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() +
										": listening for energy usage from " + dfds[i].getName(), Color.GREEN);
							}
							
							appliances.put(dfds[i].getName(), null);
							applianceSub.addReceiver(dfds[i].getName());
							
							addBehaviour(rateUpdate);
						} else {
							ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
									": stopped listening for appliance " + dfds[i].getName(), Color.RED);
							
							if(appliances.size() < 1) { 
								ProgramGUI.getInstance().printToLog(home.hashCode(), getLocalName() + 
										": stopped listening for appliances.", Color.RED);
							}
							
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
