package pt.unl.fct.di.novalincs.nohr.translation;

import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

public class OntologyUtil {

    private final OWLDataFactory factory;

    public OntologyUtil(OWLDataFactory factory) {
        this.factory = factory;
    }

    public OWLClassAssertionAxiom assertion(OWLClassExpression type, OWLIndividual individual) {
        return factory.getOWLClassAssertionAxiom(type, individual);
    }

    public OWLDisjointDataPropertiesAxiom disjoint(OWLDataPropertyExpression... properties) {
        return factory.getOWLDisjointDataPropertiesAxiom(properties);
    }

    public OWLDisjointObjectPropertiesAxiom disjoint(OWLObjectPropertyExpression... properties) {
        return factory.getOWLDisjointObjectPropertiesAxiom(properties);
    }

    public OWLEquivalentClassesAxiom equivalent(OWLClassExpression... classes) {
        return factory.getOWLEquivalentClassesAxiom(classes);
    }

    public OWLEquivalentDataPropertiesAxiom equivalent(OWLDataPropertyExpression... dataProperties) {
        return factory.getOWLEquivalentDataPropertiesAxiom(dataProperties);
    }

    public OWLEquivalentObjectPropertiesAxiom equivalent(OWLObjectPropertyExpression... objectProperties) {
        return factory.getOWLEquivalentObjectPropertiesAxiom(objectProperties);
    }

    public OWLObjectIntersectionOf intersectionOf(OWLClassExpression... classes) {
        return factory.getOWLObjectIntersectionOf(classes);
    }

    public OWLObjectIntersectionOf intersectionOf(Set<OWLClassExpression> classes) {
        return factory.getOWLObjectIntersectionOf(classes);
    }

    public OWLObjectInverseOf inverseOf(OWLObjectPropertyExpression objectProperty) {
        return factory.getOWLObjectInverseOf(objectProperty);
    }

    public OWLDatatype literal() {
        return factory.getTopDatatype();
    }

    public OWLClass nothing() {
        return factory.getOWLNothing();
    }

    public OWLObjectSomeValuesFrom someValuesFrom(OWLObjectPropertyExpression property, OWLClassExpression domain) {
        return factory.getOWLObjectSomeValuesFrom(property, domain);
    }

    public OWLDataSomeValuesFrom someValuesFrom(OWLDataPropertyExpression property, OWLDataRange range) {
        return factory.getOWLDataSomeValuesFrom(property, range);
    }

    public OWLSubClassOfAxiom subClassOf(OWLClassExpression subClass, OWLClassExpression superClass) {
        return factory.getOWLSubClassOfAxiom(subClass, superClass);
    }

    public OWLSubDataPropertyOfAxiom subDataPropertyOfAxiom(OWLDataPropertyExpression subDataProperty, OWLDataPropertyExpression superDataProperty) {
        return factory.getOWLSubDataPropertyOfAxiom(subDataProperty, superDataProperty);
    }

    public OWLSubObjectPropertyOfAxiom subObjectPropertyOfAxiom(OWLObjectPropertyExpression subObjectProperty, OWLObjectPropertyExpression superObjectProperty) {
        return factory.getOWLSubObjectPropertyOfAxiom(subObjectProperty, superObjectProperty);
    }

    public OWLSubPropertyChainOfAxiom subPropertyChainOf(List<OWLObjectPropertyExpression> chain, OWLObjectPropertyExpression superProperty) {
        return factory.getOWLSubPropertyChainOfAxiom(chain, superProperty);
    }

    public OWLClass thing() {
        return factory.getOWLThing();
    }

    public OWLDataProperty topDataProperty() {
        return factory.getOWLTopDataProperty();
    }

    public OWLObjectProperty topObjectProperty() {
        return factory.getOWLTopObjectProperty();
    }
}
