package pt.unl.fct.di.novalincs.nohr.plugin.query;

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

	private final List<ChangeListener> copyListeners = new ArrayList<>();

	public AnswersTable(OWLEditorKit owlEditorKit) {
		super(new AnswersTableModel());
		setAutoCreateColumnsFromModel(true);
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

	public void setAnswers(Query query, List<Answer> answers) {
		((AnswersTableModel) getModel()).setAnswers(query, answers);
	}

}
