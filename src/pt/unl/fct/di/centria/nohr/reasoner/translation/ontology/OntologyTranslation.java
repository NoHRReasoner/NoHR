package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;

public interface OntologyTranslation {

    public Set<Predicate> getNegativeHeadsPredicates();

    public Profiles getProfile();

    public Set<Predicate> getTabledPredicates();

    public Set<Rule> getTranslation() throws UnsupportedOWLProfile,
	    OWLOntologyCreationException, OWLOntologyStorageException;

    public boolean hasDisjunctions();

}