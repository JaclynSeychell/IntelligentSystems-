package ontologies;

import jade.content.*;

public class Exchange implements AgentAction {
	private int type;
	private float price;
	
	public int getType() {
		return type;
	}
	
	public void setType(int pType) {
		type = pType;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float pPrice) {
		price = pPrice;
	}
}
