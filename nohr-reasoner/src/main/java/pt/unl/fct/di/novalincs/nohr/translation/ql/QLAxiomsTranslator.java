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
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

/**
 * Provides DL-Lite<sub>R</sub> axiom translation operations according to
 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
abstract class QLAxiomsTranslator {

	/**
	 * An variable <i>x</i>.
	 */
	static final Variable X = var("X");

	/**
	 * An variable <i>y</i>.
	 */
	static final Variable Y = var("Y");

	final Vocabulary v;

	QLAxiomsTranslator(Vocabulary v) {
		this.v = v;
	}

	/**
	 * Partially (depending on the concrete {@link QLAxiomsTranslator} used) translate a concept assertion to a set of rules according to <b>(a1)</b>
	 * of <b>Definition 9.</b> of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL
	 * 2 QL</a>}.
	 *
	 * @param assertion
	 *            an assertion
	 */
	abstract Set<Rule> assertionTranslation(OWLClassAssertionAxiom assertion);

	/**
	 * Partially (depending on the concrete {@link QLAxiomsTranslator} used) translate a role assertion to a set of rules according to <b>(a2)</b> of
	 * <b>Definition 9.</b> of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2
	 * QL</a>}.
	 *
	 * @param assertion
	 *            an assertion.
	 * @throws IllegalArgumentException
	 *             if {@code assertion} has a non atomic concept.
	 */
	abstract Set<Rule> assertionTranslation(OWLPropertyAssertionAxiom<?, ?> assertion);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate the domain subsumption entailed by a given
	 * role subsubtion to set of rules according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>2</sub></i>.
	 * @return the translation of the domain subsumption entailed by <i>Q<sub>1</sub>&sqsube;Q<sub>2</sub></i> according to <b>(s2)</b> (the same that
	 *         the translation of <i>&exist;Q<sub>1</sub>&sqsube;&exist;Q<sub>2</sub></i> according to <b>(s1)</b>), given the implementing class
	 *         criteria.
	 */
	abstract Rule domainSubsumptionTranslation(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate the meaning of a domain meta-predicate to a
	 * set of rules according to <b>(e)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param p
	 *            a role <i>P</i>.
	 * @return the translation of the meaning <i>DP</i> or <i>DP<sup>d</sub></i> according to <b>(e)</b>, given the implementing class criteria.
	 */
	abstract Rule domainTranslation(OWLObjectProperty p);

	/**
	 * Translate the classic negation of a given concept to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param b
	 *            a basic concept <i>B</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>NA(x)</i> if <i>B</i> is an atomic concept <i>A</i>; <br>
	 *         <i>NP(x,_)</i> if <i>B</i> is an existential <i>&exist;P</i>; <br>
	 *         <i>NP(_,x)</i> if <i>B</i> is an existential <i>&exist;P<sup>-</sup></i>.
	 * @throws IllegalArgumentException
	 *             if <i>B</i> isn't a basic concept.
	 */
	Atom negTr(OWLClassExpression b, Variable x) {
		if (b instanceof OWLClass)
			return atom(v.negPred((OWLClass) b), x);
		else if (b instanceof OWLObjectSomeValuesFrom) {
			final OWLObjectSomeValuesFrom b1 = (OWLObjectSomeValuesFrom) b;
			if (!b1.getFiller().isOWLThing())
				throw new IllegalArgumentException("b: must be a basic concept, can't be: " + b);
			return negTr(b1.getProperty(), x, var());
		} else
			throw new IllegalArgumentException("b: must be a basic concept, can't be: " + b);
	}

	/**
	 * Translate the classic negation of a given role to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param q
	 *            a basic role <i>Q</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param y
	 *            a variable <i>y</i>.
	 * @return <i>NP(x,y)</i> if <i>Q</i> is an atomic role <i>P</i>; <br>
	 *         <i>NP(y,x)</i> if <i>Q</i> is an inverse role <i>P<sup>-</sup></i>.
	 */
	Atom negTr(OWLPropertyExpression q, Variable x, Variable y) {
		if (q instanceof OWLObjectProperty)
			return atom(v.negPred(q), x, y);
		else if (q instanceof OWLDataProperty)
			return atom(v.negPred(q), x, y);
		else
			return atom(v.negPred(q), y, x);
	}

	/**
	 * Translate the classic negation of the existential formed with a given basic role to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param q
	 *            a basic role <i>Q</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>NP(x,_)</i> if <i>Q</i> is an atomic role <i>P</i>; <br>
	 *         <i>NP(_,y)</i> if <i>Q</i> in an inverse role <i>P</i>.
	 */
	Atom negTrExist(OWLObjectPropertyExpression q, Variable x) {
		if (q instanceof OWLObjectProperty)
			return atom(v.negPred(q), x, var());
		else
			return atom(v.negPred(q), var(), x);
	}

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate the range subsumption entailed by a given role
	 * subsubtion to set of rules according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            a DL-Lite<sub>R</sub> basic role <i>Q<sub>2</sub></i>.
	 * @return the translation of the range subsumption entailed by <i>Q<sub>1</sub>&sqsube;Q<sub>2</sub></i> according to <b>(s2)</b> (the same that
	 *         the translation of <i>&exist;Q<sub>1</sub><sup>-</sup>&sqsube;&exist;Q<sub>2</sub><sup>-</sup></i> according to <b>(s1)</b>), given the
	 *         implementing class criteria.
	 */
	abstract Rule rangeSubsumptionTranslation(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate the meaning of a range meta-predicate to a set
	 * of rules according to <b>(e)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param p
	 *            a role <i>P</i>.
	 * @return the translation of the meaning <i>RP</i> or <i>RP<sup>d</sub></i> according to <b>(e)</b>, given the implementing class criteria.
	 */
	abstract Rule rangeTranslation(OWLObjectProperty p);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> concept subsumption
	 * axiom to a set of rules according to <b>(s1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param b1
	 *            the subsumed DL-Lite<sub>R</sub> basic concept <i>B<sub>1</sub></i>.
	 * @param b2
	 *            the subsuming DL-Lite<sub>R</sub> basic concept <i>B<sub>2</sub></i>.
	 * @return the translation of <i>B<sub>1</sub>&sqsube;&not;B<sub>2</sub></i> according to <b>(s1)</b> given the implementing class criteria.
	 * @throws IllegalArgumentException
	 *             if <i>B<sub>1</sub></i> or <i>B<sub>2</sub></i> aren't DL-Lite<sub>R</sub> basic concepts.
	 */
	abstract Set<Rule> subsumptionTranslation(OWLClassExpression b1, OWLClassExpression b2);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> role subsumption axiom
	 * to a set of rules according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            the subsumed basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            the subsuming basic role <i>Q<sub>2</sub></i>.
	 * @return translation of <i>Q<sub>1</sub>&sqsube;&not;Q<sub>2</sub></i> according to <b>(s2)</b> given the implementing class criteria.
	 * @throws IllegalArgumentException
	 *             <i>Q<sub>1</sub></i> or <i>Q<sub>2</sub></i> aren't a basic DL-Lite<sub>R</sub> roles.
	 */
	abstract Set<Rule> subsumptionTranslation(OWLPropertyExpression q1, OWLPropertyExpression q2);

	/**
	 * Translate a given DL-Lite<sub>R</sub> concept to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param b
	 *            a DL-Lite<sub>R</sub> basic concept <i>B</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param doub
	 *            specifies whether the returned atom is double (i.e. have a double meta-predicate functor).
	 * @return <i>A(x)</i> if <i>B</i> is an atomic concept <i>A</i>; <br>
	 *         <i>DP(x)</i> if <i>B</i> in an existential <i>&exist;P</i>; <br>
	 *         <i>RP(x)</i> if <i>B</i> is an existential <i>&exist;P<sup>-</sup>; <br>
	 *         and the corresponding double atoms if {@code doub} is true.
	 * @throws IllegalArgumentException
	 *             if <i>B</i> isn't a basic DL-Lite<sub>R</sub> concept.
	 */
	Atom tr(OWLClassExpression b, Variable x, boolean doub) {
		if (b instanceof OWLClass)
			return atom(v.pred((OWLClass) b, doub), x);
		else if (b instanceof OWLObjectSomeValuesFrom) {
			final OWLObjectPropertyExpression q = ((OWLObjectSomeValuesFrom) b).getProperty();
			if (q instanceof OWLObjectProperty)
				return atom(v.domPred(q, doub), x);
			else
				return atom(v.ranPred(q, doub), x);
		} else
			throw new IllegalArgumentException("c: must be an DL-LiteR concept, but was: " + b);
	}

	/**
	 * Translate a given DL-Lite<sub>R</sub> role to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param r
	 *            a DL-Lite<sub>R</sub> role <i>R</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param y
	 *            a variable <i>y</i>.
	 * @param doub
	 *            specifies whether the returned atom is double (i.e. have a double meta-predicate functor).
	 * @return <i>P(x, y)</i> if <i>R</i> is an atomic role <i>P</i>; <br>
	 *         <i>P(y, x)</i> if <i>R</i> is an inverse role <i>P<sup>-</sup></i>; <br>
	 *         and the respective double atoms if {@code doub} is true.
	 */
	Atom tr(OWLPropertyExpression r, Variable x, Variable y, boolean doub) {
		if (r instanceof OWLObjectProperty)
			return atom(v.pred(r, doub), x, y);
		else if (r instanceof OWLDataProperty)
			return atom(v.pred(r, doub), x, y);
		else
			return atom(v.pred(r, doub), y, x);
	}

}
