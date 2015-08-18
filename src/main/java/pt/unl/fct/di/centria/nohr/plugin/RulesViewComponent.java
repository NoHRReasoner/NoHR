package pt.unl.fct.di.centria.nohr.plugin;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import pt.unl.fct.di.centria.nohr.plugin.rules.RuleEditor;
import pt.unl.fct.di.centria.nohr.plugin.rules.RuleListModel;
import pt.unl.fct.di.centria.nohr.plugin.rules.RulesList;

public class RulesViewComponent extends AbstractHybridViewComponent {

	private static RuleListModel ruleListModel;

	/**
	 *
	 */
	private static final long serialVersionUID = 6087261708132206489L;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.protege.editor.core.Disposable#dispose()
	 */
	@Override
	public void disposeOWLView() {
		// TODO Auto-generated method stub

	}

	private RuleListModel getRuleListModel() {
		if (ruleListModel == null)
			ruleListModel = new RuleListModel(new RuleEditor(getOWLEditorKit()), getRuleBase());
		return ruleListModel;
	}

	@Override
	public void initialiseOWLView() throws Exception {
		setLayout(new BorderLayout());
		final RuleEditor ruleEditor = new RuleEditor(getOWLEditorKit());
		final RulesList ruleList = new RulesList(ruleEditor, getRuleListModel());
		final JScrollPane jScrollPane = new JScrollPane(ruleList);
		add(jScrollPane, BorderLayout.CENTER);
	}
}
