/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.doubPred;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.negPred;
import static pt.unl.fct.di.centria.nohr.model.Model.origPred;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * @author nunocosta
 *
 */
public abstract class AbstractAxiomsTranslator {

    protected static final Variable ANNON = var("_");
    protected static final Variable X = var("X");
    protected static final Variable Y = var("Y");
    protected final OWLOntology ontology;
    protected final OntologyLabeler ontologyLabeler;

    /**
     *
     */
    public AbstractAxiomsTranslator(OWLOntology ontology) {
	this.ontology = ontology;
	final OWLDataFactory dataFactory = ontology.getOWLOntologyManager()
		.getOWLDataFactory();
	ontologyLabeler = new OntologyLabeler(ontology,
		dataFactory
		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL
			.getIRI()));
    }

    protected Set<Rule> ruleSet(Rule... rules) {
	final Set<Rule> result = new HashSet<Rule>();
	Collections.addAll(result, rules);
	return result;
    }

    protected String sym(OWLClass c) {
	return ontologyLabeler.getLabel(c, 1);
    }

    protected String sym(OWLIndividual i) {
	return ontologyLabeler.getLabel(i, 1);
    }

    protected String sym(OWLPropertyAssertionObject o) {
	if (o instanceof OWLIndividual)
	    return sym((OWLIndividual) o);
	else if (o instanceof OWLLiteral)
	    return OntologyLabeler.escapeAtom(((OWLLiteral) o).getLiteral());
	else
	    return null;
    }

    protected String sym(OWLPropertyExpression r) {
	if (r instanceof OWLObjectPropertyExpression)
	    return ontologyLabeler.getLabel(
		    ((OWLObjectPropertyExpression) r).getNamedProperty(), 1);
	else
	    return ontologyLabeler.getLabel((OWLDataProperty) r, 1);
    }

    public abstract Set<Rule> translate(OWLClassAssertionAxiom alpha);

    public abstract Set<Rule> translate(OWLPropertyAssertionAxiom alpha);

    public Set<Rule> translateDouble(OWLClassAssertionAxiom alpha) {
	final OWLClassExpression c = alpha.getClassExpression();
	if (!(c instanceof OWLClass))
	    throw new IllegalArgumentException(
		    "assertion's concepts must be atomic");
	final String aSym = sym((OWLClass) c);
	final Predicate a = doubPred(aSym, 1);
	final Predicate na = negPred(aSym, 1);
	final Constant i = cons(sym(alpha.getIndividual()));
	return ruleSet(rule(atom(a, i), negLiteral(na, i)));
    }

    public Set<Rule> translateDouble(OWLPropertyAssertionAxiom alpha) {
	final String aSym = sym(alpha.getProperty());
	final Predicate p = doubPred(aSym, 2);
	final Predicate np = negPred(aSym, 2);
	final Constant i1 = cons(sym(alpha.getSubject()));
	final Constant i2 = cons(sym(alpha.getObject()));
	return ruleSet(rule(atom(p, i1, i2), negLiteral(np, i1, i2)));
    }

    public Set<Rule> translateOriginal(OWLClassAssertionAxiom alpha) {
	final OWLClassExpression c = alpha.getClassExpression();
	if (!(c instanceof OWLClass))
	    throw new IllegalAccessError("assertion's concepts must be atomic");
	if (c.isTopEntity() || c.isBottomEntity())
	    return ruleSet();
	final Predicate a = origPred(sym((OWLClass) c), 1);
	final Constant i = cons(sym(alpha.getIndividual()));
	return ruleSet(rule(atom(a, i)));
    }

    public Set<Rule> translateOriginal(OWLPropertyAssertionAxiom alpha) {
	final OWLPropertyExpression ope = alpha.getProperty();
	if (ope.isTopEntity() || ope.isBottomEntity())
	    return ruleSet();
	final Predicate p = origPred(sym(alpha.getProperty()), 2);
	final Constant i1 = cons(sym(alpha.getSubject()));
	final Constant i2 = cons(sym(alpha.getObject()));
	return ruleSet(rule(atom(p, i1, i2)));
    }
}
