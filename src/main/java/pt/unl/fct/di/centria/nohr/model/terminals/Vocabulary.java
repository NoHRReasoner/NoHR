/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.terminals;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Predicate;

/**
 * A mapping between symbols (strings) and the concepts, roles, or individuals, of a specified set of ontologies, that they represent.
 *
 * @author Nuno Costa
 */
public interface Vocabulary {

	/**
	 * Creates a constant representing a specified number.
	 *
	 * @param n
	 *            the number
	 * @return the numeric constant representing {@code n}.
	 */
	Constant cons(Number n);

	/**
	 * Create a constant representing a specified OWL individual.
	 *
	 * @param individual
	 *            the OWL individual
	 * @return the constant representing {@code individual}.
	 */
	Constant cons(OWLIndividual individual);

	/**
	 * Create a constant representing a specified OWL literal.
	 *
	 * @param literal
	 *            the OWL literal.
	 * @return the constant representing {@code literal}.
	 */
	Constant cons(OWLLiteral literal);

	/**
	 * Create a constant representing a OWL individual or OWL literal.
	 *
	 * @param object
	 *            the OWL individual or OWL literal.
	 * @return the constant representing {@code object}.
	 */
	Constant cons(OWLPropertyAssertionObject object);

	/**
	 * Creates a constant representing a specified symbol. If the symbol is a number, then the created constant is an numeric constant.
	 *
	 * @param symbol
	 *            the symbol.
	 * @return a numeric constant representing {@code symbol} if {@code symbol} is a number; or a rule constant representing {@code symbol},
	 *         otherwise.
	 */

	Constant cons(String symbol);

	/**
	 * Create a domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return the domain meta-predicate <i>DP</i>, if {@code doub} is true; the double domain meta-predicate, <i>DP<sup>d</sup></i>, otherwise.
	 */
	Predicate domPred(OWLPropertyExpression<?, ?> role, boolean doub);

	/**
	 * Create a double domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            the role <i>P</i>.
	 * @return the meta-predicate <i>DP<sup>d</sup></i>.
	 */
	Predicate doubDomPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create a double meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            the concept <i>A</i>.
	 * @return the double meta-predicate <i>A<sup>d</sup></i>.
	 */
	Predicate doubPred(OWLClass concept);

	/**
	 * Create a double meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the meta-predicate <i>P<sup>d</sup></i>.
	 */
	Predicate doubPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create a double meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate symbol, <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i>, of the predicate that {@code symbol} represents.
	 * @return the double meta-predicate <i>S<sup>d</sup>/n</i>.
	 */
	Predicate doubPred(String symbol, int arity);

	/**
	 * Create a double range meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role, <i>P</i>.
	 * @return the double range meta-predicate <i>DP<sup>d</sup></i>.
	 */
	Predicate doubRanPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Generate a new concept that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 *
	 * @return a new concept that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 */
	public OWLClass generateNewConcept();

	/**
	 * Generate a new role that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 *
	 * @return a new role that doesn't occur in the ontology refered by this {@link OWLEntityGenerator}.
	 */
	public OWLObjectProperty generateNewRole();

	/**
	 * The ontologies whose concepts, roles and individuals this {@link Vocabulary} mapps.
	 *
	 * @return
	 */
	public Set<OWLOntology> getOntologies();

	/**
	 * Create a negative meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A<i>.
	 * @return a negative meta-predicate <i>NA</i>.
	 */
	Predicate negPred(OWLClass concept);

	/**
	 * Create a negative meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the negative meta-predicate <i>NP</i>.
	 */
	Predicate negPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create a negative meta-predicate from a specified predicate.
	 *
	 * @param predicate
	 *            a predicate <i>P</i> or a meta-predicate <i>NP</i>, <i>DP</i>, <i>RP</i>, <i>DP<sup>d</sup></i> or <i>RP<sup>d</sup></i>.
	 * @return the meta-predicate <i>NP</i>.
	 */
	Predicate negPred(Predicate predicate);

	/**
	 * Create a negative meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a symbol, <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i>, of the predicate that {@code symbol} represents.
	 * @return the negative meta-predicate <i>NS/n</i>.
	 */
	Predicate negPred(String symbol, int arity);

	/**
	 * Create an original domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the original domain meta-predicate <i>DP</i>.
	 */
	Predicate origDomPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create an original meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @return the original meta-predicate <i>A</i>.
	 */
	Predicate origPred(OWLClass concept);

	/**
	 * Create an original meta-predicate from a specifieid role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the original meta-predicate <i>P</i>.
	 */
	Predicate origPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create an original meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate symbol <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i> of the predicate that {@code symbol} represents.
	 * @return the original meta-predicate <i>S</i>.
	 */
	Predicate origPred(String symbol, int arity);

	/**
	 * Create an original range meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the range original meta-predicate <i>RP</i>.
	 */
	Predicate origRanPred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create a predicate representing a specified concept.
	 *
	 * @param concept
	 *            a concept.
	 * @return the predicate representing {@code concept}.
	 */
	Predicate pred(OWLClass concept);

