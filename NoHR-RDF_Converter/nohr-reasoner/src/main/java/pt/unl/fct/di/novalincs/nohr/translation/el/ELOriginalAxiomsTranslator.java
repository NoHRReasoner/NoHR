/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.translation.el;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import static pt.unl.fct.di.novalincs.nohr.model.Model.rule;
import static pt.unl.fct.di.novalincs.nohr.model.Model.ruleSet;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

/**
 * Implementing class of {@link ELAxiomsTranslator} to handle the original part (i.e. the rules with original meta-predicates) of the EL
 * <sub>&perp;</sub><sup>+</sup> axioms translations (see {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>}).
 *
 * @author Nuno Costa
 */
class ELOriginalAxiomsTranslator extends ELAxiomsTranslator {

	ELOriginalAxiomsTranslator(Vocabulary v) {
		super(v);
	}

	/**
	 * Translate a role chain subsumption axiom to a set of original rules according to <b>(r2)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL
	 * Oracle for NoHR (Technical Report)</a>} .
	 *
	 * @param chain
	 *            a role chain <i>R<sub>1</sub>&#x26AA; ... &#x26AA;R<sub>k</i>.
	 * @param superRole
	 *            the subsuming role <i>S</i>
	 * @retrun <i>{ S(x, x<sub>k</sub>)&larr; tr(R<sub>1k</sub>, x, x<sub>k</sub>) }</i>.
	 */
	@Override
	Set<Rule> subsumptionTranslation(List<OWLObjectPropertyExpression> chain, OWLObjectProperty superRole) {
		return ruleSet(rule(tr(superRole, X, Y), tr(chain, X, Y)));
	}

	/**
	 * Translate a concept subsumption axiom to a set of original rules according to <b>(t1)</b>, <b>(c1)</b> and <b>(i1)</b> and <b>(i2)</b> of
	 * <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} .
	 *
	 * @param c
	 *            the subsumed concept expression <i>C</i>.
	 * @param a
	 *            the subsuming concept <i>A</i>.
	 * @return <i>{ A(x)&larr;</i> }, if <i>A</i> is the top concept; <br>
	 *         <i>{ }, if <i>C</i> is the bottom concept <i>&larr;</i>;<br>
	 *         <i>{ A(x)&larr; tr(C, x) }</i>, otherwise.
	 */
	@Override
	Set<Rule> subsumptionTranslation(OWLClassExpression c, OWLClass a) {
		// (i1) and (i2)
		// Note that the rules whose head have a negative meta-predicate functor aren't relevant in the original part of the translation.
		if (a.isOWLNothing())
			return ruleSet();
		// (t1)
		if (c.isOWLThing())
			return ruleSet(rule(tr(a, X)));
		// (a1)
		return ruleSet(rule(tr(a, X), tr(c, X)));
	}

	/**
	 * Translate a role subsumption axiom to a set of original rules according to <b>(r1)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL
	 * Oracle for NoHR (Technical Report)</a>} .
	 *
	 * @param r
	 *            the subsumed role <i>R</i>.
	 * @param s
	 *            the subsuming role <i>S</i>
	 * @return <i> { S(x, y)&larr;R(x, y) } </i>.
	 */
	@Override
	Set<Rule> subsumptionTranslation(OWLProperty r, OWLProperty s) {
		return ruleSet(rule(tr(s, X, Y), tr(r, X, Y)));
	}

	/**
	 * Translate an role composition to a list of atoms according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
	 * Report)</a>}. Corresponds to <i>tr(R<sub>1k</sub>, x, x<sub>k</sub>)</i>.
	 *
	 * @param chain
	 *            a role composition R<sub>1</sub>&#x26AA; ...&#x26AA;R<sub>k</sub>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param xk
	 *            a variable <i>x<sub>k</sub>.
	 * @return <i>[R<sub>1</sub>(x, x<sub>1</sub>), ..., R<sub>k</sub> (x<sub>k-1</sub> , x<sub>k</sub>)]</i>.
	 */
	List<Atom> tr(List<OWLObjectPropertyExpression> chain, Variable x, Variable xk) {
		return tr(chain, x, xk, false);
	}

	/**
	 * Translate an atomic concept to an atom according according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
	 * Report)</a>}. Corresponds to <i>tr(A, x)</i> .
	 *
	 * @param c
	 *            an atomic concept <i>A</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>A(x)</i>, otherwise.
	 */
	Atom tr(OWLClass c, Variable x) {
		return tr(c, x, false);
	}

	/**
	 * Translate a concept to a list of atoms according according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
	 * Report)</a>}. Corresponds to <i>tr(C, x)</i>.
	 *
	 * @param ce
	 *            an concept <i>C</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>{A(x)}</i> if <i>C</i> is an atomic concept <i>A</i>; <br>
	 *         <i>{}</i> if <i>C</i> is the top concept &#x22A5; <br>
	 *         <i>tr(C<sub>1</sub>, x) &cup; tr(C<sub>2</sub>, x) if <i>C</i> is an conjunction <i>C<sub>1</sub> &prod; <i>C<sub>2</sub></i>; </br>
	 *         <i>{R(x, x<sub>1</sub>)} &cup; tr(D, x<sub>1</sub>)</i> if <i>C</i> is an existential <i>&exist;R.D</i>.
	 */
	List<Literal> tr(OWLClassExpression ce, Variable x) {
		return tr(ce, x, false);
	}

	/**
	 * Translate an atomic role to an atom according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>}.
	 * Corresponds to <i>tr(R, x, x<sub>1</sub>)</i>.
	 *
	 * @param r
	 *            a role <i>R</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param x1
	 *            a variable <i>x<sub>1</sub>.
	 * @return <i>R(x, x<sub>1</sub>)</i>.
	 */
	Atom tr(OWLProperty p, Variable x, Variable x1) {
		return tr(p, x, x1, false);
	}

	/**
	 * Translate a concept assertion to a set of original rules according to <b>(a1)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for
	 * NoHR (Technical Report)</a>}.
	 *
	 * @param assertion
	 *            an assertion <i>A(a)</i>
	 * @return <i>{A(a)&larr;}</i>
	 */
	@Override
	Set<Rule> translation(OWLClassAssertionAxiom assertion) {
		return AssertionsTranslation.translateOriginal(v, assertion);
	}

	/**
	 * Translate a role assertion to a set of original rules according to <b>(a2)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for
	 * NoHR (Technical Report)</a>}.
	 *
	 * @param assertion
	 *            an assertion <i>R(a, b)</i>.
	 * @return <i>R(a, b)&larr;</i>.
	 */
	@Override
	Set<Rule> translation(OWLPropertyAssertionAxiom<?, ?> assertion) {
		return AssertionsTranslation.translateOriginal(v, assertion);
	}

}
