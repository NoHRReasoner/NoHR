/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.concrete.IndividualConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.LiteralConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.NumericConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.RuleConstant;
import pt.unl.fct.di.centria.nohr.model.predicates.ConceptPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RolePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RulePredicate;

/**
 * A model visitor (see {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>} ) to support different model formats, i.e.
 * string representation of the model elements. Implement this interface if you want to support a new model formant, returning the desired string
 * representation of each model element, in the corresponding {@code visit} method.
 *
 * @see ModelElement
 * @author nunocosta
 */
public interface FormatVisitor {

	/**
	 * @param constant
	 * @return the string representation of the answer {@code answer}.
	 */
	public String visit(Answer answer);

	/**
	 * @param atom
	 * @return the string representation of the atom {@code atom}.
	 */
	public String visit(Atom atom);

	/**
	 * @param predicate
	 * @return the string representation of the predicate {@code predicate}.
	 */
	public String visit(ConceptPredicate predicate);

	/**
	 * @param constant
	 *            individual constant
	 * @return the string representation of the individual constant {@code constant}.
	 */
	public String visit(IndividualConstant constant);

	/**
	 * @param constant
	 *            literal constant
	 * @return the string representation of the literal constant {@code constant}.
	 */
	public String visit(LiteralConstant constant);

	/**
	 * @param metaPredicate
	 * @return the string representation of the meta-predicate {@code metaPredicate}.
	 */
	public String visit(MetaPredicate metaPredicate);

	/**
	 * @param negativeLiteral
	 * @return the string representation of the negative literal {@code negativeLiteral}.
	 */
	public String visit(NegativeLiteral negativeLiteral);

	/**
	 * @param constant
	 *            numeric constant
	 * @return the string representation of the numeric constant {@code constant}.
	 */
	public String visit(NumericConstant constant);

	/**
	 * @param query
	 * @return the string representation of the query {@code query}.
	 */
	public String visit(Query query);

	/**
	 * @param predicate
	 * @return the string representation of the predicate {@code predicate}.
	 */
	public String visit(RolePredicate predicate);

	/**
	 * @param rule
	 * @return the string representation of the rule {@code rule}.
	 */
	public String visit(Rule rule);

	/**
	 * @param constant
	 *            literal constant
	 * @return the string representation of the rule constant {@code constant}.
	 */
	public String visit(RuleConstant constant);

	/**
	 * @param predicate
	 * @return the string representation of the predicate {@code predicate}.
	 */
	public String visit(RulePredicate predicate);

	public String visit(Symbolic symbolic);

	/**
	 * @param variable
	 * @return the string representation of the variable {@code variable}.
	 */
	public String visit(Variable variable);

}
