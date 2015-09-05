/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.query;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.Messages;

public class QueryExpressionChecker implements OWLExpressionChecker<Query>, Disposable {

	private NoHRParser parser;

	public QueryExpressionChecker(NoHRParser parser) {
		this.parser = parser;
	}

	@Override
	public void check(String str) throws OWLExpressionParserException {
		createObject(str);
	}

	@Override
	public Query createObject(String str) throws OWLExpressionParserException {

		try {
			return parser.parseQuery(str);
		} catch (final ParseException e) {
			throw new OWLExpressionParserException(Messages.invalidExpressionMessage(str, e), e.getBegin(),
					e.getEnd(), false, false, false, false, false, false, null);
		}
	}

	@Override
	public void dispose() throws Exception {
		parser = null;
	}
}