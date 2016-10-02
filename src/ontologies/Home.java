package ontologies;

import jade.content.*;

public class Home implements Concept {
	private String id;
	private int requirement;
	private int budget;
	
	public String getId() {
		return id;
	}
	
	public void setId(String pId) {
		id = pId;
	}
	
	public int getRequirement() {
		return requirement;
	}
	
	public void setRequirement(int pRequirement) {
		requirement = pRequirement;
	}
	
	public int getBudget() {
		return budget;
	}
	
	public void setBudget(int pBudget) {
		budget = pBudget;
	}
	
	public String toString() {
		return "Home {id:" + id + ", requirement:" + requirement +
				", budget:" + budget;
	}
}
