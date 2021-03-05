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

import static pt.unl.fct.di.novalincs.nohr.model.Model.atom;
import static pt.unl.fct.di.novalincs.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.novalincs.nohr.model.Model.rule;
import static pt.unl.fct.di.novalincs.nohr.model.Model.ruleSet;

import java.util.ArrayList;
import java.util.HashSet;
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
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

/**
 * Implementing class of {@link ELAxiomsTranslator} to handle the double part (i.e. the rules with double meta-predicates and those whose heads have a
 * negative meta-predicate functor) of the EL <sub>&perp;</sub> <sup>+</sup> axioms translations (see {@link <a>A Correct EL Oracle for NoHR
 * (Technical Report)</a>}).
 *
 * @author Nuno Costa
 */
class ELDoubleAxiomsTranslator extends ELAxiomsTranslator {

	ELDoubleAxiomsTranslator(Vocabulary v) {
		super(v);
	}

	/**
	 * Obtains the negated atom (i.e. with a negative meta-predicate) of a given literal.
	 *
	 * @param b
	 *            a literal <i>M(t<sub>1</sub>,...,t<sub>n</sub>)</i> or <i><b>not</b> M(t<sub>1</sub>,...,t<sub>n</sub>)</i> where <i>M</i> is a
	 *            predicate <i>P</i> or a meta-predicate <i>NP</i>, <i>DP</i>, <i>RP</i>, <i>DP<sup>d</sup></i> or <i>RP<sup>d</sup>.
	 * @return <i>NP</i>(t<sub>1</sub>, ..., t<sub>n</sub>)</i>.
	 */
	Atom negTr(Literal b) {
		final Predicate pred0 = b.getFunctor();
		final Predicate pred = v.negPred(pred0);
		return atom(pred, b.getAtom().getArguments());
	}

	/**
	 * Translate the complement of a given concept to an atom.
	 *
	 * @param a
	 *            a concept <i>A</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>NA(x)</i>.
	 */
	Atom negTr(OWLClass a, Variable x) {
		final Predicate pred = v.negPred(a);
		return atom(pred, x);
	}

	/**
	 * Translate the complement of a given role to an atom.
	 *
	 * @param r
	 *            a role <i>R</i>
	 * @param x
	 *            a variable <i>x</i>
	 * @param y
	 *            a variable <i>y</i>
	 * @return <i>NR(x, y)</i>
	 */
	Atom negTr(OWLProperty r, Variable x, Variable y) {
		final Predicate pred = v.negPred(r);
		return atom(pred, x, y);
	}

	/**
	 * Translate a role chain subsumption axiom to a set of double rules according to <b>(r2)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL
	 * Oracle for NoHR (Technical Report)</a>} .
	 *
	 * @param chain
	 *            a role chain <i>R<sub>1</sub>&#x26AA; ... &#x26AA;R<sub>k</i>.
	 * @param s
	 *            the subsuming role <i>S</i>
	 * @return see lines 2,3 and 4 of <b>(r2)</b>.
	 */
	// (r2)
	@Override
	Set<Rule> subsumptionTranslation(List<OWLObjectPropertyExpression> chain, OWLObjectProperty s) {
		final Set<Rule> result = new HashSet<Rule>();
		List<Atom> chainTr = tr(chain, X, Y);
		List<Literal> body = new ArrayList<Literal>(chainTr);
		body.add(negLiteral(negTr(s, X, Y)));
		result.add(rule(tr(s, X, Y), body));
		for (final Literal r : tr(chain, X, Y, false)) {
			chainTr = tr(chain, X, Y, false);
			body = new ArrayList<Literal>(chainTr);
			body.add(negTr(s, X, Y));
			body.remove(r);
			result.add(rule(negTr(r), body));
		}
		return result;
	}

	/**
	 * Translate a concept subsumption axiom to a set of double rules according to <b>(t1)</b>, <b>(c1)</b> and <b>(i1)</b> and <b>(i2)</b> of
	 * <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>} .
	 *
	 * @param c
	 *            the subsumed concept expression <i>C</i>.
	 * @param a
	 *            the subsuming concept <i>A</i>.
	 * @return see: <br>
	 *         line 2 of <b>(t1)</b>; <br>
	 *         lines 2,3 and 4 of <b>(c1)</b>; <br>
	 *         <b>(i1)</b>; <br>
	 *         <b>(i2)</b>.
	 */
	@Override
	Set<Rule> subsumptionTranslation(OWLClassExpression c, OWLClass a) {
		final Set<Rule> result = new HashSet<Rule>();
		// (t1)
		if (c.isOWLThing())
			result.add(rule(tr(a, X), negLiteral(negTr(a, X))));
		// (i1)
		else if (a.isOWLNothing() && !c.isAnonymous())
			result.add(rule(negTr(c.asOWLClass(), X)));
		// (i2)
		else if (a.isOWLNothing() && c.isAnonymous())
			for (final Literal b : tr(c, X, false)) {
				final List<Literal> body = tr(c, X, false);
				body.remove(b);
				result.add(rule(negTr(b), body));
			}
		// (c1)
		else {
			List<Literal> body = tr(c, X);
			body.add(negLiteral(negTr(a, X)));
			result.add(rule(tr(a, X), body));
			for (final Literal b : tr(c, X, false)) {
				body = tr(c, X, false);
				body.add(negTr(a, X));
				body.remove(b);
				result.add(rule(negTr(b), body));
			}
		}
		return result;
	}

