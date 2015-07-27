/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * @author nunocosta
 *
 */
public interface FormatVisitor {

    /**
     * @param answer
     * @return
     */
    public String visit(Answer answer);

    /**
     * @param atom
     * @return
     */
    public String visit(Atom atom);

    /**
     * @param constant
     * @return
     */
    public String visit(Constant constant);

    /**
     * @param listTermImpl
     * @return
     */
    public String visit(ListTermImpl listTermImpl);

    /**
     * @param metaPredicate
     * @return
     */
    public String visit(MetaPredicate metaPredicate);

    /**
     * @param negativeLiteral
     * @return
     */
    public String visit(NegativeLiteral negativeLiteral);

    /**
     * @param predicate
     * @return
     */
    public String visit(Predicate predicate);

    /**
     * @param query
     * @return
     */
    public String visit(Query query);

    /**
     * @param rule
     * @return
     */
    public String visit(Rule rule);

    /**
     * @param variable
     * @return
     */
    public String visit(Variable variable);

}
