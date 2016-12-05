/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public class AtomOperatorTermImpl implements AtomOperatorTerm {

    private final AtomOperator atomOperator;

    AtomOperatorTermImpl(AtomOperator atomOperator) {
        this.atomOperator = atomOperator;
    }

    @Override
    public AtomOperatorTerm accept(ModelVisitor visitor) {
        return new AtomOperatorTermImpl(this.atomOperator.accept(visitor));
    }

    @Override
    public AtomOperator getAtomOperator() {
        return this.atomOperator;
    }
    
    @Override
    public Literal getLiteral() {
        return this.atomOperator;
    }

    @Override
    public String asString() {
        return this.atomOperator.toString();
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return this.atomOperator.toString();
    }

}
