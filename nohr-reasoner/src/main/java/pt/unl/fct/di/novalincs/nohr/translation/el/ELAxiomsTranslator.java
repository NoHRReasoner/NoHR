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
import static pt.unl.fct.di.novalincs.nohr.model.Model.ruleSet;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedExpressionException;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

/**
 * Provides EL<sub>&perp;</sub><sup>+</sup> axiom translation operations
 * according to {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>}.
 *
 * @author Nuno Costa
 */
abstract class ELAxiomsTranslator {

    /**
     * An variable "X".
     */
    static final Variable X = var("X");

    /**
     * An variable "Y".
     */
    static final Variable Y = var("Y");

    final Vocabulary v;

    ELAxiomsTranslator(Vocabulary v) {
        Objects.requireNonNull(v);
        this.v = v;
    }

    /**
     * Returns a specified literal array as a literal list.
     *
     * @param literals an array of literals.
     * @return {@code literals} as a {@link List}.
     */
    List<Literal> literalsList(Literal... literals) {
        return new ArrayList<Literal>(Arrays.asList(literals));
    }

    /**
     * Partially (according to some {@link ELAxiomsTranslator} implementing
     * class's criteria) translate a role chain subsumption axiom to a set of
     * rules according to <b>(r2)</b> of <b>Definition 13.</b> of {@link <a>A
     * Correct EL Oracle for NoHR (Technical Report)</a>} .
     *
     * @param chain a role chain
     * @param superRole the subsuming role.
     */
    abstract Set<Rule> subsumptionTranslation(List<OWLObjectPropertyExpression> chain, OWLObjectProperty superRole);

    /**
     * Partially (according to some {@link ELAxiomsTranslator} implementing
     * class's criteria) translate a concept subsumption axiom to a set of rules
     * according to <b>(t1)</b>, <b>(c1)</b>, <b>(i1)</b> or <b>(i2)</b> of
     * <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for NoHR
     * (Technical Report)</a>} .
     *
     * @param ce1 the subsumed concept expression
     * @param c2 the subsuming concept.
     */
    abstract Set<Rule> subsumptionTranslation(OWLClassExpression ce1, OWLClass c2);

    /**
     * Partially (according to some {@link ELAxiomsTranslator} implementing
     * class's criteria) translate a role subsumption axiom to a set of rules
     * according to <b>(r1)</b> of <b>Definition 13.</b> of {@link <a>A Correct
     * EL Oracle for NoHR (Technical Report)</a>} .
     *
     * @param r1 the subsumed role
     * @param r2 the subsuming role.
     */
    abstract Set<Rule> subsumptionTranslation(OWLProperty r1, OWLProperty r2);

    /**
     * Translate an role composition to a list of atoms according to <b>
     * Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>}. Corresponds to <i>tr(R<sub>1k</sub>, x, x<sub>k</sub>)</i>
     * and <i>tr(R<sub>1k</sub>, x, x<sub>k</sub>)<sup>d</sup></i>.
     *
     * @param chain a role composition R<sub>1</sub>&#x26AA;
     * ...&#x26AA;R<sub>k</sub>.
     * @param x a variable <i>x</i>.
     * @param xk a variable <i>x<sub>k</sub>.
     * @param doub specifies whether the returned axioms will be doubled (i.e.
     * with double meta-predicates functors).
     * @return <i>[R<sub>1</sub><sup>d</sup>(x, x<sub>1</sub>), ...,
     * R<sub>k</sub><sup>d</sup>(x<sub>k-1</sub>, x<sub>k</sub>)]</i>, if
     * {@code doub} is true; <br>
     * <i>[R<sub>1</sub>(x, x<sub>1</sub>), ..., R<sub>k</sub> (x<sub>k-1</sub>
     * , x<sub>k</sub>)]</i>, otherwise.
     */
    List<Atom> tr(List<OWLObjectPropertyExpression> chain, Variable x, Variable xk, boolean doub) {
        final int n = chain.size();
        final List<Atom> result = new ArrayList<Atom>(n);
        Variable xi = x;
        Variable xj = x;
        for (int i = 0; i < n; i++) {
            final OWLProperty pe = (OWLProperty) chain.get(i);
            xi = xj;
            xj = i == n - 1 ? xk : var("X" + i);
            result.add(tr(pe, xi, xj, doub));
        }
        return result;
    }

