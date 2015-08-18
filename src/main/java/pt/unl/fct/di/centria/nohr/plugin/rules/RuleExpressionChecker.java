/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;
import pt.unl.fct.di.centria.nohr.parsing.Parser;

/**
 * @author nunocosta
 */
public class RuleExpressionChecker implements OWLExpressionChecker<Rule> {

	private final Parser parser;

	/**
	 *
	 */
	public RuleExpressionChecker(Parser parser) {
		this.parser = parser;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker# createObject(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#check (java.lang.String)
	 */
	@Override
	public void check(String str) throws OWLExpressionParserException {
		createObject(str);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker# createObject(java.lang.String)
	 */
	@Override
	public Rule createObject(String str) throws OWLExpressionParserException {
		try {
			return parser.parseRule(str);
		} catch (final ParseException e) {
			throw new OWLExpressionParserException(e.getMessage(), e.getBegin(), e.getEnd(), false, false, false, false,
					false, false, null);
		}
	}
}
