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
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * A {@link ModelVisitor} that replace each {@link Predicate} appearing at each
 * model element by the correspondent {@link MetaPredicate} of a specified
 * {@link PredicateType}. Each predicate is replaced by a meta-predicate of the
 * specified type, referring that predicate. Each meta-predicate is replaced by
 * a new meta-predicate of the specified type, referring the predicate that such
 * meta-predicate refer.
 *
 * @author Nuno Costa
 */
public class PredicateTypeVisitor extends DefaultModelVisitor {

    /**
     * The {@link PredicateType} of the meta-predicate by which all predicates
     * are replaced.
     */
    private final PredicateType predicateType;

    /**
     * Constructs a {@link ModelVisitor} that replace all the {@link Predicate}s
     * appearing at each model element by the correspondent
     * {@link MetaPredicate}s of a specified {@link PredicateType}.
     *
     * @param predicateType the {@link PredicateType} of the meta-predicate by
     * which all predicates will be replaced.
     */
    public PredicateTypeVisitor(PredicateType predicateType) {
        this.predicateType = predicateType;
    }

    /**
     * Returns the type of the meta-predicate by which all predicates are
     * replaced.
     *
     * @return the type of the meta-predicate by which all predicates are
     * replaced.
     */
    public PredicateType getType() {
        return predicateType;
    }

    @Override
    public Predicate visit(HybridPredicate pred) {
        return new MetaPredicateImpl(pred, predicateType);
    }

    @Override
    public Predicate visit(MetaPredicate pred) {
        return new MetaPredicateImpl(pred.getPredicate(), predicateType);
    }

}
