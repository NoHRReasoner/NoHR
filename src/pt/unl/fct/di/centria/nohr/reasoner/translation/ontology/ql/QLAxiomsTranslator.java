package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabeler;
import pt.unl.fct.di.centria.nohr.xsb.NotTerm;
import pt.unl.fct.di.centria.nohr.xsb.XsbRule;

import com.declarativa.interprolog.TermModel;

public class QLAxiomsTranslator {

    protected final TermModel DOM_ATOM = new TermModel("dom");
    protected final TermModel RAN_ATOM = new TermModel("ran");
    protected final String X = "X";
    protected final String Y = "Y";
    protected final String ANNON_VAR = "_";

    protected OntologyLabeler ol;
    protected INormalizedOntology normOnt;

    protected TermCodifier tc;

    protected OWLOntologyManager om;

    private boolean hasDisjunctions;

    private Set<String> negatedPredicates;

    private TBoxGraph graph;

    private Set<String> tabledPredicates;

    public QLAxiomsTranslator(OntologyLabeler ol,
	    INormalizedOntology normalizedOntology, OWLOntologyManager om,
	    boolean hasDisjunctions, TBoxGraph graph) {
	this.ol = ol;
	normOnt = normalizedOntology;
	this.om = om;
	this.hasDisjunctions = hasDisjunctions;
	tc = new TermCodifier(ol);
	this.graph = graph;
	tabledPredicates = new HashSet<String>();
    }

