/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * A model element, i.e a symbol (terminal or non terminal) of the abstract syntax of the Hybrid Knowledge Bases. Can accept an {@link FormatVisitor}
 * or a {@link ModelVisitor} (see {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>}).
 *
 * @see FormatVisitor
 * @see ModelVisitor
 * @author Nuno Costa
 */
public interface ModelElement<T extends ModelElement<T>> {

	public String accept(FormatVisitor visitor);

	public T accept(ModelVisitor visitor);

}
