package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.centria.nohr.model.terminals.HybridConstant;
import pt.unl.fct.di.centria.nohr.model.terminals.ModelVisitor;

public class ConstantWrapper implements HybridConstant {

	private HybridConstant wrappee;

	ConstantWrapper(HybridConstant wrappee) {
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
		this.wrappee = wrappee;
	}

}
