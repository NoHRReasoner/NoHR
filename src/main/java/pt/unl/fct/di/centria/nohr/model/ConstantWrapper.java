package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

public class ConstantWrapper implements Constant {

	private Constant wrappee;

	@Override
	public String accept(FormatVisitor visitor) {
		return wrappee.accept(visitor);
	}

	@Override
	public Constant accept(ModelVisitor visitor) {
		return wrappee.accept(visitor);
	}

	@Override
	public OWLIndividual asIndividual() {
		return wrappee.asIndividual();
	}

	@Override
	public OWLLiteral asLiteral() {
		return wrappee.asLiteral();
	}

	@Override
	public Number asNumber() {
		return wrappee.asNumber();
	}

	@Override
	public String getSymbol() {
		return wrappee.getSymbol();
	}

	@Override
	public boolean isIndividual() {
		return wrappee.isIndividual();
	}

	@Override
	public boolean isLiteral() {
		return wrappee.isLiteral();
	}

	@Override
	public boolean isNumber() {
		return wrappee.isNumber();
	}

}
