package local.translate.ql;

import java.util.ArrayList;
import java.util.List;

import local.translate.CollectionsManager;
import local.translate.DLUtils;
import local.translate.OntologyLabel;
import local.translate.PredicateCodifier;
import local.translate.RuleCreator;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import xsb.NegativeTerm;
import xsb.Rule;

import com.declarativa.interprolog.TermModel;

public class RuleCreatorQL extends RuleCreator {

	protected final TermModel DOM_ATOM = new TermModel("dom");
	protected final TermModel RAN_ATOM = new TermModel("ran");

	protected PredicateCodifier pc;

	public RuleCreatorQL(CollectionsManager cm, OntologyLabel ol) {
		super(cm, ol);
		pc = new PredicateCodifier(ol, cm);
	}

	protected TermModel trAtomic(OWLClass c, String x, boolean d) {
		TermModel[] vars = { new TermModel(x) };
		return new TermModel(pc.getPredicate(c, d), vars);
	}

	protected TermModel trAtomic(OWLObjectProperty p, String x, String y,
			boolean d) {
		TermModel[] vars = { new TermModel(x), new TermModel(y) };
		return new TermModel(pc.getPredicate(p, d), vars);
	}

	protected TermModel trAtomic(String p, String x, String y) {
		TermModel[] vars = { new TermModel(x), new TermModel(y) };
		return new TermModel(new TermModel(p), vars);
	}

	protected TermModel trNegatedAtomic(OWLClass c, String x) {
		TermModel[] vars = { new TermModel(x) };
		return new TermModel(pc.getNegativePredicate(c), vars);
	}

	protected TermModel trNegatedAtomic(OWLObjectProperty p, String x, String y) {
		TermModel[] vars = { new TermModel(x), new TermModel(y) };
		return new TermModel(pc.getNegativePredicate(p), vars);
	}

	protected TermModel trExistential(OWLObjectProperty p, String x,
			boolean inverse, boolean d) {
		TermModel[] functArgs = { pc.getPredicate(p, d) };
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
			return trNeg(p, x, "_");
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

	// (a1)
	// already handled in OntologyProceeder

	// (a2)
	// already handled in OntologyProceeder

	protected List<Rule> e() {
		List<Rule> result = new ArrayList<Rule>();
		String pVar = "P";
		String x = "X";
		result.add(new Rule(trExistential(pVar, x, false), trAtomic(pVar, x,
				"_")));
		result.add(new Rule(trExistential(pVar, x, true),
				trAtomic(pVar, "_", x)));
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
		String x = "X";
		result.add(new Rule(tr(b2, x, false), tr(b1, x, false)));
		result.add(new Rule(tr(b2, x, true), tr(b1, x, true), new NegativeTerm(
				new TermModel[] { trNeg(b2, x) })));
		result.add(new Rule(trNeg(b1, x), trNeg(b2, x)));
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
		String x = "X";
		String y = "Y";
		result.add(new Rule(tr(q2, x, y, false), tr(q1, x, y, false)));
		result.add(new Rule(tr(q2, x, y, true), tr(q1, x, y, true),
				new NegativeTerm(new TermModel[] { trNeg(q2, x, y) })));
		result.add(new Rule(trNeg(q1, x, y), trNeg(q2, x, y)));
		write(result);
		return result;
	}

	// (n1)
	// already handled in OntologyProceeder

	protected List<Rule> n2(OWLPropertyExpression property1,
			OWLPropertyExpression property2) {
		return null;
		// TODO implement
	}

	protected List<Rule> i1(OWLClass cls) {
		return null;
		// TODO implement
	}

	protected List<Rule> i2(OWLProperty prop) {
		return null;
		// TODO implement
	}

	private List<Rule> ir(OWLObjectProperty prop) {
		return null;
		// TODO implement
	}

	public void write(List<Rule> rules) {
		for (Rule rule : rules)
			super.writeLineToFile(rule.toString());
	}

	// *****************************************************************************

}
