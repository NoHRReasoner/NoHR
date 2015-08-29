/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.concrete.IndividualConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.LiteralConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.NumericConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.RuleConstant;
import pt.unl.fct.di.centria.nohr.model.predicates.ConceptPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RolePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RulePredicate;

/**
 * @author Nuno Costa
 */
public interface Visitor {

	/**
	 * @param conceptPredicate
	 */
	void visit(ConceptPredicate conceptPredicate);

	/**
	 * @param individualConstant
	 */
	void visit(IndividualConstant individualConstant);

	/**
	 * @param literalConstant
	 */
	void visit(LiteralConstant literalConstant);

	/**
	 * @param numericConstant
	 */
	void visit(NumericConstant numericConstant);

	/**
	 * @param rolePredicate
	 */
	void visit(RolePredicate rolePredicate);

	/**
	 * @param ruleConstant
	 * @return
	 */
	void visit(RuleConstant ruleConstant);

	/**
	 * @param rulePredicate
	 */
	void visit(RulePredicate rulePredicate);

	/**
	 * @param variable
	 * @return
	 */
	void visit(Variable variable);

}
