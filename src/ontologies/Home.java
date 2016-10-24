package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Home implements Concept {
	private int generationRate;
	private int usageRate;
	private int supply;
	private int budget;
	private int expenditure;
	
	public Home() {
		generationRate = 0;
		usageRate = 0;
		supply = 0;
		budget = 0;
	}
	
	public Home(int generationRate, int usageRate, int supply, int budget) {
		this.generationRate = generationRate;
		this.usageRate = usageRate;
		this.supply = supply;
		this.budget = budget;
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

	@Override
	public String toString() {
		return "Home [generationRate=" + generationRate + ", usageRate=" + usageRate + ", supply=" + supply
				+ ", budget=" + budget + "]";
	}
}