    protected Set<XsbRule> a1(OWLClass a, OWLIndividual c) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel o = trAssertion(a, c, false);
	result.add(new XsbRule(o));
	if (hasDisjunctions) {
	    TermModel d = trAssertion(a, c, true);
	    TermModel n = trNegAssertion(a, c);
	    if (isNegated(n)) {
		result.add(new XsbRule(d, new NotTerm(n)));
		addTabled(n);
	    } else
		result.add(new XsbRule(d));
	}
	return result;
    }

    protected Set<XsbRule> a2(OWLObjectProperty p, OWLIndividual c1,
	    OWLIndividual c2) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel o = trAssertion(p, c1, c2, false);
	result.add(new XsbRule(o));
	if (hasDisjunctions) {
	    TermModel d = trAssertion(p, c1, c2, true);
	    TermModel n = trNegAssertion(p, c1, c2);
	    if (isNegated(n)) {
		result.add(new XsbRule(d, new NotTerm(n)));
		addTabled(n);
	    } else
		result.add(new XsbRule(d));
	}
	return result;
    }

    private Set<XsbRule> a2(OWLObjectPropertyAssertionAxiom propAssertion) {
	return a2((OWLObjectProperty) propAssertion.getProperty(),
		propAssertion.getSubject(), propAssertion.getObject());
    }

    private void addNegated(TermModel tm) {
	negatedPredicates.add(tm.getFunctorArity());
    }

    private void addTabled(TermModel tm) {
	tabledPredicates.add(tm.getFunctorArity());
    }

    public void computeNegHeads() {
	negatedPredicates = new HashSet<String>();
	for (OWLClassExpression b : normOnt.getSubConcepts())
	    addNegated(trNeg(b, X));
	for (OWLClassExpression b : normOnt.getDisjointConcepts())
	    addNegated(trNeg(b, X));
	for (OWLPropertyExpression q : normOnt.getSubRoles())
	    addNegated(trNeg(q, X, Y));
	for (OWLPropertyExpression q : normOnt.getDisjointRoles())
	    addNegated(trNeg(q, X, Y));
	for (OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		addNegated(trNeg((OWLClass) e, X));
	    else if (e instanceof OWLProperty)
		addNegated(trNeg((OWLProperty) e, X, Y));
	for (OWLObjectProperty p : graph.getIrreflexiveRoles())
	    addNegated(trNeg(p, X, Y));
    }

    protected Set<XsbRule> e(OWLObjectProperty p) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	boolean hasSome = normOnt.getSubConcepts().contains(some(p));
	boolean hasInvSome = normOnt.getSubConcepts().contains(
		some(p.getInverseProperty()));
	if (hasSome) {
	    TermModel e = trExistential(p, X, false, false);
	    result.add(new XsbRule(e, tr(p, X, ANNON_VAR, false)));
	}
	if (hasInvSome) {
	    TermModel f = trExistential(p, X, true, false);
	    result.add(new XsbRule(f, tr(p, ANNON_VAR, X, false)));
	}
	if (hasDisjunctions) {
	    if (hasSome) {
		TermModel g = trExistential(p, X, false, true);
		result.add(new XsbRule(g, tr(p, X, ANNON_VAR, false)));
	    }
	    if (hasInvSome) {
		TermModel h = trExistential(p, X, true, true);
		result.add(new XsbRule(h, tr(p, ANNON_VAR, X, false)));
	    }
	}

	return result;
    }

    public Set<String> getNegatedPredicates() {
	return negatedPredicates;
    }

    public Set<String> getTabledPredicates() {
	return tabledPredicates;
    }

    protected Set<XsbRule> i1(OWLClass c) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel n = trNeg(c, X);
	result.add(new XsbRule(n));
	addTabled(n);

	return result;
    }

    protected Set<XsbRule> i2(OWLProperty<?, ?> p) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel n = trNeg(p, X, Y);
	result.add(new XsbRule(n));
	addTabled(n);

	return result;
    }

    protected Set<XsbRule> ir(OWLObjectProperty p) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel n = trNeg(p, X, X);
	addTabled(n);

	return result;
    }

    private boolean isNegated(TermModel tm) {
	return negatedPredicates.contains(tm.getFunctorArity());
    }

    protected Set<XsbRule> n1(OWLClassExpression b1, OWLClassExpression b2) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel n1 = trNeg(b1, X);
	TermModel n2 = trNeg(b2, X);
	result.add(new XsbRule(n1, tr(b2, X, false)));
	result.add(new XsbRule(n2, tr(b1, X, false)));
	addTabled(n1);
	addTabled(n2);

	return result;
    }

    protected Set<XsbRule> n2(OWLPropertyExpression<?, ?> q1,
	    OWLPropertyExpression<?, ?> q2) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel n1 = trNeg(q1, X, Y);
	TermModel n2 = trNeg(q2, X, Y);
	result.add(new XsbRule(n1, tr(q2, X, Y, false)));
	result.add(new XsbRule(n2, tr(q1, X, Y, false)));
	addTabled(n1);
	addTabled(n2);

	return result;
    }

    protected Set<XsbRule> s1(OWLClassExpression b1, OWLClassExpression b2) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel o1 = tr(b1, X, false);
	TermModel o2 = tr(b2, X, false);
	// boolean table = normOnt.getSubConcepts().contains(b2);
	result.add(new XsbRule(o2, o1));
	if (hasDisjunctions) {
	    TermModel d1 = tr(b1, X, true);
	    TermModel d2 = tr(b2, X, true);
	    TermModel n1 = trNeg(b1, X);
	    TermModel n2 = trNeg(b2, X);
	    if (isNegated(n2)) {
		result.add(new XsbRule(d2, d1, new NotTerm(n2)));
		addTabled(n2);
	    } else
		result.add(new XsbRule(d2, d1));
	    result.add(new XsbRule(n1, n2));
	    // if (table)
	    addTabled(d2);
	    addTabled(n1);
	}
	// if (table)
	addTabled(o2);

	return result;
    }

    protected Set<XsbRule> s2(OWLPropertyExpression<?, ?> q1,
	    OWLPropertyExpression<?, ?> q2) {
	Set<XsbRule> result = new HashSet<XsbRule>();
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
	result.add(new XsbRule(o2, o1));
	if (table)
	    addTabled(o2);
	if (s) {
	    TermModel e1 = trExistential(q1, X, false, false);
	    TermModel e2 = trExistential(q2, X, false, false);
	    result.add(new XsbRule(e2, e1));
	    addTabled(e2);
	}
	if (si) {
	    TermModel f1 = trExistential(q1, X, true, false);
	    TermModel f2 = trExistential(q2, X, true, false);
	    result.add(new XsbRule(f2, f1));
	    addTabled(f2);
	}
	if (hasDisjunctions) {
	    TermModel d1 = tr(q1, X, Y, true);
	    TermModel d2 = tr(q2, X, Y, true);
	    TermModel n1 = trNeg(q1, X, Y);
	    TermModel n2 = trNeg(q2, X, Y);
	    if (isNegated(n2)) {
		result.add(new XsbRule(d2, d1, new NotTerm(n2)));
		addTabled(n2);
	    } else
		result.add(new XsbRule(d2, d1));
	    if (s) {
		TermModel g1 = trExistential(q1, X, false, true);
		TermModel g2 = trExistential(q2, X, false, true);
		if (isNegated(n2))
		    result.add(new XsbRule(g2, g1, new NotTerm(n2)));
		else
		    result.add(new XsbRule(g2, g1));
		addTabled(g2);
	    }
	    if (si) {
		TermModel h1 = trExistential(q1, X, true, true);
		TermModel h2 = trExistential(q2, X, true, true);
		if (isNegated(n2))
		    result.add(new XsbRule(h2, h1, new NotTerm(trNeg(q2, Y, X))));
		else
		    result.add(new XsbRule(h2, h1));
		addTabled(h2);
	    }
	    result.add(new XsbRule(n1, n2));
	    addTabled(n1);
	    if (table)
		addTabled(d2);
	}

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

    Set<XsbRule> translate(OWLClassAssertionAxiom f) {
	OWLClass a = f.getClassExpression().asOWLClass();
	if (a.isOWLThing() || a.isOWLNothing())
	    return null;
	OWLIndividual i = f.getIndividual();
	return a1(a, i);
    }

    Set<XsbRule> translate(OWLDataPropertyAssertionAxiom f) {
	OWLDataProperty dataProperty = (OWLDataProperty) f.getProperty();
	if (dataProperty.isOWLTopDataProperty()
		|| dataProperty.isOWLBottomDataProperty())
	    return null;
	OWLIndividual individual = f.getSubject();
	OWLLiteral value = f.getObject();
	return translateDataPropertyAssertion(dataProperty, individual, value);
    }

    Set<XsbRule> translate(OWLDisjointClassesAxiom alpha) {
	List<OWLClassExpression> cls = alpha.getClassExpressionsAsList();
	return n1(cls.get(0), cls.get(1));
    }

    Set<XsbRule> translate(OWLNaryPropertyAxiom d) {
	Iterator<OWLPropertyExpression> dIt = d.getProperties().iterator();
	OWLPropertyExpression<?, ?> p1 = dIt.next();
	OWLPropertyExpression<?, ?> p2 = dIt.next();
	return n2(p1, p2);
    }

    Set<XsbRule> translate(OWLObjectPropertyAssertionAxiom f) {
	OWLObjectPropertyExpression q = f.getProperty();
	if (q.isOWLBottomObjectProperty() || q.isOWLTopObjectProperty())
	    return null;
	return a2(f);
    }

    Set<XsbRule> translate(OWLSubClassOfAxiom alpha) {
	OWLClassExpression b = alpha.getSubClass();
	OWLClassExpression c = alpha.getSuperClass();
	return s1(b, c);
    }

    Set<XsbRule> translate(OWLSubDataPropertyOfAxiom alpha) {
	OWLDataPropertyExpression q1 = alpha.getSubProperty();
	OWLDataPropertyExpression q2 = alpha.getSuperProperty();
	return s2(q1, q2);
    }

    Set<XsbRule> translate(OWLSubObjectPropertyOfAxiom alpha) {
	OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return s2(q1, q2);
    }

    private Set<XsbRule> translateDataPropertyAssertion(
	    OWLDataProperty dataProperty, OWLIndividual individual,
	    OWLLiteral value) {
	Set<XsbRule> result = new HashSet<XsbRule>();
	TermModel o = trAssertion(dataProperty, individual, value, false);
	result.add(new XsbRule(o));
	if (hasDisjunctions) {
	    TermModel d = trAssertion(dataProperty, individual, value, true);
	    TermModel n = trNegAssertion(dataProperty, individual, value);
	    if (isNegated(n)) {
		result.add(new XsbRule(d, new NotTerm(n)));
		addTabled(n);
	    } else
		result.add(new XsbRule(d));
	}

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

    // TODO remove unused methods

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

    // *****************************************************************************

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

    protected TermModel trNegatedAtomic(OWLProperty<?, ?> p, String x, String y) {
	TermModel[] vars = { new TermModel(x), new TermModel(y) };
	return new TermModel(tc.getNegativePredicate(p), vars);
    }

}
