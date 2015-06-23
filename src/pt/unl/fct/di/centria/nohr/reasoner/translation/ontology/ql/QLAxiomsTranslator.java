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

    protected final String ANNON_VAR = "_";
    protected final TermModel DOM_ATOM = new TermModel("dom");
    private final TBoxGraph graph;
    private final boolean hasDisjunctions;
    private Set<String> negatedPredicates;

    protected INormalizedOntology normOnt;
    protected OntologyLabeler ol;

    protected OWLOntologyManager om;

    protected final TermModel RAN_ATOM = new TermModel("ran");

    private final Set<String> tabledPredicates;

    protected TermCodifier tc;

    protected final String X = "X";

    protected final String Y = "Y";

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
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel o = trAssertion(a, c, false);
	result.add(new XsbRule(o));
	if (hasDisjunctions) {
	    final TermModel d = trAssertion(a, c, true);
	    final TermModel n = trNegAssertion(a, c);
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
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel o = trAssertion(p, c1, c2, false);
	result.add(new XsbRule(o));
	if (hasDisjunctions) {
	    final TermModel d = trAssertion(p, c1, c2, true);
	    final TermModel n = trNegAssertion(p, c1, c2);
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
	for (final OWLClassExpression b : normOnt.getSubConcepts())
	    addNegated(trNeg(b, X));
	for (final OWLClassExpression b : normOnt.getDisjointConcepts())
	    addNegated(trNeg(b, X));
	for (final OWLPropertyExpression q : normOnt.getSubRoles())
	    addNegated(trNeg(q, X, Y));
	for (final OWLPropertyExpression q : normOnt.getDisjointRoles())
	    addNegated(trNeg(q, X, Y));
	for (final OWLEntity e : graph.getUnsatisfiableEntities())
	    if (e instanceof OWLClass)
		addNegated(trNeg((OWLClass) e, X));
	    else if (e instanceof OWLProperty)
		addNegated(trNeg((OWLProperty) e, X, Y));
	for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
	    addNegated(trNeg(p, X, Y));
    }

    protected Set<XsbRule> e(OWLObjectProperty p) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final boolean hasSome = normOnt.getSubConcepts().contains(some(p));
	final boolean hasInvSome = normOnt.getSubConcepts().contains(
		some(p.getInverseProperty()));
	if (hasSome) {
	    final TermModel e = trExistential(p, X, false, false);
	    result.add(new XsbRule(e, tr(p, X, ANNON_VAR, false)));
	}
	if (hasInvSome) {
	    final TermModel f = trExistential(p, X, true, false);
	    result.add(new XsbRule(f, tr(p, ANNON_VAR, X, false)));
	}
	if (hasDisjunctions) {
	    if (hasSome) {
		final TermModel g = trExistential(p, X, false, true);
		result.add(new XsbRule(g, tr(p, X, ANNON_VAR, false)));
	    }
	    if (hasInvSome) {
		final TermModel h = trExistential(p, X, true, true);
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
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel n = trNeg(c, X);
	result.add(new XsbRule(n));
	addTabled(n);

	return result;
    }

    protected Set<XsbRule> i2(OWLProperty p) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel n = trNeg(p, X, Y);
	result.add(new XsbRule(n));
	addTabled(n);

	return result;
    }

    protected Set<XsbRule> ir(OWLObjectProperty p) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel n = trNeg(p, X, X);
	addTabled(n);

	return result;
    }

    private boolean isNegated(TermModel tm) {
	return negatedPredicates.contains(tm.getFunctorArity());
    }

    protected Set<XsbRule> n1(OWLClassExpression b1, OWLClassExpression b2) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel n1 = trNeg(b1, X);
	final TermModel n2 = trNeg(b2, X);
	result.add(new XsbRule(n1, tr(b2, X, false)));
	result.add(new XsbRule(n2, tr(b1, X, false)));
	addTabled(n1);
	addTabled(n2);

	return result;
    }

    protected Set<XsbRule> n2(OWLPropertyExpression q1, OWLPropertyExpression q2) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel n1 = trNeg(q1, X, Y);
	final TermModel n2 = trNeg(q2, X, Y);
	result.add(new XsbRule(n1, tr(q2, X, Y, false)));
	result.add(new XsbRule(n2, tr(q1, X, Y, false)));
	addTabled(n1);
	addTabled(n2);

	return result;
    }

    protected Set<XsbRule> s1(OWLClassExpression b1, OWLClassExpression b2) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel o1 = tr(b1, X, false);
	final TermModel o2 = tr(b2, X, false);
	// boolean table = normOnt.getSubConcepts().contains(b2);
	result.add(new XsbRule(o2, o1));
	if (hasDisjunctions) {
	    final TermModel d1 = tr(b1, X, true);
	    final TermModel d2 = tr(b2, X, true);
	    final TermModel n1 = trNeg(b1, X);
	    final TermModel n2 = trNeg(b2, X);
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

    protected Set<XsbRule> s2(OWLPropertyExpression q1, OWLPropertyExpression q2) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final boolean table = true; // normOnt.getSubRoles().contains(DLUtils.getRoleName(q2));
	boolean s = false;
	boolean si = false;
	if (q1 instanceof OWLObjectPropertyExpression
		&& q2 instanceof OWLObjectPropertyExpression) {
	    final OWLObjectPropertyExpression op1 = (OWLObjectPropertyExpression) q1;
	    final OWLObjectPropertyExpression op2 = (OWLObjectPropertyExpression) q2;
	    s = normOnt.getSuperConcepts().contains(some(op1))
		    && normOnt.getSubConcepts().contains(some(op2));
	    si = normOnt.getSuperConcepts().contains(
		    some(op1.getInverseProperty()))
		    && normOnt.getSubConcepts().contains(
			    some(op2.getInverseProperty()));

	}
	final TermModel o1 = tr(q1, X, Y, false);
	final TermModel o2 = tr(q2, X, Y, false);
	result.add(new XsbRule(o2, o1));
	if (table)
	    addTabled(o2);
	if (s) {
	    final TermModel e1 = trExistential(q1, X, false, false);
	    final TermModel e2 = trExistential(q2, X, false, false);
	    result.add(new XsbRule(e2, e1));
	    addTabled(e2);
	}
	if (si) {
	    final TermModel f1 = trExistential(q1, X, true, false);
	    final TermModel f2 = trExistential(q2, X, true, false);
	    result.add(new XsbRule(f2, f1));
	    addTabled(f2);
	}
	if (hasDisjunctions) {
	    final TermModel d1 = tr(q1, X, Y, true);
	    final TermModel d2 = tr(q2, X, Y, true);
	    final TermModel n1 = trNeg(q1, X, Y);
	    final TermModel n2 = trNeg(q2, X, Y);
	    if (isNegated(n2)) {
		result.add(new XsbRule(d2, d1, new NotTerm(n2)));
		addTabled(n2);
	    } else
		result.add(new XsbRule(d2, d1));
	    if (s) {
		final TermModel g1 = trExistential(q1, X, false, true);
		final TermModel g2 = trExistential(q2, X, false, true);
		if (isNegated(n2))
		    result.add(new XsbRule(g2, g1, new NotTerm(n2)));
		else
		    result.add(new XsbRule(g2, g1));
		addTabled(g2);
	    }
	    if (si) {
		final TermModel h1 = trExistential(q1, X, true, true);
		final TermModel h2 = trExistential(q2, X, true, true);
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
	final OWLDataFactory df = om.getOWLDataFactory();
	return df.getOWLObjectSomeValuesFrom(q, df.getOWLThing());
    }

    protected TermModel tr(OWLClassExpression c, String x, boolean d) {
	if (DLUtils.isAtomic(c))
	    return trAtomic(c.asOWLClass(), x, d);
	else if (DLUtils.isExistential(c)) {
	    final OWLObjectSomeValuesFrom b = (OWLObjectSomeValuesFrom) c;
	    final OWLObjectPropertyExpression p = b.getProperty();
	    return trExistential(p.getNamedProperty(), x, DLUtils.isInverse(p),
		    d);
	} else
	    return null;
    }

    protected TermModel tr(OWLPropertyExpression p, String x, String y,
	    boolean d) {
	if (!DLUtils.isInverse(p))
	    return trAtomic((OWLProperty) p, x, y, d);
	else
	    return trAtomic(
		    ((OWLObjectPropertyExpression) p).getNamedProperty(), y, x,
		    d);
    }

    Set<XsbRule> translate(OWLClassAssertionAxiom f) {
	final OWLClass a = f.getClassExpression().asOWLClass();
	if (a.isOWLThing() || a.isOWLNothing())
	    return null;
	final OWLIndividual i = f.getIndividual();
	return a1(a, i);
    }

    Set<XsbRule> translate(OWLDataPropertyAssertionAxiom f) {
	final OWLDataProperty dataProperty = (OWLDataProperty) f.getProperty();
	if (dataProperty.isOWLTopDataProperty()
		|| dataProperty.isOWLBottomDataProperty())
	    return null;
	final OWLIndividual individual = f.getSubject();
	final OWLLiteral value = f.getObject();
	return translateDataPropertyAssertion(dataProperty, individual, value);
    }

    Set<XsbRule> translate(OWLDisjointClassesAxiom alpha) {
	final List<OWLClassExpression> cls = alpha.getClassExpressionsAsList();
	return n1(cls.get(0), cls.get(1));
    }

    Set<XsbRule> translate(OWLNaryPropertyAxiom d) {
	final Iterator<OWLPropertyExpression> dIt = d.getProperties()
		.iterator();
	final OWLPropertyExpression p1 = dIt.next();
	final OWLPropertyExpression p2 = dIt.next();
	return n2(p1, p2);
    }

    Set<XsbRule> translate(OWLObjectPropertyAssertionAxiom f) {
	final OWLObjectPropertyExpression q = f.getProperty();
	if (q.isOWLBottomObjectProperty() || q.isOWLTopObjectProperty())
	    return null;
	return a2(f);
    }

    Set<XsbRule> translate(OWLSubClassOfAxiom alpha) {
	final OWLClassExpression b = alpha.getSubClass();
	final OWLClassExpression c = alpha.getSuperClass();
	return s1(b, c);
    }

    Set<XsbRule> translate(OWLSubDataPropertyOfAxiom alpha) {
	final OWLDataPropertyExpression q1 = alpha.getSubProperty();
	final OWLDataPropertyExpression q2 = alpha.getSuperProperty();
	return s2(q1, q2);
    }

    Set<XsbRule> translate(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return s2(q1, q2);
    }

    private Set<XsbRule> translateDataPropertyAssertion(
	    OWLDataProperty dataProperty, OWLIndividual individual,
	    OWLLiteral value) {
	final Set<XsbRule> result = new HashSet<XsbRule>();
	final TermModel o = trAssertion(dataProperty, individual, value, false);
	result.add(new XsbRule(o));
	if (hasDisjunctions) {
	    final TermModel d = trAssertion(dataProperty, individual, value,
		    true);
	    final TermModel n = trNegAssertion(dataProperty, individual, value);
	    if (isNegated(n)) {
		result.add(new XsbRule(d, new NotTerm(n)));
		addTabled(n);
	    } else
		result.add(new XsbRule(d));
	}

	return result;
    }

    protected TermModel trAssertion(OWLClass a, OWLIndividual c, boolean d) {
	final TermModel[] consts = { new TermModel(tc.getConstant(c)) };
	return new TermModel(tc.getPredicate(a, d), consts);
    }

    protected TermModel trAssertion(OWLDataProperty dataProperty,
	    OWLIndividual individual, OWLLiteral value, boolean d) {
	final TermModel[] consts = { new TermModel(tc.getConstant(individual)),
		new TermModel(tc.getConstant(value)) };
	return new TermModel(tc.getPredicate(dataProperty, d), consts);
    }

    // TODO remove unused methods

    protected TermModel trAssertion(OWLObjectProperty p, OWLIndividual c1,
	    OWLIndividual c2, boolean d) {
	final TermModel[] consts = { new TermModel(tc.getConstant(c1)),
		new TermModel(tc.getConstant(c2)) };
	return new TermModel(tc.getPredicate(p, d), consts);
    }

    protected TermModel trAtomic(OWLClass c, String x, boolean d) {
	final TermModel[] vars = { new TermModel(x) };
	return new TermModel(tc.getPredicate(c, d), vars);
    }

    // *****************************************************************************

    protected TermModel trAtomic(OWLProperty p, String x, String y, boolean d) {
	final TermModel[] vars = { new TermModel(x), new TermModel(y) };
	return new TermModel(tc.getPredicate(p, d), vars);
    }

    protected TermModel trExistential(OWLPropertyExpression q, String x,
	    boolean inverse, boolean d) {
	final TermModel[] vars = { new TermModel(x) };
	final boolean isInverse = DLUtils.isInverse(q);
	final OWLProperty p = !isInverse ? (OWLProperty) q
		: ((OWLObjectPropertyExpression) q).getNamedProperty();
	return new TermModel(tc.getExistPredicate(p, inverse == isInverse, d),
		vars);
    }

    protected TermModel trNeg(OWLClassExpression c, String x) {
	if (DLUtils.isAtomic(c))
	    return trNegatedAtomic(c.asOWLClass(), x);
	else if (DLUtils.isExistential(c)) {
	    final OWLObjectSomeValuesFrom b = (OWLObjectSomeValuesFrom) c;
	    final OWLObjectPropertyExpression p = b.getProperty();
	    return trNeg(p, x, ANNON_VAR);
	} else
	    return null;
    }

    protected TermModel trNeg(OWLPropertyExpression p, String x, String y) {
	if (!DLUtils.isInverse(p))
	    return trNegatedAtomic((OWLProperty) p, x, y);
	else
	    return trNegatedAtomic(
		    ((OWLObjectPropertyExpression) p).getNamedProperty(), y, x);
    }

    protected TermModel trNegAssertion(OWLClass a, OWLIndividual c) {
	final TermModel[] consts = { new TermModel(tc.getConstant(c)) };
	return new TermModel(tc.getNegativePredicate(a), consts);
    }

    protected TermModel trNegAssertion(OWLDataProperty dataProperty,
	    OWLIndividual individual, OWLLiteral value) {
	final TermModel[] consts = { new TermModel(tc.getConstant(individual)),
		new TermModel(tc.getConstant(value)) };
	return new TermModel(tc.getNegativePredicate(dataProperty), consts);
    }

    protected TermModel trNegAssertion(OWLObjectProperty p, OWLIndividual c1,
	    OWLIndividual c2) {
	final TermModel[] consts = { new TermModel(tc.getConstant(c1)),
		new TermModel(tc.getConstant(c2)) };
	return new TermModel(tc.getNegativePredicate(p), consts);
    }

    protected TermModel trNegatedAtomic(OWLClass c, String x) {
	final TermModel[] vars = { new TermModel(x) };
	return new TermModel(tc.getNegativePredicate(c), vars);
    }

    protected TermModel trNegatedAtomic(OWLProperty p, String x, String y) {
	final TermModel[] vars = { new TermModel(x), new TermModel(y) };
	return new TermModel(tc.getNegativePredicate(p), vars);
    }

}
