/**
 *
 */
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

import java.util.Set;

/**
 * Represents a <i>nonmonotonic logic program</i>. Is is a {@link Set set} of {@link Rule non-monotonic rules}.
 *
 * @author Nuno Costa
 */
public interface Program extends Set<Rule> {

	/**
	 * Adds a {@link ProgramChangeListener}, which listens all changes to this {@link Program rule-base}.
	 *
	 * @param listner
	 *            a {@link ProgramChangeListener}.
	 */
	void addListener(ProgramChangeListener listner);

	/**
	 * Removes a previously added {@link ProgramChangeListener}.
	 *
	 * @param listener
	 *            a {@link ProgramChangeListener}.
	 */
	void removeListener(ProgramChangeListener listener);

	/**
	 * Update a given {@link Program rule-base's} rule. If the given role isn't in this {@link Program rule-base}, nothing is done.
	 *
	 * @param oldRule
	 *            the rule that will be replaced.
	 * @param newRule
	 *            the new rule.
	 * @return true iff the the rule could be updated, i.e. this {@link Program rule-base} contained {@code oldRule}.
	 */
	boolean update(Rule oldRule, Rule newRule);

}