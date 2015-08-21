/**
 *
 */
package pt.unl.fct.di.centria.nohr.rulebase;

import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * @author nunocosta
 */
public interface RuleBaseListener {

	public void added(Rule rule);

	public void cleaned();

	public void removed(Rule rule);

	public void updated(Rule oldRule, Rule newRule);

}
