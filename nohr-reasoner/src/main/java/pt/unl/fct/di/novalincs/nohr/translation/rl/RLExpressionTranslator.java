package pt.unl.fct.di.novalincs.nohr.translation.rl;

import java.util.LinkedList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Variable;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

public class RLExpressionTranslator {

    private final Vocabulary vocabulary;

    private static long freshVariableIndex = 0;

    public static Variable X() {
        return Model.var("X" + freshVariableIndex++);
    }

    public static Variable X(long index) {
        freshVariableIndex = index;
        return X();
    }

    public RLExpressionTranslator(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<Atom> tb(OWLClassExpression c, Variable x, boolean doubled) {
        final List<Atom> atoms = new LinkedList<>();

        if (c instanceof OWLClass) {
            atoms.add(Model.atom(vocabulary.pred(c.asOWLClass(), doubled), x));
        } else if (c instanceof OWLObjectIntersectionOf) {
            final OWLObjectIntersectionOf objectIntersectionOf = (OWLObjectIntersectionOf) c;

            for (OWLClassExpression i : objectIntersectionOf.asConjunctSet()) {
                atoms.addAll(tb(i, x, doubled));
            }
        } else if (c instanceof OWLObjectUnionOf) {
            throw new IllegalArgumentException("Unsupported class expression " + c.toString());
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
        } else if (c instanceof OWLObjectIntersectionOf) {
            throw new IllegalArgumentException("Unsupported class expression " + c.toString());
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

        if (p instanceof OWLObjectProperty || p instanceof OWLDataProperty) {
            atoms.add(Model.atom(vocabulary.pred(p, doubled), x, y));
        } else {
            throw new IllegalArgumentException("Illegal property expression " + p.toString());
        }

        return atoms;
    }

}
