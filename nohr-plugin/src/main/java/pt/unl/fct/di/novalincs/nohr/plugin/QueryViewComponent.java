package pt.unl.fct.di.novalincs.nohr.plugin;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import com.declarativa.interprolog.util.IPPrologError;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.inference.ReasonerProgressUI;
import org.semanticweb.owlapi.model.OWLException;

import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.plugin.query.AnswersTable;
import pt.unl.fct.di.novalincs.nohr.plugin.query.QueryExpressionChecker;

/**
 * The {@link ViewComponent} where the the queries are executed.
 *
 * @author Nuno Costa
 */
public class QueryViewComponent extends AbstractNoHRViewComponent implements OWLModelManagerListener, NoHRInstanceChangedListener {

    private boolean preprocess;

    class NoHRProgressUI extends ReasonerProgressUI {

        private final SwingWorker<?, ?> task;

        public NoHRProgressUI(OWLEditorKit owlEditorKit, SwingWorker<?, ?> task) {
            super(owlEditorKit);
            this.task = task;
        }

        @Override
        public void setCancelled() {
            log.info("cancelling");
            task.cancel(true);
            super.reasonerTaskStopped();
        }

    }

    class PreprocessTask extends SwingWorker<Void, Void> {

        private NoHRProgressUI progress;

        @Override
        protected Void doInBackground() throws Exception {
            progress = new NoHRProgressUI(getOWLEditorKit(), this);
            progress.reasonerTaskStarted("Preprocessing");
            progress.reasonerTaskBusy();

            if (isNoHRStarted()) {
                NoHRInstance.getInstance().stop();
            }

            startNoHR();
            preprocess = false;
            
            return null;
        }

        @Override
        protected void done() {
            super.done();
            progress.reasonerTaskStopped();
        }
    }

    private class QueryAction extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = 1454077166930078074L;

