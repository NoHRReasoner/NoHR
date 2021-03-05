/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.ArrayList;
import java.util.List;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.HybridPredicate;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateTypeVisitor;

/**
 * Provides an auxiliary method that double a given rule according to
 * <b>Definition 3.1</b> of
 * {@link <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i>Query-driven
 * Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
 *
 * @author Nuno Costa
 */
class ProgramDoubling {

    /**
     * Double a given rule according to <b>Definition 3.1</b> of {@link
     * <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i> Query-driven
     * Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
     *
     * @param rule a rule.
     * @return the pair of rules corresponding to the doubling of {@code rule}.
     */
    static List<Rule> doubleRule(Rule rule) {
        final List<Rule> result = new ArrayList<>(2);
        final ModelVisitor originalEncoder = new PredicateTypeVisitor(PredicateType.ORIGINAL);
        final ModelVisitor doubleEncoder = new PredicateTypeVisitor(PredicateType.DOUBLE);
        final Atom head = rule.getHead();
        final List<Atom> positiveBody = rule.getPositiveBody();
        final List<Literal> negativeBody = rule.getNegativeBody();
        final Literal[] originalBody = new Literal[rule.getBody().size()];
        final Predicate headFunctor = head.getFunctor();
        final boolean isDL;

        if (headFunctor instanceof HybridPredicate) {
            final HybridPredicate hybridHeadFunctor = (HybridPredicate) headFunctor;

            isDL = hybridHeadFunctor.isConcept() || hybridHeadFunctor.isRole();
        } else {
            isDL = false;
        }

        final Literal[] doubleBody = new Literal[rule.getBody().size() + (isDL ? 1 : 0)];

        int i = 0;

        for (final Literal literal : positiveBody) {
            originalBody[i] = literal.accept(originalEncoder);
            doubleBody[i] = literal.accept(doubleEncoder);
            i++;
        }

        for (final Literal literal : negativeBody) {
            originalBody[i] = literal.accept(doubleEncoder);
            doubleBody[i] = literal.accept(originalEncoder);
            i++;
        }

        if (isDL) {
            final ModelVisitor negativeEncoder = new PredicateTypeVisitor(PredicateType.NEGATIVE);

            doubleBody[i] = Model.negLiteral(head.accept(negativeEncoder));
        }

        result.add(Model.rule(head.accept(originalEncoder), originalBody));
        result.add(Model.rule(head.accept(doubleEncoder), doubleBody));

        return result;
    }
}
