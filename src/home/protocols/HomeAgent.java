package home.protocols;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Date;

public class HomeAgent extends Agent {
	private float storedEnergy;
	private float maxPricePerUnit;
	
	private AID broker;
	
	protected void setup() {
		// Set the broker for the home agent
		Object[] args = getArguments();
		broker = new AID((String) args[0], AID.ISLOCALNAME);
		
		// Initialize incremental price querying
		addBehaviour(new TickerBehaviour(this, 10000) {
			protected void onTick() {
				brokerEnquiry(true);
			}
		} );
	}
	
	protected void brokerEnquiry(boolean buy) {
		System.out.println("Contacting broker");
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(broker);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
		
		if (buy) { 
			msg.setContent("buy-enquiry"); 
		} else {
			msg.setContent("sell-enquiry");
		}
		
		addBehaviour(new AchieveREInitiator(this, msg) {
			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
			}
			protected void handleRefuse(ACLMessage refuse) {
				System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
			}
			protected void handleFailure(ACLMessage failure) {
				if (failure.getSender().equals(myAgent.getAMS())) {
					// FAILURE notification from the JADE runtime: the receiver
					// does not exist
					System.out.println("Responder does not exist");
				}
				else {
					System.out.println("Agent "+failure.getSender().getName()+" failed to perform the requested action");
				}
			}
		} );
	}
}
