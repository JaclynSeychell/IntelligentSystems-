package ontologies;

import jade.content.*;

@SuppressWarnings("serial")
public class Appliance implements Concept {
	private int generationRate;
	private int usageRate;
	
	public Appliance() { 
		generationRate = 0;
		usageRate = 0;
	}
	
	public Appliance(int generationRate, int usageRate) {
		this.generationRate = generationRate;
		this.usageRate = usageRate;
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
	
	public int getRate() {
		return generationRate - usageRate;
	}

	@Override
	public String toString() {
		return "Appliances [generationRate=" + generationRate + ", " + 
				"usageRate=" + usageRate + "]";
	}
}
