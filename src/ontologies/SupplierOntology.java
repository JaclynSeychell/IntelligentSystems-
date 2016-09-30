package ontologies;

import jade.content.onto.*;
import jade.content.schema.*;

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
			cs.add(HOME_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
			cs.add(HOME_REQUIREMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			cs.add(HOME_BUDGET, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			
			// Retailer
			add(new ConceptSchema(RETAILER), Retailer.class);
			cs.add(RETAILER_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
			cs.add(RETAILER_SUPPLY, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			cs.add(RETAILER_PRICE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			
			// Problem
			add(new ConceptSchema(PROBLEM), Problem.class);
			cs.add(PROBLEM_NUM, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			cs.add(PROBLEM_MSG, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			
			// -- Actions -- 
			
			// Exchange
			AgentActionSchema as = new AgentActionSchema(EXCHANGE);
			add(as, Exchange.class);
			as.add(EXCHANGE_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
	        as.add(EXCHANGE_PRICE, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
	         
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}
}