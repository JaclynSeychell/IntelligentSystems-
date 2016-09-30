package ontologies;

import jade.content.*;

public class Home implements Concept {
	private String id;
	private float requirement;
	private float budget;
	
	public String getId() {
		return id;
	}
	
	public void setId(String pId) {
		id = pId;
	}
	
	public float getRequirement() {
		return requirement;
	}
	
	public void setRequirement(float pRequirement) {
		requirement = pRequirement;
	}
	
	public float getBudget() {
		return budget;
	}
	
	public void setBudget(float pBudget) {
		budget = pBudget;
	}
	
	public String toString() {
		return "Home {id:" + id + ", requirement:" + requirement +
				", budget:" + budget;
	}
}
