package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
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
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;
import pt.unl.fct.di.novalincs.nohr.translation.UnsupportedAxiomException;

public class RLOriginalAxiomTranslator implements RLAxiomTranslator {

    private final Vocabulary vocabulary;
    private final RLExpressionTranslator expressionTranslator;

    public RLOriginalAxiomTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        this.expressionTranslator = new RLExpressionTranslator(vocabulary);
    }

    @Override
    public Collection<Rule> translate(OWLSubClassOfAxiom axiom) {
        final Collection<Rule> rules = new HashSet<>();

        final OWLClassExpression c = axiom.getSubClass();
        final OWLClassExpression d = axiom.getSuperClass();

        if (d.isOWLNothing()) {
            return rules;
        }

        Variable x = RLExpressionTranslator.X(0);

        final List<Atom> body = expressionTranslator.tb(c, x, false);
        final Atom head = expressionTranslator.th(d, body, x, false).get(0);

        if (body.isEmpty()) {
            rules.add(Model.rule(head));
        } else {
            rules.add(Model.rule(head, body));
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
        final Collection<Rule> rules = new HashSet<>();

        final OWLPropertyExpression p = axiom.getSubProperty();
        final OWLPropertyExpression q = axiom.getSuperProperty();

        if (q.isBottomEntity() || p.isBottomEntity() || q.isTopEntity()) {
            return rules;
        }

        Variable x = RLExpressionTranslator.X(0);
        Variable y = RLExpressionTranslator.X();

        final Atom head = expressionTranslator.tr(q, x, y, false).get(0);

        if (p.isTopEntity()) {
            rules.add(Model.rule(head));

            return rules;
        }

        final List<Atom> body = expressionTranslator.tr(p, x, y, false);

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLEquivalentObjectPropertiesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDisjointObjectPropertiesAxiom axiom) throws UnsupportedAxiomException {
        return Collections.EMPTY_SET;
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
        return Collections.EMPTY_SET;
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

        final List<Atom> body = expressionTranslator.tr(propertyChain, false);

        final Variable y = RLExpressionTranslator.X(-1);
        final Variable x = RLExpressionTranslator.X(0);

        rules.add(Model.rule(expressionTranslator.tr(superProperty, x, y, false).get(0), body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLSubDataPropertyOfAxiom axiom) {
        final Collection<Rule> rules = new HashSet<>();

        final OWLPropertyExpression p = axiom.getSubProperty();
        final OWLPropertyExpression q = axiom.getSuperProperty();

        if (q.isBottomEntity() || p.isBottomEntity() || q.isTopEntity()) {
            return rules;
        }

        Variable x = RLExpressionTranslator.X(0);
        Variable y = RLExpressionTranslator.X();

        final Atom head = expressionTranslator.tr(q, x, y, false).get(0);

        if (p.isTopEntity()) {
            rules.add(Model.rule(head));

            return rules;
        }

        final List<Atom> body = expressionTranslator.tr(p, x, y, false);

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLEquivalentDataPropertiesAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDisjointDataPropertiesAxiom axiom) throws UnsupportedAxiomException {
        return Collections.EMPTY_SET;
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
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLNegativeObjectPropertyAssertionAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

    @Override
    public Collection<Rule> translate(OWLDataPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLNegativeDataPropertyAssertionAxiom axiom) throws UnsupportedAxiomException {
        throw new UnsupportedAxiomException(axiom);
    }

}
