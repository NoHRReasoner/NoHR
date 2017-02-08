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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.protege.editor.core.ui.list.MList;

import pt.unl.fct.di.novalincs.nohr.model.Rule;

/**
 * An {@link MList list} of {@link Rule rules}.
 *
 * @author Nuno Costa
 */
public class RulesList extends MList {

    /**
     *
     */
    private static final long serialVersionUID = 302913958066431253L;

    private final RuleListModel model;

    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                handleEdit();
            }
        }
    };

    private final RuleEditor ruleEditor;

    public void setShowIRIs(boolean value) {
        this.model.setShowIRIs(value);
    }

    public RulesList(RuleEditor ruleEditor, RuleListModel model) {
        this.model = model;
        this.ruleEditor = ruleEditor;
        setModel(model);
        addMouseListener(mouseListener);
    }

    @Override
    protected void handleAdd() {
        ruleEditor.clear();
        final Rule newRule = ruleEditor.show();
        if (newRule != null) {
            model.add(newRule);
        }
    }

    @SuppressWarnings("unchecked")
    public void setModel(RuleListModel model) {
        super.setModel(model);
    }
}