	/**
	 * Create a meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return <i>A<sup>d</sup></i> if {@code doub} is true; <i>A</i>, otherwise.
	 */
	Predicate pred(OWLClass concept, boolean doub);

	/**
	 * Create a meta-predicate from a specified concept of a specified type.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @param type
	 *            a type. Shoudln't represent a quantification (i.e. {@code type.}{@link PredicateType#isQuantification() isQuantification()} must be
	 *            false).
	 * @return <i>A</i> if {@code type} is {@link PredicateType#ORIGINAL original}; <br>
	 *         <i>A<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLE double}; <br>
	 *         <i>NA</i> if {@code type} is {@link PredicateType#NEGATIVE negative}.
	 * @throws IllegalArgumentException
	 *             if {@code type.}{@link PredicateType#isQuantification() isQuantification()} is true.
	 */
	MetaPredicate pred(OWLClass concept, PredicateType type);

	/**
	 * Create a predicate representing a specified role.
	 *
	 * @param role
	 *            a role.
	 * @return the predicate representing {@code role}.
	 */
	Predicate pred(OWLPropertyExpression<?, ?> role);

	/**
	 * Create a meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>
	 * @param doub
	 *            specified whether the meta-predicate if of a double type.
	 * @return <i>P<sup>d</sup></i> if {@code doub} is true; <i>P</i>, otherwise.
	 */
	Predicate pred(OWLPropertyExpression<?, ?> role, boolean doub);

	/**
	 * Create a meta-predicate from a specified role of a specified type.
	 *
	 * @param role
	 *            a concept <i>P</i>.
	 * @param type
	 *            a type.
	 * @return <i>P</i> if {@code type} is {@link PredicateType#ORIGINAL original}; <br>
	 *         <i>P<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLE double}; <br>
	 *         <i>NP</i> if {@code type} is {@link PredicateType#NEGATIVE negative}; <br>
	 *         <i>DP</i> if {@code type} is {@link PredicateType#ORIGINAL_DOMAIN original domain}; <br>
	 *         <i>RP</i> if {@code type} is {@link PredicateType#ORIGINAL_RANGE original range} ; <br>
	 *         <i>DP<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLE_DOMAIN double domain}; <br>
	 *         <i>RP<sup>d</sup></i> if {@code type} is {@link PredicateType#DOUBLED_RANGE double range}.
	 */
	Predicate pred(OWLPropertyExpression<?, ?> role, PredicateType type);

	/**
	 * Create a meta-predicate form a specified predicate with a specified type.
	 *
	 * @param predicate
	 *            the predicate that the meta-predicate refers.
	 * @param type
	 *            the type of the meta-predicate.
	 * @return a meta-predicate referring {@code predicate} with type {@code type}.
	 */
	MetaPredicate pred(Predicate predicate, PredicateType type);

	/*
	 * Create a predicate with a specified symbol and arity.
	 *
	 * @param symbol the symbol, <i>S</i>, that represents the predicate.
	 *
	 * @param arity the arity, <i>n</i> of the predicate.
	 *
	 * @return a predicate, <i>S/n</i> with symbol {@symbol} and arity {@code arity}.
	 */
	Predicate pred(String symbol, int arity);

	/**
	 * Create a meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate <i>S</i>.
	 * @param arity
	 *            a predicate arity <i>n</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return the double meta-predicate <i>S/n</i> if {@code doub} is true; the original meta-predicate <i>S<sup>d</sup>/n</i>, otherwise.
	 */
	Predicate pred(String symbol, int arity, boolean doub);

	/**
	 * Create a meta-predicate from a specified predicate symbol and predicate arity with a specified type.
	 *
	 * @param symbol
	 *            a predicate symbol <i>S</i>.
	 * @param arity
	 *            a predicate arity <i>n</i>.
	 * @param type
	 *            the type of the meta-predicate. Shoudn't represent a quantification (i.e. {@code type.} {@link PredicateType#isQuantification()
	 *            isQuantification()} must be false).
	 * @return <i>S/n</i> if {@code type} is {@link PredicateType#ORIGINAL original}; <br>
	 *         <i>S<sup>d</sup>/n</i>, if {@code type} is {@link PredicateType#DOUBLE double}; <br>
	 *         <i>NS/n</i>, if {@code type} is {@link PredicateType#NEGATIVE negative}.
	 * @throw IllegalArgumentException if {@code type.} {@link PredicateType#isQuantification() isQuantification()} is true.
	 */
	Predicate pred(String symbol, int arity, PredicateType type);

	/**
	 * Create a range meta-predicate from a specified {@code role}.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @param doub
	 *            specifies whether this role is of a double type.
	 * @return <i>RP<sup>d</sup></i> if {@code doub} is true; <i>RP</i>, otherwise.
	 */
	Predicate ranPred(OWLPropertyExpression<?, ?> role, boolean doub);

}
