/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owlapi.OwlChangesLoader;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractAxiomsTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.AbstractQLAxiomsTranslator;
import static pt.unl.fct.di.centria.nohr.model.Model.*;

/**
 * @author nunocosta
 *
 */
public abstract class AbstractELAxiomsTranslator extends
AbstractAxiomsTranslator {

    private static final Variable ANNON = var("_");
    protected static final Variable X = var("X");
    protected static final Variable Y = var("Y");

    /**
     *
     */
    public AbstractELAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
    }

    private Set<Literal> atomsSet(Atom... atoms) {
	final Set<Literal> result = new HashSet<Literal>();
	Collections.addAll(result, atoms);
	return result;
    }

    protected Set<Atom> tr(List<OWLPropertyExpression> chain, Variable x,
	    Variable xk, boolean doub) {
	final Set<Atom> result = new HashSet<Atom>();
	final int n = chain.size();
	Variable xi = x;
	Variable xj = x;
	for (int i = 0; i < n; i++) {
	    final OWLProperty pe = (OWLProperty) chain.get(i);
	    xi = xj;
	    xj = i == n - 1 ? xk : var("X" + i);
	    result.add(tr(pe, xi, xj, doub));
	}
	return result;
    }

    protected Atom tr(OWLClass c, Variable x, boolean doub) {
	final Predicate pred = pred(sym(c), 1, doub);
	return atom(pred, x);
    }

    protected Set<Literal> tr(OWLClassExpression ce, Variable x, boolean doub) {
	final Set<Literal> result = new HashSet<Literal>();
	if (ce.isOWLThing())
	    return atomsSet();
	else if (ce instanceof OWLClass)
	    return atomsSet(tr((OWLClass) ce, x, doub));
	else if (ce instanceof OWLObjectIntersectionOf) {
	    final Set<OWLClassExpression> ops = ((OWLObjectIntersectionOf) ce)
		    .getOperands();
	    for (final OWLClassExpression op : ops)
		result.addAll(tr(op, x, doub));
	} else if (ce instanceof OWLObjectSomeValuesFrom) {
	    final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ce;
	    final OWLProperty p = (OWLProperty) some.getProperty();
	    final OWLClassExpression filler = some.getFiller();
	    result.add(tr(p, X, Y, doub));
	    result.addAll(tr(filler, Y, doub));
	}
	return result;
    }

    protected Atom tr(OWLProperty pe, Variable x, Variable y, boolean doub) {
	final Predicate pred = pred(sym(pe), 2, doub);
	if (pe instanceof OWLProperty)
	    return atom(pred, x, y);
	return null;
    }

    public Set<Rule> translate(OWLSubClassOfAxiom axiom) {
	final OWLClassExpression ce1 = axiom.getSubClass();
	final OWLClassExpression ce2 = axiom.getSuperClass();
	return translateSubsumption(ce1, (OWLClass) ce2);
    }

    protected abstract Set<Rule> translateSubsumption(
	    List<OWLPropertyExpression> chain, OWLProperty p2);

    protected abstract Set<Rule> translateSubsumption(OWLClassExpression ce1,
	    OWLClass c2);

    protected abstract Set<Rule> translateSubsumption(OWLProperty p1,
	    OWLProperty p2);
}
