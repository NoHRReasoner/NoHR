package pt.unl.fct.di.centria.nohr.model.predicates;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.centria.nohr.model.FormatVisitable;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

/**
 * Represents a predicate. Can be a rule predicate or represent a concept, a role or a meta-predicate (see {@link MetaPredicate}) in the translation
 * of an ontology.
 *
 * @author Nuno Costa
 */
public interface Predicate extends FormatVisitable {

	@Override
	public String accept(FormatVisitor visitor);

	public Predicate accept(ModelVisitor visitor);

	/**
	 * Returns this predicate as a concept if it indeed represent a concept.
	 *
	 * @return the concept that this predicate represents.
	 * @throws ClassCastException
	 *             if this predicate doesn't represent a concept.
	 */
	public OWLClass asConcept();

	/**
	 * Returns this predicate as a meta-predicate if it is a meta-predicate.
	 *
	 * @return this predicate as a meta-predicate.
	 * @throws ClassCastException
	 *             if this predicate isn't a meta-predicate.
	 */
	public MetaPredicate asMetaPredicate();

	/**
	 * Returns this predicate as a role if it represent indeed a role.
	 *
	 * @return the rule that this predicate represents.
	 */
	public OWLProperty<?, ?> asRole();

	/**
	 * Returns the arity of this predicate.
	 *
	 * @return the arity of this predicate.
	 */
	public int getArity();

	/**
	 * Returns the signature of this predicate, i.e. the pair {@code symbol/arity}.
	 *
	 * @return the signature of this predicate.
	 */
	public String getSignature();

	/**
	 * Returns the symbol that represents this predicate.
	 *
	 * @return the symbol that represents this predicate.
	 */
	public String getSymbol();

	/**
	 * Returns true iff this predicate represents a concept.
	 *
	 * @return true iff this predicate represents a concept.
	 */
	public boolean isConcept();

	/**
	 * Returns true iff this predicate is a meta-predicate.
	 *
	 * @return true iff this predicate is a meta-predicate.
	 */
	public boolean isMetaPredicate();

	/**
	 * Returns true iff this predicate represents a role.
	 *
	 * @return true iff this predicate represents a role.
	 */
	public boolean isRole();

	/**
	 * Returns true iff this predicate does't represent a concept, a role or a meta-predicate.
	 *
	 * @return true iff this predicate represents does't represent a concept, a role or a meta-predicate.
	 */
	public boolean isRulePredicate();

}
