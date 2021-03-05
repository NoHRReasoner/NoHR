/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * Defines a <i>Hybrid Knowledge Base vocabulary</i> - the set of predicates and
 * constants appearing in that KB. Is responsable for: <br>
 * - maintain the {@link HybridPredicate predicates} and
 * {@link HybridConstant constants} representing <i>concepts</i>, <i>roles</i>
 * or
 * <i>individuals</i>, respectively, of the ontology component, and returning
 * them, given their concert representations; <br>
 * - construct and maintain new (rule) {@link HybridPredicate predicates} and
 * {@link HybridConstant constants}, given their ( {@link String}) symbols.
 * <br>
 * <br>
 * Additionally a {@link Vocabulary} can generate new concepts or rules that
 * doesn't appear in the ontology. <br>
 * <br>
 * Each {@link HybridPredicate predicate} and {@link HybridConstant constant} is
 * associated with the concrete representation with which it was obtained. What
 * those concrete representations represent can change over time. Consider the
 * following example. At some moment the ontology can contain a label annotation
 * (see {@link
 * <a href="http://www.w3.org/TR/owl2-syntax/#Annotation_Properties">Annotation
 * Properties</a>}) for a given concept, so that all
 * {@link HybridPredicate predicates} associated with the value of the
 * annotation represent that concept. If the annotation is removed from the
 * ontology, those {@link HybridPredicate predicates} will represent a new rule
 * predicate with the value as symbol. Those kind of changes are appropriately
 * reflected in {@link HybridPredicate predicates} and
 * {@link HybridConstant constants} (the return of, e.g.,
 * {@link HybridPredicate#isConcept()}, {@link HybridPredicate#isRole()} and
 * {@link HybridConstant#isIndividual()} will change appropriately). A
 * {@link VocabularyChangeListener} can listen that changes.
 *
 * @author Nuno Costa
 */
public interface Vocabulary {

    /**
     * Adds a {@link VocabularyChangeListener}
     */
    void addListener(VocabularyChangeListener listener);

    /**
     * Obtainss a constant representing a specified number.
     *
     * @param n the number
     * @return the numeric constant representing {@code n}.
     */
    Constant cons(Number n);

    /**
     * Obtains a constant representing a specified OWL individual.
     *
     * @param individual the OWL individual
     * @return the constant representing {@code individual}.
     */
    Constant cons(OWLIndividual individual);

    /**
     * Obtains a constant representing a specified OWL literal.
     *
     * @param literal the OWL literal.
     * @return the constant representing {@code literal}.
     */
    Constant cons(OWLLiteral literal);

    /**
     * Obtains a constant representing a OWL individual or OWL literal.
     *
     * @param object the OWL individual or OWL literal.
     * @return the constant representing {@code object}.
     */
    Constant cons(OWLPropertyAssertionObject object);

    /**
     * Obtainss a constant representing a specified symbol. If the symbol is a
     * number, then the created constant is an numeric constant.
     *
     * @param symbol the symbol.
     * @return a numeric constant representing {@code symbol} if {@code symbol}
     * is a number; or a rule constant representing {@code symbol}, otherwise.
     */
    Constant cons(String symbol);

    void dispose();

    /**
     * Obtains a domain meta-predicate from a specified role.
     *
     * @param role a role <i>P</i>.
     * @param doub specifies whether the meta-predicate is of a double type.
     * @return the domain meta-predicate <i>DP</i>, if {@code doub} is true; the
     * double domain meta-predicate, <i>DP<sup>d</sup></i>, otherwise.
     */
    Predicate domPred(OWLPropertyExpression role, boolean doub);

    /**
     * Obtains a double domain meta-predicate from a specified role.
     *
     * @param role the role <i>P</i>.
     * @return the meta-predicate <i>DP<sup>d</sup></i>.
     */
    Predicate doubDomPred(OWLPropertyExpression role);

    /**
     * Obtains a double meta-predicate from a specified concept.
     *
     * @param concept the concept <i>A</i>.
     * @return the double meta-predicate <i>A<sup>d</sup></i>.
     */
    Predicate doubPred(OWLClass concept);

    /**
     * Obtains a double meta-predicate from a specified role.
     *
     * @param role a role <i>P</i>.
     * @return the meta-predicate <i>P<sup>d</sup></i>.
     */
    Predicate doubPred(OWLPropertyExpression role);

    /**
     * Obtains a double meta-predicate from a specified predicate symbol and
     * predicate arity.
     *
     * @param symbol a predicate symbol, <i>S</i>.
     * @param arity the arity, <i>n</i>, of the predicate that {@code symbol}
     * represents.
     * @return the double meta-predicate <i>S<sup>d</sup>/n</i>.
     */
    Predicate doubPred(String symbol, int arity);

    /**
     * Obtains a double range meta-predicate from a specified role.
     *
     * @param role a role, <i>P</i>.
     * @return the double range meta-predicate <i>DP<sup>d</sup></i>.
     */
    Predicate doubRanPred(OWLPropertyExpression role);

    /**
     * Generate a new concept that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     *
     * @return a new concept that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     */
    public OWLClass generateNewConcept();

    /**
     * Generate a new role that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     *
     * @return a new role that doesn't occur in the ontology refered by this
     * {@link Vocabulary}.
     */
    public OWLObjectProperty generateNewRole();

    /**
     * Returns the ontology component of the Hybrid Knowledge Base of which this
     * {@link Vocabulary} is vocabulary.
     *
     * @return the ontology component of the Hybrid Knowledge Base of which this
     * {@link Vocabulary} is vocabulary.
     */
    public OWLOntology getOntology();

    /**
     * Obtains a negative meta-predicate from a specified concept.
     *
     * @param concept a concept <i>A<i>.
     * @return a negative meta-predicate <i>NA</i>.
     */
    Predicate negPred(OWLClass concept);

    /**
     * Obtains a negative meta-predicate from a specified role.
     *
     * @param role a role <i>P</i>.
     * @return the negative meta-predicate <i>NP</i>.
     */
    Predicate negPred(OWLPropertyExpression role);

    /**
     * Obtains a negative meta-predicate from a specified predicate.
     *
     * @param predicate a predicate <i>P</i> or a meta-predicate <i>NP</i>,
     * <i>DP</i>, <i>RP</i>, <i>DP<sup>d</sup></i> or <i>RP<sup>d</sup></i>.
     * @return the meta-predicate <i>NP</i>.
     */
    Predicate negPred(Predicate predicate);

    /**
     * Obtains a negative meta-predicate from a specified predicate symbol and
     * predicate arity.
     *
     * @param symbol a symbol, <i>S</i>.
     * @param arity the arity, <i>n</i>, of the predicate that {@code symbol}
     * represents.
     * @return the negative meta-predicate <i>NS/n</i>.
     */
    Predicate negPred(String symbol, int arity);

    /**
     * Obtains an original domain meta-predicate from a specified role.
     *
     * @param role a role <i>P</i>.
     * @return the original domain meta-predicate <i>DP</i>.
     */
    Predicate origDomPred(OWLPropertyExpression role);

    /**
     * Obtains an original meta-predicate from a specified concept.
     *
     * @param concept a concept <i>A</i>.
     * @return the original meta-predicate <i>A</i>.
     */
    Predicate origPred(OWLClass concept);

    /**
     * Obtains an original meta-predicate from a specifieid role.
     *
     * @param role a role <i>P</i>.
     * @return the original meta-predicate <i>P</i>.
     */
    Predicate origPred(OWLPropertyExpression role);

    /**
     * Obtains an original meta-predicate from a specified predicate symbol and
     * predicate arity.
     *
     * @param symbol a predicate symbol <i>S</i>.
     * @param arity the arity, <i>n</i> of the predicate that {@code symbol}
     * represents.
     * @return the original meta-predicate <i>S</i>.
     */
    Predicate origPred(String symbol, int arity);

    /**
     * Obtains an original range meta-predicate from a specified role.
     *
     * @param role a role <i>P</i>.
     * @return the range original meta-predicate <i>RP</i>.
     */
    Predicate origRanPred(OWLPropertyExpression role);

    /**
     * Obtains a predicate representing a specified concept.
     *
     * @param concept a concept.
     * @return the predicate representing {@code concept}.
     */
    Predicate pred(OWLClass concept);

    /**
     * Obtains a meta-predicate from a specified concept.
     *
     * @param concept a concept <i>A</i>.
     * @param doub specifies whether the meta-predicate is of a double type.
     * @return <i>A<sup>d</sup></i> if {@code doub} is true; <i>A</i>,
     * otherwise.
     */
    Predicate pred(OWLClass concept, boolean doub);

    /**
     * Obtains a meta-predicate from a specified concept of a specified type.
     *
     * @param concept a concept <i>A</i>.
     * @param type a type. Shoudln't represent a quantification (i.e.
     * {@code type.}{@link PredicateType#isQuantification() isQuantification()}
     * must be false).
     * @return <i>A</i> if {@code type} is
     * {@link PredicateType#ORIGINAL original}; <br>
     * <i>A<sup>d</sup></i> if {@code type} is
     * {@link PredicateType#DOUBLE double}; <br>
     * <i>NA</i> if {@code type} is {@link PredicateType#NEGATIVE negative}.
     * @throws IllegalArgumentException if
     * {@code type.}{@link PredicateType#isQuantification() isQuantification()}
     * is true.
     */
    MetaPredicate pred(OWLClass concept, PredicateType type);

    /**
     * Obtains a predicate representing a specified role.
     *
     * @param role a role.
     * @return the predicate representing {@code role}.
     */
    Predicate pred(OWLPropertyExpression role);

    /**
     * Obtains a meta-predicate from a specified role.
     *
     * @param role a role <i>P</i>
     * @param doub specified whether the meta-predicate if of a double type.
     * @return <i>P<sup>d</sup></i> if {@code doub} is true; <i>P</i>,
     * otherwise.
     */
    Predicate pred(OWLPropertyExpression role, boolean doub);

    /**
     * Obtains a meta-predicate from a specified role of a specified type.
     *
     * @param role a concept <i>P</i>.
     * @param type a type.
     * @return <i>P</i> if {@code type} is
     * {@link PredicateType#ORIGINAL original}; <br>
     * <i>P<sup>d</sup></i> if {@code type} is
     * {@link PredicateType#DOUBLE double}; <br>
     * <i>NP</i> if {@code type} is {@link PredicateType#NEGATIVE negative};
     * <br>
     * <i>DP</i> if {@code type} is
     * {@link PredicateType#ORIGINAL_DOMAIN original domain}; <br>
     * <i>RP</i> if {@code type} is
     * {@link PredicateType#ORIGINAL_RANGE original range} ; <br>
     * <i>DP<sup>d</sup></i> if {@code type} is
     * {@link PredicateType#DOUBLE_DOMAIN double domain}; <br>
     * <i>RP<sup>d</sup></i> if {@code type} is
     * {@link PredicateType#DOUBLED_RANGE double range}.
     */
    Predicate pred(OWLPropertyExpression role, PredicateType type);

    /**
     * Obtains a meta-predicate form a specified predicate with a specified
     * type.
     *
     * @param predicate the predicate that the meta-predicate refers.
     * @param type the type of the meta-predicate.
     * @return a meta-predicate referring {@code predicate} with type
     * {@code type}.
     */
    MetaPredicate pred(Predicate predicate, PredicateType type);

    /*
	 * Obtains a predicate with a specified symbol and arity.
	 *
	 * @param symbol the symbol, <i>S</i>, that represents the predicate.
	 *
	 * @param arity the arity, <i>n</i> of the predicate.
	 *
	 * @return a predicate, <i>S/n</i> with symbol {@symbol} and arity {@code arity}.
     */
    Predicate pred(String symbol, int arity);

    /**
     * Obtains a meta-predicate from a specified predicate symbol and predicate
     * arity.
     *
     * @param symbol a predicate <i>S</i>.
     * @param arity a predicate arity <i>n</i>.
     * @param doub specifies whether the meta-predicate is of a double type.
     * @return the double meta-predicate <i>S/n</i> if {@code doub} is true; the
     * original meta-predicate <i>S<sup>d</sup>/n</i>, otherwise.
     */
    Predicate pred(String symbol, int arity, boolean doub);

    /**
     * Obtains a meta-predicate from a specified predicate symbol and predicate
     * arity with a specified type.
     *
     * @param symbol a predicate symbol <i>S</i>.
     * @param arity a predicate arity <i>n</i>.
     * @param type the type of the meta-predicate. Shoudn't represent a
     * quantification (i.e. {@code type.} {@link PredicateType#isQuantification()
     *            isQuantification()} must be false).
     * @return <i>S/n</i> if {@code type} is
     * {@link PredicateType#ORIGINAL original}; <br>
     * <i>S<sup>d</sup>/n</i>, if {@code type} is
     * {@link PredicateType#DOUBLE double}; <br>
     * <i>NS/n</i>, if {@code type} is {@link PredicateType#NEGATIVE negative}.
     * @throws IllegalArgumentException if {@code type.}
     * {@link PredicateType#isQuantification() isQuantification()} is true.
     */
    Predicate pred(String symbol, int arity, PredicateType type);

    Predicate prologPred(String symbol, int arity);

    Predicate prologOpPred(String symbol);
    
    /**
     * Obtains a range meta-predicate from a specified {@code role}.
     *
     * @param role a role <i>P</i>.
     * @param doub specifies whether this role is of a double type.
     * @return <i>RP<sup>d</sup></i> if {@code doub} is true; <i>RP</i>,
     * otherwise.
     */
    Predicate ranPred(OWLPropertyExpression role, boolean doub);

    /**
     * Removes a {@link VocabularyChangeListener}
     */
    void removeListener(VocabularyChangeListener listener);

}
