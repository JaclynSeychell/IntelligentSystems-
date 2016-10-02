package behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveResponse extends SimpleBehaviour {
	private MessageTemplate template;
	private long timeOut;
	private long wakeupTime;
	private boolean finished = false;
	protected ACLMessage msg;
	
	public ReceiveResponse(Agent a, int millis, MessageTemplate mt) {
		super(a);
		timeOut = millis;
		template = mt;
	}
	
	public ACLMessage getMessage() {
		return msg;
	}
	
	public void onStart() {
		wakeupTime = (timeOut < 0 ? Long.MAX_VALUE : System.currentTimeMillis() + timeOut);
	}
	
	public void action() {
		if (template == null) {
			msg = myAgent.receive();
		} else {
			msg = myAgent.receive(template);
		}
			
		if (msg != null) {
			finished = true;
			handle(msg);
			return;
		}
		
		long dt = wakeupTime - System.currentTimeMillis();
		if(dt > 0) {
			block(dt);
		} else {
			finished = true;
			handle(msg);
		}
	}
	
	public void handle(ACLMessage m) {} // Can override
	
	public boolean done() { 
		return finished; 
	}
	
	public int onEnd() {
		return 0;
	}
}