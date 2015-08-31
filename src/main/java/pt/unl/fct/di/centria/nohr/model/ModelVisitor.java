package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;

/**
 * A model visitor (see {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>} ) to support different model operations.
 * The {@code visit} methods are intended to construct, from the visited elements, and according to some operation, new elements of the same type, and
 * return that new elements. Implement this interface if you want to support a new model operation, returning the result of the application of that
 * operation to each model element, in the corresponding {@code visit} method.
 *
 * @author Nuno Costa
 */
public interface ModelVisitor {

	public Constant visit(Constant constant);

	public HybridPredicate visit(HybridPredicate hybridPredicate);

	public HybridPredicate visit(MetaPredicate predicate);

	public Variable visit(Variable variable);

}
