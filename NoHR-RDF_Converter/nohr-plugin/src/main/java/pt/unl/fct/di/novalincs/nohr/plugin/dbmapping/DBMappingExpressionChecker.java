package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.Messages;

/**
 * An {@link OWLExpressionChecker} for {@link DBMapping dbMapping}.
 *
 * @author Vedran Kasalica
 * 
 * Not used so far
 */
public class DBMappingExpressionChecker implements OWLExpressionChecker<DBMapping> {

    private final NoHRParser parser;

    public DBMappingExpressionChecker(NoHRParser parser) {
        this.parser = parser;
    }

    @Override
    public void check(String str) throws OWLExpressionParserException {
        createObject(str);
    }

    @Override
    public DBMapping createObject(String str) throws OWLExpressionParserException {
        try {
            return parser.parseDBMapping(str);
        } catch (final ParseException e) {
            throw new OWLExpressionParserException(Messages.invalidExpressionMessage(str, e), e.getBegin(), e.getEnd(),
                    false, false, false, false, false, false, null);
        }
    }
}
