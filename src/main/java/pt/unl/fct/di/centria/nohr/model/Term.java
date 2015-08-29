package pt.unl.fct.di.centria.nohr.model;

/**
 * Represents a rule term. Can be a variable, a constant, or a list.
 *
 * @see Variable
 * @see Constant
 * @see ListTerm
 * @author Nuno Costa
 */
public interface Term extends Symbolic {

	@Override
	Term accept(ModelVisitor visitor);
}
