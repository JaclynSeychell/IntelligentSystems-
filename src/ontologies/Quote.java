package ontologies;

import jade.content.*;

@SuppressWarnings("serial")
public class Quote implements AgentAction {
	private int price;
	private int units;
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getUnits() {
		return units;
	}
	
	public void setUnits(int units) {
		this.units = units;
	}
	
	@Override
	public String toString() {
		return "Quote {price:" + price + ", units: " + units + "}";
	}
}
