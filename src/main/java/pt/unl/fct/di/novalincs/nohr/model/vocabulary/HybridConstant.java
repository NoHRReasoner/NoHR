package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import pt.unl.fct.di.novalincs.nohr.model.Constant;

/* A constant that can be a number, a rule constant, an ontology individual or an ontology literal. */

public interface HybridConstant extends Constant {

	OWLIndividual asIndividual();

	OWLLiteral asLiteral();

	Number asNumber();

	boolean isIndividual();

	boolean isLiteral();

	boolean isNumber();

}
