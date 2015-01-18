package local.translate;

import org.semanticweb.owlapi.expression.ParserException;

public interface OWLOntologyProceeder {

	/**
	 * Main function.
	 *
	 * @throws org.semanticweb.owlapi.expression.ParserException the parser exception
	 */
	public void proceed() throws ParserException;	

}