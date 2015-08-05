/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.List;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * @author nunocosta
 *
 */
public class IndividualConstantImpl implements Constant {

    private final OWLIndividual individual;

    public IndividualConstantImpl(OWLIndividual individual) {
	this.individual = individual;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.FormatVisitable#acept(pt.unl.fct.di.
     * centria.nohr.model.FormatVisitor)
     */
    @Override
    public String acept(FormatVisitor visitor) {
	return visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pt.unl.fct.di.centria.nohr.model.Constant#acept(pt.unl.fct.di.centria.
     * nohr.model.Visitor)
     */
    @Override
    public Constant acept(Visitor visitor) {
	return visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Term#asConstant()
     */
    @Override
    public Constant asConstant() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Term#asList()
     */
    @Override
    public List<Term> asList() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asNumber()
     */
    @Override
    public Number asNumber() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asOWLIndividual()
     */
    @Override
    public OWLIndividual asOWLIndividual() {
	return individual;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asOWLLiteral()
     */
    @Override
    public OWLLiteral asOWLLiteral() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asString()
     */
    @Override
    public String asString() {
	return individual.asOWLNamedIndividual().getIRI().getFragment();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#asTruthValue()
     */
    @Override
    public TruthValue asTruthValue() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Term#asVariable()
     */
    @Override
    public Variable asVariable() {
	throw new ClassCastException();
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Term#isConstant()
     */
    @Override
    public boolean isConstant() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Term#isList()
     */
    @Override
    public boolean isList() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isNumber()
     */
    @Override
    public boolean isNumber() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isOWLIndividual()
     */
    @Override
    public boolean isOWLIndividual() {
	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isOWLLiteral()
     */
    @Override
    public boolean isOWLLiteral() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isString()
     */
    @Override
    public boolean isString() {
	return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Constant#isTruthValue()
     */
    @Override
    public boolean isTruthValue() {
	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see pt.unl.fct.di.centria.nohr.model.Term#isVariable()
     */
    @Override
    public boolean isVariable() {
	return false;
    }

}
