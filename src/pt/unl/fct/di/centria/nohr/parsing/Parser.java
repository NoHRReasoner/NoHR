/**
 *
 */
package pt.unl.fct.di.centria.nohr.parsing;

import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * @author nunocosta
 *
 */
public interface Parser {

    public Query parseQuery(String str);

    public Rule parseRule(String srt) throws ParseException;

}
