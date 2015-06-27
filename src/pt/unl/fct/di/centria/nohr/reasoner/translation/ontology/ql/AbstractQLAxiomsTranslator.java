/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.domPred;
import static pt.unl.fct.di.centria.nohr.model.Model.negPred;
import static pt.unl.fct.di.centria.nohr.model.Model.pred;
import static pt.unl.fct.di.centria.nohr.model.Model.ranPred;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractAxiomsTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;

/**
 * @author nunocosta
 *
 */
public abstract class AbstractQLAxiomsTranslator extends
	AbstractAxiomsTranslator {

    private int opeNewCount = 0;

    public AbstractQLAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
	dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
	ontologyLabeler = new OntologyLabeler(ontology,
		dataFactory
			.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL
				.getIRI()));
    }

    public Atom negTr(OWLClassExpression c, Variable x) {
	if (c instanceof OWLClass)
	    return atom(negPred(sym((OWLClass) c), 1), x);
	else if (c instanceof OWLObjectSomeValuesFrom)
	    return negTr(((OWLObjectSomeValuesFrom) c).getProperty(), x,
		    var("_"));
	else
	    throw new IllegalArgumentException(
		    "c must be an atomic or existential class");
    }

    public Atom negTr(OWLPropertyExpression r, Variable x, Variable y) {
	if (r instanceof OWLObjectProperty)
	    return atom(negPred(sym(r), 2), x, y);
	else if (r instanceof OWLDataProperty)
	    return atom(negPred(sym(r), 2), x, y);
	else
	    return atom(negPred(sym(r), 2), y, x);
    }

    protected Atom negTrExist(OWLObjectPropertyExpression q, Variable X) {
	if (q instanceof OWLObjectProperty)
	    return atom(negPred(sym(q), 2), X, var("_"));
	else
	    return atom(negPred(sym(q), 2), var("_"), X);
    }

    private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression ope) {
	return dataFactory.getOWLObjectSomeValuesFrom(ope,
		dataFactory.getOWLThing());
    }

    protected Atom tr(OWLClassExpression c, Variable x, boolean doub) {
	if (c instanceof OWLClass)
	    return atom(pred(sym((OWLClass) c), 1, doub), x);
	else if (c instanceof OWLObjectSomeValuesFrom) {
	    final OWLObjectPropertyExpression q = ((OWLObjectSomeValuesFrom) c)
		    .getProperty();
	    if (q instanceof OWLObjectProperty)
		return atom(domPred(sym(q), doub), x);
	    else
		return atom(ranPred(sym(q), doub), x);
	} else if (c instanceof OWLObjectComplementOf) {
	    final OWLClassExpression b = ((OWLObjectComplementOf) c)
		    .getOperand();
	    if (b instanceof OWLClass)
		return atom(negPred(sym((OWLClass) b), 1), x);
	    else if (b instanceof OWLObjectSomeValuesFrom)
		return negTr(((OWLObjectSomeValuesFrom) b).getProperty(), x,
			var("_"));
	}
	throw new IllegalArgumentException("c must be an valid concept");
    }

    protected Atom tr(OWLPropertyExpression r, Variable x, Variable y,
	    boolean doub) {
	if (r instanceof OWLObjectProperty)
	    return atom(pred(sym(r), 2, doub), x, y);
	else if (r instanceof OWLDataProperty)
	    return atom(pred(sym(r), 2, doub), x, y);
	else
	    return atom(pred(sym(r), 2, doub), y, x);
    }

    public Set<Rule> translate(OWLDisjointClassesAxiom alpha) {
	final Set<Rule> result = new HashSet<Rule>();
	final List<OWLClassExpression> ops = alpha.getClassExpressionsAsList();
	for (int i = 0; i < ops.size(); i++)
	    for (int j = i + 1; j < ops.size(); j++)
		result.addAll(translateDisjunction(ops.get(i), ops.get(j)));
	return result;
    }

    public Set<Rule> translate(OWLDisjointDataPropertiesAxiom alpha) {
	final Set<Rule> result = new HashSet<Rule>();
	final List<OWLDataPropertyExpression> ops = new LinkedList<OWLDataPropertyExpression>(
		alpha.getProperties());
	for (int i = 0; i < ops.size(); i++)
	    for (final int j = i + 1; i < ops.size(); i++)
		result.addAll(translateDisjunction(ops.get(i), ops.get(j)));
	return result;
    }

    public Set<Rule> translate(OWLDisjointObjectPropertiesAxiom alpha) {
	final Set<Rule> result = new HashSet<Rule>();
	final List<OWLObjectPropertyExpression> ops = new LinkedList<OWLObjectPropertyExpression>(
		alpha.getProperties());
	for (int i = 0; i < ops.size(); i++)
	    for (final int j = i + 1; i < ops.size(); i++)
		result.addAll(translateDisjunction(ops.get(i), ops.get(j)));
	return result;
    }

    public Set<Rule> translate(OWLSubClassOfAxiom alpha) {
	final OWLClassExpression b1 = alpha.getSubClass();
	final OWLClassExpression b2 = alpha.getSuperClass();
	final Set<Rule> result = new HashSet<Rule>();
	result.addAll(translateSubsumption(b1, b2));
	return result;
    }

    public Set<Rule> translate(OWLSubPropertyAxiom<?> alpha) {
	final OWLPropertyExpression ope1 = alpha.getSubProperty();
	final OWLPropertyExpression ope2 = alpha.getSuperProperty();
	final Set<Rule> result = new HashSet<Rule>();
	result.addAll(translateSubsumption(ope1, ope2));
	return result;
    }

    protected abstract Set<Rule> translateBasicSubsumption(
	    OWLClassExpression b1, OWLClassExpression b2);

    protected abstract Set<Rule> translateDisjunction(OWLClassExpression b1,
	    OWLClassExpression b2);

    protected abstract Set<Rule> translateDisjunction(OWLPropertyExpression q1,
	    OWLPropertyExpression q2);

    public abstract Rule translateDomain(OWLObjectProperty p);

    public abstract Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha);

    public abstract Rule translateRange(OWLObjectProperty p);

    public abstract Rule translateRange(OWLSubObjectPropertyOfAxiom alpha);

    protected Set<Rule> translateSubsumption(OWLClassExpression ce1,
	    OWLClassExpression ce2) {
	if (ce1.isTopEntity() || ce1.isBottomEntity() || ce2.isTopEntity())
	    return ruleSet();
	if (ce2.isBottomEntity())
	    return ruleSet(translateUnsatisfaible((OWLClass) ce1));
	if (ce1 instanceof OWLDataSomeValuesFrom
		|| ce2 instanceof OWLDataSomeValuesFrom)
	    return ruleSet();
	if (ce2 instanceof OWLObjectComplementOf)
	    return translateSubsumption(ce1, (OWLObjectComplementOf) ce2);
	else if (ce2 instanceof OWLObjectIntersectionOf)
	    return translateSubsumption(ce1, (OWLObjectIntersectionOf) ce2);
	return translateBasicSubsumption(ce1, ce2);
    }

    protected Set<Rule> translateSubsumption(OWLClassExpression b1,
	    OWLObjectComplementOf c2) {
	return translateDisjunction(b1, c2.getOperand());
    }

    protected Set<Rule> translateSubsumption(OWLClassExpression b1,
	    OWLObjectIntersectionOf c2) {
	final List<OWLClassExpression> ops = c2.getOperandsAsList();
	final Set<Rule> result = new HashSet<Rule>();
	for (final OWLClassExpression bi : ops)
	    result.addAll(translateSubsumption(b1, bi));
	return result;
    }

    protected Set<Rule> translateSubsumption(OWLClassExpression ce1,
	    OWLObjectSomeValuesFrom ce2) {
	final OWLObjectPropertyExpression ope = ce2.getProperty();
	final OWLObjectPropertyExpression opeNew = dataFactory
		.getOWLObjectProperty(IRI.create("PNEW" + opeNewCount++));
	final OWLObjectPropertyExpression invOpeNew = opeNew
		.getInverseProperty();
	final OWLClassExpression c = ce2.getFiller();
	if (c.isOWLThing())
	    return translateBasicSubsumption(ce1, ce2);
	final Set<Rule> result = new HashSet<Rule>();
	result.addAll(translateSubsumption(opeNew, ope));
	result.addAll(translateSubsumption(some(invOpeNew), c));
	result.addAll(translateSubsumption(ce1, some(opeNew)));
	return result;
    }

    protected abstract Set<Rule> translateSubsumption(OWLPropertyExpression q1,
	    OWLPropertyExpression q2);

    public Rule translateUnsatisfaible(OWLClass a) {
	return rule(negTr(a, X));
    }

    public Rule translateUnsatisfaible(OWLProperty p) {
	return rule(negTr(p, X, Y));
    }

}
