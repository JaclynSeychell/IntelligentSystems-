package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import ontologies.*;

public class HomeAgent extends Agent implements SupplierVocabulary {
	private Home agentDetails;
	private AID broker;
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		addBehaviour(new ContactBroker(this, 5000));
	}
	
	class WaitBrokerResponse extends ParallelBehaviour {
		WaitBrokerResponse(Agent a) {
			super(a, 1);
			
			addSubBehaviour(new ReceiveResponse(myAgent));
			
			addSubBehaviour(new WakerBehaviour(myAgent, 2000) {
				protected void handleElapsedTimeout() {
					System.out.println("No response from server. Please, try later!");
				}
			});
		}
	}
	
	class ReceiveResponse extends SimpleBehaviour {
		private boolean finished = false;
		
		ReceiveResponse(Agent a) {
			super(a);
		}
		
		public void action() {
			ACLMessage msg = receive(MessageTemplate.MatchSender(broker));
			if (msg == null) {block(); return; }
			
			if (msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
				System.out.println("Response from broker: NOT UNDERSTOOD!");
			} else if (msg.getPerformative() != ACLMessage.INFORM) {
				System.out.println("Unexpected msg from broker!");
			} else {
				System.out.println("Broker has completed the request.");
			}
			finished = true;
		}
		
		public boolean done() { return finished; }
		
		public int onEnd() {
			return 0;
		}
	}
	
	
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
				System.out.println("\nCouldn't localize broker agent!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nFailed searching in the DF!");
		}
	}
	
	private void sendMessage(int performative, AgentAction action) {
		if (broker == null) lookupBroker();
		if (broker == null) {
			System.out.println("Unable to localize broker agent! \nOperation aborted!");
			return;
		}
		
		ACLMessage msg = new ACLMessage(performative);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		try {
			getContentManager().fillContent(msg, new Action(broker, action));
			msg.addReceiver(broker);
			send(msg);
			System.out.println("Contacting broker... Please wait!");
			addBehaviour(new WaitBrokerResponse(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Contact Broker
	class ContactBroker extends TickerBehaviour {
		ContactBroker(Agent a, long ms) {
			super(a, ms);
		}
		
		protected void onTick() {
			Exchange ex = new Exchange();
			ex.setPrice(100);
			ex.setType(BUY);
			sendMessage(ACLMessage.REQUEST, ex);
		}
	}
}