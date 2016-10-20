package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Home implements Concept {
	private int generationRate;
	private int usageRate;
	private int supply;
	private int budget;
	private int capacity;
	private int expenditure;
	
	public Home() {
		generationRate = 0;
		usageRate = 0;
		supply = 0;
		budget = 0;
		capacity = 0;
	}
	
	// Generation getter & setter
	public int getGenerationRate() {
		return generationRate;
	}

	public void setGenerationRate(int generationRate) {
		this.generationRate = generationRate;
	}

	// Usage getter & setter
	public int getUsageRate() {
		return usageRate;
	}

	public void setUsageRate(int usageRate) {
		this.usageRate = usageRate;
	}

	// Supply getter & setter
	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
		if (this.supply < 0) this.supply = 0; // Cannot be less than 0
	}

	// Budget getter & setter
	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}
	
	// Expenditure getter & setter
	public int getExpenditure() {
		return expenditure;
	}
	
	public void setExpenditure(int expenditure) {
		this.expenditure = expenditure;
	}
	
	public int remainingBudget() {
		return budget - expenditure;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public void setCapcity (int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "Home [generationRate=" + generationRate + ", usageRate=" + usageRate + ", supply=" + supply
				+ ", budget=" + budget + "]";
	}
}
