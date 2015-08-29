package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.concrete.IndividualConstant;
import pt.unl.fct.di.centria.nohr.model.concrete.Model;
import pt.unl.fct.di.centria.nohr.model.concrete.RuleConstant;
import pt.unl.fct.di.centria.nohr.model.predicates.ConceptPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicates;
import pt.unl.fct.di.centria.nohr.model.predicates.RolePredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.RulePredicate;

public class VocabularyMappingVisitor extends DefaultModelVisitor {

	private final VocabularyMapping vocabularyMapping;

	public VocabularyMappingVisitor(VocabularyMapping vocabularyMapping) {
		this.vocabularyMapping = vocabularyMapping;
	}

	@Override
	public Predicate visit(ConceptPredicate pred) {
		final Predicate result = vocabularyMapping.getConcept(pred.getSymbol());
		if (result == null)
			return Predicates.pred(pred.getSymbol(), 1);
		else
			return result;
	}

	@Override
	public Constant visit(IndividualConstant constant) {
		final Constant result = vocabularyMapping.getIndividual(constant.getSymbol());
		if (result == null)
			return Model.cons(constant.getSymbol());
		else
			return result;
	}

	@Override
	public Predicate visit(RolePredicate pred) {
		final Predicate result = vocabularyMapping.getRole(pred.getSymbol());
		if (result == null)
			return Predicates.pred(pred.getSymbol(), 2);
		else
			return result;
	}

	@Override
	public Constant visit(RuleConstant constant) {
		final Constant result = vocabularyMapping.getIndividual(constant.getSymbol());
		if (result == null)
			return constant;
		else
			return result;
	}

	@Override
	public Predicate visit(RulePredicate pred) {
		Predicate result = null;
		if (pred.getArity() == 1)
			result = vocabularyMapping.getConcept(pred.getSymbol());
		else if (pred.getArity() == 2)
			result = vocabularyMapping.getRole(pred.getSymbol());
		if (result == null)
			result = pred;
		return result;
	}

}
