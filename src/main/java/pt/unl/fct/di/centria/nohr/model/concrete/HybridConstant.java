package pt.unl.fct.di.centria.nohr.model.concrete;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.centria.nohr.model.Constant;

public interface HybridConstant extends Constant {

	OWLIndividual asIndividual();

	OWLLiteral asLiteral();

	Number asNumber();

	boolean isIndividual();

	boolean isLiteral();

	boolean isNumber();

}
