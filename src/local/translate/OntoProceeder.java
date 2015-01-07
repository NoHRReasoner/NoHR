package local.translate;

import java.util.List;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public interface OntoProceeder {

	public boolean isOntologyNeedToBeNormalized(OWLOntology ontology);

	/**
	 * Main function.
	 *
	 * @throws org.semanticweb.owlapi.expression.ParserException the parser exception
	 */
	public void proceed() throws ParserException;
	
	public void setOntologiesToProceed(List<OWLOntology> ontologies);
	
    public OWLOntology normalizeOntology(OWLOntology ontology, OWLOntologyManager owlOntologyManager) throws OWLOntologyCreationException, OWLOntologyStorageException;


}