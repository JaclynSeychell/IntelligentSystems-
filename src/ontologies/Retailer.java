package ontologies;

import jade.content.*;

@SuppressWarnings("serial")

public class Retailer implements Concept {
	private int generationRate;
	private int pricePerUnit;
	private int supply;
	public enum retailerType { //determines how the retailer sets its prices
		typeA, typeB, typeC, typeD
	}
	//Price-setting methods currently as follows:
	//typeA - randomly generated (but hovering around a price point)
	//typeB - reduce by 5% after each request/sale, until it hits a set minimum.
	//typeC - based on measuring demand... (need to work out how we do this)
	//typeD - completely fixed
	
	private retailerType rType;
	
	public Retailer() { 
		generationRate = 0;
		pricePerUnit = 0;
		supply = 0;
		rType = retailerType.typeA; //defaulted to typeA
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
	
	public retailerType getRetailerType() {
		return rType;
	}
	
	public void setRetailerType(retailerType rt) {
		rType = rt;
	}

	@Override
	public String toString() {
		return "Retailer [generationRate=" + generationRate + ", pricePerUnit=" + pricePerUnit + ", supply=" + supply
				+ "]";
	}
}