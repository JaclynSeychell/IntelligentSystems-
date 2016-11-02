package ontologies;

import jade.content.Concept;

@SuppressWarnings("serial")
public abstract class Trader implements Concept {
	private String name;
	private String type;
	
	Trader(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	@Override 
	public String toString() {
		return "Trader [name=" + name + ", type=" + type + "]";
	}
}
