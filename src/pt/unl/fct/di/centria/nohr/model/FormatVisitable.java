/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

/**
 * An element that can accept an {@link FormatVisitor} (see
 * {@link {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor
 * Pattern</a>}).
 *
 * @see FormatVisitor
 *
 * @author nunocosta
 *
 */
public interface FormatVisitable {

    public String accept(FormatVisitor visitor);

}
