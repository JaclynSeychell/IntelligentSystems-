package ontologies;

public interface SupplierVocabulary {
	// Basic Vocabulary
	public static final int BUY = 1;
	public static final int SELL = 2;
	public static final String BROKER_AGENT = "Broker agent";
	public static final String RETAILER_AGENT = "Retailer agent";
	
	// Ontology Vocabulary
	public static final String HOME = "Home";
	public static final String HOME_ID = "id";
	public static final String HOME_REQUIREMENT = "requirement";
	public static final String HOME_BUDGET = "budget";
	
	public static final String RETAILER = "Retailer";
	public static final String RETAILER_ID = "id";
	public static final String RETAILER_SUPPLY = "supply";
	public static final String RETAILER_PRICE = "price";
	
	public static final String EXCHANGE = "Exchange";
	public static final String EXCHANGE_TYPE = "type";
	public static final String EXCHANGE_PRICE = "price";
	
	public static final String PROBLEM = "Problem";
	public static final String PROBLEM_NUM = "num";
	public static final String PROBLEM_MSG = "msg";
}
