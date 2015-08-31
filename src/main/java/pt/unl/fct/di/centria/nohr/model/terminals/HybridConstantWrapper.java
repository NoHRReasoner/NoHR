package pt.unl.fct.di.centria.nohr.model.terminals;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;

public class HybridConstantWrapper implements HybridConstant {

	private HybridConstant wrappee;

	HybridConstantWrapper(HybridConstant wrappee) {
		if (wrappee instanceof HybridConstantWrapper)
			throw new IllegalArgumentException("wrapee: can't be a HybridConstantWrapper");
		this.wrappee = wrappee;
	}

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

	HybridConstant getWrappe() {
		return wrappee;
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

	void setWrappe(HybridConstant wrappee) {
		if (wrappee instanceof HybridConstantWrapper)
			throw new IllegalArgumentException("wrapee: can't be a HybridConstantWrapper");
		this.wrappee = wrappee;
	}

	@Override
	public String toString() {
		return getSymbol();
	}

}
