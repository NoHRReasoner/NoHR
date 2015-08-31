package pt.unl.fct.di.centria.nohr.model.predicates;

import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import pt.unl.fct.di.centria.nohr.model.DefaultModelVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.HybridPredicate;

/**
 * A {@link ModelVisitor} that replace each {@link HybridPredicate} appearing at each model element by the correspondent {@link MetaPredicate} of a
 * specified {@link PredicateType}. Each predicate is replaced by a meta-predicate of the specified type, referring that predicate. Each
 * meta-predicate is replaced by a new meta-predicate of the specified type, referring the predicate that such meta-predicate refer.
 *
 * @author Nuno Costa
 */
public class PredicateTypeVisitor extends DefaultModelVisitor {

	/**
	 * The {@link PredicateType} of the meta-predicate by which all predicates are replaced.
	 */
	private final PredicateType predicateType;

	/**
	 * Constructs a {@link ModelVisitor} that replace all the {@link HybridPredicate}s appearing at each model element by the correspondent
	 * {@link MetaPredicate}s of a specified {@link PredicateType}.
	 *
	 * @param predicateType
	 *            the {@link PredicateType} of the meta-predicate by which all predicates will be replaced.
	 */
	public PredicateTypeVisitor(PredicateType predicateType) {
		this.predicateType = predicateType;
	}

	/**
	 * Returns the type of the meta-predicate by which all predicates are replaced.
	 *
	 * @return the type of the meta-predicate by which all predicates are replaced.
	 */
	public PredicateType getType() {
		return predicateType;
	}

	@Override
	public HybridPredicate visit(HybridPredicate pred) {
		return pred(pred, predicateType);
	}

	@Override
	public HybridPredicate visit(MetaPredicate pred) {
		return pred(pred.getPredicate(), predicateType);
	}

}
