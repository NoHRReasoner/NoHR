/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.concrete;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;

/**
 * @author Nuno Costa
 */
public interface RuleConstant extends Constant {

	@Override
	String accept(FormatVisitor visitor);

	public Constant accept(ModelVisitor visitor);

}