/**
 *
 */
package pt.unl.fct.di.centria.nohr.deductivedb;

import pt.unl.fct.di.centria.nohr.model.FormatVisitable;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.TruthValue;

/**
 * Constructs Prolog goals to query a {@link PrologEngine}, with a set of predicates that are assumed to be defined, to a given Prolog system, in a
 * specified Prolog module.
 *
 * @author Nuno Costa
 */
public class PrologSystemInterface {

	/**
	 * A goal that obtain one answer to a given query with a given truth value. {@code detGoal(+Vars,?G,-TM)}:
	 */
	private static final String DET_GOAL_4 = "detGoal([%s],(%s),%s,%s)";

	/** A goal that obtain one answer to a given query. {@code detGoal(+Vars,?G,+TV,-TM)} */
	private static final String DET_GOAL_3 = "detGoal([%s],(%s),%s)";

	/** A goal to check if a given query has answers with a given truth value. {@code hasValue(+G,-TV)} */
	private static final String HAS_VALUE_2 = "hasValue((%s), %s)";

	/** A goal that obtain all answers to a given query with a given thruth value. {@code nonDetGoal(+Vars,?G,+TV,-ListTM)} */
	private static final String NON_DET_GOAL_4 = "nonDetGoal([%s],(%s),%s,%s)";

	/** A goal that obtain all answers to a given query. {@code noDetGoal(+Vars,?G,-ListTM)} */
	private static final String NON_DET_GOAL_3 = "nonDetGoal([%s],(%s),%s)";

	/** The {@link FormatVisitor} used to format the queries. */
	private final FormatVisitor formatVisitor;

	/** A Prolog module name that defines the predicates described in this class. */
	private final String prologModuleName;

	/**
	 * Constructs a {@link PrologSystemInterface} with a given {@link FormatVisitor} and Prolog module name.
	 *
	 * @param formatVisitor
	 *            the {@link FormatVisitor}.
	 * @param prologModuleName
	 *            the Prolog module name.
	 */
	PrologSystemInterface(FormatVisitor formatVisitor, String prologModuleName) {
		this.formatVisitor = formatVisitor;
		this.prologModuleName = prologModuleName;
	}

	/**
	 * Create a goal that obtain one answer to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param trueAnswers
	 *            specifies whether the answers valuations will be {@link TruthValue#TRUE true}. The answers will have a {@link TruthValue#TRUE true}
	 *            valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED undefined} valuation if {@code trueAnswers == false}; and
	 *            any of the two if {@code trueAnswers == null}.
	 * @param var
	 *            the name of the variable were the answers values list will be unified.
	 * @return the goal.
	 */
	String detGoal(Query query, Boolean trueAnswers, String var) {
		if (trueAnswers == null)
			return String.format(DET_GOAL_3, varsList(query), query.accept(formatVisitor), var);
		else
			return String.format(DET_GOAL_4, varsList(query), query.accept(formatVisitor), toString(trueAnswers), var);
	}

	/**
	 * Returns the Prolog module name that defines the predicates described in this class for the Prolog system supported by this
	 * {@link PrologSystemInterface}.
	 */
	public String getPrologModuleName() {
		return prologModuleName;
	}

	/**
	 * Create a goal that check if a given query has answers.
	 *
	 * @param query
	 *            the query.
	 * @param trueAnswers
	 *            specifies whether the considered answers valuations will be {@link TruthValue#TRUE true}. The considered answers will have a
	 *            {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED undefined} valuation if
	 *            {@code trueAnswers == false}; and any of the two if {@code trueAnswers == null}.
	 * @return the goal.
	 */
	String hasValue(Query query, boolean trueAnswer) {
		return String.format(HAS_VALUE_2, toString(query), toString(trueAnswer));
	}

	/**
	 * Create a goal that obtain all answers to a given query.
	 *
	 * @param query
	 *            the query.
	 * @param trueAnswers
	 *            specifies whether the answers valuations will be {@link TruthValue#TRUE true}. The answers will have a {@link TruthValue#TRUE true}
	 *            valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED undefined} valuation if {@code trueAnswers == false}; and
	 *            any of the two if {@code trueAnswers == null}.
	 * @param var
	 *            the name of the variable were the answers values list will be unified.
	 * @return the goal.
	 */
	String nonDetGoal(Query query, Boolean trueAnswers, String var) {
		if (trueAnswers == null)
			return String.format(NON_DET_GOAL_3, varsList(query), toString(query), var);
		else
			return String.format(NON_DET_GOAL_4, varsList(query), toString(query), toString(trueAnswers), var);
	}

	/**
	 * Returns the string representation of a truth value.
	 *
	 * @param trueValue
	 *            specifies whether the truth value is {@link TruthValue#TRUE true} or not (i.e. {@link TruthValue#UNDEFINED undefined}).
	 * @return the representation of {@code trueValue}.
	 */
	private String toString(boolean trueValue) {
		return trueValue ? "true" : "undefined";
	}

	/**
	 * Returns the string representation of a given model element.
	 *
	 * @param element
	 *            the model element.
	 * @return the representation of {@code element}.
	 */
	String toString(FormatVisitable element) {
		return element.accept(formatVisitor);
	}

	/**
	 * Returns the string representation of the variable list of a given query.
	 *
	 * @param the
	 *            query.
	 * @return the string representation of the {@code query}'s variable list.
	 */
	private String varsList(Query query) {
		return Model.concat(query.getVariables(), formatVisitor, ",");
	}

}
