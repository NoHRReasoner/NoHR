
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

/**
 * A {@link Program program} listener.
 *
 * @author Nuno Costa
 */
public interface ProgramChangeListener {

	/**
	 * Called when a given rule was added to the program.
	 *
	 * @param rule
	 *            the rule that was added.
	 */
	public void added(Rule rule);

	/**
	 * Called when the program was cleared.
	 */
	public void cleared();

	/**
	 * Called when a given rule was removed from the program.
	 *
	 * @param rule
	 *            the rule that was added.
	 */
	public void removed(Rule rule);

	/**
	 * Called when a given rule was updated
	 *
	 * @param oldRule
	 *            the old rule.
	 * @param newRule
	 *            the new rule.
	 */
	public void updated(Rule oldRule, Rule newRule);

}
