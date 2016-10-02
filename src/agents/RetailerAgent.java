package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import java.util.Random;

import ontologies.*;
import utility.*;

@SuppressWarnings("serial")

public class RetailerAgent extends Agent implements SupplierVocabulary {
	private Retailer retailer = new Retailer();
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());
	
	void setupRetailer() {
		retailer.setGenerationRate(rnd.nextInt(10));
		retailer.setPricePerUnit(rnd.nextInt(5));
		retailer.setSupply(rnd.nextInt(2000));
		System.out.println(retailer.toString());
		addBehaviour(updateRetailer);
	}
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Setup retailer state
		setupRetailer();
		
		// Register in the DF
		addBehaviour(new RegisterInDF(this, RETAILER_AGENT));
		
		// Run agent
		process();
	}
	
	void process() {
		System.out.println("Agent " + getLocalName() + " waiting for requests...");
		MessageTemplate template = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
		
		addBehaviour(new AchieveREResponder(this, template) {
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": REQUEST received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
				
				if (checkAction()) {
					// We agree to perform the action. Note that in the FIPA-Request
					// protocol the AGREE message is optional. Return null if you
					// don't want to send it.
					System.out.println("Agent " + getLocalName() + ": Agree");
					ACLMessage agree = request.createReply();
					agree.setPerformative(ACLMessage.AGREE);
					return agree;
				}
				else {
					// We refuse to perform the action
					System.out.println("Agent "+getLocalName()+": Refuse");
					throw new RefuseException("check-failed");
				}
			}
			
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				if (performAction()) {
					System.out.println("Agent " + getLocalName() + ": Action successfully performed");
					ACLMessage inform = request.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}
				else {
					System.out.println("Agent " + getLocalName() + ": Action failed");
					throw new FailureException("unexpected-error");
				}	
			}
		} );
	}
	
	 private boolean checkAction() {
	  	// Simulate a check by generating a random number
	  	return (Math.random() > 0.2);
	  }
	  
	  private boolean performAction() {
	  	// Simulate action execution by generating a random number
	  	return (Math.random() > 0.2);
	  }
	
	TickerBehaviour updateRetailer = new TickerBehaviour(this, rnd.nextInt(10000)) {
		@Override
		public void onTick() {
			retailer.setSupply(retailer.getSupply() + retailer.getGenerationRate());
		
			System.out.println(myAgent.getLocalName() + " updating supply...\n Change = " + 
					retailer.getGenerationRate() + "\n Supply = " + retailer.getSupply());
		}
	};
}
