package local.translate.ql;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;
import local.translate.RuleCreator;

public class RuleCreatorQL extends RuleCreator {

	public RuleCreatorQL(CollectionsManager c, OntologyLabel ol) {
		super(c, ol);
	}

	/** Translate class subsumption axioms with general subclass (atomic or complex) and atomic superclass.
	 * 
	 * @param classExpression general subclass expression.
	 * @param owlClass named superclass.
	 **/
	@Override
	public void writeDoubledRules(OWLClassExpression classExpression,
			OWLClassExpression owlClass) {
		// TODO implement
	}

	/** Translate class subsumption axioms with atomic subclass and complex superclass, from equivalent axioms.
	 * 
	 * @param expression named subclass.
	 * @param superclass named superclass.
	 **/
	@Override
	public void writeRuleC1(OWLClassExpression expression, OWLClass superclass,
			boolean lastIndex) {
		// TODO implement
	}

	/** Translate class subsumption axioms with atomic superclass and complex subclass (not atomic), from equivalent axioms.
	 * 
	 * @param owlClass named superclass.
	 * @param rightPartOfRule complex (not atomic) subclass expression.
	 **/
	@Override
	public void writeEquivalentRule(OWLClass owlClass,
			OWLClassExpression rightPartOfRule) {
		// TODO implement
	}

	/** Translate property subsumption axioms with general subproperty (atomic or complex) and atomic superproperty
     *
     * @param general (atomic or complex) subproperty expression
	 * @param named superproperty
	 **/
	@Override
	public void writeRuleR1(OWLObjectPropertyExpression expression,
			OWLObjectProperty superclass) {
		// TODO implement
	}

    /** Translates a given data property with its property name, individual, and value into a rule (or two rules).
    *
	*  @param data property.
    *  @param individual.
    *  @param value.
    */
	@Override
	public void translateDataPropertyAssertion(OWLDataProperty dataProperty,
			OWLIndividual individual, OWLLiteral value) {
		// TODO implement
	}
	
	
}
