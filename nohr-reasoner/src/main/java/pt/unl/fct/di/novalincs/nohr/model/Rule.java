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

import java.util.List;

/**
 * Represents a rule <i> H &larr; A<sub>1</sub>, ..., A<sub>m</sub>, <b>not</b> B<sub>1</sub>, ..., <b>not</b> B<sub>n</sub></i>, where <i>H</i>, <i>A
 * <sub>i</sub></i> and <i>B<sub>i</sub></i> are atoms and <i><b>not</b></i> the default negation operator. If <i>n=m=0</i> then the rule is called
 * fact. <i>H</i> is called head; <i>A<sub>1</sub>, ..., A <sub>m</sub>, <b>not</b> B <sub>1</sub>, ..., <b>not</b> B <sub>n</sub></i> body; <i>A
 * <sub>1</sub>, ..., A<sub>m</sub></i> positive body; and <i><b>not</b> B<sub>1</sub>, ..., <b>not</b> B <sub>n</sub></i> negative body.
 *
 * @see Atom
 * @see Literal
 * @author Nuno Costa
 */
public interface Rule extends ModelElement<Rule> {

	/**
	 * Returns the body of this literal.
	 *
	 * @return the list of literals at the body of this rule.
	 */
	public List<? extends Literal> getBody();

	/**
	 * Returns the head of this rule.
	 *
	 * @return the atom at the head of this rule.
	 */
	public Atom getHead();

	/**
	 * Returns the negative body of this rule.
	 *
	 * @return the list of literals at the negative body of this rule.
	 */
	public List<Literal> getNegativeBody();

	/**
	 * Returns the positive body of this rule.
	 *
	 * @return the list of atoms at the positive body of this rule.
	 */
	public List<Atom> getPositiveBody();

	/**
	 * Returns true iff the rule is a fact.
	 *
	 * @return {@code getBody().isEmpty()}
	 */
	public boolean isFact();
}
