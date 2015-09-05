package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * Represents an negative literal <i><b>not</b> P(t<sub>1</sub>, ..., t <sub>n</sub>)</i>, where <i>P</i> is a predicate, <i>t<sub>i</sub></i>, with
 * <i>1&le;i&le;n</i> terms and <i><b>not</b></i> the default negation operator.
 *
 * @author Nuno Costa
 * @see Predicate
 * @see Term
 * @see Literal
 */

public interface NegativeLiteral extends Literal {

	@Override
	public NegativeLiteral accept(ModelVisitor visitor);

	public boolean isExistentiallyNegative();

}
