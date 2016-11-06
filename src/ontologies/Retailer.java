package ontologies;

import java.util.Random;

import jade.content.*;
import utility.Utility;

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
	private int orders;
	private RetailerType type;
	
	public Retailer() { 
		super("Retailer", RETAILER_AGENT);
		generationRate = 0;
		pricePerUnit = 0;
		supply = 0;
		orders = 0;
		type = RetailerType.FIXED; // Default to fixed price
	}
	
	public Retailer(String name, int generationRate, int pricePerUnit, int supply, RetailerType type) {
		super(name, RETAILER_AGENT);
		this.generationRate = generationRate;
		this.pricePerUnit = pricePerUnit;
		this.supply = supply;
		this.type = type;
		orders = 0;
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
	
	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}
	
	public int getBuyPrice() {
		Random rnd = Utility.newRandom(hashCode());	
		float variance = rnd.nextFloat();
		if (variance < 0.2) {
			variance = 0.2f;
		} else if (variance > 0.8) {
			variance = 0.8f;
		}
		
		int result = (int)(pricePerUnit * variance);
		if(result >= pricePerUnit && pricePerUnit > 1) {
			result = pricePerUnit - 1;
		}
		
		return (int)(pricePerUnit * variance);
	}

	@Override
	public String toString() {
		return "[type=" + getType() + ", name=" + getName() + ", generationRate=" + generationRate + 
				", pricePerUnit=" + pricePerUnit + ", supply=" + supply + ", type=" + type + "]";
	}
}