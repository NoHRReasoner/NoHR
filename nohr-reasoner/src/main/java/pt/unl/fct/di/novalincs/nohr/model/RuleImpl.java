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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.utils.StringUtils;

/**
 * Implementation of {@link Rule}.
 *
 * @author Nuno Costa
 */
class RuleImpl implements Rule {

	/** The literals at the body of the rule. */
	private final List<? extends Literal> body;

	/** The head of the rule */
	private final Atom head;

	/**
	 * Constructs a rule with a specified atom as head a literals list has body.
	 *
	 * @param head
	 *            the head of the rule.
	 * @param body
	 *            the list of literals at the body of the rule. Can be null, in which case the rule is a fact.
	 */
	RuleImpl(Atom head, List<? extends Literal> body) {
		Objects.requireNonNull(head);
		this.head = head;
		this.body = body;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Rule accept(ModelVisitor visitor) {
		final List<Literal> body = new LinkedList<Literal>();
		for (final Literal literal : this.body)
			body.add(literal.accept(visitor));
		return new RuleImpl(head.accept(visitor), body);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RuleImpl))
			return false;
		final RuleImpl other = (RuleImpl) obj;
		if (!head.equals(other.head))
			return false;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}

	@Override
	public List<? extends Literal> getBody() {
		if (body == null)
			return Collections.<Literal> emptyList();
		return body;
	}

	@Override
	public Atom getHead() {
		return head;
	}

	@Override
	public List<Literal> getNegativeBody() {
		if (body == null)
			return Collections.<Literal> emptyList();
		final List<Literal> result = new ArrayList<Literal>(body.size());
		for (final Literal literal : body)
			if (literal instanceof NegativeLiteral)
				result.add(literal);
		return result;
	}

	@Override
	public List<Atom> getPositiveBody() {
		if (body == null)
			return Collections.<Atom> emptyList();
		final List<Atom> result = new ArrayList<Atom>(body.size());
		for (final Literal literal : body)
			if (literal instanceof Atom)
				result.add((Atom) literal);
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + head.hashCode();
		result = prime * result + (body == null ? 0 : body.hashCode());
		return result;
	}

	@Override
	public boolean isFact() {
		return body.isEmpty();
	}

	@Override
	public String toString() {
		return head + (isFact() ? "" : " :- ") + StringUtils.concat(", ", body);
	}
}
