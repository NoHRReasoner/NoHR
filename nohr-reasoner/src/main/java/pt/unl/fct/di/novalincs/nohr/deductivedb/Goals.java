/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.deductivedb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.ModelElement;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.TruthValue;

/**
 * Constructs Prolog goals to query a {@link PrologEngine}, with a set of predicates that are assumed to be defined, to a given Prolog system, in a
 * specified Prolog module.
 *
 * @author Nuno Costa
 */
class Goals {

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
	static String detGoal(FormatVisitor formatVisitor, Query query, Boolean trueAnswers, String var) {
		if (trueAnswers == null)
			return String.format(DET_GOAL_3, varsList(formatVisitor, query), query.accept(formatVisitor), var);
		else
			return String.format(DET_GOAL_4, varsList(formatVisitor, query), query.accept(formatVisitor),
					toString(trueAnswers), var);
	}

	/**
	 * Create a goal that check if a given query has answers.
	 *
	 * @param query
	 *            the query.
	 * @param trueAnswer
	 *            specifies whether the considered answers valuations will be {@link TruthValue#TRUE true}. The considered answers will have a
	 *            {@link TruthValue#TRUE true} valuation if {@code trueAnswers == true}; a {@link TruthValue#UNDEFINED undefined} valuation if
	 *            {@code trueAnswers == false}; and any of the two if {@code trueAnswers == null}.
	 * @return the goal.
	 */
	static String hasValue(FormatVisitor formatVisitor, Query query, boolean trueAnswer) {
		return String.format(HAS_VALUE_2, toString(formatVisitor, query), toString(trueAnswer));
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
	static String nonDetGoal(FormatVisitor formatVisitor, Query query, Boolean trueAnswers, String var) {
		if (trueAnswers == null)
			return String.format(NON_DET_GOAL_3, varsList(formatVisitor, query), toString(formatVisitor, query), var);
		else
			return String.format(NON_DET_GOAL_4, varsList(formatVisitor, query), toString(formatVisitor, query),
					toString(trueAnswers), var);
	}

	/**
	 * Returns the string representation of a truth value.
	 *
	 * @param trueValue
	 *            specifies whether the truth value is {@link TruthValue#TRUE true} or not (i.e. {@link TruthValue#UNDEFINED undefined}).
	 * @return the representation of {@code trueValue}.
	 */
	private static String toString(boolean trueValue) {
		return trueValue ? "true" : "undefined";
	}

	/**
	 * Returns the string representation of a given model element.
	 *
	 * @param element
	 *            the model element.
	 * @return the representation of {@code element}.
	 */
	static String toString(FormatVisitor formatVisitor, ModelElement<?> element) {
		return element.accept(formatVisitor);
	}

	/**
	 * Returns the string representation of the variable list of a given query.
	 *
	 * @param the
	 *            query.
	 * @return the string representation of the {@code query}'s variable list.
	 */
	private static String varsList(FormatVisitor formatVisitor, Query query) {
		return Model.concat(query.getVariables(), formatVisitor, ",");
	}

}
