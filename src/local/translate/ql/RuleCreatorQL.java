package local.translate.ql;

import java.util.ArrayList;
import java.util.List;

import local.translate.CollectionsManager;
import local.translate.OntologyLabel;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import xsb.NegativeTerm;
import xsb.Rule;

import com.declarativa.interprolog.TermModel;

public class RuleCreatorQL {

	protected final TermModel DOM_ATOM = new TermModel("dom");
	protected final TermModel RAN_ATOM = new TermModel("ran");
	protected final String X = "X";
	protected final String Y = "Y";
	protected final String ANNON_VAR = "_";
	
	protected CollectionsManager cm;
	protected OntologyLabel ol;

	protected TermCodifier tc;

	public RuleCreatorQL(CollectionsManager cm, OntologyLabel ol) {
		this.cm = cm;
		this.ol = ol;
		tc = new TermCodifier(ol, cm);
	}

	protected TermModel trAssertion(OWLClass a, OWLIndividual c, boolean d) {
		TermModel[] consts = { new TermModel(tc.getConstant(c)) };
		return new TermModel(tc.getPredicate(a, d), consts);
	}

	protected TermModel trAssertion(OWLObjectProperty p, OWLIndividual c1,
			OWLIndividual c2, boolean d) {
		TermModel[] consts = { new TermModel(tc.getConstant(c1)),
				new TermModel(tc.getConstant(c2)) };
		return new TermModel(tc.getPredicate(p, d), consts);
	}
	
	protected TermModel trNegAssertion(OWLClass a, OWLIndividual c) {
		TermModel[] consts = { new TermModel(tc.getConstant(c)) };
		return new TermModel(tc.getNegativePredicate(a), consts);
	}

	protected TermModel trNegAssertion(OWLObjectProperty p, OWLIndividual c1,
			OWLIndividual c2) {
		TermModel[] consts = { new TermModel(tc.getConstant(c1)),
				new TermModel(tc.getConstant(c2)) };
		return new TermModel(tc.getNegativePredicate(p), consts);
	}

	protected TermModel trAtomic(OWLClass c, String x, boolean d) {
		TermModel[] vars = { new TermModel(x) };
		return new TermModel(tc.getPredicate(c, d), vars);
	}

	protected TermModel trAtomic(OWLObjectProperty p, String x, String y,
			boolean d) {
		TermModel[] vars = { new TermModel(x), new TermModel(y) };
		return new TermModel(tc.getPredicate(p, d), vars);
	}

	protected TermModel trAtomic(String p, String x, String y) {
		TermModel[] vars = { new TermModel(x), new TermModel(y) };
		return new TermModel(new TermModel(p), vars);
	}

	protected TermModel trNegatedAtomic(OWLClass c, String x) {
		TermModel[] vars = { new TermModel(x) };
		return new TermModel(tc.getNegativePredicate(c), vars);
	}

	protected TermModel trNegatedAtomic(OWLObjectProperty p, String x, String y) {
		TermModel[] vars = { new TermModel(x), new TermModel(y) };
		return new TermModel(tc.getNegativePredicate(p), vars);
	}

	protected TermModel trExistential(OWLObjectProperty p, String x,
			boolean inverse, boolean d) {
		TermModel[] functArgs = { tc.getPredicate(p, d) };
		TermModel funct = new TermModel(!inverse ? DOM_ATOM : RAN_ATOM,
				functArgs);
		TermModel[] vars = { new TermModel(x) };
		return new TermModel(funct, vars);
	}

	protected TermModel trExistential(String pVar, String x, boolean inverse) {
		TermModel[] functVars = { new TermModel(pVar) };
		TermModel funct = new TermModel(!inverse ? DOM_ATOM : RAN_ATOM,
				functVars);
		TermModel[] vars = { new TermModel(x) };
		return new TermModel(funct, vars);
	}

	// *****************************************************************************
	// DL-Lite_R translation
	// *****************************************************************************

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

	protected TermModel tr(OWLObjectPropertyExpression p, String x, String y,
			boolean d) {
		if (!DLUtils.isInverse(p))
			return trAtomic(p.getNamedProperty(), x, y, d);
		else
			return trAtomic(p.getNamedProperty(), y, x, d);
	}

