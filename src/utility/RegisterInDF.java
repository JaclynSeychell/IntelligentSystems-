package utility;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public class RegisterInDF extends OneShotBehaviour {
	private String type;
	
	public RegisterInDF(Agent a, String pType) {
		super(a);
		type = pType;
	}
	
	public void action() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(myAgent.getName());
		sd.setOwnership("Group");
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(myAgent.getAID());
		dfd.addServices(sd);
		
		try {
			DFAgentDescription[] dfds = DFService.search(myAgent, dfd);
			if (dfds.length > 0) {
				DFService.deregister(myAgent, dfd);
			}
			DFService.register(myAgent, dfd);
			System.out.println(myAgent.getLocalName() + " is ready.");
		} catch (Exception e) {
			System.out.println("Failed registering with DF! Shutting down...");
			e.printStackTrace();
			myAgent.doDelete();
		}
	}
}
