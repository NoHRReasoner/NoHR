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

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * Implementation of a {@link Predicate} representing a role.
 *
 * @author Nuno Costa
 */
class RolePredicateImpl implements HybridPredicate {

    /**
     * The role represented by this predicate.
     */
    private final OWLProperty role;

    /**
     * Constructs a predicate representing a specified role.
     *
     * @param role the role represented by the predicate. Must have a IRI
     * fragment.
     * @throws IllegalArgumentException if {@code role} hasn't a IRI fragment.
     */
    RolePredicateImpl(OWLProperty role) {
        Objects.requireNonNull(role);

        this.role = role;
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
        return role;
    }

    @Override
    public String asString() {
        return role.getIRI().toQuotedString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RolePredicateImpl)) {
            return false;
        }
        final RolePredicateImpl other = (RolePredicateImpl) obj;
        if (!role.getIRI().equals(other.role.getIRI())) {
            return false;
        }
        return true;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see pt.unl.fct.di.centria.nohr.model.predicates.Predicate#getArity()
     */
    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public String getSignature() {
        return asString() + "/" + getArity();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + role.getIRI().hashCode();
        return result;
    }

    @Override
    public boolean isConcept() {
        return false;
    }

    @Override
    public boolean isRole() {
        return true;
    }

    @Override
    public String toString() {
        final String fragment = role.getIRI().toURI().getFragment();

        if (fragment != null) {
            return fragment;
        } else {
            return role.getIRI().toQuotedString();
        }
    }

}
