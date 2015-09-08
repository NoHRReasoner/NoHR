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
import java.util.Map;

/**
 * Represents an rule literal. Can be a positive literal, i.e. an atom <i>P(t <sub>1</sub>, ..., t<sub>n</sub>)</i>, or a negative literal, i.e. an
 * negated atom <i><b>not</b> P(t<sub>1</sub>, ..., t<sub>n</sub>)</i>, where <i>P</i> is a predicate, <i>t<sub>i</sub></i>, with <i>1&le;i&le;n</i> ,
 * terms, and <i><b>not</b></i> the default negation operator. <i>P</i> is called functor and <i>t<sub>1</sub>, ..., t<sub>n</sub></i> arguments.
 * <i>n</i> is the literal's arity.
 *
 * @see Predicate
 * @see Term
 * @see Atom
 * @author Nuno Costa
 */
public interface Literal extends ModelElement<Literal> {

	/**
	 * Apply a substitution to this literal.
	 *
	 * @param substitution
	 *            the substitution, i.e. the mapping between variables and terms by which each variable must be replaced.
	 * @return this literal with each variable occurrence replaced by the terms to which the variable is mapped in {@code substitution}.
	 */
	public Literal apply(Map<Variable, Term> substitution);

	/**
	 * Apply a singleton substitution to this literal.
	 *
	 * @param variable
	 *            the variable that must be replaced in this literal.
	 * @param term
	 *            the term by which the variable {@code variable} must be replaced.
	 * @return this literal with each variable occurrence of {@code variable} replaced by {@code term}.
	 */
	public Literal apply(Variable variable, Term term);

	/**
	 * Returns the arguments list of this literal.
	 *
	 * @return the arguments list of this literal.
	 */
	public List<Term> getArguments();

	/**
	 * Returns the arity of this literal.
	 *
	 * @return the arity of this literal.
	 */
	public int getArity();

	/**
	 * Returns the atom corresponding to this literal. If it is positive returns itself as an atom; if it is negative returns the atom that is
	 * negated.
	 *
	 * @return the atom corresponding to this literal.
	 */

	public Atom getAtom();

	/**
	 * Returns the functor predicate of this literal.
	 *
	 * @return the functor predicate of this literal.
	 */
	public Predicate getFunctor();

	/**
	 * Returns the variables occurring in this literal, in the same order that they appear, and without repetitions.
	 *
	 * @return the variables occurring it this literal, in the same order that they appear, without repetitions.
	 */
	public List<Variable> getVariables();

	/**
	 * Returns true iff no variable occur in this literal.
	 *
	 * @return true iff no variable occur in this literal.
	 */
	public boolean isGrounded();

}
