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
import java.util.Objects;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * Implementation of {@link Predicate}.
 *
 * @author Nuno Costa
 */
class RulePredicateImpl implements HybridPredicate {

    /**
     * The arity of this predicate
     */
    private final int arity;

    /**
     * The symbol that represents this predicate
     */
    private final String symbol;

    /**
     * Constructs a predicate represented by a specified symbol with a specified
     * arity.
     *
     * @param symbol the symbol that represents this predicate. Must be an
     * non-empty string.
     * @param arity the arity of this predicate. Must be a positive integer.
     * @throws IllegalArgumentException if {@code symbol} is an empty string or
     * {@code arity} is negative.
     */
    RulePredicateImpl(String symbol, int arity) {
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(arity);

        if (symbol.length() <= 0) {
            throw new IllegalArgumentException("symbol: can't be an empty string");
        }

        if (arity < 0) {
            throw new IllegalArgumentException("arity: must be positive");
        }

        this.symbol = symbol;
        this.arity = arity;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Predicate accept(ModelVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public OWLClass asConcept() {
        throw new ClassCastException();
    }

    @Override
    public OWLProperty asRole() {
        throw new ClassCastException();
    }

    @Override
    public String asString() {
        return symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RulePredicateImpl)) {
            return false;
        }

        final RulePredicateImpl other = (RulePredicateImpl) obj;

        if (arity != other.arity) {
            return false;
        }

        return symbol.equals(other.symbol);
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public String getSignature() {
        return symbol + "/" + arity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + arity;
        result = prime * result + symbol.hashCode();

        return result;
    }

    @Override
    public boolean isConcept() {
        return false;
    }

    @Override
    public boolean isRole() {
        return false;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
