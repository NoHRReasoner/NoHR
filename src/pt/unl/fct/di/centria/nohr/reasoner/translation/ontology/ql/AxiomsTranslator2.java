/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.domPred;
import static pt.unl.fct.di.centria.nohr.model.Model.doubDomPred;
import static pt.unl.fct.di.centria.nohr.model.Model.doubPred;
import static pt.unl.fct.di.centria.nohr.model.Model.doubRanPred;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.negPred;
import static pt.unl.fct.di.centria.nohr.model.Model.origDomPred;
import static pt.unl.fct.di.centria.nohr.model.Model.origPred;
import static pt.unl.fct.di.centria.nohr.model.Model.origRanPred;
import static pt.unl.fct.di.centria.nohr.model.Model.pred;
import static pt.unl.fct.di.centria.nohr.model.Model.ranPred;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.util.Iterator;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;

/**
 * @author nunocosta
 *
 */
public class AxiomsTranslator2 {

    private static final Variable X = var("X");

    private static final Variable Y = var("Y");
    private final OntologyLabeler ontologyLabeler;

    /**
     *
     */
    public AxiomsTranslator2(OWLOntologyManager ontologyManager, OWLOntology ont) {
	ontologyLabeler = new OntologyLabeler(ont, ontologyManager
		.getOWLDataFactory().getOWLAnnotationProperty(
			OWLRDFVocabulary.RDFS_LABEL.getIRI()));
    }

    private Atom doubTr(OWLClassExpression c, Variable x) {
	return tr(c, x, true);
    }

    private Atom doubTr(OWLPropertyExpression<?, ?> r, Variable x, Variable y) {
	return tr(r, x, y, true);
    }

    private Atom doubTrExist(OWLObjectPropertyExpression q, Variable X) {
	if (q instanceof OWLObjectProperty)
	    return atom(doubDomPred(sym(q)), X);
	else
	    return atom(doubRanPred(sym(q)), X);
    }

    private Atom negTr(OWLClassExpression c, Variable x) {
	if (c instanceof OWLClass)
	    return atom(negPred(sym((OWLClass) c), 1), x);
	else if (c instanceof OWLObjectSomeValuesFrom)
	    return negTr(((OWLObjectSomeValuesFrom) c).getProperty(), x,
		    var("_"));
	else
	    throw new IllegalArgumentException(
		    "c must be an atomic or existential class");
    }

    private Atom negTr(OWLPropertyExpression<?, ?> r, Variable x, Variable y) {
	if (r instanceof OWLObjectProperty)
	    return atom(negPred(sym(r), 2), x, y);
	else if (r instanceof OWLDataProperty)
	    return atom(negPred(sym(r), 2), x, y);
	else
	    return atom(negPred(sym(r), 2), y, x);
    }

    private Atom negTrExist(OWLObjectPropertyExpression q, Variable X) {
	if (q instanceof OWLObjectProperty)
	    return atom(negPred(sym(q), 2), X, var("_"));
	else
	    return atom(negPred(sym(q), 2), var("_"), X);
    }

    private Atom origTr(OWLClassExpression c, Variable x) {
	return tr(c, x, false);
    }

    private Atom origTr(OWLPropertyExpression<?, ?> r, Variable x, Variable y) {
	return tr(r, x, y, false);
    }

    private Atom origTrExist(OWLObjectPropertyExpression q, Variable X) {
	if (q instanceof OWLObjectProperty)
	    return atom(origDomPred(sym(q)), X);
	else
	    return atom(origRanPred(sym(q)), X);
    }

    private String sym(OWLClass c) {
	return ontologyLabeler.getLabel(c, 1);
    }

    private String sym(OWLIndividual i) {
	return ontologyLabeler.getLabel(i, 1);
    }

    private String sym(OWLPropertyExpression<?, ?> r) {
	if (r instanceof OWLObjectPropertyExpression)
	    return ontologyLabeler.getLabel(
		    ((OWLObjectPropertyExpression) r).getNamedProperty(), 1);
	else
	    return ontologyLabeler.getLabel((OWLDataProperty) r, 1);
    }

