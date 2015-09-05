/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.query;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Term;

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
		fireTableStructureChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Term.class;
	}

	@Override
	public int getColumnCount() {
		if (query == null)
			return 0;
		return query.getVariables().size() + 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (query == null || columnIndex == 0)
			return "";
		return query.getVariables().get(columnIndex - 1).toString();
	}

	@Override
	public int getRowCount() {
		if (answers == null)
			return 0;
		return answers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String result;
		if (columnIndex == 0)
			result = answers.get(rowIndex).getValuation().name().toLowerCase();
		else
			result = answers.get(rowIndex).getValues().get(columnIndex - 1).toString();
		return result;
	}

	public void setAnswers(Query query, List<Answer> answers) {
		this.query = query;
		this.answers = answers;
		super.fireTableStructureChanged();
		super.fireTableDataChanged();
	}

}
