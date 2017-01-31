package pt.unl.fct.di.novalincs.nohr.translation.dl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import static pt.unl.fct.di.novalincs.nohr.model.Model.var;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

public class DLAtomTranslator {

    static final Variable X = Model.var("X");
    static final Variable Y = Model.var("Y");

    public final Vocabulary vocabulary;

    public DLAtomTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    Atom negTr(Literal b) {
        return Model.atom(vocabulary.negPred(b.getFunctor()), b.getAtom().getArguments());
    }

    public Atom negTr(OWLClassExpression c, Variable x) {
        return Model.atom(vocabulary.negPred(c.asOWLClass()), x);
    }

    public Atom negTr(OWLPropertyExpression p, Variable x, Variable y) {
        return Model.atom(vocabulary.negPred(p), x, y);
    }

    public List<Literal> tr(OWLClassExpression c, Variable x, boolean doubled) {
        final List<Literal> ret = new LinkedList<>();

        if (c.isOWLThing()) {
            return ret;
        }

        if (c instanceof OWLClass) {
            ret.add(Model.atom(vocabulary.pred(c.asOWLClass(), doubled)));
        } else if (c instanceof OWLObjectComplementOf) {
            final OWLObjectComplementOf complement = (OWLObjectComplementOf) c;
            final OWLClassExpression operand = complement.getOperand();

            if (!operand.isAnonymous()) {
                ret.add(Model.atom(vocabulary.negPred(operand.asOWLClass()), x));
            } else if (operand instanceof OWLObjectSomeValuesFrom) {
                final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) operand;

                if (!some.getFiller().isOWLThing()) {
                    throw new IllegalArgumentException("Expression must be a basic concept.");
                }

                ret.add(Model.atom(vocabulary.negPred(some.getProperty()), x, Model.var()));
            } else {
                throw new IllegalArgumentException("Expression must be a basic concept or existential.");
            }
        } else if (c instanceof OWLObjectIntersectionOf) {
            final Set<OWLClassExpression> intersection = c.asConjunctSet();

            for (OWLClassExpression i : intersection) {
                ret.addAll(tr(i, x, doubled));
            }
        } else if (c instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) c;
            final OWLObjectPropertyExpression p = some.getProperty();
            final OWLClassExpression filler = some.getFiller();

            ret.addAll(tr(p, X, Y, doubled));
            ret.addAll(tr(some.getFiller(), x, doubled));
        }

        return ret;
    }

    List<Atom> tr(List<OWLObjectPropertyExpression> chain, Variable x, Variable xk, boolean doubled) {
        final int n = chain.size();
        final List<Atom> result = new ArrayList<>(n);

        Variable xi;
        Variable xj = x;

        for (int i = 0; i < n; i++) {
            final OWLPropertyExpression p = chain.get(i);

            xi = xj;
            xj = i == n - 1 ? xk : var("X" + i);

            result.addAll(tr(p, xi, xj, doubled));
        }

        return result;
    }

    public List<Atom> tr(OWLPropertyExpression p, Variable x, Variable y, boolean doubled) {
        final List<Atom> ret = new LinkedList<>();

        if (p instanceof OWLObjectComplementOf) {
            OWLClassExpression operand = ((OWLObjectComplementOf) p).getOperand();

            if (operand instanceof OWLObjectProperty) {
                ret.add(Model.atom(vocabulary.negPred(operand.asOWLClass()), x, y));
            } else if (operand instanceof OWLObjectInverseOf) {
                ret.add(Model.atom(vocabulary.negPred(((OWLObjectInverseOf) operand).getNamedProperty()), y, x));
            }
        } else if (p instanceof OWLObjectProperty) {
            ret.add(Model.atom(vocabulary.pred(p, doubled), x, y));
        } else if (p instanceof OWLDataProperty) {
            ret.add(Model.atom(vocabulary.pred(p, doubled), x, y));
        } else if (p instanceof OWLObjectInverseOf) {
            ret.add(Model.atom(vocabulary.pred(p, doubled), y, x));
        }

        return ret;
    }

}
