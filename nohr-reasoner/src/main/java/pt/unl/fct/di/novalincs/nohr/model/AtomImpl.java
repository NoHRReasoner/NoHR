package pt.unl.fct.di.novalincs.nohr.model;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * Implementation of {@link Atom}
 *
 * @author Nuno Costa
 */
class AtomImpl implements Atom {

    /**
     * The list of arguments.
     */
    private final List<Term> arguments;

    /**
     * The functor predicate.
     */
    private final Predicate predicate;

    /**
     * Constructs an atom with a specified predicate as functor and list of
     * terms as arguments.
     *
     * @param predicate the functor predicate.
     * @param arguments the arguments list. Can be null, in which case the atom
     * is treated has having a empty arguments list.
     * @throws IllegalArgumentException if the size of {@code arguments} is
     * different from the predicate arity.
     */
    AtomImpl(Predicate predicate, List<Term> arguments) {
        Objects.requireNonNull(predicate);
        if (arguments == null && predicate.getArity() > 0) {
            throw new IllegalArgumentException("arguments must have a size equal to the predicate arity");
        }
        if (arguments != null) {
            if (predicate.getArity() != arguments.size()) {
                throw new IllegalArgumentException("arguments must have a size equal to the predicate arity");
            }
        }
        this.predicate = predicate;
        this.arguments = arguments;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Atom accept(ModelVisitor visitor) {
        final Predicate pred = predicate.accept(visitor);
        final List<Term> args = new LinkedList<>();

        if (arguments == null) {
            return new AtomImpl(pred, null);
        }

        for (final Term term : arguments) {
            args.add(term.accept(visitor));
        }

        return new AtomImpl(pred, args);
    }

    @Override
    public Atom apply(Map<Variable, Term> substitution) {
        final List<Term> args = new LinkedList<>(arguments);
        final ListIterator<Term> argsIt = args.listIterator();

        while (argsIt.hasNext()) {
            final Term t = argsIt.next();

            if (substitution.containsKey(t)) {
                argsIt.remove();
                argsIt.add(substitution.get(t));
            }
        }

        return new AtomImpl(predicate, args);
    }

    @Override
    public Atom apply(Variable var, Term term) {
        final List<Term> args = new LinkedList<>(arguments);
        final ListIterator<Term> argsIt = args.listIterator();

        while (argsIt.hasNext()) {
            final Term t = argsIt.next();

            if (t.equals(var)) {
                argsIt.remove();
                argsIt.add(term);
            }
        }

        return new AtomImpl(predicate, args);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AtomImpl)) {
            return false;
        }
        final AtomImpl other = (AtomImpl) obj;
        if (!predicate.equals(other.predicate)) {
            return false;
        }
        if (arguments == null) {
            if (other.arguments != null) {
                return false;
            }
        } else if (!arguments.equals(other.arguments)) {
            return false;
        }
        return true;
    }

    @Override
    public List<Term> getArguments() {
        return arguments;
    }

    @Override
    public int getArity() {
        return predicate.getArity();
    }

    @Override
    public Atom getAtom() {
        return this;
    }

    @Override
    public Predicate getFunctor() {
        return predicate;
    }

    @Override
    public List<Variable> getVariables() {
        final List<Variable> result = new LinkedList<Variable>();
        for (final Term arg : arguments) {
            if (arg instanceof Variable && !result.contains(arg)) {
                result.add((Variable) arg);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + predicate.hashCode();
        result = prime * result + (arguments == null ? 0 : arguments.hashCode());
        return result;
    }

    @Override
    public boolean isGrounded() {
        for (final Term term : arguments) {
            if (term instanceof Variable) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return predicate + (getArity() > 0 ? "(" + StringUtils.concat(",", arguments) + ")" : "()");
    }

}
