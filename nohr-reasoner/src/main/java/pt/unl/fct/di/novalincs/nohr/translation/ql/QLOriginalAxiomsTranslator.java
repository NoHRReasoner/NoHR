/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation.ql;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import static pt.unl.fct.di.novalincs.nohr.model.Model.atom;
import static pt.unl.fct.di.novalincs.nohr.model.Model.rule;
import static pt.unl.fct.di.novalincs.nohr.model.Model.ruleSet;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

/**
 * Implementing class of {@link QLAxiomsTranslator} to handle the original part (i.e. the rules with original meta-predicates) of the DL-Lite
 * <sub>R</sub> axioms translations (see {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for
 * NoHR: OWL 2 QL</a>}).
 *
 * @author Nuno Costa
 */
class QLOriginalAxiomsTranslator extends QLAxiomsTranslator {

	QLOriginalAxiomsTranslator(Vocabulary v) {
		super(v);
	}

	/**
	 * Translate a concept assertion to a set of original rules according to <b>(a1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
	 *
	 * @param assertion
	 *            a DL-Lite<sub>R</sub> concept assertion <i>A(a)</i>.
	 * @return <i>A(a)&larr;</i>.
	 * @throws IllegalAccessException
	 *             if {@code assertion} has a non atomic concept.
	 */
	@Override
	Set<Rule> assertionTranslation(OWLClassAssertionAxiom assertion) {
		return AssertionsTranslation.translateOriginal(v, assertion);
	}

	/**
	 * Translate a role assertion to a set of rules according to <b>(a2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
	 *
	 * @param assertion
	 *            a DL-Lite<sub>R</sub> role assertion <i>P(a, b)</i>.
	 * @return <i>P(a,b)&larr;<b>not</b> NP(a,b)</i>
	 */
	@Override
	Set<Rule> assertionTranslation(OWLPropertyAssertionAxiom<?, ?> assertion) {
		return AssertionsTranslation.translateOriginal(v, assertion);
	}

	/**
	 * Translate the domain subsumption entailed by a given role subsubtion to set of original rules according to <b>(s2)</b> of <b>Definition 9.</b>
	 * of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>2</sub></i>.
	 * @return <i>{tr(&exist;Q<sub>2</sub>, x)&larr;tr(&exist;Q<sub>1</sub></i>, x)}</i>.
	 */
	@Override
	Rule domainSubsumptionTranslation(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2) {
		return rule(existTr(q2, X), existTr(q1, X));
	}

	/**
	 * Translate the meaning of a domain meta-predicate to a set of original rules according to <b>(e)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param p
	 *            a role <i>P</i>.
	 * @return <i>DP(x)&larr;P(x,_)</i>.
	 */
	@Override
	Rule domainTranslation(OWLObjectProperty p) {
		return rule(atom(v.origDomPred(p), X), atom(v.origPred(p), X, var()));
	}

	/**
	 * Translate the existential quantification of a given DL-Lite<sub>R</sub> role to an original atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param q
	 *            a DL-Lite<sub>R</sub> concept <i>Q</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>DP(x)</i> if <i>Q</i> is an atomic role<i>P</i>; <br>
	 *         <i>RP(x)</i> if <i>Q</i> in an inverse role<i>P<sup>-</sup></i>.
	 * @throws IllegalArgumentException
	 *             if <i>R</i> isn't a DL-Lite<sub>R</sub> role.
	 */
	Atom existTr(OWLObjectPropertyExpression q, Variable x) {
		if (q instanceof OWLObjectProperty)
			return atom(v.origDomPred(q), x);
		else if (q instanceof OWLObjectInverseOf)
			return atom(v.origRanPred(q), x);
		else
			throw new IllegalArgumentException("q: must be a basic role");
	}

	/**
	 * Translate the range subsumption entailed by a given role subsubtion to set of original rules according to <b>(s2)</b> of <b>Definition 9.</b>
	 * of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>2</sub></i>.
	 * @return <i>tr(&exist;Q<sub>2</sub><sup>-</sup>, x)&larr;tr(&exist;Q<sub>1</sub><sup>-</sup>, x)</i>.
	 */
	@Override
	Rule rangeSubsumptionTranslation(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2) {
		return rule(existTr(q2, X), existTr(q1, X));
	}

