/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Ontology individual {@link Constant} implementation.
 *
 * @see Term
 *
 * @author nunocosta
 *
 */
public class IndividualConstantImpl implements Constant {

    /** The ontology individual. */
    private final OWLIndividual individual;

    /**
     * Constructs an ontology individual constant with a specified individual.
     *
     * @param individual
     *            the ontology individual
     */
    IndividualConstantImpl(OWLIndividual individual) {
	this.individual = individual;
    }

    @Override
    public String accept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Constant acept(ModelVisitor visitor) {
	return visitor.visit(this);
    }

    @Override
    public Constant asConstant() {
	throw new ClassCastException();
    }

    @Override
    public List<Term> asList() {
	throw new ClassCastException();
    }

    @Override
    public Number asNumber() {
	throw new ClassCastException();
    }

    @Override
    public OWLIndividual asOWLIndividual() {
	return individual;
    }

    @Override
    public OWLLiteral asOWLLiteral() {
	throw new ClassCastException();
    }

    @Override
    public String asRuleConstant() {
	return individual.asOWLNamedIndividual().getIRI().getFragment();
    }

    @Override
    public TruthValue asTruthValue() {
	throw new ClassCastException();
    }

    @Override
    public Variable asVariable() {
	throw new ClassCastException();
    }

    @Override
    public boolean isConstant() {
	return false;
    }

    @Override
    public boolean isList() {
	return false;
    }

    @Override
    public boolean isNumber() {
	return false;
    }

    @Override
    public boolean isOWLIndividual() {
	return true;
    }

    @Override
    public boolean isOWLLiteral() {
	return false;
    }

    @Override
    public boolean isRuleConstant() {
	return true;
    }

    @Override
    public boolean isTruthValue() {
	return false;
    }

    @Override
    public boolean isVariable() {
	return false;
    }

}
