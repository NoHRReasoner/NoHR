package local.translate.ql;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

public class DLUtils {

	public static boolean isAtomic(OWLClassExpression cls) {
		return cls instanceof OWLClass;
	}

	public static boolean isExistential(OWLClassExpression cls) {
		return cls instanceof OWLObjectSomeValuesFrom;
	}

	public static boolean isAtomic(OWLObjectPropertyExpression prop) {
		return prop instanceof OWLObjectProperty;
	}

	public static boolean isInverse(OWLPropertyExpression p) {
		return p instanceof OWLObjectInverseOf;
	}
}
