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
import static pt.unl.fct.di.novalincs.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.novalincs.nohr.model.Model.rule;
import static pt.unl.fct.di.novalincs.nohr.model.Model.ruleSet;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

/**
 * Implementing class of {@link QLAxiomsTranslator} to handle the double part
 * (i.e. the rules with double meta-predicates and those whose heads have a
 * negative meta-predicate functor) of the DL-Lite<sub>R</sub> axioms
 * translations (see
 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
 * Step for NoHR: OWL 2 QL</a>}). ).
 *
 * @author Nuno Costa
 */
class QLDoubleAxiomsTranslator extends QLAxiomsTranslator {

    QLDoubleAxiomsTranslator(Vocabulary v) {
        super(v);
    }

    /**
     * Translate a concept assertion to a set of original rules according to
     * <b>(a1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}.
     *
     * @param assertion an assertion <i>A(a)</i>.
     * @return <i>A<sup>d</sup>(a)&larr;<b>not </b> NA(a)</i>.
     * @throws IllegalArgumentException if {@code assertion} has a non atomic
     * concept.
     */
    @Override
    Set<Rule> assertionTranslation(OWLClassAssertionAxiom assertion) {
        return AssertionsTranslation.translateDouble(v, assertion);
    }

    /**
     * Translate a role assertion to a set of double rules according to
     * <b>(a2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}.
     *
     * @param assertion a DL-Lite<sub>R</sub> role assertion <i>P(a, b)</i>.
     * @return <i>P<sup>d</sup>(a,b)&larr;<b>not</b> NP(a,b)</i>
     */
    @Override
    Set<Rule> assertionTranslation(OWLPropertyAssertionAxiom<?, ?> assertion) {
        return AssertionsTranslation.translateDouble(v, assertion);
    }

    /**
     * Translate a DL-Lite<sub>R</sub> negative concept subsumption axiom to a
     * set of double rules according to <b>(n1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param b1 a DL-Lite<sub>R</sub> basic concept operand,
     * <i>B<sub>1</sub></i>.
     * @param b2 a DL-Lite<sub>R</sub> basic concept operand,
     * <i>B<sub>2</sub></i>.
     * @return <i>{tr(&not;B<sub>1</sub>, x)&larr;tr(B<sub>2</sub>, x), <br>
     * tr(&not;B<sub>2</sub>, x)&larr;tr(B<sub>1</sub>, x)}</i>.
     * @throws IllegalArgumentException if <i>B<sub>1</sub></i> or
     * <i>B<sub>2</su></i> aren't DL-Lite<sub>R</sub> basic concepts.
     */
    Set<Rule> disjunctionTranslation(OWLClassExpression b1, OWLClassExpression b2) {
        if (b1.isBottomEntity() || b2.isBottomEntity()) {
            return ruleSet();
        }
        if (b1.isOWLThing()) {
            return unsatisfiabilityTranslation((OWLClass) b2);
        }
        if (b2.isOWLThing()) {
            return unsatisfiabilityTranslation((OWLClass) b1);
        }
        return ruleSet(rule(negTr(b1, X), tr(b2, X, false)), rule(negTr(b2, X), tr(b1, X, false)));
    }

    /**
     * Translate a DL-Lite<sub>R</sub> negative role subsumption axiom to a set
     * of double rules according to <b>(n2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param q1 a DL-Lite<sub>R</sub> basic role operand, <i>Q<sub>1</sub></i>.
     * @param q2 a DL-Lite<sub>R</sub> basic role operand, <i>Q<sub>2</sub></i>.
     * @return <i>{tr(&not;Q<sub>1</sub>, x, y)&larr;tr(&not;Q<sub>2</sub>, x,
     * y),<br>
     * tr(&not;Q<sub>2</sub>, x, y)&larr;tr(&not;Q<sub>1</sub>)}</i>.
     * @throws IllegalArgumentException if <i>Q<sub>1</sub></i> or
     * <i>Q<sub>2</sub></i> aren't DL-Lite<sub>R</sub> basic roles.
     */
    Set<Rule> disjunctionTranslation(OWLPropertyExpression q1, OWLPropertyExpression q2) {
        if (q1.isBottomEntity() || q2.isBottomEntity()) {
            return ruleSet();
        }
        if (q1.isTopEntity()) {
            return unsatisfiabilityTranslation((OWLProperty) q2);
        }
        if (q2.isTopEntity()) {
            return unsatisfiabilityTranslation((OWLProperty) q1);
        }
        return ruleSet(rule(negTr(q1, X, Y), tr(q2, X, Y, false)), rule(negTr(q2, X, Y), tr(q1, X, Y, false)));
    }

