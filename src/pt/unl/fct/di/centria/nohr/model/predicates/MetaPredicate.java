/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

import pt.unl.fct.di.centria.nohr.model.FormatVisitor;

/**
 * @author nunocosta
 *
 */
public interface MetaPredicate extends Predicate {

    @Override
    public String acept(FormatVisitor visitor);

    public Predicate getPredicate();

    public char getPrefix();

    public PredicateType getType();

}