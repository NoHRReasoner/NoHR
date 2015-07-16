/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import static pt.unl.fct.di.centria.nohr.model.Model.rule;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * @author nunocosta
 *
 */
public class ELOriginalAxiomsTranslator extends AbstractELAxiomsTranslator {

    public ELOriginalAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
    }

    Set<Literal> tr(List<OWLObjectPropertyExpression> chain, Variable x,
	    Variable y) {
	return tr(chain, x, y);
    }

    Atom tr(OWLClass c, Variable x) {
	return tr(c, x, false);
    }

    List<Literal> tr(OWLClassExpression ce, Variable x) {
	return tr(ce, x, false);
    }

    Atom tr(OWLProperty p, Variable x, Variable y) {
	return tr(p, x, y, false);
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom alpha) {
	return translateOriginal(alpha);
    }

    @Override
    public Set<Rule> translate(OWLPropertyAssertionAxiom<?, ?> alpha) {
	return translateOriginal(alpha);
    }

    @Override
    protected Set<Rule> translateSubsumption(
	    List<OWLObjectPropertyExpression> pes1, OWLObjectProperty p2) {
	return ruleSet(rule(tr(p2, X, Y), tr(pes1, X, Y)));
    }

    @Override
    protected Set<Rule> translateSubsumption(OWLClassExpression c, OWLClass a) {
	if (a.isOWLNothing())
	    return ruleSet();
	if (c.isOWLThing())
	    return ruleSet(rule(tr(a, X)));
	return ruleSet(rule(tr(a, X), tr(c, X)));
    }

    @Override
    protected Set<Rule> translateSubsumption(OWLProperty p1, OWLProperty p2) {
	return ruleSet(rule(tr(p2, X, Y), tr(p1, X, Y)));
    }

}
