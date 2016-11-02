package ontologies;

public interface SupplierVocabulary {
	// Basic Vocabulary
	public static final int BUY = 1;
	public static final int SELL = 2;
	public static final int NO_RETAILERS = 3;
	public static final String HOME_AGENT = "Home agent";
	public static final String BROKER_AGENT = "Broker agent";
	public static final String RETAILER_AGENT = "Retailer agent";
	public static final String APPLIANCE_AGENT = "Appliance agent";
	public static final String PB_NO_RETAILERS = "No retailers to trade with";
	
	// Ontology Vocabulary
	public static final String HOME = "Home";
	public static final String HOME_GENERATION_RATE = "generationRate";
	public static final String HOME_USAGE_RATE = "usageRate";
	public static final String HOME_SUPPLY = "supply";
	public static final String HOME_BUDGET = "budget";
	public static final String HOME_EXPENDITURE = "expenditure";
	
	public static final String RETAILER = "Retailer";
	public static final String RETAILER_GENERATION_RATE = "generationRate";
	public static final String RETAILER_SUPPLY = "supply";
	public static final String RETAILER_PRICE_PER_UNIT = "pricePerUnit";
	
	public static final String APPLIANCE = "Applicance";
	public static final String APPLIANCE_GENERATION_RATE = "generationRate";
	public static final String APPLIANCE_USAGE_RATE = "usageRate";
	
	public static final String EXCHANGE = "Exchange";
	public static final String EXCHANGE_TYPE = "type";
	public static final String EXCHANGE_PRICE = "price";
	public static final String EXCHANGE_UNITS = "units";
	
	public static final String PROBLEM = "Problem";
	public static final String PROBLEM_NUM = "num";
	public static final String PROBLEM_MSG = "msg";
}