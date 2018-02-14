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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.novalincs.nohr.model.HashSetProgram;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.ProgramPersistenceManager;

/**
 * An {@link ListModel list model} of {@link Rule rules}.
 */
public class RuleListModel extends AbstractListModel<Object> {

    protected static final Logger log = Logger.getLogger(RuleListModel.class);

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

    private final Program program;

    private final RuleEditor ruleEditor;

    private final List<Object> ruleItems;

    private final ProgramPersistenceManager programPersistenceManager;

    private boolean showIRIs;

    /**
     *
     */
    public RuleListModel(OWLEditorKit editorKit, RuleEditor ruleEditor,
            ProgramPersistenceManager programPersistenceManager, Program program) {
        super();
        this.programPersistenceManager = programPersistenceManager;

        this.showIRIs = false;
        this.ruleEditor = ruleEditor;
        this.program = program;
        ruleItems = new ArrayList<Object>(program.size());
        ruleItems.add(HEADER);
        for (final Rule rule : program) {
            ruleItems.add(new RuleListItem(ruleItems.size() - 1, this, rule));
        }
    }

    boolean add(Rule rule) {
        final boolean added = program.add(rule);

        if (added) {
            final int index = ruleItems.size();
            ruleItems.add(new RuleListItem(index, this, rule));
            super.fireIntervalAdded(this, index, index);
        }

        return added;
    }

    public void clear() {
        final int size = ruleItems.size();
        program.clear();
        ruleItems.clear();
        ruleItems.add(HEADER);
        super.fireIntervalRemoved(this, 1, size);
    }

    Rule edit(int index, Rule rule) {
        ruleEditor.setRule(rule);
        final Rule newRule = ruleEditor.show();
        boolean updated = false;
        if (newRule != null) {
            updated = program.update(rule, newRule);
        }
        fireContentsChanged(this, index, index);
        return updated ? newRule : null;
    }

    @Override
    public Object getElementAt(int index) {
        return ruleItems.get(index);
    }

    public boolean getShowIRIs() {
        return showIRIs;
    }

    @Override
    public int getSize() {
        return ruleItems.size();
    }

    public void load(File file) throws IOException, PrologParserException, ParseException {
    	HashSetProgram tempProgram = new HashSetProgram(Collections.<Rule>emptySet());
        programPersistenceManager.load(file, tempProgram);
        final int size = program.size();
        program.clear();
        program.addAll(tempProgram.getRules());
        ruleItems.clear();
        ruleItems.add(HEADER);

        for (final Rule rule : program) {
            ruleItems.add(new RuleListItem(ruleItems.size(), this, rule));

        }
        super.fireContentsChanged(this, 0, Math.max(program.size() - 1, size - 1));
    }

    boolean remove(int index, Rule rule) {
        final boolean removed = program.remove(rule);
        if (removed) {

            // We also need to alter the indices of elements following the one to be deleted
            if (index < ruleItems.size() - 1) {
                for (int i = index + 1; i <= ruleItems.size() - 1; i++) {
                    ((RuleListItem) getElementAt(i)).setIndex(i - 1);
                }
            }
            ruleItems.remove(index);
            super.fireIntervalRemoved(this, index, index);
        }
        return removed;
    }

    public void save(File file) throws IOException {
        ProgramPersistenceManager.write(program, file);
    }

    public void setShowIRIs(boolean value) {
        this.showIRIs = value;
        fireContentsChanged(this, 0, ruleItems.size());
    }
}
