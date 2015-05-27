package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.CollectionsManager;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabel;
import pt.unl.fct.di.centria.nohr.xsb.NotTerm;
import pt.unl.fct.di.centria.nohr.xsb.Rule;

import com.declarativa.interprolog.TermModel;

public class QLAxiomsTranslator {

    protected final TermModel DOM_ATOM = new TermModel("dom");
    protected final TermModel RAN_ATOM = new TermModel("ran");
    protected final String X = "X";
    protected final String Y = "Y";
    protected final String ANNON_VAR = "_";

    protected CollectionsManager cm;
    protected OntologyLabel ol;
    protected INormalizedOntology normOnt;

    protected TermCodifier tc;

    protected OWLOntologyManager om;

    public QLAxiomsTranslator(CollectionsManager cm, OntologyLabel ol,
	    INormalizedOntology normalizedOntology, OWLOntologyManager om) {
	this.cm = cm;
	this.ol = ol;
	normOnt = normalizedOntology;
	this.om = om;
	tc = new TermCodifier(ol, cm);
    }

    protected List<Rule> a1(OWLClass a, OWLIndividual c) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel o = trAssertion(a, c, false);
	result.add(new Rule(o));
	if (cm.isAnyDisjointStatement()) {
	    TermModel d = trAssertion(a, c, true);
	    TermModel n = trNegAssertion(a, c);
	    if (cm.isInNegHead(n)) {
		result.add(new Rule(d, new NotTerm(n)));
		cm.addTabled(n);
	    } else
		result.add(new Rule(d));
	}
	write(result);
	return result;
    }

    protected List<Rule> a2(OWLObjectProperty p, OWLIndividual c1,
	    OWLIndividual c2) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel o = trAssertion(p, c1, c2, false);
	result.add(new Rule(o));
	if (cm.isAnyDisjointStatement()) {
	    TermModel d = trAssertion(p, c1, c2, true);
	    TermModel n = trNegAssertion(p, c1, c2);
	    if (cm.isInNegHead(n)) {
		result.add(new Rule(d, new NotTerm(n)));
		cm.addTabled(n);
	    } else
		result.add(new Rule(d));
	}
	write(result);
	return result;
    }

    public void a2(OWLObjectPropertyAssertionAxiom propAssertion) {
	a2((OWLObjectProperty) propAssertion.getProperty(),
		propAssertion.getSubject(), propAssertion.getObject());
    }

    protected List<Rule> e(OWLObjectProperty p) {
	List<Rule> result = new ArrayList<Rule>();
	boolean hasSome = normOnt.getSubConcepts().contains(some(p));
	boolean hasInvSome = normOnt.getSubConcepts().contains(
		some(p.getInverseProperty()));
	if (hasSome) {
	    TermModel e = trExistential(p, X, false, false);
	    result.add(new Rule(e, tr(p, X, ANNON_VAR, false)));
	}
	if (hasInvSome) {
	    TermModel f = trExistential(p, X, true, false);
	    result.add(new Rule(f, tr(p, ANNON_VAR, X, false)));
	}
	if (cm.isAnyDisjointStatement()) {
	    if (hasSome) {
		TermModel g = trExistential(p, X, false, true);
		result.add(new Rule(g, tr(p, X, ANNON_VAR, false)));
	    }
	    if (hasInvSome) {
		TermModel h = trExistential(p, X, true, true);
		result.add(new Rule(h, tr(p, ANNON_VAR, X, false)));
	    }
	}
	write(result);
	return result;
    }

    protected List<Rule> i1(OWLClass c) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel n = trNeg(c, X);
	result.add(new Rule(n));
	cm.addTabled(n);
	write(result);
	return result;
    }

    protected List<Rule> i2(OWLProperty<?, ?> p) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel n = trNeg(p, X, Y);
	result.add(new Rule(n));
	cm.addTabled(n);
	write(result);
	return result;
    }

    protected List<Rule> ir(OWLObjectProperty p) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel n = trNeg(p, X, X);
	cm.addTabled(n);
	write(result);
	return result;
    }

    protected List<Rule> n1(OWLClassExpression b1, OWLClassExpression b2) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel n1 = trNeg(b1, X);
	TermModel n2 = trNeg(b2, X);
	result.add(new Rule(n1, tr(b2, X, false)));
	result.add(new Rule(n2, tr(b1, X, false)));
	cm.addTabled(n1);
	cm.addTabled(n2);
	write(result);
	return result;
    }

    protected List<Rule> n2(OWLPropertyExpression<?, ?> q1,
	    OWLPropertyExpression<?, ?> q2) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel n1 = trNeg(q1, X, Y);
	TermModel n2 = trNeg(q2, X, Y);
	result.add(new Rule(n1, tr(q2, X, Y, false)));
	result.add(new Rule(n2, tr(q1, X, Y, false)));
	cm.addTabled(n1);
	cm.addTabled(n2);
	write(result);
	return result;
    }

    protected List<Rule> s1(OWLClassExpression b1, OWLClassExpression b2) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel o1 = tr(b1, X, false);
	TermModel o2 = tr(b2, X, false);
	// boolean table = normOnt.getSubConcepts().contains(b2);
	result.add(new Rule(o2, o1));
	if (cm.isAnyDisjointStatement()) {
	    TermModel d1 = tr(b1, X, true);
	    TermModel d2 = tr(b2, X, true);
	    TermModel n1 = trNeg(b1, X);
	    TermModel n2 = trNeg(b2, X);
	    if (cm.isInNegHead(n2)) {
		result.add(new Rule(d2, d1, new NotTerm(n2)));
		cm.addTabled(n2);
	    } else
		result.add(new Rule(d2, d1));
	    result.add(new Rule(n1, n2));
	    // if (table)
	    cm.addTabled(d2);
	    cm.addTabled(n1);
	}
	// if (table)
	cm.addTabled(o2);
	write(result);
	return result;
    }

    protected List<Rule> s2(OWLPropertyExpression<?, ?> q1,
	    OWLPropertyExpression<?, ?> q2) {
	List<Rule> result = new ArrayList<Rule>();
	boolean table = true; // normOnt.getSubRoles().contains(DLUtils.getRoleName(q2));
	boolean s = false;
	boolean si = false;
	if (q1 instanceof OWLObjectPropertyExpression
		&& q2 instanceof OWLObjectPropertyExpression) {
	    OWLObjectPropertyExpression op1 = (OWLObjectPropertyExpression) q1;
	    OWLObjectPropertyExpression op2 = (OWLObjectPropertyExpression) q2;
	    s = normOnt.getSuperConcepts().contains(some(op1))
		    && normOnt.getSubConcepts().contains(some(op2));
	    si = normOnt.getSuperConcepts().contains(
		    some(op1.getInverseProperty()))
		    && normOnt.getSubConcepts().contains(
			    some(op2.getInverseProperty()));

	}
	TermModel o1 = tr(q1, X, Y, false);
	TermModel o2 = tr(q2, X, Y, false);
	result.add(new Rule(o2, o1));
	if (table)
	    cm.addTabled(o2);
	if (s) {
	    TermModel e1 = trExistential(q1, X, false, false);
	    TermModel e2 = trExistential(q2, X, false, false);
	    result.add(new Rule(e2, e1));
	    cm.addTabled(e2);
	}
	if (si) {
	    TermModel f1 = trExistential(q1, X, true, false);
	    TermModel f2 = trExistential(q2, X, true, false);
	    result.add(new Rule(f2, f1));
	    cm.addTabled(f2);
	}
	if (cm.isAnyDisjointStatement()) {
	    TermModel d1 = tr(q1, X, Y, true);
	    TermModel d2 = tr(q2, X, Y, true);
	    TermModel n1 = trNeg(q1, X, Y);
	    TermModel n2 = trNeg(q2, X, Y);
	    if (cm.isInNegHead(n2)) {
		result.add(new Rule(d2, d1, new NotTerm(n2)));
		cm.addTabled(n2);
	    } else
		result.add(new Rule(d2, d1));
	    if (s) {
		TermModel g1 = trExistential(q1, X, false, true);
		TermModel g2 = trExistential(q2, X, false, true);
		if (cm.isInNegHead(n2))
		    result.add(new Rule(g2, g1, new NotTerm(n2)));
		else
		    result.add(new Rule(g2, g1));
		cm.addTabled(g2);
	    }
	    if (si) {
		TermModel h1 = trExistential(q1, X, true, true);
		TermModel h2 = trExistential(q2, X, true, true);
		if (cm.isInNegHead(n2))
		    result.add(new Rule(h2, h1, new NotTerm(trNeg(q2, Y, X))));
		else
		    result.add(new Rule(h2, h1));
		cm.addTabled(h2);
	    }
	    result.add(new Rule(n1, n2));
	    cm.addTabled(n1);
	    if (table)
		cm.addTabled(d2);
	}
	write(result);
	return result;
    }

    // *****************************************************************************
    // DL-Lite_R translation
    // *****************************************************************************

    protected OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression q) {
	OWLDataFactory df = om.getOWLDataFactory();
	return df.getOWLObjectSomeValuesFrom(q, df.getOWLThing());
    }

    protected TermModel tr(OWLClassExpression c, String x, boolean d) {
	if (DLUtils.isAtomic(c))
	    return trAtomic(c.asOWLClass(), x, d);
	else if (DLUtils.isExistential(c)) {
	    OWLObjectSomeValuesFrom b = (OWLObjectSomeValuesFrom) c;
	    OWLObjectPropertyExpression p = b.getProperty();
	    return trExistential(p.getNamedProperty(), x, DLUtils.isInverse(p),
		    d);
	} else
	    return null;
    }

    protected TermModel tr(OWLPropertyExpression<?, ?> p, String x, String y,
	    boolean d) {
	if (!DLUtils.isInverse(p))
	    return trAtomic((OWLProperty<?, ?>) p, x, y, d);
	else
	    return trAtomic(
		    ((OWLObjectPropertyExpression) p).getNamedProperty(), y, x,
		    d);
    }

    public List<Rule> translateDataPropertyAssertion(
	    OWLDataProperty dataProperty, OWLIndividual individual,
	    OWLLiteral value) {
	List<Rule> result = new ArrayList<Rule>();
	TermModel o = trAssertion(dataProperty, individual, value, false);
	result.add(new Rule(o));
	if (cm.isAnyDisjointStatement()) {
	    TermModel d = trAssertion(dataProperty, individual, value, true);
	    TermModel n = trNegAssertion(dataProperty, individual, value);
	    if (cm.isInNegHead(n)) {
		result.add(new Rule(d, new NotTerm(n)));
		cm.addTabled(n);
	    } else
		result.add(new Rule(d));
	}
	write(result);
	return result;
    }

    protected TermModel trAssertion(OWLClass a, OWLIndividual c, boolean d) {
	TermModel[] consts = { new TermModel(tc.getConstant(c)) };
	return new TermModel(tc.getPredicate(a, d), consts);
    }

    protected TermModel trAssertion(OWLDataProperty dataProperty,
	    OWLIndividual individual, OWLLiteral value, boolean d) {
	TermModel[] consts = { new TermModel(tc.getConstant(individual)),
		new TermModel(tc.getConstant(value)) };
	return new TermModel(tc.getPredicate(dataProperty, d), consts);
    }

    protected TermModel trAssertion(OWLObjectProperty p, OWLIndividual c1,
	    OWLIndividual c2, boolean d) {
	TermModel[] consts = { new TermModel(tc.getConstant(c1)),
		new TermModel(tc.getConstant(c2)) };
	return new TermModel(tc.getPredicate(p, d), consts);
    }

    protected TermModel trAtomic(OWLClass c, String x, boolean d) {
	TermModel[] vars = { new TermModel(x) };
	return new TermModel(tc.getPredicate(c, d), vars);
    }

    protected TermModel trAtomic(OWLProperty<?, ?> p, String x, String y,
	    boolean d) {
	TermModel[] vars = { new TermModel(x), new TermModel(y) };
	return new TermModel(tc.getPredicate(p, d), vars);
    }

    protected TermModel trExistential(OWLPropertyExpression<?, ?> q, String x,
	    boolean inverse, boolean d) {
	TermModel[] vars = { new TermModel(x) };
	boolean isInverse = DLUtils.isInverse(q);
	OWLProperty<?, ?> p = !isInverse ? (OWLProperty<?, ?>) q
		: ((OWLObjectPropertyExpression) q).getNamedProperty();
	return new TermModel(tc.getExistPredicate(p, inverse == isInverse, d),
		vars);
    }

    protected TermModel trNeg(OWLClassExpression c, String x) {
	if (DLUtils.isAtomic(c))
	    return trNegatedAtomic(c.asOWLClass(), x);
	else if (DLUtils.isExistential(c)) {
	    OWLObjectSomeValuesFrom b = (OWLObjectSomeValuesFrom) c;
	    OWLObjectPropertyExpression p = b.getProperty();
	    return trNeg(p, x, ANNON_VAR);
	} else
	    return null;
    }

    protected TermModel trNeg(OWLPropertyExpression<?, ?> p, String x, String y) {
	if (!DLUtils.isInverse(p))
	    return trNegatedAtomic((OWLProperty<?, ?>) p, x, y);
	else
	    return trNegatedAtomic(
		    ((OWLObjectPropertyExpression) p).getNamedProperty(), y, x);
    }

    protected TermModel trNegAssertion(OWLClass a, OWLIndividual c) {
	TermModel[] consts = { new TermModel(tc.getConstant(c)) };
	return new TermModel(tc.getNegativePredicate(a), consts);
    }

    protected TermModel trNegAssertion(OWLDataProperty dataProperty,
	    OWLIndividual individual, OWLLiteral value) {
	TermModel[] consts = { new TermModel(tc.getConstant(individual)),
		new TermModel(tc.getConstant(value)) };
	return new TermModel(tc.getNegativePredicate(dataProperty), consts);
    }

    // TODO remove unused methods

    protected TermModel trNegAssertion(OWLObjectProperty p, OWLIndividual c1,
	    OWLIndividual c2) {
	TermModel[] consts = { new TermModel(tc.getConstant(c1)),
		new TermModel(tc.getConstant(c2)) };
	return new TermModel(tc.getNegativePredicate(p), consts);
    }

    protected TermModel trNegatedAtomic(OWLClass c, String x) {
	TermModel[] vars = { new TermModel(x) };
	return new TermModel(tc.getNegativePredicate(c), vars);
    }

    // *****************************************************************************

    protected TermModel trNegatedAtomic(OWLProperty<?, ?> p, String x, String y) {
	TermModel[] vars = { new TermModel(x), new TermModel(y) };
	return new TermModel(tc.getNegativePredicate(p), vars);
    }

    public void write(List<Rule> rules) {
	for (Rule rule : rules)
	    cm.addTranslatedOntology(rule.toString());
    }

}
