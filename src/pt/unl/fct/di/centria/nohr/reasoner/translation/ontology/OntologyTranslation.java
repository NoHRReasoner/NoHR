package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.util.Set;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

public interface OntologyTranslation {

    public Set<Predicate> getNegativeHeadsPredicates();

    public Profiles getProfile();

    public Set<Predicate> getTabledPredicates();

    public Set<Rule> getTranslation();

    public boolean hasDisjunctions();

}