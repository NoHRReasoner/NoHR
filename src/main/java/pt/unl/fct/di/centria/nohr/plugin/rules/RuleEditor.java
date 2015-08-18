/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owlapi.model.OWLException;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.plugin.ParserSingleton;

/**
 * @author nunocosta
 */
public class RuleEditor {

	private final ExpressionEditor<Rule> editor;

	private final OWLEditorKit editorKit;

	/**
	 *
	 */
	public RuleEditor(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		editor = new ExpressionEditor<Rule>(editorKit, new RuleExpressionChecker(ParserSingleton.getParser()));
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
