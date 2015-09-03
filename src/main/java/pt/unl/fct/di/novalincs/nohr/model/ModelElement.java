/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * An element that can accept an {@link FormatVisitor} (see {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>}).
 *
 * @see FormatVisitor
 * @author nunocosta
 */
public interface ModelElement<T extends ModelElement<T>> {

	public String accept(FormatVisitor visitor);

	public T accept(ModelVisitor visitor);

}
