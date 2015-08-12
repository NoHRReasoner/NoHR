/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

/**
 * Implementation of a {@link Predicate} representing a concept.
 *
 * @author Nuno Costa
 *
 */
public class ConceptPredicateImpl implements Predicate {

    /** The concept represented by this predicate. */
    private final OWLClass concept;

    /**
     * Constructs a predicate representing a specified concept.
     *
     * @param concept
     *            the concept represented by the predicate. Must have a IRI
     *            fragment.
     *
     * @throws IllegalArgumentException
     *             if {@code concept} hasn't a IRI fragment;
     */
    ConceptPredicateImpl(OWLClass concept) {
	Objects.requireNonNull(concept);
	if (concept.getIRI().getFragment() == null)
	    throw new IllegalArgumentException("concept: must have an IRI fragment");
	this.concept = concept;
    }

    @Override
    public String accept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Predicate accept(ModelVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public OWLClass asConcept() {
	return concept;
    }

    @Override
    public MetaPredicate asMetaPredicate() {
	throw new ClassCastException();
    }

    @Override
    public OWLProperty<?, ?> asRole() {
	throw new ClassCastException();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof ConceptPredicateImpl))
	    return false;
	final ConceptPredicateImpl other = (ConceptPredicateImpl) obj;
	if (!concept.getIRI().equals(other.concept.getIRI()))
	    return false;
	return true;
    }

    @Override
    public int getArity() {
	return 1;
    }

    @Override
    public String getSignature() {
	return getSymbol() + "/" + getArity();
    }

    @Override
    public String getSymbol() {
	return concept.getIRI().getFragment();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + concept.getIRI().hashCode();
	return result;
    }

    @Override
    public boolean isConcept() {
	return true;
    }

    @Override
    public boolean isMetaPredicate() {
	return false;
    }

    @Override
    public boolean isRole() {
	return false;
    }

    @Override
    public boolean isRulePredicate() {
	return false;
    }

    @Override
    public String toString() {
	return getSymbol();
    }

}
