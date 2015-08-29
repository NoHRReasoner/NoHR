/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.DOUBLE;
import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.DOUBLED_RANGE;
import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.DOUBLE_DOMAIN;
import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.NEGATIVE;
import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.ORIGINAL;
import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.ORIGINAL_DOMAIN;
import static pt.unl.fct.di.centria.nohr.model.predicates.PredicateType.ORIGINAL_RANGE;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;
import pt.unl.fct.di.centria.nohr.reasoner.translation.DLUtils;

/**
 * Predicates factory. For more conciseness statically import (see
 * {@link <a href= https://docs.oracle.com/javase/1.5.0/docs/guide/language/static-import.html>Static Import</a> }) this class if you want to use
 * their factory methods.
 *
 * @author Nuno Costa
 */
public class Predicates {

	/**
	 * Create a domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return the domain meta-predicate <i>DP</i>, if {@code doub} is true; the double domain meta-predicate, <i>DP<sup>d</sup></i>, otherwise.
	 */
	public static Predicate domPred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLE_DOMAIN);
		else
			return pred(role, ORIGINAL_DOMAIN);
	}

	/**
	 * Create a double domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            the role <i>P</i>.
	 * @return the meta-predicate <i>DP<sup>d</sup></i>.
	 */
	public static Predicate doubDomPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLE_DOMAIN);
	}

	/**
	 * Create a double meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            the concept <i>A</i>.
	 * @return the double meta-predicate <i>A<sup>d</sup></i>.
	 */
	public static Predicate doubPred(OWLClass concept) {
		return pred(concept, DOUBLE);
	}

	/**
	 * Create a double meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the meta-predicate <i>P<sup>d</sup></i>.
	 */
	public static Predicate doubPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLE);
	}

	/**
	 * Create a double meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate symbol, <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i>, of the predicate that {@code symbol} represents.
	 * @return the double meta-predicate <i>S<sup>d</sup>/n</i>.
	 */
	public static Predicate doubPred(String symbol, int arity) {
		return pred(symbol, arity, DOUBLE);
	}

	/**
	 * Create a double range meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role, <i>P</i>.
	 * @return the double range meta-predicate <i>DP<sup>d</sup></i>.
	 */
	public static Predicate doubRanPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, DOUBLED_RANGE);
	}

	/**
	 * Create a negative meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A<i>.
	 * @return a negative meta-predicate <i>NA</i>.
	 */
	public static Predicate negPred(OWLClass concept) {
		return pred(concept, NEGATIVE);
	}

	/**
	 * Create a negative meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the negative meta-predicate <i>NP</i>.
	 */
	public static Predicate negPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, NEGATIVE);
	}

	/**
	 * Create a negative meta-predicate from a specified predicate.
	 *
	 * @param predicate
	 *            a predicate <i>P</i> or a meta-predicate <i>NP</i>, <i>DP</i>, <i>RP</i>, <i>DP<sup>d</sup></i> or <i>RP<sup>d</sup></i>.
	 * @return the meta-predicate <i>NP</i>.
	 */
	public static Predicate negPred(Predicate predicate) {
		Predicate pred = predicate;
		if (predicate instanceof MetaPredicate)
			pred = ((MetaPredicate) predicate).getPredicate();
		return new MetaPredicateImpl(pred, NEGATIVE);
	}

	/**
	 * Create a negative meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a symbol, <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i>, of the predicate that {@code symbol} represents.
	 * @return the negative meta-predicate <i>NS/n</i>.
	 */
	public static Predicate negPred(String symbol, int arity) {
		return pred(symbol, arity, NEGATIVE);
	}

	/**
	 * Create an original domain meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the original domain meta-predicate <i>DP</i>.
	 */
	public static Predicate origDomPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL_DOMAIN);
	}

	/**
	 * Create an original meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @return the original meta-predicate <i>A</i>.
	 */
	public static Predicate origPred(OWLClass concept) {
		return pred(concept, ORIGINAL);
	}

	/**
	 * Create an original meta-predicate from a specifieid role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the original meta-predicate <i>P</i>.
	 */
	public static Predicate origPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL);
	}

	/**
	 * Create an original meta-predicate from a specified predicate symbol and predicate arity.
	 *
	 * @param symbol
	 *            a predicate symbol <i>S</i>.
	 * @param arity
	 *            the arity, <i>n</i> of the predicate that {@code symbol} represents.
	 * @return the original meta-predicate <i>S</i>.
	 */
	public static Predicate origPred(String symbol, int arity) {
		return pred(symbol, arity, ORIGINAL);
	}

	/**
	 * Create an original range meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @return the range original meta-predicate <i>RP</i>.
	 */
	public static Predicate origRanPred(OWLPropertyExpression<?, ?> role) {
		return pred(role, ORIGINAL_RANGE);
	}

	/**
	 * Create a predicate representing a specified concept.
	 *
	 * @param concept
	 *            a concept.
	 * @return the predicate representing {@code concept}.
	 */
	public static Predicate pred(OWLClass concept) {
		return new ConceptPredicateImpl(concept);
	}

	/**
	 * Create a meta-predicate from a specified concept.
	 *
	 * @param concept
	 *            a concept <i>A</i>.
	 * @param doub
	 *            specifies whether the meta-predicate is of a double type.
	 * @return <i>A<sup>d</sup></i> if {@code doub} is true; <i>A</i>, otherwise.
	 */
	public static Predicate pred(OWLClass concept, boolean doub) {
		if (doub)
			return pred(concept, DOUBLE);
		else
			return pred(concept, ORIGINAL);
	}

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
	static MetaPredicate pred(OWLClass concept, PredicateType type) {
		return new MetaPredicateImpl(pred(concept), type);
	}

	/**
	 * Create a predicate representing a specified role.
	 *
	 * @param role
	 *            a role.
	 * @return the predicate representing {@code role}.
	 */
	public static Predicate pred(OWLPropertyExpression<?, ?> role) {
		return new RolePredicateImpl(DLUtils.atomic(role));
	}

	/**
	 * Create a meta-predicate from a specified role.
	 *
	 * @param role
	 *            a role <i>P</i>
	 * @param doub
	 *            specified whether the meta-predicate if of a double type.
	 * @return <i>P<sup>d</sup></i> if {@code doub} is true; <i>P</i>, otherwise.
	 */
	public static Predicate pred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLE);
		else
			return pred(role, ORIGINAL);
	}

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
	static Predicate pred(OWLPropertyExpression<?, ?> role, PredicateType type) {
		return new MetaPredicateImpl(new RolePredicateImpl(DLUtils.atomic(role)), type);
	}

	/**
	 * Create a meta-predicate form a specified predicate with a specified type.
	 *
	 * @param predicate
	 *            the predicate that the meta-predicate refers.
	 * @param type
	 *            the type of the meta-predicate.
	 * @return a meta-predicate referring {@code predicate} with type {@code type}.
	 */
	static MetaPredicate pred(Predicate predicate, PredicateType type) {
		return new MetaPredicateImpl(predicate, type);
	}

	/**
	 * Create a predicate with a specified symbol and arity.
	 *
	 * @param symbol
	 *            the symbol, <i>S</i>, that represents the predicate.
	 * @param arity
	 *            the arity, <i>n</i> of the predicate.
	 * @return a predicate, <i>S/n</i> with symbol {@symbol} and arity {@code arity}.
	 */
	public static Predicate pred(String symbol, int arity) {
		return new RulePredicateImpl(symbol, arity);
	}

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
	public static Predicate pred(String symbol, int arity, boolean doub) {
		if (doub)
			return pred(symbol, arity, DOUBLE);
		else
			return pred(symbol, arity, ORIGINAL);
	}

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
	static Predicate pred(String symbol, int arity, PredicateType type) {
		return new MetaPredicateImpl(pred(symbol, arity), type);
	}

	/**
	 * Create the predicate represented by a specified symbol with a specified arity, given a specified {@link VocabularyMapping}.
	 *
	 * @param symbol
	 *            a symbol <i>S</i>.
	 * @param arity
	 *            an arity <i>n</i>.
	 * @param vocabularyMapping
	 *            a {@link VocabularyMapping}.
	 * @return the predicate representing a concept <i>A</i> if {@code arity} is {@literal 1} and {@code vocabularyMapping.}
	 *         {@link VocabularyMapping#getConcept(String) getConcept(symbol)} returns <i>A</i>; <br>
	 *         the predicate representing a role <i>P</i> if {@code arity} is {@literal 2} and {@code vocabularyMapping.}
	 *         {@link VocabularyMapping#getRole(String) getRole(symbol)} returns the role <i>P</i>; <br>
	 *         the predicate represented by {@code symbol} with arity {@code arity}, otherwise.
	 */

	public static Predicate pred(String symbol, int arity, VocabularyMapping vocabularyMapping) {
		if (vocabularyMapping == null)
			return pred(symbol, arity);
		if (arity == 1) {
			final OWLClass concept = vocabularyMapping.getConcept(symbol);
			if (concept != null)
				return pred(concept);
		}
		if (arity == 2) {
			final OWLProperty<?, ?> role = vocabularyMapping.getRole(symbol);
			if (role != null)
				return pred(role);
		}
		return pred(symbol, arity);
	}

	/**
	 * Create a range meta-predicate from a specified {@code role}.
	 *
	 * @param role
	 *            a role <i>P</i>.
	 * @param doub
	 *            specifies whether this role is of a double type.
	 * @return <i>RP<sup>d</sup></i> if {@code doub} is true; <i>RP</i>, otherwise.
	 */
	public static Predicate ranPred(OWLPropertyExpression<?, ?> role, boolean doub) {
		if (doub)
			return pred(role, DOUBLED_RANGE);
		else
			return pred(role, ORIGINAL_RANGE);
	}

}
