package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLExpressionTranslator;

public class RLDoubledAxiomTranslator implements RLAxiomTranslator {

    private final RLExpressionTranslator expressionTranslator;
    private final Vocabulary vocabulary;

    public RLDoubledAxiomTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        this.expressionTranslator = new RLExpressionTranslator(vocabulary);
    }

    @Override
    public Collection<Rule> translate(OWLClassAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyDomainAxiom axiom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyRangeAxiom axiom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Rule> translate(OWLPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLSubClassOfAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        OWLClassExpression c = axiom.getSubClass();
        OWLClassExpression d = axiom.getSuperClass();

        for (OWLClassExpression i : c.asConjunctSet()) {
            if (i.isOWLNothing()) {
                return ret;
            }
        }

        if (d.isOWLNothing() && !c.isAnonymous()) {
            ret.add(Model.rule(Model.atom(vocabulary.negPred(c.asOWLClass()), RLExpressionTranslator.X(0))));
        } else if (d.isOWLNothing() && c.isAnonymous()) {
            for (final Literal b : expressionTranslator.tb(c, RLExpressionTranslator.X(0), false)) {
                final List<Literal> body = new ArrayList<Literal>(expressionTranslator.tb(c, RLExpressionTranslator.X(0), false));

                body.remove(b);

                ret.add(Model.rule(Model.atom(vocabulary.negPred(b.getFunctor()), b.getAtom().getArguments()), body));
            }
        } // (c1)
        else {
            List<Literal> body = new ArrayList<Literal>(expressionTranslator.tb(c, RLExpressionTranslator.X(0), true));

            body.add(Model.negLiteral(Model.atom(vocabulary.negPred(c.asOWLClass()), RLExpressionTranslator.X(0))));
            ret.add(Model.rule((Atom) expressionTranslator.tb(d, RLExpressionTranslator.X(0), true).get(0), body));

            for (final Literal b : expressionTranslator.tb(c, RLExpressionTranslator.X(0), false)) {
                body = new ArrayList<Literal>(expressionTranslator.tb(c, RLExpressionTranslator.X(0), false));

                body.add(Model.atom(vocabulary.negPred(c.asOWLClass()), RLExpressionTranslator.X(0)));
                body.remove(b);

                ret.add(Model.rule(Model.atom(vocabulary.negPred(b.getFunctor()), b.getAtom().getArguments()), body));
            }
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLSubPropertyAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        final OWLPropertyExpression p = axiom.getSubProperty();
        final OWLPropertyExpression q = axiom.getSuperProperty();

        if (p.isBottomEntity() || q.isTopEntity()) {
            return ret;
        }

        if (p.isTopEntity()) {
            ret.add(Model.rule(expressionTranslator.tr(q, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), true).get(0), Model.negLiteral(Model.atom(vocabulary.negPred(q), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)))));
        } else if (q.isBottomEntity()) {
            ret.add(Model.rule(Model.atom(vocabulary.negPred(p), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)),
                    expressionTranslator.tr(p, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), false)));
            ret.add(Model.rule(Model.atom(vocabulary.negPred(p), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)),
                    expressionTranslator.tr(p, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), false)));
        } else {
            ret.add(Model.rule(expressionTranslator.tr(q, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), true).get(0),
                    expressionTranslator.tr(p, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), true).get(0),
                    Model.negLiteral(Model.atom(vocabulary.negPred(q), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)))));
            ret.add(Model.rule(Model.atom(vocabulary.negPred(p), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)),
                    Model.atom(vocabulary.negPred(q), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1))));
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLTransitiveObjectPropertyAxiom axiom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Rule> translate(OWLDisjointClassesAxiom axiom) {
        Set<Rule> ret = new HashSet<>();

        for (final OWLDisjointClassesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLClassExpression> concepts = i.getClassExpressionsAsList();

            final OWLClassExpression c = concepts.get(0);
            final OWLClassExpression d = concepts.get(1);

            if (!c.isBottomEntity() && !d.isBottomEntity()) {
                ret.add(Model.rule(Model.atom(vocabulary.negPred((OWLClass) c), RLExpressionTranslator.X(0)), expressionTranslator.tb(d, RLExpressionTranslator.X(0), false)));
                ret.add(Model.rule(Model.atom(vocabulary.negPred((OWLClass) d), RLExpressionTranslator.X(0)), expressionTranslator.tb(c, RLExpressionTranslator.X(0), false)));
            }
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLDisjointDataPropertiesAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        for (final OWLDisjointDataPropertiesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLDataPropertyExpression> roles = new ArrayList<>(i.getProperties());

            final OWLDataPropertyExpression p = roles.get(0);
            final OWLDataPropertyExpression q = roles.get(1);

            ret.addAll(translateDisjointRoles(p, q));
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLDisjointObjectPropertiesAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        for (final OWLDisjointObjectPropertiesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLObjectPropertyExpression> roles = new ArrayList<>(i.getProperties());

            final OWLObjectPropertyExpression p = roles.get(0);
            final OWLObjectPropertyExpression q = roles.get(1);

            ret.addAll(translateDisjointRoles(p, q));
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLIrreflexiveObjectPropertyAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        ret.add(Model.rule(Model.atom(vocabulary.negPred(axiom.getProperty()), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1))));

        return ret;
    }

    private Collection<Rule> translateDisjointRoles(OWLPropertyExpression p, OWLPropertyExpression q) {
        final Set<Rule> ret = new HashSet<>();

        if (!p.isBottomEntity() && !q.isBottomEntity()) {
            if (p.isTopEntity()) {
                ret.add(Model.rule(Model.atom(vocabulary.negPred(q), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1))));
            } else if (q.isTopEntity()) {
                ret.add(Model.rule(Model.atom(vocabulary.negPred(p), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1))));
            } else {
                ret.add(Model.rule(Model.atom(vocabulary.negPred(p), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)), expressionTranslator.tr(q, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), false)));
                ret.add(Model.rule(Model.atom(vocabulary.negPred(q), RLExpressionTranslator.X(0), RLExpressionTranslator.X(1)), expressionTranslator.tr(p, RLExpressionTranslator.X(0), RLExpressionTranslator.X(1), false)));
            }
        }

        return ret;
    }

}