    /**
     * Translate an atomic concept to an atom according according to <b>
     * Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>}. Corresponds to <i>tr(A, x)</i> and <i>tr(A, x)<sup>d</sup>.
     *
     * @param c an atomic concept <i>A</i>.
     * @param x a variable <i>x</i>.
     * @param doub specifies whether the atom will be doubled (i.e. with an
     * double meta-predicate functor).
     * @return <i>A<sup>d</sup>(x)</i>, if {@code doub} is true; <br>
     * <i>A(x)</i>, otherwise.
     */
    Atom tr(OWLClass c, Variable x, boolean doub) {
        final Predicate pred = v.pred(c, doub);
        return atom(pred, x);
    }

    /**
     * Translate a concept to a list of atoms according according to <b>
     * Definition 12.</b> of {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>}. Corresponds to <i>tr(C, x)</i> and <i>tr(C, x)<sup>d</sup>.
     *
     * @param ce an concept <i>C</i>.
     * @param x a variable <i>x</i>.
     * @param doub specifies whether the atom will be doubled (i.e. with an
     * double meta-predicate functor).
     * @throws UnsupportedExpressionException if {@code ce} isn't a supported
     * expression - atomic concept, a conjunction or a existential
     * (SomeObjectFrom only).
     * @return <i>{A(x)}</i> if <i>C</i> is an atomic concept <i>A</i>; <br>
     * <i>{}</i> if <i>C</i> is the top concept &#x22A5; <br>
     * <i>tr(C<sub>1</sub>, x) &cup; tr(C<sub>2</sub>, x) if <i>C</i> is an
     * conjunction <i>C<sub>1</sub> &prod; <i>C<sub>2</sub></i>; </br>
     * <i>{R(x, x<sub>1</sub>)} &cup; tr(D, x<sub>1</sub>)</i> if <i>C</i> is an
     * existential <i>&exist;R.D</i> <br>
     * ; and the corresponding double translation if {@code doub} is true.
     */
    List<Literal> tr(OWLClassExpression ce, Variable x, boolean doub) {
        final List<Literal> result = new ArrayList<Literal>();
        if (ce.isOWLThing()) {
            return literalsList();
        } else if (ce instanceof OWLClass && !ce.isOWLThing()) {
            return literalsList(tr(ce.asOWLClass(), x, doub));
        } else if (ce instanceof OWLObjectIntersectionOf) {
            final Set<OWLClassExpression> ops = ce.asConjunctSet();
            for (final OWLClassExpression op : ops) {
                result.addAll(tr(op, x, doub));
            }
        } else if (ce instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce;
            final OWLProperty p = some.getProperty().asOWLObjectProperty();
            final OWLClassExpression filler = some.getFiller();
            result.add(tr(p, X, Y, doub));
            result.addAll(tr(filler, Y, doub));
        } else if (ce instanceof OWLDataSomeValuesFrom) {           
            final OWLDataSomeValuesFrom some = (OWLDataSomeValuesFrom) ce;
            final OWLProperty p = some.getProperty().asOWLDataProperty();
            final OWLDataRange filler = some.getFiller();

            result.add(tr(p, X, Y, doub));
           
            if (!filler.isTopDatatype()) {
              throw new UnsupportedExpressionException(ce);
            }
        } else {
            throw new UnsupportedExpressionException(ce);
        }
        return result;
    }

