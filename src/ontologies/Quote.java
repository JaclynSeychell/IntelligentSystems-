package ontologies;

import jade.content.*;

@SuppressWarnings("serial")
public class Quote implements AgentAction {
	private int sellPrice;
	private int buyPrice;
	private int units;
	
	public int getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(int buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}
	
	@Override
	public String toString() {
		return "Quote {buyPrice:" + buyPrice + ", sellPrice: " + sellPrice + 
				", units: " + units + "}";
	}
}
