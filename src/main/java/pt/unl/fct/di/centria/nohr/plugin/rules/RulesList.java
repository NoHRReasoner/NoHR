package pt.unl.fct.di.centria.nohr.plugin.rules;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.protege.editor.core.ui.list.MList;

import pt.unl.fct.di.centria.nohr.model.Rule;

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
			if (e.getClickCount() == 2)
				handleEdit();
		}
	};

	private final RuleEditor ruleEditor;

	public RulesList(RuleEditor ruleEditor, RuleListModel model) {
		this.model = model;
		this.ruleEditor = ruleEditor;
		setModel(model);
		addMouseListener(mouseListener);
	}

	@Override
	protected void handleAdd() {
		final Rule newRule = ruleEditor.show();
		if (newRule != null)
			model.add(newRule);
	}

	@SuppressWarnings("unchecked")
	private void setModel(RuleListModel model) {
		super.setModel(model);
	}
}
