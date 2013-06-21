package local.translate;

import org.semanticweb.owlapi.model.OWLOntology;

public class OWLOntologyClone implements Cloneable {
	private OWLOntology ontology;
	public OWLOntologyClone(OWLOntology owlOntology) {
		this.ontology = owlOntology;
	}
	public OWLOntology getOntology(){
		return ontology;
	}
	public OWLOntologyClone clone() throws CloneNotSupportedException {
		OWLOntologyClone clone = (OWLOntologyClone)super.clone();
		clone.ontology = ontology;
		return clone;
	}
}