    /**
     * Translate an atomic role to an atom according to <b> Definition 12.</b>
     * of {@link <a>A Correct EL Oracle for NoHR (Technical Report)</a>}.
     * Corresponds to <i>tr(R, x, x<sub>1</sub>)</i> and <i>tr(R, x,
     * x<sub>1</sub>)<sup>d</sup></i>.
     *
     * @param r a role <i>R</i>.
     * @param x a variable <i>x</i>.
     * @param x1 a variable <i>x<sub>1</sub>.
     * @param doub specifies whether the returned axioms will be doubled (i.e.
     * with a double meta-predicate functor).
     * @throws IllegalArgumentException if {@code r} isn't an atomic role.
     * @return <i>R<sup>d</sup>(x, x<sub>1</sub>)</i>, if {@code doub} is true;
     * <br>
     * <i>R(x, x<sub>1</sub></i>), otherwise.
     */
    Atom tr(OWLProperty r, Variable x, Variable x1, boolean doub) {
        final Predicate pred = v.pred(r, doub);
        if (r instanceof OWLProperty) {
            return atom(pred, x, x1);
        } else {
            throw new IllegalArgumentException("r: must be an atomic (non data) rule");
        }
    }

    /**
     * Partially (depending on the concrete {@link ELAxiomsTranslator} used)
     * translate a concept assertion to a set of rules according to <b>(a1)</b>
     * of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for NoHR
     * (Technical Report)</a>}.
     *
     * @param assertion an assertion
     */
    abstract Set<Rule> translation(OWLClassAssertionAxiom assertion);

    /**
     * Partially (depending on the concrete {@link ELAxiomsTranslator} used)
     * translate a role assertion to a set of rules according to <b>(a2)</b> of
     * <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for NoHR
     * (Technical Report)</a>}.
     *
     * @param assertion an assertion
     */
    abstract Set<Rule> translation(OWLPropertyAssertionAxiom<?, ?> assertion);

    /**
     * Partially (depending on the concrete {@link ELAxiomsTranslator} used)
     * translate a concept subsumption axiom to a set of rules according to
     * <b>(t1)</b>, <b>(c1)</b>, <b>(i1)</b> or <b>(i2)</b> of <b>Definition
     * 13.</b>, and respective assumed simplifications, of {@link <a>A Correct
     * EL Oracle for NoHR (Technical Report)</a>} .
     *
     * @param axiom an axiom
     */
    Set<Rule> translation(OWLSubClassOfAxiom axiom) {
        final OWLClassExpression ce1 = axiom.getSubClass();
        final OWLClassExpression ce2 = axiom.getSuperClass();
        if (ce2.isAnonymous()) {
            return ruleSet();
        }
        for (final OWLClassExpression ci : ce1.asConjunctSet()) {
            if (ci.isOWLNothing()) {
                return ruleSet();
            }
        }
        if (ce2.isOWLThing()) {
            return ruleSet();
        }
        return subsumptionTranslation(ce1, (OWLClass) ce2);
    }

    /**
     * Partially (depending on the concrete {@link ELAxiomsTranslator} used)
     * translate a concept subsumption axiom to a set of rules according to
     * <b>(r1)</b> of <b>Definition 13.</b>, and respective assumed
     * simplifications, of {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>} .
     *
     * @param axiom an axiom
     */
    Set<Rule> translation(OWLSubPropertyAxiom<?> axiom) {
        final OWLProperty pe1 = (OWLProperty) axiom.getSubProperty();
        final OWLProperty pe2 = (OWLProperty) axiom.getSuperProperty();
        return subsumptionTranslation(pe1, pe2);
    }

    /**
     * Partially (depending on the concrete {@link ELAxiomsTranslator} used)
     * translate a role chain subsumption axiom to a set of rules according to
     * <b>(r2)</b> of <b>Definition 13.</b> of {@link <a>A Correct EL Oracle for
     * NoHR (Technical Report)</a>} .
     *
     * @param axiom an axiom
     */
    Set<Rule> translation(OWLSubPropertyChainOfAxiom axiom) {
        final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
        final OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
        return subsumptionTranslation(chain, (OWLObjectProperty) superProperty);
    }
}
