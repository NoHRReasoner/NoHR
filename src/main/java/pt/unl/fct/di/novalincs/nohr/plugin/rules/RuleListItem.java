/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.rules;

import org.protege.editor.core.ui.list.MListItem;

import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * A {@link final Rule rule} {@link final MListItem item} of a certain {@link RulesList}.**
 *
 * @author Nuno Costa
 */
class RuleListItem implements MListItem {

	/** The index of the item in the {@link RuleListModel} that it belongs. */
	private final int index;

	/** The {@link RuleListModel} to which the item belongs. */
	private final RuleListModel model;

	private Rule rule;

	/**
	 * @param model
	 *            the {@link RuleListModel} to which the item belongs.
	 * @param the
	 *            index of the item in the {@link RuleListModel} that it belongs .
	 */
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
		return "A nonmonotonic rule.";
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

	@Override
	public String toString() {
		return rule.toString();
	}
}