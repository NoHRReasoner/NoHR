/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.ruleSet;
import static pt.unl.fct.di.centria.nohr.model.Model.var;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.domPred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.negPred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.ranPred;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
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
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * @author nunocosta
 */
public abstract class QLAxiomsTranslator {

	protected static final Variable X = var("X");

	protected static final Variable Y = var("Y");

	private int opeNewCount = 0;
	protected final OWLOntology ontology;

	public QLAxiomsTranslator(OWLOntology ontology) {
		this.ontology = ontology;
	}

	protected Atom negTr(OWLClassExpression c, Variable x) {
		if (c instanceof OWLClass)
			return atom(negPred((OWLClass) c), x);
		else if (c instanceof OWLObjectSomeValuesFrom)
			return negTr(((OWLObjectSomeValuesFrom) c).getProperty(), x, var("_"));
		else
			throw new IllegalArgumentException("c must be an atomic or existential class");
	}

	protected Atom negTr(OWLPropertyExpression<?, ?> r, Variable x, Variable y) {
		if (r instanceof OWLObjectProperty)
			return atom(negPred(r), x, y);
		else if (r instanceof OWLDataProperty)
			return atom(negPred(r), x, y);
		else
			return atom(negPred(r), y, x);
	}

	protected Atom negTrExist(OWLObjectPropertyExpression q, Variable X) {
		if (q instanceof OWLObjectProperty)
			return atom(negPred(q), X, var("_"));
		else
			return atom(negPred(q), var("_"), X);
	}

	private OWLObjectProperty newConcept() {
		return ontology.getOWLOntologyManager().getOWLDataFactory()
				.getOWLObjectProperty(IRI.create("PNEW" + opeNewCount++));
	}

	private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression ope) {
		final OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		return dataFactory.getOWLObjectSomeValuesFrom(ope, dataFactory.getOWLThing());
	}

	protected Atom tr(OWLClassExpression c, Variable x, boolean doub) {
		if (c instanceof OWLClass)
			return atom(pred((OWLClass) c, doub), x);
		else if (c instanceof OWLObjectSomeValuesFrom) {
			final OWLObjectPropertyExpression q = ((OWLObjectSomeValuesFrom) c).getProperty();
			if (q instanceof OWLObjectProperty)
				return atom(domPred(q, doub), x);
			else
				return atom(ranPred(q, doub), x);
		} else if (c instanceof OWLObjectComplementOf) {
			final OWLClassExpression b = ((OWLObjectComplementOf) c).getOperand();
			if (b instanceof OWLClass)
				return atom(negPred((OWLClass) b), x);
			else if (b instanceof OWLObjectSomeValuesFrom)
				return negTr(((OWLObjectSomeValuesFrom) b).getProperty(), x, var("_"));
		}
		throw new IllegalArgumentException();
	}

	protected Atom tr(OWLPropertyExpression<?, ?> r, Variable x, Variable y, boolean doub) {
		if (r instanceof OWLObjectProperty)
			return atom(pred(r, doub), x, y);
		else if (r instanceof OWLDataProperty)
			return atom(pred(r, doub), x, y);
		else
			return atom(pred(r, doub), y, x);
	}

	protected abstract Set<Rule> translate(OWLClassAssertionAxiom alpha);

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
		final List<OWLDataPropertyExpression> ops = new LinkedList<OWLDataPropertyExpression>(alpha.getProperties());
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

	protected abstract Set<Rule> translate(OWLPropertyAssertionAxiom<?, ?> alpha);

	public Set<Rule> translate(OWLSubClassOfAxiom alpha) {
		final OWLClassExpression b1 = alpha.getSubClass();
		final OWLClassExpression b2 = alpha.getSuperClass();
		final Set<Rule> result = new HashSet<Rule>();
		result.addAll(translateSubsumption(b1, b2));
		return result;
	}

	public Set<Rule> translate(OWLSubPropertyAxiom<?> alpha) {
		final OWLPropertyExpression<?, ?> ope1 = alpha.getSubProperty();
		final OWLPropertyExpression<?, ?> ope2 = alpha.getSuperProperty();
		final Set<Rule> result = new HashSet<Rule>();
		result.addAll(translateSubsumption(ope1, ope2));
		return result;
	}

	protected abstract Set<Rule> translateBasicSubsumption(OWLClassExpression b1, OWLClassExpression b2);

	protected abstract Set<Rule> translateDisjunction(OWLClassExpression b1, OWLClassExpression b2);

	protected abstract Set<Rule> translateDisjunction(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2);

	public abstract Rule translateDomain(OWLObjectProperty p);

	public abstract Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha);

	public abstract Rule translateRange(OWLObjectProperty p);

	public abstract Rule translateRange(OWLSubObjectPropertyOfAxiom alpha);

	protected Set<Rule> translateSubsumption(OWLClassExpression ce1, OWLClassExpression ce2) {
		if (ce1.isBottomEntity() || ce2.isTopEntity())
			return ruleSet();
		if (ce2.isBottomEntity())
			return ruleSet(translateUnsatisfaible((OWLClass) ce1));
		if (ce1 instanceof OWLDataSomeValuesFrom || ce2 instanceof OWLDataSomeValuesFrom)
			return ruleSet();
		if (ce2 instanceof OWLObjectComplementOf)
			return translateSubsumption(ce1, (OWLObjectComplementOf) ce2);
		else if (ce2 instanceof OWLObjectIntersectionOf)
			return translateSubsumption(ce1, (OWLObjectIntersectionOf) ce2);
		return translateBasicSubsumption(ce1, ce2);
	}

	protected Set<Rule> translateSubsumption(OWLClassExpression b1, OWLObjectComplementOf c2) {
		return translateDisjunction(b1, c2.getOperand());
	}

	protected Set<Rule> translateSubsumption(OWLClassExpression b1, OWLObjectIntersectionOf c2) {
		final List<OWLClassExpression> ops = c2.getOperandsAsList();
		final Set<Rule> result = new HashSet<Rule>();
		for (final OWLClassExpression bi : ops)
			result.addAll(translateSubsumption(b1, bi));
		return result;
	}

	protected Set<Rule> translateSubsumption(OWLClassExpression ce1, OWLObjectSomeValuesFrom ce2) {
		final OWLObjectPropertyExpression ope = ce2.getProperty();
		final OWLObjectPropertyExpression opeNew = newConcept();
		final OWLObjectPropertyExpression invOpeNew = opeNew.getInverseProperty();
		final OWLClassExpression c = ce2.getFiller();
		if (c.isOWLThing())
			return translateBasicSubsumption(ce1, ce2);
		final Set<Rule> result = new HashSet<Rule>();
		result.addAll(translateSubsumption(opeNew, ope));
		result.addAll(translateSubsumption(some(invOpeNew), c));
		result.addAll(translateSubsumption(ce1, some(opeNew)));
		return result;
	}

	protected abstract Set<Rule> translateSubsumption(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2);

	public Rule translateUnsatisfaible(OWLClass a) {
		return rule(negTr(a, X));
	}

	public Rule translateUnsatisfaible(OWLProperty<?, ?> p) {
		return rule(negTr(p, X, Y));
	}

}
