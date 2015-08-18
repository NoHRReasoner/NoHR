/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * An implementation of {@link ModelVisitor} that simply returns {@code element} in each {@code visit(E element)} method if {@code E} is an leaf type
 * (i.e. {@link Constant}, {@link Predicate}, or {@link Variable}}), and {@code element.accept(this)} otherwise. Extend this class if you want create
 * a {@link ModelVisitor} that manipulates that only some model types.
 *
 * @author Nuno Costa
 */
public class DefaultModelVisitor implements ModelVisitor {

	@Override
	public Answer visit(Answer answer) {
		return answer.accept(this);
	}

	@Override
	public Constant visit(Constant constant) {
		return constant;
	}

	@Override
	public Term visit(ListTerm termList) {
		return termList.accept(this);
	}

	@Override
	public Literal visit(Literal literal) {
		return literal.accept(this);
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
	public Predicate visit(Predicate predicate) {
		return predicate;
	}

	@Override
	public Query visit(Query query) {
		return query.accept(this);
	}

	@Override
	public Rule visit(Rule rule) {
		return rule.accept(this);
	}

	@Override
	public Term visit(Term termList) {
		return termList.accept(this);
	}

	@Override
	public Variable visit(Variable variable) {
		return variable;
	}

}
