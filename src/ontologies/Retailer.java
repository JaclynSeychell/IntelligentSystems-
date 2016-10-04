package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Retailer implements Concept {
	private int generationRate;
	private int pricePerUnit;
	private int supply;
	
	public Retailer() {
		generationRate = 0;
		pricePerUnit = 0;
		supply = 0;
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

	@Override
	public String toString() {
		return "Retailer [generationRate=" + generationRate + ", pricePerUnit=" + pricePerUnit + ", supply=" + supply
				+ "]";
	}
}