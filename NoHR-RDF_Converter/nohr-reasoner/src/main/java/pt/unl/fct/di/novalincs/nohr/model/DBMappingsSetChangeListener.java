
/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;


/**
 * A {@link DBMappingSet dBMappingSet} listener. It has no implementation so far, 
 * but all the functions are triggered so it can be used to add functionalities.
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
