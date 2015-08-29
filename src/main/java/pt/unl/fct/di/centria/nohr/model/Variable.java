package pt.unl.fct.di.centria.nohr.model;

/**
 * Represents variable.
 *
 * @author Nuno Costa
 */

public interface Variable extends Term, Comparable<Variable> {

	Variable accept(ModelVisitor visitor);
}
