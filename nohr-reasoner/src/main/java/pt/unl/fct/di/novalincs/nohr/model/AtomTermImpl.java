/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

public class AtomTermImpl implements AtomTerm {

    private final Atom atom;

    AtomTermImpl(Atom atom) {
        this.atom = atom;
    }

    @Override
    public AtomTerm accept(ModelVisitor visitor) {
        return new AtomTermImpl(this.atom.accept(visitor));
    }

    @Override
    public String asString() {
        return this.atom.toString();
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Atom getAtom() {
        return this.atom;
    }
    
    @Override
    public Literal getLiteral()  {
        return this.getAtom();
    }

    @Override
    public String toString() {
        return this.atom.toString();
    }

}
