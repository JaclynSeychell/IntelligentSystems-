package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Home extends Trader implements Concept, SupplierVocabulary {
	private int generationRate;
	private int usageRate;
	private int supply;
	private int budget;
	private int income;
	
	public Home() {
		super("Home", HOME_AGENT);
		generationRate = 0;
		usageRate = 0;
		supply = 0;
		budget = 0;
		income = 0;
	}
	
	public Home(String name, int generationRate, int usageRate, int supply, int budget, int income) {
		super(name, HOME_AGENT);
		this.generationRate = generationRate;
		this.usageRate = usageRate;
		this.supply = supply;
		this.budget = budget;
		this.income = income;
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
	public int getIncome() {
		return income;
	}
	
	public void setIncome(int income) {
		this.income = income;
	}

	@Override
	public String toString() {
		return "[type=" + getType() + ", name=" + getName() + ", generationRate=" + 
				generationRate + ", usageRate=" + usageRate + ", supply=" + 
				supply + ", budget=" + budget + ", income=" + income + "]";
	}
}
