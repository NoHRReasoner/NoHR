package pt.unl.fct.di.centria.nohr.model;

/**
 * Represent an rule atom <i>P(t<sub>1</sub>, ..., t<sub>n</sub>)</i>, where <i>P</i> is a predicate and each <i> t<sub>i</sub>, with 1&le;i&le;n </i>
 * , a term.
 *
 * @see HybridPredicate
 * @see Term
 * @author Nuno Costa
 */

public interface Atom extends Literal {

	@Override
	Atom accept(ModelVisitor model);
}
