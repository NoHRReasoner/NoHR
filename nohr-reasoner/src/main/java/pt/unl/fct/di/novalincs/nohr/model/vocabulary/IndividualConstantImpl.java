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

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Term;

/**
 * Ontology individual {@link Constant} implementation.
 *
 * @see Term
 * @author Nuno Costa
 */
class IndividualConstantImpl implements HybridConstant {

    /**
     * The ontology individual.
     */
    private final OWLIndividual individual;

    /**
     * The preferred (user-friendly) concrete representation of the concept
     * represented by this predicate. Can change over the time.
     */
    private String label;

    /**
     * Constructs an ontology individual constant with a specified individual.
     *
     * @param individual the ontology individual
     */
    IndividualConstantImpl(OWLIndividual individual) {
        Objects.requireNonNull(individual);
        this.individual = individual;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Constant accept(ModelVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public OWLIndividual asIndividual() {
        return individual;
    }

    @Override
    public OWLLiteral asLiteral() {
        throw new ClassCastException();
    }

    @Override
    public Number asNumber() {
        throw new ClassCastException();
    }

    @Override
    public String asString() {
        return individual.asOWLNamedIndividual().getIRI().toQuotedString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
  
        final IndividualConstantImpl other = (IndividualConstantImpl) obj;
        
        if (individual == null) {
            if (other.individual != null) {
                return false;
            }
        } else if (!individual.toStringID().equals(other.individual.toStringID())) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + individual.toStringID().hashCode();
        
        return result;
    }

    @Override
    public boolean isIndividual() {
        return true;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public String toString() {
        final String fragment = individual.asOWLNamedIndividual().getIRI().toURI().getFragment();

        if (fragment != null) {
            return fragment;
        } else {
            return individual.asOWLNamedIndividual().getIRI().toString();
        }
    }
}
