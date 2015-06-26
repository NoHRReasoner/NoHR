/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.doubDomPred;
import static pt.unl.fct.di.centria.nohr.model.Model.doubPred;
import static pt.unl.fct.di.centria.nohr.model.Model.doubRanPred;
import static pt.unl.fct.di.centria.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.Model.negPred;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * @author nunocosta
 *
 */
public class QLDoubleAxiomsTranslator extends AbstractQLAxiomsTranslator {

    public QLDoubleAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
    }

    private Atom existTr(OWLObjectPropertyExpression q, Variable X) {
	if (q instanceof OWLObjectProperty)
	    return atom(doubDomPred(sym(q)), X);
	else
	    return atom(doubRanPred(sym(q)), X);
    }

    private Atom tr(OWLClassExpression c, Variable x) {
	return tr(c, x, true);
    }

    private Atom tr(OWLPropertyExpression r, Variable x, Variable y) {
	return tr(r, x, y, true);
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom alpha) {
	final OWLClassExpression c = alpha.getClassExpression();
	if (!(c instanceof OWLClass))
	    throw new IllegalArgumentException(
		    "assertion's concepts must be atomic");
	final String aSym = sym((OWLClass) c);
	final Predicate a = doubPred(aSym, 1);
	final Predicate na = negPred(aSym, 1);
	final Constant i = cons(sym(alpha.getIndividual()));
	return ruleSet(rule(atom(a, i), negLiteral(na, i)));
    }

    @Override
    public Set<Rule> translate(OWLPropertyAssertionAxiom alpha) {
	final String aSym = sym(alpha.getProperty());
	final Predicate p = doubPred(aSym, 2);
	final Predicate np = negPred(aSym, 2);
	final Constant i1 = cons(sym(alpha.getSubject()));
	final Constant i2 = cons(sym(alpha.getObject()));
	return ruleSet(rule(atom(p, i1, i2), negLiteral(np, i1, i2)));
    }

    @Override
    protected Set<Rule> translateBasicSubsumption(OWLClassExpression b1,
	    OWLClassExpression b2) {
	return ruleSet(rule(tr(b2, X), tr(b1, X), negLiteral(negTr(b2, X))),
		rule(negTr(b1, X), negTr(b2, X)));
    }

    @Override
    protected Set<Rule> translateDisjunction(OWLClassExpression b1,
	    OWLClassExpression b2) {
	if (b1.isBottomEntity() || b2.isBottomEntity())
	    return ruleSet();
	if (b1.isOWLThing())
	    return ruleSet(translateUnsatisfaible((OWLClass) b2));
	if (b2.isOWLThing())
	    return ruleSet(translateUnsatisfaible((OWLClass) b1));
	return ruleSet(rule(negTr(b1, X), tr(b2, X, false)),
		rule(negTr(b2, X), tr(b1, X, false)));
    }

    @Override
    protected Set<Rule> translateDisjunction(OWLPropertyExpression q1,
	    OWLPropertyExpression q2) {
	if (q1.isBottomEntity() || q2.isBottomEntity())
	    return ruleSet();
	if (q1.isTopEntity())
	    return ruleSet(translateUnsatisfaible((OWLProperty) q2));
	if (q2.isTopEntity())
	    return ruleSet(translateUnsatisfaible((OWLProperty) q1));
	return ruleSet(rule(negTr(q1, X, Y), tr(q2, X, Y, false)),
		rule(negTr(q2, X, Y), tr(q1, X, Y, false)));
    }

    @Override
    public Rule translateDomain(OWLObjectProperty p) {
	final String pSym = sym(p);
	return rule(atom(doubDomPred(pSym), X),
		atom(doubPred(pSym, 2), X, ANNON));
    }

    @Override
    public Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return rule(existTr(q2, X), existTr(q1, X),
		negLiteral(negTrExist(q2, X)));
    }

    public Rule translateIrreflexive(OWLObjectProperty p) {
	return rule(negTr(p, X, X));
    }

    @Override
    public Rule translateRange(OWLObjectProperty p) {
	final String pSym = sym(p);
	return rule(atom(doubRanPred(pSym), X),
		atom(doubPred(pSym, 2), ANNON, X));
    }

    @Override
    public Rule translateRange(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty()
		.getInverseProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty()
		.getInverseProperty();
	return rule(existTr(q2, X), existTr(q1, X),
		negLiteral(negTrExist(q2, X)));
    }

    @Override
    protected Set<Rule> translateSubsumption(OWLPropertyExpression q1,
	    OWLPropertyExpression q2) {
	if (q1.isTopEntity() || q1.isBottomEntity() || q2.isTopEntity())
	    return ruleSet();
	if (q2.isBottomEntity())
	    return translateDisjunction(q1, q1);
	return ruleSet(
		rule(tr(q2, X, Y), tr(q1, X, Y), negLiteral(negTr(q2, X, Y))),
		rule(negTr(q1, X, Y), negTr(q2, X, Y)));
    }

}
