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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * Implementation of {@link Answer}.
 *
 * @author Nuno Costa
 */
class AnswerImpl implements Answer {

    /**
     * The query to which the answer applies.
     */
    private final Query query;

    /**
     * The truth value of the answer.
     */
    private final TruthValue truthValue;

    /**
     * The list of terms to which each query's variable is mapped.
     */
    private final List<Term> values;

    /**
     * Constructs an answer to a specified query, with the specified truth value
     * and substitution.
     *
     * @param query the query to which the answer applies.
     * @param truthValue the truth value of the answer.
     * @param values the list of terms to which each query's variable is mapped.
     */
    AnswerImpl(Query query, TruthValue truthValue, List<Term> values) {
        Objects.requireNonNull(query);
        Objects.requireNonNull(truthValue);
        Objects.requireNonNull(values);

        this.query = query;
        this.truthValue = truthValue;
        this.values = values;
    }

    @Override
    public String accept(FormatVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Answer accept(ModelVisitor visitor) {
        final List<Term> vals = new ArrayList<>();

        for (final Term val : values) {
            vals.add(val.accept(visitor));
        }

        return new AnswerImpl(query, truthValue, vals);
    }

    @Override
    public List<Literal> apply() {
        final Map<Variable, Term> substitution = new HashMap<>();
     
        for (final Entry<Variable, Integer> entry : query.getIndex().entrySet()) {
            substitution.put(entry.getKey(), values.get(entry.getValue()));
        }
        
        final List<Literal> literals = new LinkedList<>();
        
        for (final Literal literal : query.getLiterals()) {
            literals.add(literal.apply(substitution));
        }
        
        return literals;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AnswerImpl)) {
            return false;
        }
        final AnswerImpl other = (AnswerImpl) obj;
        if (truthValue != other.truthValue) {
            return false;
        }
        if (!values.equals(other.values)) {
            return false;
        }
        if (!query.getIndex().equals(other.query.getIndex())) {
            return false;
        }
        return true;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public TruthValue getValuation() {
        return truthValue;
    }

    @Override
    public Term getValue(Variable var) {
        return values.get(query.getIndex().get(var));
    }

    @Override
    public List<Term> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + truthValue.hashCode();
        result = prime * result + values.hashCode();
        result = prime * result + query.getIndex().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return StringUtils.concat(",", apply());
    }

}
