package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

/**
 * Represents a rule term. Can be a variable, a constant, or a list.
 *
 * @see Variable
 * @see Constant
 * @see ListTerm
 * @author Nuno Costa
 */
public interface Term extends FormatVisitable {

	public Term accept(ModelVisitor visitor);

	/**
	 * Returns this term as a constant if it is indeed a constant.
	 *
	 * @return this term as a constant.
	 * @throws ClassCastException
	 *             if this term isn't a constant.
	 */
	public Constant asConstant();

	/**
	 * Returns this term as a list of terms if it is indeed a list.
	 *
	 * @return this term as a list of terms.
	 * @throws ClassCastException
	 *             if this term isn't a list.
	 */
	public List<Term> asList();

	/**
	 * Returns this term as a variable if it is indeed a variable.
	 *
	 * @return this term as a variable.
	 * @throws ClassCastException
	 *             if this term isn't a variable.
	 */
	public Variable asVariable();

	/**
	 * Returns true iff this term is a constant.
	 *
	 * @return true iff this term is a constant.
	 */
	public boolean isConstant();

	/**
	 * Returns true iff this term is a list.
	 *
	 * @return true iff this term is a list.
	 */
	public boolean isList();

	/**
	 * Returns true iff this term is a variable
	 *
	 * @return true iff this term is a variable.
	 */
	public boolean isVariable();

}
