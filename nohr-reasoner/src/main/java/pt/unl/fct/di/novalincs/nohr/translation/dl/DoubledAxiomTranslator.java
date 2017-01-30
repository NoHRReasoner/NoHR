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
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import static pt.unl.fct.di.novalincs.nohr.model.Model.negLiteral;
import static pt.unl.fct.di.novalincs.nohr.model.Model.rule;
import static pt.unl.fct.di.novalincs.nohr.model.Model.ruleSet;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.AssertionsTranslation;

public class DoubledAxiomTranslator implements AxiomTranslator {

    private final AtomTranslator atomTranslator;
    private final Vocabulary vocabulary;

    public DoubledAxiomTranslator(Vocabulary vocabulary) {
        this.atomTranslator = new AtomTranslator(vocabulary);
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

        OWLClassExpression c = axiom.getSubClass();
        OWLClassExpression d = axiom.getSuperClass();

        for (OWLClassExpression i : c.asConjunctSet()) {
            if (i.isOWLNothing()) {
                return ret;
            }
        }

        if (d.isAnonymous() || d.isOWLThing()) {
            return ret;
        }

        if (c.isOWLThing()) {
            ret.add(Model.rule((Atom) atomTranslator.tr(d, AtomTranslator.X, true).get(0), Model.negLiteral(atomTranslator.negTr(d, AtomTranslator.X))));
        } else if (d.isOWLNothing() && !c.isAnonymous()) {
            ret.add(Model.rule(atomTranslator.negTr(c.asOWLClass(), AtomTranslator.X)));
        } else if (d.isOWLNothing() && c.isAnonymous()) {
            for (final Literal b : atomTranslator.tr(c, AtomTranslator.X, false)) {
                final List<Literal> body = atomTranslator.tr(c, AtomTranslator.X, false);

                body.remove(b);

                ret.add(Model.rule(atomTranslator.negTr(b), body));
            }
        } // (c1)
        else {
            List<Literal> body = atomTranslator.tr(c, AtomTranslator.X, true);

            body.add(Model.negLiteral(atomTranslator.negTr(d, AtomTranslator.X)));
            ret.add(Model.rule((Atom) atomTranslator.tr(d, AtomTranslator.X, true).get(0), body));

            for (final Literal b : atomTranslator.tr(c, AtomTranslator.X, false)) {
                body = atomTranslator.tr(c, AtomTranslator.X, false);

                body.add(atomTranslator.negTr(d, AtomTranslator.X));
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
            ret.add(Model.rule(atomTranslator.tr(q, AtomTranslator.X, AtomTranslator.Y, true).get(0), Model.negLiteral(atomTranslator.negTr(q, AtomTranslator.X, AtomTranslator.Y))));
        } else if (q.isBottomEntity()) {
            ret.add(Model.rule(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y),
                    atomTranslator.tr(p, AtomTranslator.X, AtomTranslator.Y, false)));
            ret.add(Model.rule(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y),
                    atomTranslator.tr(p, AtomTranslator.X, AtomTranslator.Y, false)));
        } else {
            ret.add(Model.rule(atomTranslator.tr(q, AtomTranslator.X, AtomTranslator.Y, true).get(0),
                    atomTranslator.tr(p, AtomTranslator.X, AtomTranslator.Y, true).get(0),
                    Model.negLiteral(atomTranslator.negTr(q, AtomTranslator.X, AtomTranslator.Y))));
            ret.add(Model.rule(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y),
                    atomTranslator.negTr(q, AtomTranslator.X, AtomTranslator.Y)));
        }

        return ret;
    }

    @Override
    public Collection<Rule> translate(OWLSubPropertyChainOfAxiom axiom) {
        Set<Rule> ret = new HashSet<>();

        final List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
        final OWLObjectPropertyExpression p = axiom.getSuperProperty();

        List<Atom> chainTr = atomTranslator.tr(chain, AtomTranslator.X, AtomTranslator.Y, true);
        List<Literal> body = new ArrayList<Literal>(chainTr);

        body.add(Model.negLiteral(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y)));
        ret.add(Model.rule(atomTranslator.tr(p, AtomTranslator.X, AtomTranslator.Y, true).get(0), body));

        for (final Literal r : atomTranslator.tr(chain, AtomTranslator.X, AtomTranslator.Y, false)) {
            chainTr = atomTranslator.tr(chain, AtomTranslator.X, AtomTranslator.Y, false);

            body = new ArrayList<Literal>(chainTr);

            body.add(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y));
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
                    ret.add(Model.rule(atomTranslator.negTr(d, AtomTranslator.X)));
                } else if (d.isOWLThing()) {
                    ret.add(Model.rule(atomTranslator.negTr(c, AtomTranslator.X)));
                } else {
                    ret.add(Model.rule(atomTranslator.negTr(c, AtomTranslator.X), atomTranslator.tr(d, AtomTranslator.X, false)));
                    ret.add(Model.rule(atomTranslator.negTr(d, AtomTranslator.X), atomTranslator.tr(c, AtomTranslator.X, false)));
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
                ret.add(Model.rule(atomTranslator.negTr(q, AtomTranslator.X, AtomTranslator.Y)));
            } else if (q.isTopEntity()) {
                ret.add(Model.rule(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y)));
            } else {
                ret.add(Model.rule(atomTranslator.negTr(p, AtomTranslator.X, AtomTranslator.Y), atomTranslator.tr(q, AtomTranslator.X, AtomTranslator.Y, false)));
                ret.add(Model.rule(atomTranslator.negTr(q, AtomTranslator.X, AtomTranslator.Y), atomTranslator.tr(p, AtomTranslator.X, AtomTranslator.Y, false)));
            }
        }

        return ret;
    }

}