	private TermModel trNeg(OWLObjectPropertyExpression p, String x, String y) {
		if (!DLUtils.isInverse(p))
			return trNegatedAtomic(p.getNamedProperty(), x, y);
		else
			return trNegatedAtomic(p.getNamedProperty(), y, x);
	}

	protected List<Rule> e() {
		List<Rule> result = new ArrayList<Rule>();
		String pVar = "P";
		result.add(new Rule(trExistential(pVar, X, false), trAtomic(pVar, X,
				ANNON_VAR)));
		result.add(new Rule(trExistential(pVar, X, true), trAtomic(pVar,
				ANNON_VAR, X)));
		write(result);
		return result;
	}

	protected List<Rule> a1(OWLClass a, OWLIndividual c) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trAssertion(a, c, false)));
		if (cm.isAnyDisjointStatement())
			result.add(new Rule(trAssertion(a, c, true), new NegativeTerm(
					new TermModel[] { trNegAssertion(a, c) })));
		write(result);
		return result;
	}

	protected List<Rule> a2(OWLObjectProperty p, OWLIndividual c1,
			OWLIndividual c2) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trAssertion(p, c1, c2, false)));
		if (cm.isAnyDisjointStatement())
			result.add(new Rule(trAssertion(p, c1, c2, true), new NegativeTerm(
					new TermModel[] { trNegAssertion(p, c1, c2) })));
		write(result);
		return result;
	}

	protected List<Rule> s1(OWLSubClassOfAxiom subsumption) {
		OWLClassExpression b1 = subsumption.getSubClass();
		OWLClassExpression b2 = subsumption.getSuperClass();
		return s1(b1, b2);
	}

	protected List<Rule> s1(OWLClassExpression b1, OWLClassExpression b2) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(tr(b2, X, false), tr(b1, X, false)));
		//if (cm.isAnyDisjointStatement())
			result.add(new Rule(tr(b2, X, true), tr(b1, X, true),
					new NegativeTerm(new TermModel[] { trNeg(b2, X) })));
		result.add(new Rule(trNeg(b1, X), trNeg(b2, X)));
		write(result);
		return result;
	}

	protected List<Rule> s2(OWLSubObjectPropertyOfAxiom subsumption) {
		OWLObjectPropertyExpression q1 = subsumption.getSubProperty();
		OWLObjectPropertyExpression q2 = subsumption.getSuperProperty();
		return s2(q1, q2);
	}

	protected List<Rule> s2(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(tr(q2, X, Y, false), tr(q1, X, Y, false)));
		//if (cm.isAnyDisjointStatement())
			result.add(new Rule(tr(q2, X, Y, true), tr(q1, X, Y, true),
					new NegativeTerm(new TermModel[] { trNeg(q2, X, Y) })));
		result.add(new Rule(trNeg(q1, X, Y), trNeg(q2, X, Y)));
		write(result);
		return result;
	}

	protected List<Rule> n1(OWLClassExpression b1, OWLClassExpression b2) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trNeg(b1, X), tr(b2, X, false)));
		//if (cm.isAnyDisjointStatement())
			result.add(new Rule(trNeg(b2, X), tr(b1, X, false)));
		write(result);
		return result;
	}

	protected List<Rule> n2(OWLObjectPropertyExpression q1,
			OWLObjectPropertyExpression q2) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trNeg(q1, X, Y), tr(q2, X, Y, false)));
		//if (cm.isAnyDisjointStatement())
			result.add(new Rule(trNeg(q2, X, Y), tr(q1, X, Y, false)));
		write(result);
		return result;
	}

	protected List<Rule> i1(OWLClass c) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trNeg(c, X)));
		write(result);
		return result;
	}

	protected List<Rule> i2(OWLObjectProperty p) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trNeg(p, X, Y)));
		write(result);
		return result;
	}

	protected List<Rule> ir(OWLObjectProperty p) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(new Rule(trNeg(p, X, X)));
		write(result);
		return result;
	}

	public void write(List<Rule> rules) {
		for (Rule rule : rules)
			cm.addTranslatedOntology(rule.toString());
	}

	// *****************************************************************************

}
