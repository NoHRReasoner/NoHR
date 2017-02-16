/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.rules;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import org.protege.editor.core.ui.list.MListItem;
import pt.unl.fct.di.novalincs.nohr.deductivedb.NoHRFormatVisitor;

import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * A {@link Rule rule} {@link MListItem item} of a certain {@link RulesList}.**
 *
 * @author Nuno Costa
 */
class RuleListItem implements MListItem {

    /**
     * The index of the item in the {@link RuleListModel} that it belongs.
     */
    private int index;

    /**
     * The {@link RuleListModel} to which the item belongs.
     */
    private final RuleListModel model;

    private Rule rule;

    /**
     * @param model the {@link RuleListModel} to which the item belongs.
     * @param index the index of the item in the {@link RuleListModel} that it
     * belongs .
     */
    RuleListItem(int index, RuleListModel model, Rule rule) {
        this.index = index;
        this.model = model;
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getTooltip() {
        return rule.accept(new NoHRFormatVisitor(true)); //"A nonmonotonic rule.";
    }

    @Override
    public boolean handleDelete() {
        return model.remove(index, rule);
    }

    @Override
    public void handleEdit() {
        final Rule newRule = model.edit(index, rule);

        if (newRule != null) {
            rule = newRule;
        }
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
        return rule.accept(new NoHRFormatVisitor(model.getShowIRIs()));
    }
}
