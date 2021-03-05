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
import java.util.Objects;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * Implementation of {@link MetaPredicate}}.
 *
 * @author Nuno Costa
 */
class MetaPredicateImpl implements MetaPredicate {

    /**
     * The predicate that this meta-predicate refers.
     */
    final Predicate predicate;

    /**
     * The type of this meta-predicate.
     */
    final PredicateType type;

    /**
     * Constructs a meta-predicate referring a specified predicate with a
     * specified type.
     *
     * @param predicate the predicate referred by the meta-predicate. Shouldn't
     * be a meta-predicate.
     * @param type the type of the meta-predicate. Souldn't be an quantification
     * type (see {@link PredicateType#isQuantification()}) if {@code predicate}
     * doen't represent a role.
     * @throws IllegalArgumentException if {@code predicate} is a
     * meta-predicate; or {@code type} is an quantification type and
     * {@code predicate} doesn't represent a role.
     */
    MetaPredicateImpl(Predicate predicate, PredicateType type) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(type);
        if (predicate instanceof MetaPredicate) {
            throw new IllegalArgumentException("predicate: shouldn't be a meta-predicate");
        }
        if (type.isQuantification() && predicate instanceof HybridPredicate && !((HybridPredicate) predicate).isRole()) {
            throw new IllegalArgumentException("type: can't be the quantification type " + type.name());
        }
        this.predicate = predicate;
        this.type = type;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public MetaPredicate accept(ModelVisitor visitor) {
        return new MetaPredicateImpl(predicate.accept(visitor), type);
    }

    @Override
    public String asString() {
        return type.marker() + predicate.asString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MetaPredicateImpl)) {
            return false;
        }
        final MetaPredicateImpl other = (MetaPredicateImpl) obj;
        if (type != other.type) {
            return false;
        }
        if (!predicate.equals(other.predicate)) {
            return false;
        }
        return true;
    }

    @Override
    public int getArity() {
        if (type.isQuantification()) {
            return 1;
        }
        return predicate.getArity();
    }

    @Override
    public Predicate getPredicate() {
        return predicate;
    }

    @Override
    public String getSignature() {
        return asString() + "/" + getArity();
    }

    @Override
    public PredicateType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + type.hashCode();
        result = prime * result + predicate.hashCode();
        return result;
    }

    @Override
    public boolean hasType(PredicateType type) {
        return type == this.type;
    }

    @Override
    public String toString() {
        return asString();
    }

}
