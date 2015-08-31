package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.concrete.ModelVisitor;

/**
 * Represents a rule term. Can be a variable, a constant, or a list.
 *
 * @see Variable
 * @see Constant
 * @see ListTerm
 * @author Nuno Costa
 */
public interface Term extends Symbol {

	@Override
	Term accept(ModelVisitor visitor);
}
