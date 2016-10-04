package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
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
	
	private static final int TICK_TIME = 10000;
	
	void setupRetailer() {
		retailer.setGenerationRate(rnd.nextInt(10));
		retailer.setPricePerUnit(rnd.nextInt(5) + 1);
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
		DFRegistry.register(this, RETAILER_AGENT);
		
		// Run agent
		process();
	}
	
	protected void takeDown() {
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
	}
	
	void process() {
		System.out.println("Agent " + getLocalName() + " waiting for requests...");
		MessageTemplate template = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
		
		// Handle purchase confirmation
		addBehaviour(new AchieveREResponder(this, template) {
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": REQUEST received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
		
				System.out.println("Agent " + getLocalName() + ": Agree");
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
			}
			
			/*protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				System.out.println("Agent " + getLocalName() + ": Action successfully performed");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			}*/
		} );
		
		MessageTemplate queryTemplate = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF) );
		
		// Handle purchase confirmation
		addBehaviour(new AchieveREResponder(this, queryTemplate) {
			protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent " + getLocalName() + ": QUERY_REF received from " + 
						request.getSender().getName() + ". Action is " + request.getContent());
				
				System.out.println("Agent " + getLocalName() + ": Agree");
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;
			}
			
			protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
				System.out.println("Agent " + getLocalName() + ": Quote generated");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				inform.setContent("" + retailer.getPricePerUnit());
				return inform;
			}
		} );
	}
	
	TickerBehaviour updateRetailer = new TickerBehaviour(this, rnd.nextInt(TICK_TIME)) {
		@Override
		public void onTick() {
			retailer.setSupply(retailer.getSupply() + retailer.getGenerationRate());
		
			System.out.println(myAgent.getLocalName() + " updating supply...\n Change = " + 
					retailer.getGenerationRate() + "\n Supply = " + retailer.getSupply());
			
			reset(rnd.nextInt(TICK_TIME));
		}
	};
}
