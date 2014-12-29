package local.translate.ql;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;
import local.translate.RuleCreator;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

public class RuleCreatorQL extends RuleCreator {

	public RuleCreatorQL(CollectionsManager c, OntologyLabel ol) {
		super(c, ol);
	}

//*****************************************************************************
//                          DL-Lite_R translation
//*****************************************************************************
	private String tr(OWLClassExpression c, String x) {
		//TODO implement
		return null;
	}
	
	private String tr(OWLPropertyExpression r, String x, String y) {
		//TODO implement
		return null;
	}
	
	private String trd(OWLClassExpression c, String x) {
		//TODO implement
		return null;
	}
		
	private String trd(OWLPropertyExpression r, String x, String y) {
		//TODO implement
		return null;
	}
		
	private void e() {
		//TODO implement
	}
	
	private void s1(OWLClassExpression subclass, OWLClassExpression superclass) {
		//TODO implement
	}
	
    private void s2(OWLPropertyExpression subproperty, OWLPropertyExpression superproperty) {
		//TODO implement
	}
    
    //(n1)
    //already handled in OntologyProceeder
    
    private void n2(OWLPropertyExpression property1, OWLPropertyExpression property2) {
		//TODO implement
    }
    
    private void i1(OWLClass cls) {
       //TODO implement
    }
    
    private void i2(OWLProperty prop) {
		//TODO implement
    }
    
    private void ir(OWLObjectProperty prop) {
		//TODO implement
    }
//*****************************************************************************    
	/** Translate class subsumption axioms with general subclass (atomic or complex) and atomic superclass.
	 * 
	 * @param subclass general subclass expression.
	 * @param superclass named superclass.
	 **/
	@Override
	public void writeDoubledRules(OWLClassExpression subclass,
			OWLClassExpression superclass) {
		s1(subclass, superclass);
	}

	/** Translate class subsumption axioms with atomic subclass and complex superclass, from equivalent axioms.
	 * 
	 * @param subclass named subclass.
	 * @param superclass named superclass.
	 **/
	@Override
	public void writeRuleC1(OWLClassExpression subclass, OWLClass superclass,
			boolean lastIndex) {
		s1(subclass, superclass);
	}

	/** Translate class subsumption axioms with atomic superclass and complex subclass (not atomic), from equivalent axioms.
	 * 
	 * @param superclass named superclass.
	 * @param subclass complex (not atomic) subclass expression.
	 **/
	@Override
	public void writeEquivalentRule(OWLClass superclass,
			OWLClassExpression subclass) {
		s1(subclass, superclass);
	}

	/** Translate property subsumption axioms with general subproperty (atomic or complex) and atomic superproperty
     *
     * @param subproperty general (atomic or complex) subproperty expression
	 * @param superproperty named superproperty
	 **/
	@Override
	public void writeRuleR1(OWLObjectPropertyExpression subproperty,
			OWLObjectProperty superproperty) {
		s2(subproperty, superproperty);
	}

	@Override
	public void translateDataPropertyAssertion(OWLDataProperty dataProperty,
			OWLIndividual individual, OWLLiteral value) {
		// TODO implement
	}
	
	
}
