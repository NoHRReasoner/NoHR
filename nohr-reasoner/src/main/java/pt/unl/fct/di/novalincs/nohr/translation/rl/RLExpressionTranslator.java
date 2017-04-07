package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Literal;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import static pt.unl.fct.di.novalincs.nohr.model.Model.atom;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

public class RLExpressionTranslator {

    private final Vocabulary vocabulary;

    private static long freshVariableIndex = 0;

    public static Variable X() {
        return Model.var("X" + freshVariableIndex++);
    }

    public static Variable X(long index) {
        if (index < 0) {
            return Model.var("X" + (freshVariableIndex - 1));
        }

        freshVariableIndex = index;
        return X();
    }

    public RLExpressionTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Atom negtr(Literal l) {
        return Model.atom(vocabulary.negPred(l.getFunctor()), l.getAtom().getArguments());
    }

    public Atom negtr(OWLClassExpression c, Variable x) {
        if (c instanceof OWLClass) {
            return atom(vocabulary.negPred(c.asOWLClass()), x);
        } else if (c instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) c;

            if (objectSomeValuesFrom.getFiller().isOWLThing()) {
                return negtr(objectSomeValuesFrom.getProperty(), x, x);
            }
        }

        throw new IllegalArgumentException("Illegal class expression " + c.toString());
    }

    public Atom negtr(OWLPropertyExpression p, Variable x, Variable y) {
        if (p instanceof OWLObjectInverseOf) {
            return Model.atom(vocabulary.negPred(((OWLObjectInverseOf) p).getInverse()), y, x);
        } else if (p instanceof OWLObjectProperty || p instanceof OWLDataProperty) {
            return Model.atom(vocabulary.negPred(p), x, y);
        }

        throw new IllegalArgumentException("Illegal property expression " + p.toString());
    }

    public List<Atom> tb(OWLClassExpression c, Variable x, boolean doubled) {
        final List<Atom> atoms = new LinkedList<>();

        if (c.isOWLThing()) {
            return atoms;
        } else if (c instanceof OWLClass) {
            atoms.add(Model.atom(vocabulary.pred(c.asOWLClass(), doubled), x));
        } else if (c instanceof OWLObjectIntersectionOf) {
            final OWLObjectIntersectionOf objectIntersectionOf = (OWLObjectIntersectionOf) c;

            for (OWLClassExpression i : objectIntersectionOf.asConjunctSet()) {
                atoms.addAll(tb(i, x, doubled));
            }
        } else if (c instanceof OWLObjectSomeValuesFrom) {
            final OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) c;

            final OWLPropertyExpression p = objectSomeValuesFrom.getProperty();
            final OWLClassExpression d = objectSomeValuesFrom.getFiller();

            final Variable y = X();

            atoms.addAll(tr(p, x, y, doubled));
            atoms.addAll(tb(d, y, doubled));
        } else {
            throw new IllegalArgumentException("Illegal class expression " + c.toString());
        }

        return atoms;
    }

    public List<Atom> th(OWLClassExpression c, Variable x, boolean doubled) {
        final List<Atom> atoms = new LinkedList<>();

        if (c instanceof OWLClass) {
            atoms.add(Model.atom(vocabulary.pred(c.asOWLClass(), doubled), x));
        } else {
            throw new IllegalArgumentException("Illegal class expression " + c.toString());
        }

        return atoms;
    }

    public List<Atom> th(OWLClassExpression c, List<Atom> body, Variable x, boolean doubled) {
        if (c instanceof OWLObjectAllValuesFrom) {
            final List<Atom> atoms = new LinkedList<>();
            final OWLObjectAllValuesFrom objectAllValuesFrom = (OWLObjectAllValuesFrom) c;

            final Variable y = X();

            atoms.addAll(th(objectAllValuesFrom.getFiller(), body, y, false));
            body.addAll(tr(objectAllValuesFrom.getProperty(), x, y, false));

            return atoms;
        } else {
            return th(c, x, doubled);
        }
    }

    public List<Atom> tr(OWLPropertyExpression p, Variable x, Variable y, boolean doubled) {
        final List<Atom> atoms = new LinkedList<>();

        if (p instanceof OWLObjectInverseOf) {
            atoms.add(Model.atom(vocabulary.pred(p, doubled), y, x));
        } else if (p instanceof OWLObjectProperty || p instanceof OWLDataProperty) {
            atoms.add(Model.atom(vocabulary.pred(p, doubled), x, y));
        } else {
            throw new IllegalArgumentException("Illegal property expression " + p.toString());
        }

        return atoms;
    }

    public List<Atom> tr(List<OWLObjectPropertyExpression> chain, boolean doubled) {
        final int n = chain.size();
        final List<Atom> result = new ArrayList<>(n);

        Variable xi;
        Variable xj = X(0);

        for (int i = 0; i < n; i++) {
            final OWLPropertyExpression p = chain.get(i);

            xi = xj;
            xj = X();

            result.addAll(tr(p, xi, xj, doubled));
        }

        return result;
    }
}
