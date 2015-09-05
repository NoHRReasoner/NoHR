/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

/**
 * Represents a terminal symbol of the abstract syntax.
 *
 * @author Nuno Costa
 */
public interface Symbol extends ModelElement<Symbol> {

	/**
	 * Returns the string representation of this symbol. That representation must univocally identify the symbol, i.e. the following property must be
	 * satisfied, where {@code s} and {@code r} are two {@link Symbol symbols}: {@code s.asString().equals(r.asString())} iff {@code s.equals(r)}.
	 */
	String asString();

}
