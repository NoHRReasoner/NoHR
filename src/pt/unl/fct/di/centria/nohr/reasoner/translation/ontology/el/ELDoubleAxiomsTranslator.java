/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.HashSet;
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
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import static pt.unl.fct.di.centria.nohr.model.Model.*;

/**
 * @author nunocosta
 *
 */
public class ELDoubleAxiomsTranslator extends AbstractELAxiomsTranslator {

    /**
     * @param ontology
     */
    public ELDoubleAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
    }

    public Atom negTr(Literal b) {
	final Predicate pred0 = b.getPredicate();
	final Predicate pred = negPred(pred0.getSymbol(), pred0.getArity());
	return atom(pred, b.getAtom().getArguments());
    }

    public Atom negTr(OWLClass c, Variable x) {
	final Predicate pred = negPred(sym(c), 1);
	return atom(pred, x);
    }

    public Atom negTr(OWLProperty p, Variable x, Variable y) {
	final Predicate pred = negPred(sym(p), 2);
	return atom(pred, x, y);
    }

    Set<Literal> tr(List<OWLObjectPropertyExpression> chain, Variable x,
	    Variable y) {
	return tr(chain, x, y);
    }

    private Atom tr(OWLClass c, Variable x) {
	return tr(c, x, true);
    }

    public List<Literal> tr(OWLClassExpression ce, Variable x) {
	return tr(ce, x, true);
    }

    Atom tr(OWLProperty p, Variable x, Variable y) {
	return tr(p, x, y, true);
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom alpha) {
	return translateDouble(alpha);
    }

    @Override
    public Set<Rule> translate(OWLPropertyAssertionAxiom alpha) {
	return translateDouble(alpha);
    }

    // (r2)
    @Override
    public Set<Rule> translateSubsumption(
	    List<OWLObjectPropertyExpression> chain, OWLObjectProperty s) {
	final Set<Rule> result = new HashSet<Rule>();
	final Set<Literal> chainTr = tr(chain, X, Y);
	Set<Literal> body = new HashSet<Literal>(chainTr);
	body.add(negLiteral(negTr(s, X, Y)));
	result.add(rule(tr(s, X, Y), body));
	for (final Literal r : chainTr) {
	    body = new HashSet<Literal>(chainTr);
	    body.remove(r);
	    result.add(rule(negTr(r), body));
	}
	return result;
    }

    @Override
    public Set<Rule> translateSubsumption(OWLClassExpression c, OWLClass a) {
	final Set<Rule> result = new HashSet<Rule>();
	// (t1)
	if (c.isOWLThing())
	    result.add(rule(tr(a, X), negLiteral(negTr(a, X))));
	// (i1)
	else if (a.isOWLNothing() && !c.isAnonymous())
	    result.add(rule(negTr(c.asOWLClass(), X)));
	// (i2)
	else if (a.isOWLNothing() && c.isAnonymous())
	    for (final Literal b : tr(c, X, false)) {
		final List<Literal> body = tr(c, X, false);
		body.remove(b);
		result.add(rule(negTr(b), body));
	    }
	// (c1)
	else {
	    List<Literal> body = tr(c, X);
	    body.add(negLiteral(negTr(a, X)));
	    result.add(rule(tr(a, X), body));
	    for (final Literal b : tr(c, X, false)) {
		body = tr(c, X, false);
		body.add(negTr(a, X));
		body.remove(b);
		result.add(rule(negTr(b), body));
	    }
	}
	return result;
    }

    // (r1)
    @Override
    public Set<Rule> translateSubsumption(OWLProperty r, OWLProperty s) {
	return ruleSet(
		rule(tr(r, X, Y), tr(s, X, Y), negLiteral(negTr(r, X, Y))),
		rule(negTr(r, X, Y), negTr(s, X, Y)));
    }
}