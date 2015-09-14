/**
 *
 */
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

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.TruthValue;

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
	private static final String NO_ANSWERS = "no answers found";
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
		if (answers.size() == 0)
			return 1;
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
		if (answers.isEmpty())
			return 1;
		return answers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String result;
		if (columnIndex == 0) {
			if (answers.isEmpty())
				if (query.getVariables().isEmpty())
					return TruthValue.FALSE.name().toLowerCase();
				else
					return NO_ANSWERS;
			result = answers.get(rowIndex).getValuation().name().toLowerCase();
		} else
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
