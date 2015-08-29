package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.concrete.IndividualConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.LiteralConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.NumericConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.RuleConstant;
import pt.unl.fct.di.centria.nohr.model.predicates.ConceptPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RolePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RulePredicate;

public abstract class DefaultFormatVisitor implements FormatVisitor {

	@Override
	public String visit(ConceptPredicate predicate) {
		return visit((Symbolic) predicate);
	}

	@Override
	public String visit(IndividualConstant constant) {
		return visit((Symbolic) constant);
	}

	@Override
	public String visit(LiteralConstant constant) {
		return visit((Symbolic) constant);
	}

	@Override
	public String visit(MetaPredicate metaPredicate) {
		return visit((Symbolic) metaPredicate);
	}

	@Override
	public String visit(NumericConstant constant) {
		return visit((Symbolic) constant);
	}

	@Override
	public String visit(RolePredicate predicate) {
		return visit((Symbolic) predicate);
	}

	@Override
	public String visit(RuleConstant constant) {
		return visit((Symbolic) constant);
	}

	@Override
	public String visit(RulePredicate predicate) {
		return visit((Symbolic) predicate);
	}

	@Override
	public abstract String visit(Symbolic symbolic);

	@Override
	public String visit(Variable variable) {
		return visit((Symbolic) variable);
	}

}
