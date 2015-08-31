package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.terminals.ModelVisitor;

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

	public NegativeLiteral accept(ModelVisitor visitor);

	// TODO revise the need of this method (maybe the FormatVistor must be
	// responsible for handle these format details).
	public boolean isExistentiallyNegative();

}
