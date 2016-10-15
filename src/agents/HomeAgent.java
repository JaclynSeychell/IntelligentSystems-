package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import java.sql.Date;
import java.util.Random;
import java.util.Vector;

import behaviours.*;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class HomeAgent extends Agent implements SupplierVocabulary {
	private Home home = new Home();
	private AID broker;
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());	
	
	private int bestPrice;
	
	private boolean queryFinished;
	private boolean purchaseFinished;
	
	private static final int TICK_TIME = (60000 * 5);
	
	// Initialize home values
	void setupHome() {
		home.setBudget(rnd.nextInt(100));
		home.setGenerationRate(rnd.nextInt(10));
		home.setUsageRate(rnd.nextInt(100));
		home.setSupply(rnd.nextInt(2000));
		System.out.println(home.toString());
		addBehaviour(updateHome);
	}
	
	// Prepare message content
	Exchange getExchange() {
		Exchange ex = new Exchange();
		ex.setType(SupplierVocabulary.BUY);
		ex.setPrice(home.getBudget());
		return ex;
	}
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Setup home state
		setupHome();
		
		// Find the broker agent
		lookupBroker();
		
		// Run agent
		process();
	}
	
	void process() {
		System.out.println("Contacting the broker");
		
		SequentialBehaviour tradingSequence = new SequentialBehaviour();
		addBehaviour(tradingSequence);
	
		// Get prices
		tradingSequence.addSubBehaviour(new OneShotBehaviour() {
			@Override 
			public void action() { 
				System.out.println("\n\nCommencing Query");
				query();
			}
		});
		
		tradingSequence.addSubBehaviour(new SimpleBehaviour() {
			@Override
			public void action() {};
			
			@Override 
			public boolean done() { 
				if (queryFinished) System.out.println("Finished Query");
				return queryFinished; 
			}
		});
		
		//Choose Units 
		tradingSequence.addSubBehaviour(new OneShotBehaviour() {
			@Override 
			public void action() { 
				if(queryFinished = true){
					System.out.println("\nChoosing amount of Units\n");
					unitsWanted();
				}
				
			}
		});
		
		// Buy units
		tradingSequence.addSubBehaviour(new OneShotBehaviour() {
			@Override 
			public void action() { 
				System.out.println("\nCommencing Purchase\n");
				purchase();
			}
		});
		
		tradingSequence.addSubBehaviour(new SimpleBehaviour() {
			@Override
			public void action() {}
			
			@Override 
			public boolean done() { 
				if (purchaseFinished) System.out.println("\n\nFinished Purchase");
				return purchaseFinished; 
			}
		});
		
		// Wait to restart behaviour
		tradingSequence.addSubBehaviour(new DelayBehaviour(this, rnd.nextInt(10000)) {
			@Override 
			public void handleElapsedTimeout() { 
				queryFinished = purchaseFinished = false;
				process(); 
			}
		});
	}
	
	TickerBehaviour updateHome = new TickerBehaviour(this, rnd.nextInt(TICK_TIME)) {
		@Override
		public void onTick() {
			int supplyChange = (home.getGenerationRate() + home.getUsageRate());
			home.setSupply(home.getSupply() - supplyChange);
			
			System.out.println(myAgent.getLocalName() + " updating supply...\n Change = " 
					+ supplyChange + "\n Supply = " + home.getSupply());
			
			reset(rnd.nextInt(TICK_TIME));
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
	
	void query() {
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
			}
			
			protected void handleAllResultNotifications(Vector notifications) {
				System.out.println("Query communication concluded");
				queryFinished = true;
					
				if (notifications.size() == 0) {
					// Some responder didn't reply within the specified timeout
					System.out.println("Timeout expired: no response received");
				}
			}
		});
	}
	
	void purchase() {
		if (broker == null) lookupBroker();
		if (broker == null) {
			System.out.println("Unable to localize broker agent! \nOperation aborted!");
			return;
		}
		
		// Setup REQUEST message
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
		msg.addReceiver(broker);
		
		addBehaviour(new AchieveREInitiator(this, msg) {
			protected void handleAgree(ACLMessage agree) {
				System.out.println("Agent " + agree.getSender().getName() + 
						" agreed to purchase units on our behalf.");
			}
			
			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent " + inform.getSender().getName() + 
						" has purchased some energy units.");
			}
			
			protected void handleAllResultNotifications(Vector notifications) {
				System.out.println("Purchase communication concluded");
				purchaseFinished = true;
				
				if (notifications.size() == 0) {
					// Some responder didn't reply within the specified timeout
					System.out.println("Timeout expired: no response received");
				}
			}
		});
	}
	
	void  unitsWanted()
	{
		// Determine how many units it wants to purchase 
		// Units are brought based off how much is needed to stock up if cheap,
		// otherwise will only buy small amount if expensive 
		
		int supplyRate = home.getSupply();			 // Current supply the home holds
		int homeCapacity = 30;   				// Presuming capacity of 30 units of energy 
		int budget = home.getBudget();
		int energyPrice = home.getGenerationRate();  		// Price in query (?)Unknown if correct method
		int unitsBought = 0;					// Units of energy willing to buy 
		
		//Purchase 10 units regardless of price when empty 
		if( supplyRate == 0){
			unitsBought = 10;
			purchase();
		}
		
		//Purchase full stock up if price of energy is less than 5
		if( energyPrice <= 5 && supplyRate != 0){
			unitsBought = homeCapacity;
			purchase();
		}
		
		//Desperate Mode 
		if(energyPrice > budget && supplyRate == 0){
			unitsBought = 5;
			purchase();
		}
		
	}
}