	/**
	 * Translate the meaning of a range meta-predicate to a set of original rules according to <b>(e)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param p
	 *            a role <i>P</i>.
	 * @return <i>RP(x)&larr;P(_,x)</i>.
	 */
	@Override
	Rule rangeTranslation(OWLObjectProperty p) {
		return rule(atom(v.origRanPred(p), X), atom(v.origPred(p), var(), X));
	}

	/**
	 * Translate a DL-Lite<sub>R</sub> concept subsumption axiom to a set of original rules according to <b>(s1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param b1
	 *            the subsumed DL-Lite<sub>R</sub> basic concept <i>B<sub>1</sub></i>.
	 * @param b2
	 *            the subsuming DL-Lite<sub>R</sub> basic concept <i>B<sub>2</sub></i>.
	 * @return <i>{tr(B<sub>2</sub>, x)}&larr;</i> if <i>B<sub>1</sub></i> is the top concept <i>&top;</i>;<br>
	 *         <i>{tr(B<sub>2</sub>, x)&larr;tr(B<sub>1</sub>, x)}</i>, otherwise.
	 * @throws IllegalArgumentException
	 *             if <i>B<sub>1</sub></i> or <i>B<sub>2</sub></i> aren't DL-Lite<sub>R</sub> basic concepts.
	 */
	@Override
	Set<Rule> subsumptionTranslation(OWLClassExpression b1, OWLClassExpression b2) {
		if (b1.isOWLThing())
			return ruleSet(rule(tr(b2, X)));
		return ruleSet(rule(tr(b2, X), tr(b1, X)));
	}

	/**
	 * Translate a DL-Lite<sub>R</sub> role subsumption axiom to a set of rules according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            the subsumed basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            the subsuming basic role <i>Q<sub>2</sub></i>.
	 * @return <i>{tr(Q<sub>2</sub>, x, y)&larr;tr(Q<sub>1</sub>, x, y)}</i>.
	 * @throws IllegalArgumentException
	 *             <i>Q<sub>1</sub></i> or <i>Q<sub>2</sub></i> aren't a basic DL-Lite<sub>R</sub> roles.
	 */
	@Override
	Set<Rule> subsumptionTranslation(OWLPropertyExpression q1, OWLPropertyExpression q2) {
		if (q1.isBottomEntity() || q2.isTopEntity())
			return ruleSet();
		if (q1.isTopEntity())
			return ruleSet(rule(tr(q2, X, Y)));
		if (q2.isBottomEntity())
			return ruleSet();
		return ruleSet(rule(tr(q2, X, Y), tr(q1, X, Y)));
	}

	/**
	 * Translate a given DL-Lite<sub>R</sub> concept to an original atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param b
	 *            a DL-Lite<sub>R</sub> concept <i>C</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>A(x)</i> if <i>B</i> is an atomic concept <i>A</i>; <br>
	 *         <i>DP(x)</i> if <i>B</i> in an existential <i>&exist;P</i>; <br>
	 *         <i>RP(x)</i> if <i>B</i> is an existential <i>&exist;P<sup>-</sup></i>.
	 * @throws IllegalArgumentException
	 *             if <i>B</i> isn't a basic DL-Lite<sub>R</sub> concept.
	 */
	Atom tr(OWLClassExpression b, Variable x) {
		return tr(b, x, false);
	}

	/**
	 * Translate a given DL-Lite<sub>R</sub> role to an original atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param r
	 *            a DL-Lite<sub>R</sub> role <i>R</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param y
	 *            a variable <i>y</i>.
	 * @return <i>P(x, y)</i> if <i>R</i> is an atomic role <i>P</i>; <br>
	 *         <i>P(y, x)</i> if <i>R</i> is an inverse role <i>P<sup>-</sup></i>.
	 */
	Atom tr(OWLPropertyExpression r, Variable x, Variable y) {
		return tr(r, x, y, false);
	}
}
