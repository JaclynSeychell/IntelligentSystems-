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
	// Agents
	private HashMap<AID, Integer> appliances = new HashMap<AID, Integer>();
	private Home home = new Home();
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
		
		ProgramGUI.printToLog(home.toString() + "\n", Color.GREEN);
		addBehaviour(update);
		
		// Find the broker agent
		lookupBroker();
		
		// Run agent
		query();
	}
	
	void query() {
		System.out.println("--\tInitiating Communication with Broker\t--");
		
		SequentialBehaviour querySequence = new SequentialBehaviour();
		addBehaviour(querySequence);
	
		// Get Quotes
		querySequence.addSubBehaviour(new OneShotBehaviour() {
			@Override 
			public void action() { 
				System.out.println("--\tSending query request\t--");
				sendQuery();
			}
		});
		
		querySequence.addSubBehaviour(new SimpleBehaviour() {
			@Override
			public void action() {};
			
			@Override 
			public boolean done() { 
				if (queryFinished) {
					System.out.println("--\tQuery request complete\t--");
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
					System.out.println("--Sending purchase request--");
					exchange = buyUnits();
					purchaseRequest(exchange);
				}
			});
			
			purchaseSequence.addSubBehaviour(new SimpleBehaviour() {
				@Override
				public void action() {}
				
				@Override 
				public boolean done() { 
					if (purchaseFinished) System.out.println("--\tPurchase request complete\t--");
					return purchaseFinished; 
				}
			});
		} 
		
		purchaseSequence.addSubBehaviour(new DelayBehaviour(this, tradeTicks) {
			@Override 
			public void handleElapsedTimeout() { 
				queryFinished = purchaseFinished = false;
				System.out.println("--\tConcluding Communication with Broker\t--\n");
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
			
			System.out.println(myAgent.getLocalName() + " updating supply...\n Change = " 
					+ supplyChange + "\n Supply = " + home.getSupply());
			
			home.setExpenditure(0);
			System.out.println(myAgent.getLocalName() + " budget reset");
			
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
				System.out.println("Localized broker agent");
			} else {
				System.out.println("Couldn't localize broker agent!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed searching in the DF!");
		}
	}
	
	void sendQuery() {
		if (broker == null) lookupBroker();
		if (broker == null) {
			System.out.println("Unable to localize broker agent! \nOperation aborted!");
			return;
		}
		
		// Setup REQUEST message
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
		msg.addReceiver(broker);
		
		addBehaviour(new AchieveREInitiator(this, msg) {
			protected void handleAgree(ACLMessage agree) {	
				System.out.println("Agent " + agree.getSender().getName() + 
						" agreed to find the best price.");
			}
			
			protected void handleInform(ACLMessage inform) {
					System.out.println("Agent " + inform.getSender().getName() + 
							" has retrieved the best price " + Integer.parseInt(inform.getContent()));
					bestPrice = Integer.parseInt(inform.getContent());
			}
			
			protected void handleAllResultNotifications(Vector notifications) {
				queryFinished = true;
					
				if (notifications.size() == 0) {
					// Some responder didn't reply within the specified timeout
					System.out.println("Timeout expired: no response received");
				}
			}
		});
	}
	
	void purchaseRequest(Exchange ex) {
		if (broker == null) lookupBroker();
		if (broker == null) {
			System.out.println("Unable to localize broker agent! \nOperation aborted!");
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
				protected void handleAgree(ACLMessage agree) {
					System.out.println("Agent " + agree.getSender().getName() + 
							" agreed to purchase units on our behalf.");
				}
				
				protected void handleInform(ACLMessage inform) {
					System.out.println("Agent " + inform.getSender().getName() + 
							" has purchased some energy units.");
					
					home.setExpenditure(home.getExpenditure() + (exchange.getPrice() * exchange.getUnits()));
					
					System.out.println("\tBudget: " + home.getBudget() + 
							"\n\tExpenditure: " + home.getExpenditure());
				}
				
				protected void handleAllResultNotifications(Vector notifications) {
					purchaseFinished = true;
					
					if (notifications.size() == 0) {
						// Some responder didn't reply within the specified timeout
						System.out.println("Timeout expired: no response received");
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
	void subscribeToRetailers() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(APPLIANCE_AGENT);
		dfd.addServices(sd);
	
		// Handle registration of new retailers
  		addBehaviour(new SubscriptionInitiator(this,
			DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, null)) {
  			
  			ACLMessage applianceSub = new ACLMessage(ACLMessage.QUERY_REF);
  			
  			@Override
  			protected void handleInform(ACLMessage inform) {
  				try {
  					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
  					DFAgentDescription[] df = DFService.search(myAgent, dfd);
  					applianceSub.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
  					
  					// Register additional price update subscription
  					SubscriptionInitiator rateUpdate = new SubscriptionInitiator(myAgent, applianceSub) {		
  						@Override
  						protected void handleInform(ACLMessage inform) {
  							System.out.println("Rate update: " + inform.getSender().getName() +
  									" to $" + Integer.parseInt(inform.getContent()) + " per unit");
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
