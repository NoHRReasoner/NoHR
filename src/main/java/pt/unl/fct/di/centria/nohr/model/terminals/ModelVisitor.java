package pt.unl.fct.di.centria.nohr.model.terminals;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.Variable;

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

	public Constant visit(HybridConstant constant);

	public Predicate visit(HybridPredicate hybridPredicate);

	public Predicate visit(MetaPredicate predicate);

	public Predicate visit(Predicate predicate);

	public Variable visit(Variable variable);

}
