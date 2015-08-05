/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import org.protege.editor.core.ui.list.MListItem;

import pt.unl.fct.di.centria.nohr.model.Rule;

class RuleListItem implements MListItem {

    private final int index;

    private final RuleListModel model;

    private Rule rule;

    RuleListItem(int index, RuleListModel model, Rule rule) {
	this.index = index;
	this.model = model;
	this.rule = rule;
    }

    public Rule getRule() {
	return rule;
    }

    @Override
    public String getTooltip() {
	return "rule";
    }

    @Override
    public boolean handleDelete() {
	return model.remove(index, rule);
    }

    @Override
    public void handleEdit() {
	final Rule newRule = model.edit(index, rule);
	if (newRule != null)
	    rule = newRule;
    }

    @Override
    public boolean isDeleteable() {
	return true;
    }

    @Override
    public boolean isEditable() {
	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return rule.toString();
    }
}