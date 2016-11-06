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
	
	public int updateUnit = 1000; // seconds;
	
	// Utility
	private Random rnd = Utility.newRandom(hashCode());
	
	// Run parameters
	private int updateTicks = 60000;
	private boolean updateTickRange = false;
	private int updateTickMin;
	private int updateTickMax;
	
	public int randomRangeTimer(int min, int max) {
		float result = (rnd.nextFloat() * (max - min) + min) * updateUnit;
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
			updateTickMin = (int)updateData[1];
			updateTickMax = (int)updateData[2];
			
			updateTicks = updateTickRange ? randomRangeTimer(updateTickMin, updateTickMax) : (int)updateTickMax * updateUnit;
		} else {
			appliance = new Appliance();
		}
		
//		ProgramGUI.getInstance().printToLog(appliance.hashCode(), appliance.toString(), Color.GREEN.darker());
		ProgramGUI.getInstance().printToLog(appliance.hashCode(), getLocalName(), "created", Color.GREEN.darker());
		
		// Register in the DF
		DFRegistry.register(this, APPLIANCE_AGENT);
		subscriptionHandler();
	}
	
	// Deregister this agent
	@Override
	protected void takeDown() {
		ProgramGUI.getInstance().printToLog(appliance.hashCode(), getLocalName(), "shutdown", Color.RED);
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
			
			//TODO Confirm
			ProgramGUI.getInstance().printToLog(appliance.hashCode(), getLocalName(), msg, Color.GREEN.darker());
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
				
				//TODO Confirm
				ProgramGUI.getInstance().printToLog(appliance.hashCode(), myAgent.getLocalName(), "new subcriber " + 
						subscription.getSender().getLocalName(), Color.GREEN.darker());
				
				sub = getSubscription(subscription);
				notification = subscription.createReply();
				notification.setPerformative(ACLMessage.INFORM);
				
				// Send rate data out to subscribers
				addBehaviour(new TickerBehaviour(myAgent, updateTicks) {
					@Override
					public void onTick() {
						ProgramGUI.getInstance().printToLog(appliance.hashCode(), myAgent.getLocalName(),  
								"energy usage shared...", Color.GREEN.darker());
						
						System.out.println(getLocalName() + ": " + appliance.getRate());
						notification.setContent(Integer.toString(appliance.getRate()));
						sub.notify(notification);
						
						if(updateTickRange) {
							updateTicks = randomRangeTimer(updateTickMin, updateTickMax);
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
