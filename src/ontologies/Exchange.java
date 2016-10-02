package ontologies;

import jade.content.*;

public class Exchange implements AgentAction {
	private int type;
	private int price;
	
	public int getType() {
		return type;
	}
	
	public void setType(int pType) {
		type = pType;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int pPrice) {
		price = pPrice;
	}
}
