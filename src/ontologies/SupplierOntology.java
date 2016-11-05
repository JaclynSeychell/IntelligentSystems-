package ontologies;

import jade.content.onto.*;
import jade.content.schema.*;

@SuppressWarnings("serial")

public class SupplierOntology extends Ontology implements SupplierVocabulary {
	// identifier
	public static final String ONTOLOGY_NAME = "Supplier-Ontology";
	
	// singleton
	private static Ontology instance = new SupplierOntology();

	// getter -> singleton
	public static Ontology getInstance() { return instance; }
	
	// constructor
	private SupplierOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());
		
		try {
			// -- Concepts -- 
			
			// Home
			ConceptSchema cs = new ConceptSchema(HOME);
			add(cs, Home.class);
			cs.add(HOME_GENERATION_RATE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(HOME_USAGE_RATE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(HOME_SUPPLY, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(HOME_BUDGET, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(HOME_EXPENDITURE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			
			// Retailer
			add(new ConceptSchema(RETAILER), Retailer.class);
			cs.add(RETAILER_GENERATION_RATE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(RETAILER_SUPPLY, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(RETAILER_PRICE_PER_UNIT, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			
			//Appliances
			add(new ConceptSchema(APPLIANCE), Appliance.class);
			cs.add(APPLIANCE_GENERATION_RATE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(APPLIANCE_USAGE_RATE,(PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			
			// Problem
			add(new ConceptSchema(PROBLEM), Problem.class);
			cs.add(PROBLEM_NUM, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(PROBLEM_MSG, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
			
			// -- Actions -- 
			
			// Exchange
			AgentActionSchema as = new AgentActionSchema(EXCHANGE);
			add(as, Exchange.class);
			as.add(EXCHANGE_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
	        as.add(EXCHANGE_PRICE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
	        as.add(EXCHANGE_UNITS, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
	        
	        AgentActionSchema qas = new AgentActionSchema(QUOTE);
			add(qas, Quote.class);
			qas.add(QUOTE_PRICE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			qas.add(QUOTE_UNITS, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
	         
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}
}
