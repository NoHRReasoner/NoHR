/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.*;

import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.*;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AssertionsTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.DLUtils;

/**
 * @author nunocosta
 */
public class QLOriginalAxiomsTranslator extends QLAxiomsTranslator {

	private Atom existTr(OWLObjectPropertyExpression q, Variable X) {
		if (!DLUtils.isInverse(q))
			return atom(origDomPred(q), X);
		else
			return atom(origRanPred(q), X);
	}

	private Atom tr(OWLClassExpression c, Variable x) {
		return tr(c, x, false);
	}

	private Atom tr(OWLPropertyExpression<?, ?> r, Variable x, Variable y) {
		return tr(r, x, y, false);
	}

	@Override
	public Set<Rule> translateAssertion(OWLClassAssertionAxiom alpha) {
		return AssertionsTranslation.translateOriginal(alpha);
	}

	@Override
	public Set<Rule> translateAssertion(OWLPropertyAssertionAxiom<?, ?> alpha) {
		return AssertionsTranslation.translateOriginal(alpha);
	}

	@Override
	public Set<Rule> translateDisjunction(OWLClassExpression b1, OWLClassExpression owlClassExpression) {
		return ruleSet();
	}

	@Override
	public Set<Rule> translateDisjunction(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2) {
		return ruleSet();
	}

	@Override
	public Rule translateDomain(OWLObjectProperty p) {
		return rule(atom(origDomPred(p), X), atom(origPred(p), X, var()));
	}

	@Override
	public Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha) {
		final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
		final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
		return rule(existTr(q2, X), existTr(q1, X));
	}

	@Override
	public Rule translateRange(OWLObjectProperty p) {
		return rule(atom(origRanPred(p), X), atom(origPred(p), var(), X));
	}

	@Override
	public Rule translateRange(OWLSubObjectPropertyOfAxiom alpha) {
		final OWLObjectPropertyExpression q1 = alpha.getSubProperty().getInverseProperty();
		final OWLObjectPropertyExpression q2 = alpha.getSuperProperty().getInverseProperty();
		return rule(existTr(q2, X), existTr(q1, X));
	}

	@Override
	public Set<Rule> translateSubsumption(OWLClassExpression b1, OWLClassExpression b2) {
		if (b1.isOWLThing())
			return ruleSet(rule(tr(b2, X)));
		return ruleSet(rule(tr(b2, X), tr(b1, X)));
	}

	@Override
	public Set<Rule> translateSubsumption(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2) {
		if (q1.isBottomEntity() || q2.isTopEntity())
			return ruleSet();
		if (q1.isTopEntity())
			return ruleSet(rule(tr(q2, X, Y)));
		if (q2.isBottomEntity())
			return translateUnsatisfaible((OWLProperty<?, ?>) q1);
		return ruleSet(rule(tr(q2, X, Y), tr(q1, X, Y)));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLAxiomsTranslator#translateUnsatisfaible(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public Set<Rule> translateUnsatisfaible(OWLClass a) {
		return ruleSet();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLAxiomsTranslator#translateUnsatisfaible(org.semanticweb.owlapi.model.OWLProperty)
	 */
	@Override
	public Set<Rule> translateUnsatisfaible(OWLProperty<?, ?> p) {
		return ruleSet();
	}
}
