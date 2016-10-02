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

import ontologies.*;
import behaviours.*;
import utility.*;

@SuppressWarnings("serial")
public class HomeAgent extends Agent implements SupplierVocabulary {
	private Home home = new Home();
	private AID broker;
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());
	
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
		SequentialBehaviour tradingSequence = new SequentialBehaviour();
		addBehaviour(tradingSequence);
	
		tradingSequence.addSubBehaviour(new SimpleBehaviour() {
			@Override 
			public void action() { 
				contactBroker(ACLMessage.REQUEST, getExchange()); 
			}
			
			@Override 
			public boolean done() { return true; }
		});
		
		/*tradingSequence.addSubBehaviour(new DelayBehaviour(this, rnd.nextInt(5000)) {
			@Override 
			public void handleElapsedTimeout() { process(); }
		});*/
	}
	
	TickerBehaviour updateHome = new TickerBehaviour(this, rnd.nextInt(10000)) {
		@Override
		public void onTick() {
			int supplyChange = (home.getGenerationRate() + home.getUsageRate());
			home.setSupply(home.getSupply() - supplyChange);
			
			System.out.println(myAgent.getLocalName() + " updating supply...\n Change = " 
					+ supplyChange + "\n Supply = " + home.getSupply());
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
	
	void contactBroker(int performative, Concept action) {
		if (broker == null) lookupBroker();
		if (broker == null) {
			System.out.println("Unable to localize broker agent! \nOperation aborted!");
			return;
		}
		
		// Setup REQUEST message
		ACLMessage msg = new ACLMessage(performative);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
		msg.addReceiver(broker);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		
		try {
			getContentManager().fillContent(msg, new Action(broker, action));
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		addBehaviour(new AchieveREInitiator(this, msg) {
			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent " + inform.getSender().getName() + 
						" successfully performed the requested action");
			}
			
			protected void handleRefuse(ACLMessage refuse) {
				System.out.println("Agent " + refuse.getSender().getName() + 
						" refused to perform the requested action");
			}
			
			protected void handleFailure(ACLMessage failure) {
				if (failure.getSender().equals(myAgent.getAMS())) {
					System.out.println("Responder does not exist");
				}
				else {
					System.out.println("Agent " + failure.getSender().getName() + 
							" failed to perform the requested action");
				}
			}
			protected void handleAllResultNotifications(Vector notifications) {
				System.out.println("Timeout expired: no response received");
				if (notifications.size() == 0) {
					// Some responder didn't reply within the specified timeout
					System.out.println("Timeout expired: no response received");
				}
			}
		});
	}
}