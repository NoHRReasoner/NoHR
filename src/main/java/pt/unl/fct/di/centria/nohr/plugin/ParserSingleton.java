/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin;

import pt.unl.fct.di.centria.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;

/**
 * @author nunocosta
 */
public class ParserSingleton {

	private static NoHRParser parser;

	public static NoHRParser getParser() {
		if (parser == null)
			parser = new NoHRRecursiveDescentParser(null);
		return parser;
	}

}
