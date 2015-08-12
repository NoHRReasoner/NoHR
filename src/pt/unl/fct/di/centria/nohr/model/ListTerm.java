/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

/**
 * Represents a list term.
 *
 * @author Nuno Costa
 *
 */
public interface ListTerm extends Term {

    @Override
    public String accept(FormatVisitor visitor);

    @Override
    public ListTerm accept(ModelVisitor visitor);
}
