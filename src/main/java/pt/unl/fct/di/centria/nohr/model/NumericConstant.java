/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

/**
 * @author Nuno Costa
 */
public interface NumericConstant extends Constant {

	@Override
	String accept(FormatVisitor visitor);

	@Override
	NumericConstant accept(ModelVisitor visitor);

	Number getNumber();

}