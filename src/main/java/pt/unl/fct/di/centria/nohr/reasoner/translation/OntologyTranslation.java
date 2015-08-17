package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.Set;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * Represents the translation of the ontology component of some hybrid KB.
 *
 * @author Nuno Costa
 *
 */
public interface OntologyTranslation {

    /**
     * Returns the set of negative meta-predicates that appear at the head of
     * some rule of this translation.
     *
     * @return the set of negative meta-predicates appearing at the head of some
     *         rule of this translation.
     */
    public Set<Predicate> getNegativeHeadsPredicates();

    /**
     * Returns set of predicates that need to be tabled.
     *
     * @return the set of predicates that need to be tabled.
     */
    public Set<Predicate> getPredicatesToTable();

    /**
     * Returns the profile of the translated ontology.
     *
     * @return the profile of the translated ontology.
     */
    public Profile getProfile();

    /**
     * Returns the set of rules corresponding to this translation.
     *
     * @return the set of rule corresponding to this translation.
     */
    public Set<Rule> getRules();

    /**
     * Retruns true iff the translated ontology has disjunctions.
     *
     * @return iff the translated ontology has disjunctions.
     */
    public boolean hasDisjunctions();

}