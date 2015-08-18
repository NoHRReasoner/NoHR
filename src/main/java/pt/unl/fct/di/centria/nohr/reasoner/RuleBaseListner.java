/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * @author nunocosta
 */
public interface RuleBaseListner {

	public void added(Rule rule);

	public void cleaned();

	public void removed(Rule rule);

	public void updated(Rule oldRule, Rule newRule);

}
