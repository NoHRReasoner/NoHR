/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import java.io.IOException;
import java.util.HashSet;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.parsing.Parser;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

/**
 * @author nunocosta
 *
 */
public class RuleExpressionChecker implements OWLExpressionChecker<Rule> {

    /**
     *
     */
    public RuleExpressionChecker() {
	// TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#check
     * (java.lang.String)
     */
    @Override
    public void check(String str) throws OWLExpressionParserException {
	createObject(str);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#
     * createObject(java.lang.String)
     */
    @Override
    public Rule createObject(String str) throws OWLExpressionParserException {
	try {
	    return Parser.parseRule(str);
	} catch (final PrologParserException e) {
	    throw new OWLExpressionParserException(e.getMessage(),
		    e.getStringPosition(), e.getStringPosition() + 1, false,
		    false, false, false, false, false, new HashSet<String>());
	} catch (final IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	return null;
    }
}