    private Atom tr(OWLClassExpression c, Variable x, boolean doub) {
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

    private Atom tr(OWLPropertyExpression<?, ?> r, Variable x, Variable y,
	    boolean doub) {
	if (r instanceof OWLObjectProperty)
	    return atom(pred(sym(r), 2), x, y);
	else if (r instanceof OWLDataProperty)
	    return atom(pred(sym(r), 2), x, y);
	else
	    return atom(pred(sym(r), 2, doub), y, x);
    }

    public Rule[] translateDouble(OWLClassAssertionAxiom alpha) {
	final OWLClassExpression c = alpha.getClassExpression();
	if (!(c instanceof OWLClass))
	    throw new IllegalArgumentException(
		    "assertion's concepts must be atomic");
	final String aSym = sym((OWLClass) c);
	final Predicate a = doubPred(aSym, 1);
	final Predicate na = negPred(aSym, 1);
	final Constant i = cons(sym(alpha.getIndividual()));
	return new Rule[] { rule(atom(a, i), negLiteral(na, i)) };
    }

    public Rule[] translateDouble(OWLDisjointClassesAxiom alpha) {
	final List<OWLClassExpression> ops = alpha.getClassExpressionsAsList();
	final OWLClassExpression b1 = ops.get(0);
	final OWLClassExpression b2 = ops.get(1);
	return new Rule[] { rule(negTr(b1, X), negTr(b2, X)),
		rule(negTr(b2, X), negTr(b1, X)) };
    }

    public Rule[] translateDouble(OWLDisjointDataPropertiesAxiom alpha) {
	final Iterator<OWLDataPropertyExpression> ops = alpha.getProperties()
		.iterator();
	final OWLDataPropertyExpression q1 = ops.next();
	final OWLDataPropertyExpression q2 = ops.next();
	return new Rule[] { rule(negTr(q1, X, Y), negTr(q2, X, Y)),
		rule(negTr(q2, X, Y), negTr(q1, X, Y)) };
    }

    public Rule[] translateDouble(OWLDisjointObjectPropertiesAxiom alpha) {
	final Iterator<OWLObjectPropertyExpression> ops = alpha.getProperties()
		.iterator();
	final OWLObjectPropertyExpression q1 = ops.next();
	final OWLObjectPropertyExpression q2 = ops.next();
	return new Rule[] { rule(negTr(q1, X, Y), negTr(q2, X, Y)),
		rule(negTr(q2, X, Y), negTr(q1, X, Y)) };
    }

    public Rule[] translateDouble(
	    OWLPropertyAssertionAxiom<?, OWLIndividual> alpha) {
	final String aSym = sym(alpha.getProperty());
	final Predicate p = doubPred(aSym, 2);
	final Predicate np = negPred(aSym, 2);
	final Constant i1 = cons(sym(alpha.getSubject()));
	final Constant i2 = cons(sym(alpha.getObject()));
	return new Rule[] { rule(atom(p, i1, i2), negLiteral(np, i1, i2)) };
    }

    public Rule[] translateDouble(OWLSubClassOfAxiom alpha) {
	final OWLClassExpression b1 = alpha.getSubClass();
	final OWLClassExpression b2 = alpha.getSuperClass();
	return new Rule[] {
		rule(doubTr(b2, X), doubTr(b1, X), negLiteral(negTr(b2, X))),
		rule(negTr(b1, X), negTr(b2, X)) };
    }

    public Rule[] translateDouble(OWLSubPropertyAxiom<?> alpha) {
	final OWLPropertyExpression<?, ?> q1 = alpha.getSubProperty();
	final OWLPropertyExpression<?, ?> q2 = alpha.getSuperProperty();
	return new Rule[] {
		rule(doubTr(q2, X, Y), doubTr(q1, X, Y),
			negLiteral(negTr(q2, X, Y))),
			rule(negTr(q1, X, Y), negTr(q2, X, Y)) };
    }

    public Rule translateDoubleDomain(
	    OWLSubPropertyAxiom<OWLObjectPropertyExpression> alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return rule(doubTrExist(q2, X), doubTrExist(q1, X),
		negLiteral(negTrExist(q2, X)));
    }

    public Rule translateDoubleRange(
	    OWLSubPropertyAxiom<OWLObjectPropertyExpression> alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty()
		.getInverseProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty()
		.getInverseProperty();
	return rule(doubTrExist(q2, X), doubTrExist(q1, X),
		negLiteral(negTrExist(q2, X)));
    }

    public Rule translateIrreflexive(OWLObjectProperty p) {
	return rule(negTr(p, X, X));
    }

    public Rule[] translateOriginal(OWLClassAssertionAxiom alpha) {
	final OWLClassExpression c = alpha.getClassExpression();
	if (!(c instanceof OWLClass))
	    throw new IllegalAccessError("assertion's concepts must be atomic");
	final Predicate a = origPred(sym((OWLClass) c), 1);
	final Constant i = cons(sym(alpha.getIndividual()));
	return new Rule[] { rule(atom(a, i)) };
    }

    public Rule[] translateOriginal(
	    OWLPropertyAssertionAxiom<?, OWLIndividual> alpha) {
	final Predicate p = origPred(sym(alpha.getProperty()), 2);
	final Constant i1 = cons(sym(alpha.getSubject()));
	final Constant i2 = cons(sym(alpha.getObject()));
	return new Rule[] { rule(atom(p, i1, i2)) };
    }

    public Rule[] translateOriginal(OWLSubClassOfAxiom alpha) {
	final OWLClassExpression b1 = alpha.getSubClass();
	final OWLClassExpression b2 = alpha.getSuperClass();
	return new Rule[] { rule(origTr(b2, X), origTr(b1, X)) };
    }

    public Rule[] translateOriginal(OWLSubPropertyAxiom<?> alpha) {
	final OWLPropertyExpression<?, ?> q1 = alpha.getSubProperty();
	final OWLPropertyExpression<?, ?> q2 = alpha.getSuperProperty();
	return new Rule[] { rule(origTr(q2, X, Y), origTr(q1, X, Y)) };
    }

    public Rule translateOriginalDomain(
	    OWLSubPropertyAxiom<OWLObjectPropertyExpression> alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return rule(origTrExist(q2, X), origTrExist(q1, X));
    }

    public Rule translateOriginalRange(
	    OWLSubPropertyAxiom<OWLObjectPropertyExpression> alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty()
		.getInverseProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty()
		.getInverseProperty();
	return rule(origTrExist(q2, X), origTrExist(q1, X));
    }

    public Rule translateUnsatisfaible(OWLClass a) {
	return rule(negTr(a, X));
    }

    public Rule translateUnsatisfaible(OWLProperty<?, ?> p) {
	return rule(negTr(p, X, Y));
    }

}
