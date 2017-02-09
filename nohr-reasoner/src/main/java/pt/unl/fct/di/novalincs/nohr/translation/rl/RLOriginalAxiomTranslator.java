package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

public class RLOriginalAxiomTranslator implements RLAxiomTranslator {

    private final RLExpressionTranslator expressionTranslator;
    private final Vocabulary vocabulary;

    public RLOriginalAxiomTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        this.expressionTranslator = new RLExpressionTranslator(vocabulary);
    }

    @Override
    public Collection<Rule> translate(OWLClassAssertionAxiom axiom) {
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyDomainAxiom axiom) {
        final Collection<Rule> rules = new HashSet<>();

        final OWLClassExpression c = axiom.getDomain();
        final OWLPropertyExpression p = axiom.getProperty();

        Variable x = RLExpressionTranslator.X(0);
        Variable y = RLExpressionTranslator.X();

        final Atom head = expressionTranslator.th(c, x, false).get(0);
        final List<Atom> body = expressionTranslator.tr(p, x, y, false);

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLObjectPropertyRangeAxiom axiom) {
        final Collection<Rule> rules = new HashSet<>();

        final OWLClassExpression c = axiom.getRange();
        final OWLPropertyExpression p = axiom.getProperty();

        Variable x = RLExpressionTranslator.X(0);
        Variable y = RLExpressionTranslator.X();

        final Atom head = expressionTranslator.th(c, y, false).get(0);
        final List<Atom> body = expressionTranslator.tr(p, x, y, false);

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
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

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLSubPropertyAxiom axiom) {
        final Collection<Rule> rules = new HashSet<>();

        final OWLPropertyExpression p = axiom.getSubProperty();
        final OWLPropertyExpression q = axiom.getSuperProperty();

        if (q.isBottomEntity()) {
            return rules;
        }

        Variable x = RLExpressionTranslator.X(0);
        Variable y = RLExpressionTranslator.X();

        final Atom head = expressionTranslator.tr(q, x, y, false).get(0);
        final List<Atom> body = expressionTranslator.tr(p, x, y, false);

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLTransitiveObjectPropertyAxiom axiom) {
        final Collection<Rule> rules = new HashSet<>();

        final OWLPropertyExpression p = axiom.getProperty();

        Variable x = RLExpressionTranslator.X(0);
        Variable y = RLExpressionTranslator.X();
        Variable z = RLExpressionTranslator.X();

        final Atom head = expressionTranslator.tr(p, x, z, false).get(0);
        final List<Atom> body = expressionTranslator.tr(p, x, y, false);

        body.addAll(expressionTranslator.tr(p, y, z, false));

        rules.add(Model.rule(head, body));

        return rules;
    }

    @Override
    public Collection<Rule> translate(OWLDisjointClassesAxiom axiom) {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<Rule> translate(OWLDisjointDataPropertiesAxiom axiom) {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<Rule> translate(OWLDisjointObjectPropertiesAxiom axiom) {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<Rule> translate(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return Collections.EMPTY_SET;
    }

}
