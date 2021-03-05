package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
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

public class DLDoubledAxiomTranslator implements DLAxiomTranslator {

    private final DLExpressionTranslator atomTranslator;
    private final Vocabulary vocabulary;

    public DLDoubledAxiomTranslator(Vocabulary vocabulary) {
        this.atomTranslator = new DLExpressionTranslator(vocabulary);
        this.vocabulary = vocabulary;
    }

    @Override
    public Set<Rule> translate(OWLClassAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Collection<Rule> translate(OWLPropertyAssertionAxiom axiom) {
        return AssertionsTranslation.translateDouble(vocabulary, axiom);
    }

    @Override
    public Set<Rule> translate(OWLSubClassOfAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        OWLClassExpression subClass = axiom.getSubClass();
        OWLClassExpression superClass = axiom.getSuperClass();

        if (superClass.isOWLThing() || superClass.isAnonymous()) {
            return ret;
        }

        for (OWLClassExpression i : subClass.asConjunctSet()) {
            if (i.isOWLNothing()) {
                return ret;
            }
        }

        if (subClass.isOWLThing()) {
            ret.add(Model.rule((Atom) atomTranslator.tr(superClass, DLExpressionTranslator.X, true).get(0), Model.negLiteral(atomTranslator.negTr(superClass, DLExpressionTranslator.X))));
        } else if (superClass.isOWLNothing() && !subClass.isAnonymous()) {
            ret.add(Model.rule(atomTranslator.negTr(subClass.asOWLClass(), DLExpressionTranslator.X)));
        } else if (superClass.isOWLNothing() && subClass.isAnonymous()) {
            for (final Literal b : atomTranslator.tr(subClass, DLExpressionTranslator.X, false)) {
                final List<Literal> body = atomTranslator.tr(subClass, DLExpressionTranslator.X, false);

                body.remove(b);

                ret.add(Model.rule(atomTranslator.negTr(b), body));
            }
        } else {
            List<Literal> body = atomTranslator.tr(subClass, DLExpressionTranslator.X, true);

            body.add(Model.negLiteral(atomTranslator.negTr(superClass, DLExpressionTranslator.X)));
            ret.add(Model.rule((Atom) atomTranslator.tr(superClass, DLExpressionTranslator.X, true).get(0), body));

            for (final Literal b : atomTranslator.tr(subClass, DLExpressionTranslator.X, false)) {
                body = atomTranslator.tr(subClass, DLExpressionTranslator.X, false);

                body.add(atomTranslator.negTr(superClass, DLExpressionTranslator.X));
                body.remove(b);

                ret.add(Model.rule(atomTranslator.negTr(b), body));
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
            ret.add(Model.rule(atomTranslator.tr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y, true).get(0), Model.negLiteral(atomTranslator.negTr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y))));
        } else if (q.isBottomEntity()) {
            ret.add(Model.rule(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y),
                    atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)));
            ret.add(Model.rule(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y),
                    atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)));
        } else {
            ret.add(Model.rule(atomTranslator.tr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y, true).get(0),
                    atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, true).get(0),
                    Model.negLiteral(atomTranslator.negTr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y))));
            ret.add(Model.rule(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y),
                    atomTranslator.negTr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y)));
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLSubPropertyChainOfAxiom axiom) {
        Set<Rule> ret = new HashSet<>();

        final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
        final OWLObjectPropertyExpression p = axiom.getSuperProperty();

        List<Atom> chainTr = atomTranslator.tr(chain, DLExpressionTranslator.X, DLExpressionTranslator.Y, true);
        List<Literal> body = new ArrayList<Literal>(chainTr);

        body.add(Model.negLiteral(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y)));
        ret.add(Model.rule(atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, true).get(0), body));

        for (final Literal r : atomTranslator.tr(chain, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)) {
            chainTr = atomTranslator.tr(chain, DLExpressionTranslator.X, DLExpressionTranslator.Y, false);

            body = new ArrayList<Literal>(chainTr);

            body.add(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y));
            body.remove(r);

            ret.add(Model.rule(atomTranslator.negTr(r), body));
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLDisjointClassesAxiom axiom) {
        Set<Rule> ret = new HashSet<>();

        for (final OWLDisjointClassesAxiom i : axiom.asPairwiseAxioms()) {
            final List<OWLClassExpression> concepts = i.getClassExpressionsAsList();

            final OWLClassExpression c = concepts.get(0);
            final OWLClassExpression d = concepts.get(1);

            if (!c.isBottomEntity() && !d.isBottomEntity()) {
                if (c.isOWLThing()) {
                    ret.add(Model.rule(atomTranslator.negTr(d, DLExpressionTranslator.X)));
                } else if (d.isOWLThing()) {
                    ret.add(Model.rule(atomTranslator.negTr(c, DLExpressionTranslator.X)));
                } else {
                    ret.add(Model.rule(atomTranslator.negTr(c, DLExpressionTranslator.X), atomTranslator.tr(d, DLExpressionTranslator.X, false)));
                    ret.add(Model.rule(atomTranslator.negTr(d, DLExpressionTranslator.X), atomTranslator.tr(c, DLExpressionTranslator.X, false)));
                }
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

    private Collection<Rule> translateDisjointRoles(OWLPropertyExpression p, OWLPropertyExpression q) {
        final Set<Rule> ret = new HashSet<>();

        if (!p.isBottomEntity() && !q.isBottomEntity()) {
            if (p.isTopEntity()) {
                ret.add(Model.rule(atomTranslator.negTr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y)));
            } else if (q.isTopEntity()) {
                ret.add(Model.rule(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y)));
            } else {
                ret.add(Model.rule(atomTranslator.negTr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y), atomTranslator.tr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)));
                ret.add(Model.rule(atomTranslator.negTr(q, DLExpressionTranslator.X, DLExpressionTranslator.Y), atomTranslator.tr(p, DLExpressionTranslator.X, DLExpressionTranslator.Y, false)));
            }
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLIrreflexiveObjectPropertyAxiom axiom) {
        final Set<Rule> ret = new HashSet<>();

        ret.add(Model.rule(atomTranslator.negTr(axiom.getProperty(), DLExpressionTranslator.X, DLExpressionTranslator.X)));

        return ret;
    }

}
