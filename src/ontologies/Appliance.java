package ontologies;

import jade.content.*;

@SuppressWarnings("serial")
public class Appliance extends Trader implements Concept, SupplierVocabulary {
	private int generationRate;
	private int usageRate;
	
	public Appliance() { 
		super("Appliance", APPLIANCE_AGENT);
		generationRate = 0;
		usageRate = 0;
	}
	
	public Appliance(String name, int generationRate, int usageRate) {
		super(name, APPLIANCE_AGENT);
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
		return "Appliance [name= " + getName() + ", generationRate=" + 
			generationRate + ", usageRate=" + usageRate + "]";
	}
}
