/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin;

import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;
import pt.unl.fct.di.centria.nohr.parsing.Parser;

/**
 * @author nunocosta
 */
public class ParserSingleton {

	private static Parser parser;

	public static Parser getParser() {
		if (parser == null)
			parser = new NoHRParser(null);
		return parser;
	}

}
