package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public interface TBoxGraph {

	public Set<OWLObjectProperty> getIrreflexiveRoles();

	public Set<OWLEntity> getUnsatisfiableEntities();

}