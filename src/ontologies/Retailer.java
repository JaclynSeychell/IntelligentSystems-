package ontologies;

import jade.content.*;

public class Retailer implements Concept {
	private String id;
	private float supply;
	private float price;
	
	public String getId() {
		return id;
	}
	
	public void setId(String pId) {
		id = pId;
	}
	
	public float getSupply() {
		return supply;
	}
	
	public void setSupply(float pSupply) {
		supply = pSupply;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setBudget(float pPrice) {
		price = pPrice;
	}
	
	public String toString() {
		return "Retailer {id:" + id + ", supply:" + supply +
				", price:" + price;
	}
}
