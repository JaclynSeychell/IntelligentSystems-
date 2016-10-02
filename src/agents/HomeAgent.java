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

import java.util.Random;

import ontologies.*;
import behaviours.*;
import utility.*;


public class HomeAgent extends Agent implements SupplierVocabulary {
	private Home homeDetails;
	private AID broker;
	private ACLMessage brokerResponse;
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	private Random rnd = Utility.newRandom(hashCode());
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		SequentialBehaviour seq = new SequentialBehaviour();
		addBehaviour(seq);
		
		Exchange ex = new Exchange();
		ex.setPrice(100);
		ex.setType(BUY);
		sendMessage(ACLMessage.REQUEST, ex);
		
		seq.addSubBehaviour(handleBrokerResponse);
		
		seq.addSubBehaviour(new DelayBehaviour(this, rnd.nextInt(5000)) {
			@Override
			public void handleElapsedTimeout() {
				setup();
			}
		});
	}
	
	// Code to handle the response from the broker after some request
	private ReceiveResponse handleBrokerResponse = new ReceiveResponse (this, 1000, MessageTemplate.MatchSender(broker)) {
		@Override 
		public void handle(ACLMessage m) {
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
					System.out.println("Response from broker: NOT UNDERSTOOD!");
				} else if (msg.getPerformative() != ACLMessage.INFORM) {
					System.out.println("Unexpected msg from broker!");
				} else {
					System.out.println("Broker has completed the request.");
				}
			} else {
				System.out.println("Broker did not respond in time");
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
				System.out.println("Localized broker agent");
			} else {
				System.out.println("Couldn't localize broker agent!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed searching in the DF!");
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
		msg.setConversationId(Utility.genCID(getLocalName(), hashCode()));
		
		try {
			getContentManager().fillContent(msg, new Action(broker, action));
			msg.addReceiver(broker);
			send(msg);
			System.out.println("Contacting broker... Please wait!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}