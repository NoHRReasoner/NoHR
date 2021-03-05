package pt.unl.fct.di.novalincs.nohr.plugin.query;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.owl.OWLEditorKit;

import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Query;

/**
 * An {@link JTable} for {@link Answer answers}.
 *
 * @author Nuno Costa
 */
public class AnswersTable extends JTable {

    /**
     *
     */
    private static final long serialVersionUID = 4898607738785645673L;
    /**
     *
     */

    private final List<ChangeListener> copyListeners = new ArrayList<ChangeListener>();

    public AnswersTable(OWLEditorKit owlEditorKit) {
        super(new AnswersTableModel());
        setAutoCreateColumnsFromModel(true);
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    final ChangeEvent ev = new ChangeEvent(AnswersTable.this);
                    for (final ChangeListener l : new ArrayList<ChangeListener>(copyListeners)) {
                        l.stateChanged(ev);
                    }
                }
            }
        });
    }

    public void setAnswers(Query query, List<Answer> answers) {
        ((AnswersTableModel) getModel()).setAnswers(query, answers);
    }

    public void setError(String message) {
        ((AnswersTableModel) getModel()).setError(message);
    }

    public void setShowIRIs(boolean value) {
        ((AnswersTableModel) this.getModel()).setShowIRIs(value);
    }

    public void clear() {
        ((AnswersTableModel) this.getModel()).setAnswers(null, null);
    }

}
