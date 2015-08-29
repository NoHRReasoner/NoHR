package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

/**
 * An answer to a query. Represents a substitution (i.e. a mapping between variables and terms) for a query's literal list, corresponding to an answer
 * to that query.
 *
 * @see pt.unl.fct.di.centria.nohr.model.Query Query
 * @see pt.unl.fct.di.centria.nohr.model.Literal Literal
 * @author Nuno Costa
 */

public interface Answer extends ModelElement<Answer> {

	@Override
	public Answer accept(ModelVisitor visitor);

	/**
	 * Apply the answer's substitutions to the query literals.
	 *
	 * @return the query's literal list with each variable replaced by the corresponding answer's term.
	 */
	public List<Literal> apply();

	/**
	 * Returns the query to which the answer corresponds.
	 *
	 * @return the query to which the answer corresponds.
	 */
	public Query getQuery();

	/**
	 * Returns the truth value of the answer.
	 *
	 * @return the truth value of the answer.
	 */
	public TruthValue getValuation();

	/**
	 * Returns the term to which a variable is mapped.
	 *
	 * @param variable
	 *            the variable.
	 * @return the term to which variable {@code variable} is mapped.
	 */
	public Term getValue(Variable variable);

	/**
	 * Returns the list of terms to which each query's variable is mapped, in the same order that those variables appear in that query (see
	 * {@link pt.unl.fct.di.centria.nohr.model.Query#getVariables()} ).
	 *
	 * @return the list of terms to which query's variables are mapped.
	 */
	public List<Term> getValues();
}
