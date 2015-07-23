/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import pt.unl.fct.di.centria.nohr.plugin.rules.DisposableRuleBase;

/**
 * @author nunocosta
 *
 */
public abstract class AbstractHybridViewComponent extends
AbstractOWLViewComponent {

    /**
     *
     */
    private static final long serialVersionUID = -2850791395194206722L;

    /**
     *
     */
    public AbstractHybridViewComponent() {
	super();
    }

    public DisposableRuleBase getRuleBase() {
	DisposableRuleBase ruleBase = getOWLModelManager().get("RuleBase");
	if (ruleBase == null) {
	    ruleBase = new DisposableRuleBase();
	    getOWLModelManager().put("RuleBase", ruleBase);
	}
	ruleBase.getRules();
	return ruleBase;
    }

}
