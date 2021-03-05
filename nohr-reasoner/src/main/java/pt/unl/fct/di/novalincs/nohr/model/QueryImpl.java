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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateTypeVisitor;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * Implementation of {@link Query}.
 *
 * @author Nuno Costa
 */
class QueryImpl implements Query {

	/** The query's literals */
	private final List<Literal> literals;

	/** The query's free variables */
	private final List<Variable> variables;

	private final Map<Variable, Integer> index;

	/**
	 * Construct a query with a specified list of literals and a specified list of free variables.
	 *
	 * @param literals
	 *            the query's literals.
	 * @param variables
	 *            the query's free variables. All the those variables must appear in {@code literals}.
	 * @throws IllegalArgumentException
	 *             if {@code variables} contains some variable that doesn't appear in {@code literals}.
	 */
	QueryImpl(List<Literal> literals, List<Variable> variables) {
		Objects.requireNonNull(literals);
		Objects.requireNonNull(variables);
		final Set<Variable> vars = new HashSet<Variable>();
		for (final Literal literal : literals)
			vars.addAll(literal.getVariables());
		for (final Variable var : variables)
			if (!vars.contains(var))
				throw new IllegalArgumentException("variables " + var + "doesn't appear in" + literals);
		this.literals = literals;
		this.variables = variables;
		index = new HashMap<Variable, Integer>();
		int i = 0;
		for (final Variable var : variables)
			index.put(var, i++);
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Query accept(ModelVisitor visitor) {
		final List<Literal> lits = new LinkedList<Literal>();
		final List<Variable> vars = new LinkedList<Variable>();
		for (final Literal literal : literals)
			lits.add(literal.accept(visitor));
		for (final Variable var : variables)
			vars.add(var.accept(visitor));
		return new QueryImpl(lits, vars);
	}

	@Override
	public Query apply(List<Term> termList) {
		if (termList.size() != variables.size())
			throw new IllegalArgumentException(
					"termList size must have the same size that the number of variables of the query");
		final Map<Variable, Term> map = new HashMap<Variable, Term>();
		final Iterator<Variable> varsIt = variables.iterator();
		final Iterator<Term> listIt = termList.iterator();
		while (varsIt.hasNext() && listIt.hasNext())
			map.put(varsIt.next(), listIt.next());
		final List<Literal> lits = new LinkedList<Literal>();
		for (final Literal literal : literals)
			lits.add(literal.apply(map));
		return new QueryImpl(lits, new LinkedList<Variable>());
	}

	private Query encode(PredicateType predicateType) {
		final ModelVisitor encoder = new PredicateTypeVisitor(predicateType);
		return accept(encoder);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QueryImpl))
			return false;
		final QueryImpl other = (QueryImpl) obj;
		if (!variables.equals(other.variables))
			return false;
		if (!literals.equals(other.literals))
			return false;
		return true;
	}

	@Override
	public Query getDouble() {
		return encode(PredicateType.DOUBLE);
	}

	@Override
	public Map<Variable, Integer> getIndex() {
		return index;
	}

	@Override
	public List<Literal> getLiterals() {
		return literals;
	}

	@Override
	public Query getOriginal() {
		return encode(PredicateType.ORIGINAL);
	}

	@Override
	public List<Variable> getVariables() {
		return variables;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + variables.hashCode();
		result = prime * result + literals.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final String vars = StringUtils.concat(",", variables);
		return "q(" + vars + "):-" + StringUtils.concat(",", literals);
	}
}