        public QueryAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showTrueAnswersCheckBox.setEnabled(
                    showUndefinedAnswersCheckBox.isSelected() || showInconsistentAnswersCheckBox.isSelected());
            showUndefinedAnswersCheckBox
                    .setEnabled(showTrueAnswersCheckBox.isSelected() || showInconsistentAnswersCheckBox.isSelected());
            showInconsistentAnswersCheckBox
                    .setEnabled(showTrueAnswersCheckBox.isSelected() || showUndefinedAnswersCheckBox.isSelected());
            if (!isNoHRStarted()) {
                preprocess();
            }
            final QueryTask queryTask = new QueryTask();
            queryTask.execute();
        }
    }

    class QueryTask extends SwingWorker<Void, Void> {

        private NoHRProgressUI progress;

        @Override
        protected Void doInBackground() throws Exception {
            progress = new NoHRProgressUI(getOWLEditorKit(), this);
            progress.reasonerTaskStarted("Reasoning");
            progress.reasonerTaskBusy();
            doQuery();
            return null;
        }

        @Override
        protected void done() {
            super.done();
            progress.reasonerTaskStopped();
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -1056667873605254959L;

    Logger log = Logger.getLogger(QueryViewComponent.class);

    private ExpressionEditor<Query> queryEditor;

    private AnswersTable answersTable;

    private JCheckBox showTrueAnswersCheckBox;

    private JCheckBox showUndefinedAnswersCheckBox;

    private JCheckBox showInconsistentAnswersCheckBox;

    private JButton executeButton;

    private boolean requiresRefresh = false;

    private JComponent createAnswersPanel() {
        final JComponent answersPanel = new JPanel(new BorderLayout(10, 10));
        answersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Query answers"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        answersTable = new AnswersTable(getOWLEditorKit());
        //answersTable.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), 14));
        answersTable.setAutoCreateColumnsFromModel(true);
        answersTable.setRowHeight(this.getFont().getSize());
        final JScrollPane answersScrollPane = new JScrollPane(answersTable);
        answersTable.setFillsViewportHeight(true);
        answersPanel.add(answersScrollPane);
        return answersPanel;
    }

    private JComponent createOptionsBox() {
        final Box optionsBox = new Box(BoxLayout.Y_AXIS);
        showTrueAnswersCheckBox = new JCheckBox(new QueryAction("true"));
        showTrueAnswersCheckBox.setSelected(true);
        optionsBox.add(showTrueAnswersCheckBox);
        optionsBox.add(Box.createVerticalStrut(3));

        showUndefinedAnswersCheckBox = new JCheckBox(new QueryAction("undefined"));
        showUndefinedAnswersCheckBox.setSelected(true);
        optionsBox.add(showUndefinedAnswersCheckBox);
        optionsBox.add(Box.createVerticalStrut(3));

        showInconsistentAnswersCheckBox = new JCheckBox(new QueryAction("inconsistent"));
        showInconsistentAnswersCheckBox.setSelected(true);
        optionsBox.add(showInconsistentAnswersCheckBox);
        optionsBox.add(Box.createVerticalStrut(5));

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        optionsBox.add(separator);
        optionsBox.add(Box.createVerticalStrut(5));

        final JCheckBox showIRIsCheckBox = new JCheckBox("Show IRIs");
        showIRIsCheckBox.setSelected(false);

        showIRIsCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                answersTable.setShowIRIs(showIRIsCheckBox.isSelected());
            }
        });

        optionsBox.add(showIRIsCheckBox);

        return optionsBox;
    }

    private JComponent createQueryPanel() {
        final JPanel editorPanel = new JPanel(new BorderLayout());

        final QueryExpressionChecker checker = new QueryExpressionChecker(getParser());
        queryEditor = new ExpressionEditor<>(getOWLEditorKit(), checker);
        queryEditor.addStatusChangedListener(new InputVerificationStatusChangedListener() {
            @Override
            public void verifiedStatusChanged(boolean newState) {
                executeButton.setEnabled(newState);
            }
        });
        queryEditor.setPreferredSize(new Dimension(100, 50));

        editorPanel.add(ComponentFactory.createScrollPane(queryEditor), BorderLayout.CENTER);
        final JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
        executeButton = new JButton(new QueryAction("Execute"));
        buttonHolder.add(executeButton);

        editorPanel.add(buttonHolder, BorderLayout.SOUTH);
        editorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Query"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        return editorPanel;
    }

    @Override
    public void disposeOWLView() {
        getOWLModelManager().removeListener(this);
        NoHRInstance.getInstance().removeListener(this);
    }

    private void doQuery() {
        if (isShowing()) {
            try {
                final Query query = queryEditor.createObject();

                if (query != null && isNoHRStarted()) {
                    final List<Answer> answers = getHybridKB().allAnswers(query, showTrueAnswersCheckBox.isSelected(), showUndefinedAnswersCheckBox.isSelected(), showInconsistentAnswersCheckBox.isSelected());

                    answersTable.setAnswers(query, answers);
                }
            } catch (final OWLException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Exception caught trying to do the query", e);
                }
            } catch (final UnsupportedAxiomsException e) {
                Messages.violations(this, e);
            } catch (IPPrologError e) {
                log.error(e.getMessage());
                answersTable.setError("prolog runtime error (see log for details)");
            }

            requiresRefresh = false;
        } else {
            requiresRefresh = true;
        }
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)
                || event.isType(EventType.ONTOLOGY_LOADED)
                || event.isType(EventType.ONTOLOGY_RELOADED)) {
            repreprocess();
        }
    }

    private void repreprocess() {
        answersTable.clear();
        reset();

        if (isShowing()) {
            preprocess();
        } else {
            preprocess = true;
        }
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(10, 10));

        final JComponent editorPanel = createQueryPanel();
        final JComponent answersPanel = createAnswersPanel();
        final JComponent optionsBox = createOptionsBox();

        answersPanel.add(optionsBox, BorderLayout.EAST);

        final JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPanel, answersPanel);

        splitter.setDividerLocation(0.3);
        add(splitter, BorderLayout.CENTER);

        preprocess();

        getOWLModelManager().addListener(this);
        NoHRInstance.getInstance().addListener(this);

        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if (isShowing()) {
                    if (!isNoHRStarted() || preprocess) {
                        preprocess();
                    }

                    if (requiresRefresh) {
                        final QueryTask queryTask = new QueryTask();
                        queryTask.execute();
                    }
                }
            }
        });
    }

    @Override
    public void instanceChanged(NoHRInstanceChangedEvent evt) {
        if (evt.getType() == NoHRInstanceChangedEventType.REQUEST_RESTART) {
            repreprocess();
        }
    }

    protected void preprocess() {
        final PreprocessTask preprocessTask = new PreprocessTask();

        preprocessTask.execute();
    }
}
