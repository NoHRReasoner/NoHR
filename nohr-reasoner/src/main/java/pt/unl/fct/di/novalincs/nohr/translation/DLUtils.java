package pt.unl.fct.di.novalincs.nohr.translation;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

/**
 * Provides some DL (Description Logic) utility functions.
 *
 * @author Nuno Costa
 */
public class DLUtils {

    /**
     * Gets the atomic role of a given basic DL-Lite<sub>R</sub>basic
     * DL-Lite<sub>R</sub> role expression.
     *
     * @param role a role expression <i>P</i> or <i>P<sup>-</i>.
     * @return <i>P</i>.
     */
    public static OWLProperty atomic(OWLPropertyExpression role) {
        if (role.isObjectPropertyExpression()) {
            final OWLObjectPropertyExpression ope = (OWLObjectPropertyExpression) role;

            return ope.getNamedProperty();
        } else if (role.isDataPropertyExpression()) {
            return (OWLDataProperty) role;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static OWLClassAssertionAxiom assertion(OWLOntology ontology, OWLClassExpression c, OWLIndividual a) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(c, a);
    }

    public static OWLClass bottom(OWLOntology ontology) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing();
    }

    public static OWLProperty bottomProperty(OWLOntology ontology) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLBottomObjectProperty();
    }

    public static OWLObjectIntersectionOf conjunction(OWLOntology ontology, Set<OWLClassExpression> concepts) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectIntersectionOf(concepts);
    }

    public static OWLObjectIntersectionOf conjunction(OWLOntology ontology, OWLClassExpression... concepts) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectIntersectionOf(concepts);
    }

    public static OWLDisjointObjectPropertiesAxiom disjunction(OWLOntology ontology, OWLObjectPropertyExpression p, OWLObjectPropertyExpression q) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(p, q);
    }

    public static boolean hasDisjunctions(OWLOntology ontology) {
        for (final OWLSubClassOfAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            for (final OWLClassExpression ci : axiom.getSuperClass().asConjunctSet()) {
                if (ci.isOWLNothing()) {
                    return true;
                }
            }
        }

        return ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY) > 0 || ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES) > 0;
    }

    public static boolean hasExistential(OWLClassExpression ce) {
        for (final OWLClassExpression cei : ce.asConjunctSet()) {
            if (DLUtils.isExistential(cei)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isExistential(OWLClassExpression c) {
        return c instanceof OWLObjectSomeValuesFrom;
    }

    public static OWLDataRange topDatatype(OWLOntology ontology) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getTopDatatype();
    }

    public static OWLDataSomeValuesFrom some(OWLOntology ontology, OWLDataPropertyExpression r, OWLDataRange d) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDataSomeValuesFrom(r, d);
    }

    public static OWLObjectSomeValuesFrom some(OWLOntology ontology, OWLObjectPropertyExpression r, OWLClassExpression c) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectSomeValuesFrom(r, c);
    }

    public static OWLSubClassOfAxiom subsumption(OWLOntology ontology, OWLClassExpression c, OWLClassExpression d) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(c, d);
    }

    public static OWLSubObjectPropertyOfAxiom subsumption(OWLOntology ontology, OWLObjectPropertyExpression p, OWLObjectPropertyExpression q) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(p, q);
    }

    public static OWLSubPropertyChainOfAxiom subsumption(OWLOntology ontology, List<OWLObjectPropertyExpression> chain, OWLObjectPropertyExpression superRole) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubPropertyChainOfAxiom(chain, superRole);
    }

    public static OWLClass top(OWLOntology ontology) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
    }

    public static OWLProperty topProperty(OWLOntology ontology) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLTopObjectProperty();
    }

    public static OWLClassExpression some(OWLOntology ontology, OWLObjectPropertyExpression r) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectSomeValuesFrom(r, ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
    }

    public static OWLClassExpression only(OWLOntology ontology, OWLObjectPropertyExpression r) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectAllValuesFrom(r, ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
    }

    public static OWLClassExpression only(OWLOntology ontology, OWLObjectPropertyExpression r, OWLClassExpression c) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectAllValuesFrom(r, c);
    }

    public static OWLClassExpression not(OWLOntology ontology, OWLClassExpression c) {
        return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectComplementOf(c);
    }

}
