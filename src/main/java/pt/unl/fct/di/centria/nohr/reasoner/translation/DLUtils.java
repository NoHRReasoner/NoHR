package pt.unl.fct.di.centria.nohr.reasoner.translation;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

public class DLUtils {

    public static OWLProperty<?, ?> getRoleName(OWLPropertyExpression<?, ?> q) {
	if (q instanceof OWLDataPropertyExpression)
	    return ((OWLDataPropertyExpression) q).asOWLDataProperty();
	else if (q instanceof OWLObjectPropertyExpression)
	    return ((OWLObjectPropertyExpression) q).getNamedProperty();
	else
	    return null;
    }

    public static boolean isAtomic(OWLClassExpression cls) {
	return cls instanceof OWLClass;
    }

    public static boolean isAtomic(OWLObjectPropertyExpression prop) {
	return prop.getSimplified() instanceof OWLObjectProperty;
    }

    public static boolean isExistential(OWLClassExpression cls) {
	return cls instanceof OWLObjectSomeValuesFrom;
    }

    public static boolean isInverse(OWLPropertyExpression<?, ?> q) {
	if (q instanceof OWLDataPropertyExpression)
	    return false;
	if (q instanceof OWLObjectPropertyExpression) {
	    final OWLObjectPropertyExpression p = (OWLObjectPropertyExpression) q;
	    return p.getSimplified() instanceof OWLObjectInverseOf;
	}
	return false;
    }

    /**
     * @param role
     * @return
     */
    public static OWLProperty<?, ?> atomic(OWLPropertyExpression<?, ?> role) {
        if (role.isObjectPropertyExpression()) {
            final OWLObjectPropertyExpression ope = (OWLObjectPropertyExpression) role;
            return ope.getNamedProperty();
        } else if (role.isDataPropertyExpression())
            return (OWLDataProperty) role;
        else
            throw new IllegalArgumentException();
    }
}
