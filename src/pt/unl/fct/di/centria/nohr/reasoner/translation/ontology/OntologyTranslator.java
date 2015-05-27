package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import org.semanticweb.owlapi.expression.ParserException;

public interface OntologyTranslator {

	/**
	 * Main function.
	 *
	 * @throws org.semanticweb.owlapi.expression.ParserException the parser exception
	 */
	public void proceed() throws ParserException;	

}