	/**
	 * Translate a role subsumption axiom to a set of double rules according to <b>(r1)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle
	 * for NoHR (Technical Report)</a>} .
	 *
	 * @param r
	 *            the subsumed role <i>R</i>.
	 * @param s
	 *            the subsuming role <i>S</i>.
	 * @return see lines 2 and 3 of <b>(i1)</b>.
	 */
	// (r1)
	@Override
	Set<Rule> subsumptionTranslation(OWLProperty r, OWLProperty s) {
		return ruleSet(rule(tr(r, X, Y), tr(s, X, Y), negLiteral(negTr(r, X, Y))),
				rule(negTr(r, X, Y), negTr(s, X, Y)));
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
	 * @return <i>[R<sub>1</sub><sup>d</sup>(x, x<sub>1</sub>), ..., R<sub>k</sub><sup>d</sup>(x<sub>k-1</sub>, x<sub>k</sub>)]</i>.
	 */
	List<Atom> tr(List<OWLObjectPropertyExpression> chain, Variable x, Variable xk) {
		return tr(chain, x, xk, true);
	}

	/**
	 * Translate an atomic concept to an atom according according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
	 * Report)</a>}. Corresponds to <i>tr(A, x)<sup>d</sup>.
	 *
	 * @param c
	 *            an atomic concept <i>A</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>A<sup>d</sup>(x)</i>.
	 */
	Atom tr(OWLClass c, Variable x) {
		return tr(c, x, true);
	}

	/**
	 * Translate a concept to a list of atoms according according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
	 * Report)</a>}. Corresponds to <i>tr(C, x)<sup>d</sup>.
	 *
	 * @param ce
	 *            an concept <i>C</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>{A<sup>d</sup>(x)}</i> if <i>C</i> is an atomic concept <i>A</i>; <br>
	 *         <i>{}</i> if <i>C</i> is the top concept &#x22A5; <br>
	 *         <i>tr(C<sub>1</sub>, x)<sup>d</sup> &cup; tr(C<sub>2</sub>, x)<sup>d</sup> if <i>C</i> is an conjunction <i>C<sub>1</sub> &sqcap; <i>C
	 *         <sub>2</sub></i>; </br>
	 *         <i>{R<sup>d</sup>(x, x<sub>1</sub>)} &cup; tr(D, x<sub>1</sub>)<sup>d</sup></i> if <i>C</i> is an existential <i>&exist;R.D</i> <br>
	 *         .
	 */
	List<Literal> tr(OWLClassExpression ce, Variable x) {
		return tr(ce, x, true);
	}

	/**
	 * Translate an atomic role to an atom according to <b> Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>}.
	 * Corresponds to <i>tr(R, x, x<sub>1</sub>)<sup>d</sup></i>.
	 *
	 * @param r
	 *            a role <i>R</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param x1
	 *            a variable <i>x<sub>1</sub>.
	 * @return <i>R<sup>d</sup>(x, x<sub>1</sub>)</i>.
	 */
	Atom tr(OWLProperty r, Variable x, Variable y) {
		return tr(r, x, y, true);
	}

	/**
	 * Translate a concept assertion to a set of double rules according to <b>(a1)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for
	 * NoHR (Technical Report)</a>}.
	 *
	 * @param assertion
	 *            an assertion <i>A(a)</i>
	 * @return <i>{A<sup>d</sup>(a)&larr;}</i>
	 */
	@Override
	Set<Rule> translation(OWLClassAssertionAxiom assertion) {
		return AssertionsTranslation.translateDouble(v, assertion);
	}

	/**
	 * Translate a role assertion to a set of double rules according to <b>(a2)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for NoHR
	 * (Technical Report)</a>}.
	 *
	 * @param assertion
	 *            an assertion <i>R(a, b)</i>.
	 * @return <i>R<sup>d</sup>(a, b)&larr;</i>.
	 */
	@Override
	Set<Rule> translation(OWLPropertyAssertionAxiom<?, ?> alpha) {
		return AssertionsTranslation.translateDouble(v, alpha);
	}
}
