package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import java.util.Random;

import gui.ProgramGUI;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class ApplianceAgent extends GuiAgent implements SupplierVocabulary{
	private Appliance appliance;
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	private Random rnd = Utility.newRandom(hashCode());
	
	private int updateTicks = 60000;
	private boolean updateTickRange = false;
	private float updateTickMin;
	private float updateTickMax;
	
	transient protected ProgramGUI myGui; 
	
	public int randomRange(float min, float max) {
		float result = (rnd.nextFloat() * (max - min) + min) * 60000;
		return (int)result;
	}
	
	@Override
	protected void setup() {
		// Register language and ontology
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Setup appliance state
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			appliance = (Appliance)args[0];
			Object[] updateData = (Object[])args[1];
			
			updateTickRange = (boolean)updateData[0];
			updateTickMin = (float)updateData[1];
			updateTickMax = (float)updateData[2];
			
			updateTicks = updateTickRange ? randomRange(updateTickMin, updateTickMax) : (int)updateTickMax * 60000;
		} else {
			appliance = new Appliance();
		}
		
		System.out.println(appliance.toString());
		
		// Register in the DF
		DFRegistry.register(this, APPLIANCE_AGENT);
		subscriptionHandler();
	}
	
	// Deregister this agent
	@Override
	protected void takeDown() {
		System.out.println("Shutting down " + this.getName() + ".");
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(APPLIANCE_AGENT);
			dfd.addServices(sd);
			
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			
			if(dfds.length > 0) {
				System.out.println("\tRemaining appliances:");
			} else {
				System.out.println("\tNo remaining appliances.");
			}
			
			for(int i = 0; i < dfds.length; i++) {
				System.out.print("\t" + dfds[i].getName());
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	@Override
	protected void onGuiEvent(GuiEvent ev) {
	
	}
	
	void alertGui(Object response) {
		
	}
	
	// -- Utility Methods -- 
	void subscriptionHandler() {
		MessageTemplate mt = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE)
		);
		
		SubscriptionResponder sr = new SubscriptionResponder(this, mt) {
			ACLMessage notification;
			Subscription sub;
			
			@Override
			protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
				super.handleSubscription(subscription);
				System.out.println("Subscription: \n\t" + 
						subscription.getSender().getName() + " successfully subscribed to " + myAgent.getName());
				
				sub = getSubscription(subscription);
				notification = subscription.createReply();
				notification.setPerformative(ACLMessage.INFORM);
				
				
				// Send rate data out to subscribers
				addBehaviour(new TickerBehaviour(myAgent, updateTicks) {
					@Override
					public void onTick() {
						System.out.println(myAgent.getLocalName() + " rate: " 
								+ appliance.getRate() + " units.");
						
						notification.setContent(Integer.toString(appliance.getRate()));
						sub.notify(notification);
						
						if(updateTickRange) {
							updateTicks = randomRange(updateTickMin, updateTickMax);
						}
						reset(updateTicks);

					}
				});
				
				return null;
			}	
		};
		
		addBehaviour(sr);
	}
}
