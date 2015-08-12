/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import static pt.unl.fct.di.centria.nohr.model.Model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicates;

/**
 * @author nunocosta
 *
 */
public abstract class AbstractELAxiomsTranslator {

    protected static final Variable X = var("X");
    protected static final Variable Y = var("Y");

    protected List<Literal> atomsList(Atom... atoms) {
	final List<Literal> result = new ArrayList<Literal>(atoms.length);
	Collections.addAll(result, atoms);
	return result;
    }

    protected List<Atom> tr(List<OWLObjectPropertyExpression> chain, Variable x, Variable xk, boolean doub) {
	final int n = chain.size();
	final List<Atom> result = new ArrayList<Atom>(n);
	Variable xi = x;
	Variable xj = x;
	for (int i = 0; i < n; i++) {
	    final OWLProperty<?, ?> pe = (OWLProperty<?, ?>) chain.get(i);
	    xi = xj;
	    xj = i == n - 1 ? xk : var("X" + i);
	    result.add(tr(pe, xi, xj, doub));
	}
	return result;
    }

    protected Atom tr(OWLClass c, Variable x, boolean doub) {
	final Predicate pred = Predicates.pred(c, doub);
	return atom(pred, x);
    }

    protected List<Literal> tr(OWLClassExpression ce, Variable x, boolean doub) {
	final List<Literal> result = new ArrayList<Literal>();
	if (ce.isOWLThing())
	    return atomsList();
	else if (ce instanceof OWLClass && !ce.isOWLThing())
	    return atomsList(tr(ce.asOWLClass(), x, doub));
	else if (ce instanceof OWLObjectIntersectionOf) {
	    final Set<OWLClassExpression> ops = ce.asConjunctSet();
	    for (final OWLClassExpression op : ops)
		result.addAll(tr(op, x, doub));
	} else if (ce instanceof OWLObjectSomeValuesFrom) {
	    final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce;
	    final OWLProperty<?, ?> p = some.getProperty().asOWLObjectProperty();
	    final OWLClassExpression filler = some.getFiller();
	    result.add(tr(p, X, Y, doub));
	    result.addAll(tr(filler, Y, doub));
	}
	return result;
    }

    protected Atom tr(OWLProperty<?, ?> p, Variable x, Variable y, boolean doub) {
	final Predicate pred = Predicates.pred(p, doub);
	if (p instanceof OWLProperty)
	    return atom(pred, x, y);
	return null;
    }

    public abstract Set<Rule> translate(OWLClassAssertionAxiom alpha);

    public abstract Set<Rule> translate(OWLPropertyAssertionAxiom<?, ?> alpha);

    public Set<Rule> translate(OWLSubClassOfAxiom axiom) {
	final OWLClassExpression ce1 = axiom.getSubClass();
	final OWLClassExpression ce2 = axiom.getSuperClass();
	if (ce2.isAnonymous())
	    return ruleSet();
	for (final OWLClassExpression ci : ce1.asConjunctSet())
	    if (ci.isOWLNothing())
		return ruleSet();
	if (ce2.isOWLThing())
	    return ruleSet();
	return translateSubsumption(ce1, (OWLClass) ce2);
    }

    public Set<Rule> translate(OWLSubPropertyAxiom<?> axiom) {
	final OWLProperty<?, ?> pe1 = (OWLProperty<?, ?>) axiom.getSubProperty();
	final OWLProperty<?, ?> pe2 = (OWLProperty<?, ?>) axiom.getSuperProperty();
	return translateSubsumption(pe1, pe2);
    }

    /**
     * @param axiom
     * @return
     */
    public Set<Rule> translate(OWLSubPropertyChainOfAxiom axiom) {
	final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
	final OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
	return translateSubsumption(chain, (OWLObjectProperty) superProperty);
    }

    protected abstract Set<Rule> translateSubsumption(List<OWLObjectPropertyExpression> chain, OWLObjectProperty p2);

    protected abstract Set<Rule> translateSubsumption(OWLClassExpression ce1, OWLClass c2);

    protected abstract Set<Rule> translateSubsumption(OWLProperty<?, ?> p1, OWLProperty<?, ?> p2);
}
