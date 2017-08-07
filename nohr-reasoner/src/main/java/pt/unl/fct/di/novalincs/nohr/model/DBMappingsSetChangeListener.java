
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
 * A {@link DBMappingSet dBMappingSet} listener.
 *
 * @author Vedran Kasalica
 */
public interface DBMappingsSetChangeListener {

	/**
	 * Called when a given dBMapping was added to the DB Mapping Set.
	 *
	 * @param dBMapping
	 *            the dBMapping that was added.
	 */
	public void added(DBMapping dBMapping);

	/**
	 * Called when the DB Mapping Set was cleared.
	 */
	public void cleared();

	/**
	 * Called when a given DBMapping was removed from the DB Mapping Set.
	 *
	 * @param dBMapping
	 *            the dBMapping that was removed.
	 */
	public void removed(DBMapping dBMapping);

	/**
	 * Called when a given dBMapping was updated
	 *
	 * @param oldDBMapping
	 *            the old dBMapping.
	 * @param newDBMapping
	 *            the new dBMapping.
	 */
	public void updated(DBMapping oldDBMapping, DBMapping newDBMapping);

}
