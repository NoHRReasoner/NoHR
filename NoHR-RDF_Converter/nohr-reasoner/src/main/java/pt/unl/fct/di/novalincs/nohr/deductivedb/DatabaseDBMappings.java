package pt.unl.fct.di.novalincs.nohr.deductivedb;


import java.util.Collection;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;

/**
 * Represents mappings of a logic program (i.e. set of {@link DBMapping dbMapping}) loaded in a certain {@link DeductiveDatabase}. All changes to a dbMappings are reflected
 * in the later queries to the {@link DeductiveDatabase} where the set of database mappings is loaded.
 *
 * @author Vedran Kasalica
 */
public interface DatabaseDBMappings {

	/**
	 * Adds a given {@link DBMapping dbMapping} to this {@link DatabaseMappings program}.
	 *
	 * @param dbMapping
	 *            the database mapping to be added.
	 */
	void add(DBMapping dbMapping);

	/**
	 * Adds all database mappings of given {@link Collection collection} of {@link DBMapping dbMappings} to this {@link DatabaseMappings program}.
	 *
	 * @param dbMappings
	 *            the collection of database mappings to be added.
	 */
	void addAll(Collection<DBMapping> dbMappings);

	/**
	 * Removes all the {@link DBMapping dbMapping} from this {@link DatabaseMappings program}.
	 */
	void clear();

	/**
	 * Returns the {@link DeductiveDatabase} where this {@link DatabaseMappings program} is loaded.
	 *
	 * @return the {@link DeductiveDatabase} where this {@link DatabaseMappings program} is loaded.
	 */
	DeductiveDatabase getDeductiveDatabase();

	/**
	 * Removes a given {@link DBMapping dbMapping} from this {@link DatabaseMappings program}.
	 *
	 * @param dbMapping
	 *            the database mapping to be removed.
	 */
	void remove(DBMapping dbMapping);

	/**
	 * Removes all database mappings of given {@link Collection collection} of {@link DBMapping dbMappings} from this {@link DatabaseMappings program}.
	 *
	 * @param dbMappings
	 *            the collection of database mappings to be removed.
	 */
	void removeAll(Collection<DBMapping> dbMappings);
}
