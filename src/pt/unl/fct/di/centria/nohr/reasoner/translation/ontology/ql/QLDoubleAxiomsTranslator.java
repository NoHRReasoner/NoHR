/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.Model.var;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.doubDomPred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.doubPred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.doubRanPred;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * @author nunocosta
 *
 */
public class QLDoubleAxiomsTranslator extends AbstractQLAxiomsTranslator {

    public QLDoubleAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
    }

    Atom existTr(OWLObjectPropertyExpression q, Variable X) {
	if (!DLUtils.isInverse(q))
	    return atom(doubDomPred(q), X);
	else
	    return atom(doubRanPred(q.getNamedProperty()), X);
    }

    Atom tr(OWLClassExpression c, Variable x) {
	return tr(c, x, true);
    }

    Atom tr(OWLPropertyExpression<?, ?> r, Variable x, Variable y) {
	return tr(r, x, y, true);
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom alpha) {
	return translateDouble(alpha);
    }

    @Override
    public Set<Rule> translate(OWLPropertyAssertionAxiom<?, ?> alpha) {
	return translateDouble(alpha);
    }

    @Override
    protected Set<Rule> translateBasicSubsumption(OWLClassExpression b1, OWLClassExpression b2) {
	if (b1.isOWLThing())
	    ruleSet(rule(tr(b2, X), negLiteral(negTr(b2, X), true)));
	return ruleSet(rule(tr(b2, X), tr(b1, X), negLiteral(negTr(b2, X), true)), rule(negTr(b1, X), negTr(b2, X)));
    }

    @Override
    protected Set<Rule> translateDisjunction(OWLClassExpression b1, OWLClassExpression b2) {
	if (b1.isBottomEntity() || b2.isBottomEntity())
	    return ruleSet();
	if (b1.isOWLThing())
	    return ruleSet(translateUnsatisfaible((OWLClass) b2));
	if (b2.isOWLThing())
	    return ruleSet(translateUnsatisfaible((OWLClass) b1));
	return ruleSet(rule(negTr(b1, X), tr(b2, X, false)), rule(negTr(b2, X), tr(b1, X, false)));
    }

    @Override
    protected Set<Rule> translateDisjunction(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2) {
	if (q1.isBottomEntity() || q2.isBottomEntity())
	    return ruleSet();
	if (q1.isTopEntity())
	    return ruleSet(translateUnsatisfaible((OWLProperty<?, ?>) q2));
	if (q2.isTopEntity())
	    return ruleSet(translateUnsatisfaible((OWLProperty<?, ?>) q1));
	return ruleSet(rule(negTr(q1, X, Y), tr(q2, X, Y, false)), rule(negTr(q2, X, Y), tr(q1, X, Y, false)));
    }

    @Override
    public Rule translateDomain(OWLObjectProperty p) {
	return rule(atom(doubDomPred(p), X), atom(doubPred(p), X, var()));
    }

    @Override
    public Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return rule(existTr(q2, X), existTr(q1, X), negLiteral(negTrExist(q2, X), true));
    }

    public Rule translateIrreflexive(OWLObjectProperty p) {
	return rule(negTr(p, X, X));
    }

    @Override
    public Rule translateRange(OWLObjectProperty p) {
	return rule(atom(doubRanPred(p), X), atom(doubPred(p), var(), X));
    }

    @Override
    public Rule translateRange(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty().getInverseProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty().getInverseProperty();
	return rule(existTr(q2, X), existTr(q1, X), negLiteral(negTrExist(q2, X), true));
    }

    @Override
    protected Set<Rule> translateSubsumption(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2) {
	if (q1.isBottomEntity() || q2.isTopEntity())
	    return ruleSet();
	if (q1.isTopEntity())
	    return ruleSet(rule(tr(q2, X, Y), negLiteral(negTr(q2, X, Y))));
	if (q2.isBottomEntity())
	    return translateDisjunction(q1, q1);
	return ruleSet(rule(tr(q2, X, Y), tr(q1, X, Y), negLiteral(negTr(q2, X, Y))),
		rule(negTr(q1, X, Y), negTr(q2, X, Y)));
    }

}
