/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.query;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;

/**
 * A {@link TableModel} for {@link Answer answers}.
 *
 * @author Nuno Costa
 */
public class AnswersTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -6876572230591220016L;
	private List<Answer> answers;
	private Query query;

	public AnswersTableModel() {

	}

	/**
	 *
	 */
	public AnswersTableModel(Query query, List<Answer> answers) {
		this.query = query;
		this.answers = answers;
		fireTableRowsInserted(0, answers.size() - 1);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		if (query == null)
			return 0;
		return query.getVariables().size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (query == null)
			return "";
		return query.getVariables().get(columnIndex).toString();
	}

	@Override
	public int getRowCount() {
		if (answers == null)
			return 0;
		return answers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final Term term = answers.get(rowIndex).getValues().get(columnIndex);
		if (term.isConstant()) {
			final Constant constant = term.asConstant();
			if (constant.isOWLIndividual())
				return constant.asOWLIndividual();
			else if (constant.isOWLLiteral())
				return constant.asOWLLiteral();
			else
				return constant;
		}
		return term;
	}

	public void setAnswers(Query query, List<Answer> answers) {
		this.query = query;
		this.answers = answers;
		super.fireTableStructureChanged();
		super.fireTableDataChanged();
	}

}