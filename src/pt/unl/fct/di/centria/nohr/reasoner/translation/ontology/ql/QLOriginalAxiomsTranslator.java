/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.origDomPred;
import static pt.unl.fct.di.centria.nohr.model.Model.origPred;
import static pt.unl.fct.di.centria.nohr.model.Model.origRanPred;
import static pt.unl.fct.di.centria.nohr.model.Model.rule;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * @author nunocosta
 *
 */
public class QLOriginalAxiomsTranslator extends AbstractQLAxiomsTranslator {

    public QLOriginalAxiomsTranslator(OWLOntology ontology) {
	super(ontology);
    }

    private Atom existTr(OWLObjectPropertyExpression q, Variable X) {
	if (q instanceof OWLObjectProperty)
	    return atom(origDomPred(sym(q)), X);
	else
	    return atom(origRanPred(sym(q)), X);
    }

    private Atom tr(OWLClassExpression c, Variable x) {
	return tr(c, x, false);
    }

    private Atom tr(OWLPropertyExpression r, Variable x, Variable y) {
	return tr(r, x, y, false);
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom alpha) {
	return translateOriginal(alpha);
    }

    @Override
    public Set<Rule> translate(OWLPropertyAssertionAxiom alpha) {
	return translateOriginal(alpha);
    }

    @Override
    protected Set<Rule> translateBasicSubsumption(OWLClassExpression b1,
	    OWLClassExpression b2) {
	return ruleSet(rule(tr(b2, X), tr(b1, X)));
    }

    @Override
    protected Set<Rule> translateDisjunction(OWLClassExpression b1,
	    OWLClassExpression owlClassExpression) {
	return ruleSet();
    }

    @Override
    protected Set<Rule> translateDisjunction(OWLPropertyExpression q1,
	    OWLPropertyExpression q2) {
	return ruleSet();
    }

    @Override
    public Rule translateDomain(OWLObjectProperty p) {
	final String pSym = sym(p);
	return rule(atom(origDomPred(pSym), X),
		atom(origPred(pSym, 2), X, ANNON));
    }

    @Override
    public Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty();
	return rule(existTr(q2, X), existTr(q1, X));
    }

    @Override
    public Rule translateRange(OWLObjectProperty p) {
	final String pSym = sym(p);
	return rule(atom(origRanPred(pSym), X),
		atom(origPred(pSym, 2), ANNON, X));
    }

    @Override
    public Rule translateRange(OWLSubObjectPropertyOfAxiom alpha) {
	final OWLObjectPropertyExpression q1 = alpha.getSubProperty()
		.getInverseProperty();
	final OWLObjectPropertyExpression q2 = alpha.getSuperProperty()
		.getInverseProperty();
	return rule(existTr(q2, X), existTr(q1, X));
    }

    @Override
    protected Set<Rule> translateSubsumption(OWLPropertyExpression q1,
	    OWLPropertyExpression q2) {
	if (q1.isTopEntity() || q1.isBottomEntity() || q2.isTopEntity())
	    return ruleSet();
	if (q2.isBottomEntity())
	    return ruleSet(translateUnsatisfaible((OWLProperty) q1));
	return ruleSet(rule(tr(q2, X, Y), tr(q1, X, Y)));
    }
}