    /**
     * Translate the domain subsumption entailed by a given role subsubtion to
     * set of original rules according to <b>(s2)</b> of <b>Definition 9.</b>
     * of {@link
     * <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param q1 a DL-Lite<sub>R</sub> basic role <i>Q<sub>1</sub></i>.
     * @param q2 a DL-Lite<sub>R</sub> basic role <i>Q<sub>2</sub></i>.
     * @return <i>{tr<sup>d</sup>(&exist;Q<sub>2</sub>,
     * x)&larr;tr<sup>d</sup>(&exist;Q<sub>1</sub></i>, x)}</i>.
     */
    @Override
    Rule domainSubsumptionTranslation(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2) {
        return rule(existTr(q2, X), existTr(q1, X), negLiteral(negTrExist(q2, X), true));
    }

    /**
     * Translate the meaning of a domain meta-predicate to a set of double rules
     * according to <b>(e)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param p a role <i>P</i>.
     * @return <i>DP<sup>d</sup>(x)&larr;P<sup>d</sup>(x,_)</i>.
     */
    @Override
    Rule domainTranslation(OWLObjectProperty p) {
        return rule(atom(v.doubDomPred(p), X), atom(v.doubPred(p), X, var()));
    }

    /**
     * Translate the existential quantification of a given DL-Lite<sub>R</sub>
     * role to a double atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}).
     *
     * @param q a DL-Lite<sub>R</sub> concept <i>Q</i>.
     * @param x a variable <i>x</i>.
     * @return <i>DP<sup>d</sup>(x)</i> if <i>Q</i> is an atomic role<i>P</i>;
     * <br>
     * <i>RP<sup>d</sup>(x)</i> if <i>Q</i> in an inverse
     * role<i>P<sup>-</sup></i>.
     * @throws IllegalArgumentException if <i>R</i> isn't a DL-Lite<sub>R</sub>
     * role.
     */
    Atom existTr(OWLObjectPropertyExpression q, Variable x) {
        if (q instanceof OWLObjectProperty) {
            return atom(v.doubDomPred(q), x);
        } else if (q instanceof OWLObjectInverseOf) {
            return atom(v.doubRanPred(q.getNamedProperty()), x);
        } else {
            throw new IllegalArgumentException("q: must be a basic role");
        }
    }

    /**
     * Translate the range subsumption entailed by a given role subsumption to
     * set of double rules according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param q1 a DL-Lite<sub>R</sub> basic role <i>Q<sub>1</sub></i>.
     * @param q2 a DL-Lite<sub>R</sub> basic role <i>Q<sub>2</sub></i>.
     * @return <i>tr<sup>d</sup>(&exist;Q<sub>2</sub><sup>-</sup>,
     * x)&larr;tr<sup>d</sup>(&exist;Q<sub>1</sub><sup>-</sup>, x)</i>.
     */
    @Override
    Rule rangeSubsumptionTranslation(OWLObjectPropertyExpression q1, OWLObjectPropertyExpression q2) {
        return rule(existTr(q2, X), existTr(q1, X), negLiteral(negTrExist(q2, X), true));
    }

    /**
     * Translate the meaning of a range meta-predicate to a set of double rules
     * according to <b>(e)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param p a role <i>P</i>.
     * @return <i>RP<sup>d</sup>(x)&larr;P<sup>d</sup>(_,x)</i>.
     */
    @Override
    Rule rangeTranslation(OWLObjectProperty p) {
        return rule(atom(v.doubRanPred(p), X), atom(v.doubPred(p), var(), X));
    }

    /**
     * Translate a DL-Lite<sub>R</sub> concept subsumption axiom to a set of
     * double rules according to <b>(s1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param b1 the subsumed DL-Lite<sub>R</sub> basic concept
     * <i>B<sub>1</sub></i>.
     * @param b2 the subsuming DL-Lite<sub>R</sub> basic concept
     * <i>B<sub>2</sub></i>.
     * @return <i>tr<sup>d</sup>(B<sub>2</sub>, x)&larr;</i> if
     * <i>B<sub>1</sub></i> is the top concept <i>&top;</i><br>
     * {tr<sup>d</sup>(B<sub>2</sub>, x)&larr;tr<sup>d</sup>(B<sub>1</sub>,
     * x)}</i>.
     * @throws IllegalArgumentException if <i>B<sub>1</sub></i> or
     * <i>B<sub>2</sub></i> aren't DL-Lite<sub>R</sub> basic concepts.
     */
    @Override
    Set<Rule> subsumptionTranslation(OWLClassExpression b1, OWLClassExpression b2) {
        if (b1.isOWLThing()) {
            return ruleSet(rule(tr(b2, X), negLiteral(negTr(b2, X), true)));
        }
        return ruleSet(rule(tr(b2, X), tr(b1, X), negLiteral(negTr(b2, X), true)), rule(negTr(b1, X), negTr(b2, X)));
    }

