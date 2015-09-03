/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

/**
 * @author nunocosta
 */
public interface ProgramChangeListener {

	public void added(Rule rule);

	public void cleaned();

	public void removed(Rule rule);

	public void updated(Rule oldRule, Rule newRule);

}
