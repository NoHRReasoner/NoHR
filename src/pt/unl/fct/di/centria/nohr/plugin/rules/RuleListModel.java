/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import org.protege.editor.core.ui.list.MListSectionHeader;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.reasoner.RuleBase;

public class RuleListModel extends AbstractListModel<Object> {

    private static final MListSectionHeader HEADER = new MListSectionHeader() {

	@Override
	public boolean canAdd() {
	    return true;
	}

	@Override
	public String getName() {
	    return "Rules";
	}
    };

    private static final long serialVersionUID = -5766699966244129502L;

    private final RuleBase ruleBase;

    private final RuleEditor ruleEditor;

    private final List<Object> ruleItems;

    /**
     *
     */

    public RuleListModel(RuleEditor ruleEditor, RuleBase ruleBase) {
	super();
	this.ruleEditor = ruleEditor;
	this.ruleBase = ruleBase;
	ruleItems = new ArrayList<Object>(ruleBase.size());
	ruleItems.add(HEADER);
	for (final Rule rule : ruleBase.getRules())
	    ruleItems.add(new RuleListItem(ruleItems.size() - 1, this, rule));
    }

    boolean add(Rule rule) {
	final boolean added = ruleBase.add(rule);
	if (added) {
	    final int index = ruleItems.size();
	    ruleItems.add(new RuleListItem(index, this, rule));
	    super.fireIntervalAdded(this, index, index);
	}
	return added;
    }

    Rule edit(int index, Rule rule) {
	final Rule newRule = ruleEditor.show();
	boolean updated = false;
	if (newRule != null)
	    updated = ruleBase.update(rule, newRule);
	fireContentsChanged(this, index, index);
	return updated ? newRule : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    @Override
    public Object getElementAt(int index) {
	return ruleItems.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getSize()
     */
    @Override
    public int getSize() {
	return ruleItems.size();
    }

    boolean remove(int index, Rule rule) {
	final boolean removed = ruleBase.remove(rule);
	if (removed) {
	    ruleItems.remove(index);
	    super.fireIntervalRemoved(this, index, index);
	}
	return removed;
    }

}