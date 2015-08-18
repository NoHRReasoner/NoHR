/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.query;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Constant;

/**
 * @author nunocosta
 */
public class AnswersTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -6876572230591220016L;
	private final List<Answer> answers;

	/**
	 *
	 */
	public AnswersTableModel(List<Answer> answer) {
		answers = answer;
		fireTableRowsInserted(0, answers.size() - 1);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Constant.class;
	}

	@Override
	public int getColumnCount() {
		if (answers == null)
			return 0;
		if (answers.isEmpty())
			return 0;
		return answers.get(0).getQuery().getVariables().size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (answers == null)
			return null;
		if (answers.isEmpty())
			return "";
		return answers.get(0).getQuery().getVariables().get(columnIndex).toString();
	}

	@Override
	public int getRowCount() {
		if (answers == null)
			return 0;
		return answers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return answers.get(rowIndex).getValues().get(columnIndex);
	}

}
