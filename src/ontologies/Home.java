package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Home implements Concept {
	private int generationRate;
	private int usageRate;
	private int supply;
	private int budget;
	
	public Home() {
		generationRate = 0;
		usageRate = 0;
		supply = 0;
		budget = 0;
	}
	
	public int getGenerationRate() {
		return generationRate;
	}

	public void setGenerationRate(int generationRate) {
		this.generationRate = generationRate;
	}

	public int getUsageRate() {
		return usageRate;
	}

	public void setUsageRate(int usageRate) {
		this.usageRate = usageRate;
	}

	public int getSupply() {
		return supply;
	}


	public void setSupply(int supply) {
		this.supply = supply;
		if (this.supply < 0) this.supply = 0; // Cannot be less than 0
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	@Override
	public String toString() {
		return "Home [generationRate=" + generationRate + ", usageRate=" + usageRate + ", supply=" + supply
				+ ", budget=" + budget + "]";
	}
}
