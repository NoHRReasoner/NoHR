package pt.unl.fct.di.centria.nohr.plugin.query;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.table.BasicLinkedOWLObjectTable;

import pt.unl.fct.di.centria.nohr.model.Answer;

public class AnswersTable extends BasicLinkedOWLObjectTable {

    /**
     *
     */
    private static final long serialVersionUID = 4898607738785645673L;
    /**
     *
     */

    private final List<ChangeListener> copyListeners = new ArrayList<>();

    public AnswersTable(OWLEditorKit owlEditorKit) {
	super(new DefaultTableModel(), owlEditorKit);
	getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	    @Override
	    public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
		    final ChangeEvent ev = new ChangeEvent(AnswersTable.this);
		    for (final ChangeListener l : new ArrayList<>(copyListeners))
			l.stateChanged(ev);
		}
	    }
	});
    }

    public void setAnswers(List<Answer> answers) {
	setModel(new AnswersTableModel(answers));
    }

}
