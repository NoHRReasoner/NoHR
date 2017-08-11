/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;


import java.util.Set;

/**
 * Represents a <i>set of all database mappings</i>. It is a {@link Set set} of {@link DBMapping dbMappings}.
 *
 * @author Vedran Kasalica
 */
public interface DBMappingSet extends Set<DBMapping> {

	/**
	 * Adds a {@link DBMappingsSetChangeListener}, which listens all changes to this {@link DBMappingSet set of mappings}.
	 *
	 * @param listner
	 *            a {@link DBMappingsSetChangeListener}.
	 */
	void addListener(DBMappingsSetChangeListener listner);

	/**
	 * Removes a previously added {@link DBMappingsSetChangeListener}.
	 *
	 * @param listener
	 *            a {@link DBMappingsSetChangeListener}.
	 */
	void removeListener(DBMappingsSetChangeListener listener);

	/**
	 * Update a given {@link DBMapping database mapping} mapping. If the given mapping isn't in this {@link DBMappingSet set of mappings}, nothing is done.
	 *
	 */
	boolean update(DBMapping oldDBMapping, DBMapping newDBMapping);


}