package pt.unl.fct.di.centria.nohr.model;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Represents a constant. A constant can be a number, a rule constant, an
 * ontology individual or an ontology literal.
 *
 * @see Term
 */
public interface Constant extends Term {

    @Override
    public Constant acept(ModelVisitor visitor);

    /**
     * Returns this constant as a number if it is indeed a number.
     *
     * @return this constant as a number.
     *
     * @throws ClassCastException
     *             if this constant isn't a number.
     */
    public Number asNumber();

    /**
     * Returns this constant as an OWL individual if it is indeed an ontology
     * individual.
     *
     * @return this constant as an OWL individual.
     *
     * @throws ClassCastException
     *             if this constant isn't a ontology individual
     */
    public OWLIndividual asOWLIndividual();

    /**
     * Returns this constant as an ontology literal if it is indeed an ontology
     * literal.
     *
     * @return this constant as an ontology literal.
     *
     * @throws ClassCastException
     *             if this isn't a ontology literal.
     */
    public OWLLiteral asOWLLiteral();

    /**
     * Returns the string representing of this constant if it is an rule
     * constant.
     *
     * @return this constant as a string representing this constant.
     *
     * @throws ClassCastException
     *             if this isn't a rule constant.
     */
    public String asRuleConstant();

    /**
     * Returns the truth value corresponding to this constant if it represents a
     * truth value
     *
     * @return {@link TruthValue#TRUE}, iff the string the representation of
     *         this constant is constant is {@literal "true"}; or
     *         {@link TruthValue#UNDEFINED}, iff the string representation of
     *         this constant is {@literal "undefined"}
     *
     * @throws ClassCastException
     *             if this constant isn't a rule constant or if his string
     *             representation isn't {@literal "true"} nor
     *             {@literal "undefined"}
     */
    public TruthValue asTruthValue();

    /**
     * Returns true iff this constant is a number.
     *
     * @return true iff this constant is a number.
     */
    public boolean isNumber();

    /**
     * Returns true iff this constant is an ontology individual.
     *
     * @return true iff this constant is an ontology individual.
     */
    public boolean isOWLIndividual();

    /**
     * Returns true iff this constant is an ontology literal.
     *
     * @return true iff this constant is an ontology literal.
     */
    public boolean isOWLLiteral();

    /**
     * Returns true iff this constant is an rule constant.
     *
     * @return true iff this constant is an rule constant.
     */
    public boolean isRuleConstant();

    /**
     * Returns true iff this constant is an rule constant corresponding to a
     * truth value, {@code true} or {@code undefined}.
     *
     * @return returns true iff this constant is an rule corresponding to a
     *         truth value.
     */
    public boolean isTruthValue();

}
