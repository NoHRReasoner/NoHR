package pt.unl.fct.di.novalincs.nohr.translation.normalization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;

public class Reducer {

    public static <T extends OWLAxiom> boolean reduce(Set<T> axioms, Normalizer<T> normalizer) {
        boolean changed = false;
        final Iterator<T> it = axioms.iterator();
        final Set<T> newAxioms = new HashSet<>();

        while (it.hasNext()) {
            final T axiom = it.next();
            final boolean hasChange = normalizer.addNormalization(axiom, newAxioms);

            if (hasChange) {
                it.remove();
                changed = true;
            }
        }

        if (changed) {
            axioms.addAll(newAxioms);
        }

        return changed;
    }

}
