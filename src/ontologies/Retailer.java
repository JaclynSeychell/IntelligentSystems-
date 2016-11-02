package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Retailer extends Trader implements Concept, SupplierVocabulary {
	// Determine retailer behaviour
	public enum RetailerType { 
		FIXED,
		RANDOM,
		DROPPING, 
		DEMAND;
	}
	
	private int generationRate;
	private int pricePerUnit;
	private int supply;
	private RetailerType type;
	
	public Retailer() { 
		super("Retailer", RETAILER_AGENT);
		generationRate = 0;
		pricePerUnit = 0;
		supply = 0;
		type = RetailerType.FIXED; // Default to fixed price
	}
	
	public Retailer(String name, int generationRate, int pricePerUnit, int supply, RetailerType type) {
		super(name, RETAILER_AGENT);
		this.generationRate = generationRate;
		this.pricePerUnit = pricePerUnit;
		this.supply = supply;
		this.type = type;
	}
	
	public int getGenerationRate() {
		return generationRate;
	}

	public void setGenerationRate(int generationRate) {
		this.generationRate = generationRate;
	}

	public int getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(int pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
		if (this.supply < 0) this.supply = 0; // Cannot be less than 0
	}
	
	public RetailerType getRetailerType() {
		return type;
	}
	
	public void setRetailerType(RetailerType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[type=" + getType() + ", name=" + getName() + ", generationRate=" + generationRate + 
				", pricePerUnit=" + pricePerUnit + ", supply=" + supply + ", type=" + type + "]";
	}
}