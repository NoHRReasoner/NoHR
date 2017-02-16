package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;
import pt.unl.fct.di.novalincs.nohr.translation.UnsupportedAxiomException;

public class RLDoubledAxiomTranslator implements RLAxiomTranslator {

    private final Vocabulary vocabulary;
    private final RLExpressionTranslator expressionTranslator;

    public RLDoubledAxiomTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        this.expressionTranslator = new RLExpressionTranslator(vocabulary);
    }

    @Override
    public Collection<Rule> translate(OWLSubClassOfAxiom axiom) {
        final Set<Rule> rules = new HashSet<>();

        final Variable x = RLExpressionTranslator.X(0);

        OWLClassExpression subClass = axiom.getSubClass();
        OWLClassExpression superClass = axiom.getSuperClass();

        if (subClass.isOWLThing()) {
            if (superClass instanceof OWLClass) {
                final Atom head = expressionTranslator.th(subClass, x, true).get(0);
                final Literal body = Model.negLiteral(expressionTranslator.negtr(subClass, x));

                rules.add(Model.rule(head, body));
            }
        } else if (superClass.isOWLNothing() && !subClass.isAnonymous()) {
            Model.rule(expressionTranslator.negtr(subClass.asOWLClass(), x));
        } else if (superClass.isOWLNothing() && subClass.isAnonymous()) {
            for (final Literal literal : expressionTranslator.tb(subClass, x, false)) {
                final List<Literal> body = new ArrayList<Literal>(expressionTranslator.tb(subClass, x, false));

                body.remove(literal);

                rules.add(Model.rule(expressionTranslator.negtr(literal), body));
            }
        } else {
            List<Literal> body = new ArrayList<Literal>(expressionTranslator.tb(subClass, x, true));

            body.add(Model.negLiteral(expressionTranslator.negtr(superClass.asOWLClass(), x)));

            final Atom head = expressionTranslator.th(superClass, x, true).get(0);

            rules.add(Model.rule(head, body));

            for (final Literal literal : expressionTranslator.tb(subClass, x, false)) {
                body = new ArrayList<Literal>(expressionTranslator.tb(subClass, x, false));

                body.add(expressionTranslator.negtr(superClass.asOWLClass(), x));
                body.remove(literal);

                rules.add(Model.rule(expressionTranslator.negtr(literal), body));
            }
        }

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLEquivalentClassesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDisjointClassesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLSubObjectPropertyOfAxiom axiom) {
        final Set<Rule> rules = new HashSet<>();

        final OWLPropertyExpression subProperty = axiom.getSubProperty();
        final OWLPropertyExpression superProperty = axiom.getSuperProperty();

        final Variable x = RLExpressionTranslator.X(0);
        final Variable y = RLExpressionTranslator.X();

        rules.add(Model.rule(
                expressionTranslator.tr(subProperty, x, y, true).get(0),
                expressionTranslator.tr(superProperty, x, y, true).get(0),
                Model.negLiteral(expressionTranslator.negtr(subProperty, x, y))
        ));

        rules.add(Model.rule(
                expressionTranslator.negtr(subProperty, x, y),
                expressionTranslator.negtr(superProperty, x, y)
        ));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLEquivalentObjectPropertiesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDisjointObjectPropertiesAxiom axiom) {
        final Set<Rule> rules = new HashSet<>();

        for (final OWLDisjointObjectPropertiesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLObjectPropertyExpression> roles = new ArrayList<>(i.getProperties());

            final OWLObjectPropertyExpression p = roles.get(0);
            final OWLObjectPropertyExpression q = roles.get(1);

            rules.addAll(translateDisjointRoles(p, q));
        }

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLInverseObjectPropertiesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyDomainAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyRangeAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLFunctionalObjectPropertyAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLInverseFunctionalObjectPropertyAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLIrreflexiveObjectPropertyAxiom axiom) {
        final Set<Rule> rules = new HashSet<>();
        final Variable x = RLExpressionTranslator.X(0);

        rules.add(Model.rule(
                expressionTranslator.negtr(axiom.getProperty(), x, x)
        ));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLSymmetricObjectPropertyAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLAsymmetricObjectPropertyAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLTransitiveObjectPropertyAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLSubPropertyChainOfAxiom axiom) throws UnsupportedAxiomException {
        final Collection<Rule> rules = new HashSet<>();

        List<OWLObjectPropertyExpression> propertyChain = axiom.getPropertyChain();
        OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();

        final List<Atom> body = expressionTranslator.tr(propertyChain, true);

        final Variable y = RLExpressionTranslator.X(-1);
        final Variable x = RLExpressionTranslator.X(0);

        rules.add(Model.rule(expressionTranslator.tr(superProperty, x, y, true).get(0), body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLSubDataPropertyOfAxiom axiom) {
        final Set<Rule> rules = new HashSet<>();

        final OWLPropertyExpression subProperty = axiom.getSubProperty();
        final OWLPropertyExpression superProperty = axiom.getSuperProperty();

        final Variable x = RLExpressionTranslator.X(0);
        final Variable y = RLExpressionTranslator.X();

        rules.add(Model.rule(
                expressionTranslator.tr(subProperty, x, y, true).get(0),
                expressionTranslator.tr(superProperty, x, y, true).get(0),
                Model.negLiteral(expressionTranslator.negtr(subProperty, x, y))
        ));

        rules.add(Model.rule(
                expressionTranslator.negtr(subProperty, x, y),
                expressionTranslator.negtr(superProperty, x, y)
        ));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLEquivalentDataPropertiesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDisjointDataPropertiesAxiom axiom) {
        final Set<Rule> rules = new HashSet<>();

        for (final OWLDisjointDataPropertiesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLDataPropertyExpression> roles = new ArrayList<>(i.getProperties());

            final OWLDataPropertyExpression p = roles.get(0);
            final OWLDataPropertyExpression q = roles.get(1);

            rules.addAll(translateDisjointRoles(p, q));
        }

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLDataPropertyDomainAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDataPropertyRangeAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLFunctionalDataPropertyAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLSameIndividualAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDifferentIndividualsAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLClassAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLNegativeObjectPropertyAssertionAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDataPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLNegativeDataPropertyAssertionAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    private Collection<Rule> translateDisjointRoles(OWLPropertyExpression p, OWLPropertyExpression q) {
        final Set<Rule> rules = new HashSet<>();

        final Variable x = RLExpressionTranslator.X(0);
        final Variable y = RLExpressionTranslator.X(1);

        if (!p.isBottomEntity() && !q.isBottomEntity()) {
            if (p.isTopEntity()) {
                rules.add(Model.rule(
                        expressionTranslator.negtr(q, x, y)
                ));
            } else if (q.isTopEntity()) {
                rules.add(Model.rule(
                        expressionTranslator.negtr(p, x, y)
                ));
            } else {
                rules.add(Model.rule(
                        expressionTranslator.negtr(p, x, y),
                        expressionTranslator.tr(q, x, y, false)
                ));

                rules.add(Model.rule(
                        expressionTranslator.negtr(q, x, y),
                        expressionTranslator.tr(p, x, y, false)
                ));
            }
        }

        return rules;
    }
}