    /**
     * Translate a DL-Lite<sub>R</sub> role subsumption axiom to a set of rules
     * according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param q1 the subsumed basic role <i>Q<sub>1</sub></i>.
     * @param q2 the subsuming basic role <i>Q<sub>2</sub></i>.
     * @return <i>{tr(Q<sub>2</sub>, x, y)&larr;tr(Q<sub>1</sub>, x, y)}</i>.
     * @throws IllegalArgumentException
     * <i>Q<sub>1</sub></i> or <i>Q<sub>2</sub></i> aren't a basic
     * DL-Lite<sub>R</sub> roles.
     */
    @Override
    Set<Rule> subsumptionTranslation(OWLPropertyExpression q1, OWLPropertyExpression q2) {
        if (q1.isBottomEntity() || q2.isTopEntity()) {
            return ruleSet();
        }
        if (q1.isTopEntity()) {
            return ruleSet(rule(tr(q2, X, Y), negLiteral(negTr(q2, X, Y))));
        }
        if (q2.isBottomEntity()) {
            return disjunctionTranslation(q1, q1);
        }
        return ruleSet(rule(tr(q2, X, Y), tr(q1, X, Y), negLiteral(negTr(q2, X, Y))),
                rule(negTr(q1, X, Y), negTr(q2, X, Y)));
    }

    /**
     * Translate a given DL-Lite<sub>R</sub> concept to a double atom (see
     * <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}).
     *
     * @param b a DL-Lite<sub>R</sub> concept <i>B</i>.
     * @param x a variable <i>x</i>.
     * @return <i>A<sup>d</sup>(x)</i> if <i>B</i> is an atomic concept
     * <i>A</i>; <br>
     * <i>DP<sup>d</sup>(x)</i> if <i>B</i> in an existential <i>&exist;P</i>;
     * <br>
     * <i>RP<sup>d</sup>(x)</i> if <i>B</i> is an existential
     * <i>&exist;P<sup>-</sup></i>.
     * @throws IllegalArgumentException if <i>B</i> isn't a basic
     * DL-Lite<sub>R</sub> concept.
     */
    Atom tr(OWLClassExpression b, Variable x) {
        return tr(b, x, true);
    }

    /**
     * Translate a given DL-Lite<sub>R</sub> role to a double atom (see
     * <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}).
     *
     * @param r a DL-Lite<sub>R</sub> role <i>R</i>.
     * @param x a variable <i>x</i>.
     * @param y a variable <i>y</i>.
     * @return <i>P<sup>d</sup>(x, y)</i> if <i>R</i> is an atomic role
     * <i>P</i>; <br>
     * <i>P<sup>d</sup>(y, x)</i> if <i>R</i> is an inverse role
     * <i>P<sup>-</sup></i>.
     */
    Atom tr(OWLPropertyExpression r, Variable x, Variable y) {
        return tr(r, x, y, true);
    }

    /**
     * Translate a DL-Lite<sub>R</sub> role unreflexivity according to
     * <b>(ir)</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param p the irreflexive role <i>P</i>.
     * @return <i>NP(x, x)</i>.
     */
    Rule unreflexivityTranslation(OWLObjectProperty p) {
        return rule(negTr(p, X, X));
    }

    /**
     * Translate a DL-Lite<sub>R</sub> concept unsatisfiability to a set of
     * rules according to <b>(i1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param a the unsatisfiable concept <i>A</i>.
     * @return <i>NA(x)</i>.
     */
    Set<Rule> unsatisfiabilityTranslation(OWLClass a) {
        return ruleSet(rule(negTr(a, X)));
    }

    /**
     * Translate a DL-Lite<sub>R</sub> role unsatisfiability according to
     * <b>(i2)</i> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} .
     *
     * @param p the unsatisfiable role <i>P</i>.
     * @return <i>NP(x, y)</i>.
     */
    Set<Rule> unsatisfiabilityTranslation(OWLProperty p) {
        return ruleSet(rule(negTr(p, X, Y)));
    }

}
