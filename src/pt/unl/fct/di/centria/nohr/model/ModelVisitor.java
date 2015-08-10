package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * A model visitor (see
 * {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor
 * Pattern</a>} ) to support different model operations. The {@code visit}
 * methods are intended to construct, from the visited elements, and according
 * to some operation, new elements of the same type, and return that new
 * elements.
 *
 * Implement this interface if you want to support a new model operation,
 * returning the result of the application of that operation to each model
 * element, in the corresponding {@code visit} method.
 *
 * @author nunocosta
 *
 */
public interface ModelVisitor {

    public Constant visit(Constant constant);

    public Term visit(ListTerm list);

    public Literal visit(Literal literal);

    public NegativeLiteral visit(NegativeLiteral literal);

    public Predicate visit(Predicate pred);

    public Query visit(Query query);

    public Rule visit(Rule rule);

    public Term visit(Term term);

    public Variable visit(Variable variable);

}
