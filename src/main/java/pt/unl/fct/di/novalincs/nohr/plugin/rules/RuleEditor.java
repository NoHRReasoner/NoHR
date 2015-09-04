/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.rules;

import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owlapi.model.OWLException;

import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;

/**
 * An editor, showed in an dialog, that can be used to edit text containing rule expressions. The editor is backed by a parser that checks that the
 * text is well formed and provides feedback if the text is not well formed.
 *
 * @author Nuno Costa
 */
public class RuleEditor {

	private final ExpressionEditor<Rule> editor;

	private final OWLEditorKit editorKit;

	/**
	 *
	 */
	public RuleEditor(OWLEditorKit editorKit, NoHRParser parser) {
		this.editorKit = editorKit;
		editor = new ExpressionEditor<Rule>(editorKit, new RuleExpressionChecker(parser));
	}

	public void clear() {
		editor.setText("");
	}

	public void setRule(Rule rule) {
		editor.setText(rule.toString());
	}

	public Rule show() {
		final UIHelper uiHelper = new UIHelper(editorKit);
		final int ret = uiHelper.showValidatingDialog("Rule Editor", editor, null);
		if (ret == JOptionPane.OK_OPTION)
			try {
				return editor.createObject();
			} catch (final OWLException e) { // e is an
				// OWLExpresionParserException
				return null;
			}

		return null;
	}

}
