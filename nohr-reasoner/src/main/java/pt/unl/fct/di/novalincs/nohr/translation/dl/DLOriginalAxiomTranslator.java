package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

public class DLOriginalAxiomTranslator implements DLAxiomTranslator {

    private final DLExpressionTranslator atomTranslator;
    private final Vocabulary vocabulary;

    public DLOriginalAxiomTranslator(Vocabulary vocabulary) {
        atomTranslator = new DLExpressionTranslator(vocabulary);
        this.vocabulary = vocabulary;
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom axiom) {
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateOriginal(vocabulary, axiom);
    }

    @Override
    public Set<Rule> translate(OWLSubClassOfAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        final OWLClassExpression subClass = axiom.getSubClass();
        final OWLClassExpression superClass = axiom.getSuperClass();

        if (superClass.isOWLThing() || superClass.isOWLNothing() || superClass.isAnonymous()) {
            return ret;
        }

        for (OWLClassExpression i : subClass.asConjunctSet()) {
            if (i.isOWLNothing()) {
                return ret;
            }
        }

        final Atom head = (Atom) atomTranslator.tr(superClass, DLExpressionTranslator.X, false).get(0);

        if (subClass.isOWLThing()) {
            ret.add(Model.rule(head));

            return ret;
        }

        final List<Literal> body = atomTranslator.tr(subClass, DLExpressionTranslator.X, false);

        ret.add(Model.rule(head, body));

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLSubPropertyAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        final OWLPropertyExpression p = axiom.getSubProperty();
        final OWLPropertyExpression q = axiom.getSuperProperty();

        if (p.isBottomEntity() || q.isTopEntity() || q.isBottomEntity()) {
            return ret;
        }

        if (p.isTopEntity()) {
            ret.add(Model.rule(atomTranslator.tr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y, false).get(0)));
        } else {
            ret.add(Model.rule(atomTranslator.tr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y, false).get(0),
                    atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)));
        }

        return ret;
    }

    @Override
    public Set<Rule> translate(OWLSubPropertyChainOfAxiom axiom) {
        Set<Rule> ret = new HashSet<>();

        final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
        final OWLObjectPropertyExpression p = axiom.getSuperProperty();

        ret.add(Model.rule(atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, false).get(0), atomTranslator.tr(chain, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)));

        return ret;
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
