package ontologies;

@SuppressWarnings("serial")
public class Broker extends Trader implements SupplierVocabulary {
	public Broker() {
		super("Broker", BROKER_AGENT);
	}
	
	public Broker(String name) {
		super(name, BROKER_AGENT);
	}
}
