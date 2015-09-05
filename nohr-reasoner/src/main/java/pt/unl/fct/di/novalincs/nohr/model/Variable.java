package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * Represents variable.
 *
 * @author Nuno Costa
 */

public interface Variable extends Term, Comparable<Variable> {

	Variable accept(ModelVisitor visitor);
}
