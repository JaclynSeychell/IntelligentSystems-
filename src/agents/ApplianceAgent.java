package agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;

import java.awt.Color;
import java.util.Random;

import gui.ProgramGUI;
import ontologies.*;
import utility.*;

@SuppressWarnings("serial")
public class ApplianceAgent extends Agent implements SupplierVocabulary{
	private Appliance appliance;
	
	// Language
	private Codec codec = new SLCodec();
	private Ontology ontology = SupplierOntology.getInstance();
	
	// Utility
	private Random rnd = Utility.newRandom(hashCode());
	
	// Run parameters
	private int updateTicks = 60000;
	private boolean updateTickRange = false;
	private float updateTickMin;
	private float updateTickMax;
	
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
		
		ProgramGUI.getInstance().printToLog(appliance.hashCode(), appliance.toString(), Color.GREEN);
		
		// Register in the DF
		DFRegistry.register(this, APPLIANCE_AGENT);
		subscriptionHandler();
	}
	
	// Deregister this agent
	@Override
	protected void takeDown() {
		ProgramGUI.getInstance().printToLog(appliance.hashCode(), getLocalName() + ": agent shutdown", Color.RED);
		try { DFService.deregister(this); } catch (Exception e) { e.printStackTrace(); };
		
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(APPLIANCE_AGENT);
			dfd.addServices(sd);
			
			DFAgentDescription[] dfds = DFService.search(this, dfd);
			String msg = getLocalName() + ": ";
			if(dfds.length > 0) {
				msg = "remaining appliances:";
			} else {
				msg = "no remaining appliances.";
			}
			
			for(int i = 0; i < dfds.length; i++) {
				msg += "\t" + dfds[i].getName();
			}
			
			ProgramGUI.getInstance().printToLog(appliance.hashCode(), msg, Color.GREEN);
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
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
				
				ProgramGUI.getInstance().printToLog(appliance.hashCode(), myAgent.getLocalName() + ": new subcriber " + 
						subscription.getSender().getLocalName(), Color.GREEN);
				
				sub = getSubscription(subscription);
				notification = subscription.createReply();
				notification.setPerformative(ACLMessage.INFORM);
				
				// Send rate data out to subscribers
				addBehaviour(new TickerBehaviour(myAgent, updateTicks) {
					@Override
					public void onTick() {
						ProgramGUI.getInstance().printToLog(appliance.hashCode(), myAgent.getLocalName() + 
								": Notifying home agent of energy usage.", Color.BLACK);
						
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
