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
 * An implementation of {@link ModelVisitor} that simply returns {@code element} in each {@code visit(E element)} method if {@code E} is an leaf type
 * (i.e. {@link Constant}, {@link Predicate}, or {@link Variable}}), and {@code element.accept(this)} otherwise. Extend this class if you want create
 * a {@link ModelVisitor} that manipulates that only some model types.
 *
 * @author Nuno Costa
 */
public class DefaultModelVisitor implements ModelVisitor {

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

	@Override
	public Answer visit(Answer answer) {
		return answer.accept(this);
	}

	@Override
	public Atom visit(Atom atom) {
		return atom.accept(this);
	}

	@Override
	public Predicate visit(ConceptPredicate pred) {
		return pred;
	}

	@Override
	public Constant visit(Constant constant) {
		return constant;
	}

	@Override
	public Constant visit(IndividualConstant constant) {
		return constant;
	}

	@Override
	public Literal visit(Literal literal) {
		return literal.accept(this);
	}

	@Override
	public Constant visit(LiteralConstant constant) {
		return constant;
	}

	@Override
	public Predicate visit(MetaPredicate metaPredicate) {
		return metaPredicate.accept(this);
	}

	@Override
	public NegativeLiteral visit(NegativeLiteral negativeLiteral) {
		return negativeLiteral.accept(this);
	}

	@Override
	public Constant visit(NumericConstant constant) {
		return constant;
	}

	@Override
	public Predicate visit(Predicate predicate) {
		return predicate;
	}

	@Override
	public Query visit(Query query) {
		return query.accept(this);
	}

	@Override
	public Predicate visit(RolePredicate pred) {
		return pred;
	}

	@Override
	public Rule visit(Rule rule) {
		return rule.accept(this);
	}

	@Override
	public Constant visit(RuleConstant constant) {
		return constant;
	}

	@Override
	public Predicate visit(RulePredicate pred) {
		return pred;
	}

	@Override
	public Term visit(Term term) {
		return term.accept(this);
	}

	@Override
	public Variable visit(Variable variable) {
		return variable;
	}

}
