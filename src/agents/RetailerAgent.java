package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import ontologies.*;
import utility.*;

public class RetailerAgent extends Agent implements SupplierVocabulary {
	private Retailer retailerDetails;
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new RegisterInDF(this, RETAILER_AGENT));
		seq.addSubBehaviour(new ReceiveMessages(this));
		addBehaviour(seq);
	}
	
	class ReceiveMessages extends CyclicBehaviour {
		public ReceiveMessages(Agent a) {
			super(a);
		}
		
		public void action() {
			ACLMessage msg = receive();
			if(msg == null) { block(); return; }
			
			try {
				ContentElement content = getContentManager().extractContent(msg);
				Concept action = ((Action)content).getAction();
				
				switch(msg.getPerformative()) {
				case (ACLMessage.QUERY_REF):
					System.out.println("Request from " + msg.getSender().getLocalName());
				
					if (action instanceof Exchange) {
						addBehaviour(new HandleExchange(myAgent, msg));
					} else {
						replyNotUnderstood(msg);
					}
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class HandleExchange extends OneShotBehaviour {
		private ACLMessage request;
		
		HandleExchange(Agent a, ACLMessage pRequest) {
			super(a);
			request = pRequest;
		}
		
		public void action() {
			try {
				ContentElement content = getContentManager().extractContent(request);
				Exchange ex = (Exchange)((Action)content).getAction();
				Exchange rEx = new Exchange();
				if (ex.getType() == BUY) {
					rEx.setType(SELL);
					rEx.setPrice(90);
				}
				
		        ACLMessage reply = request.createReply();
		        reply.setPerformative(ACLMessage.INFORM);
		        getContentManager().fillContent(reply, new Action(request.getSender(), rEx));
		        send(reply);
		        System.out.println("Responding with quote");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void replyNotUnderstood(ACLMessage msg) {
		try {
			ContentElement content = getContentManager().extractContent(msg);
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			getContentManager().fillContent(reply, content);
			send(reply);
			System.out.println("Not understood!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
