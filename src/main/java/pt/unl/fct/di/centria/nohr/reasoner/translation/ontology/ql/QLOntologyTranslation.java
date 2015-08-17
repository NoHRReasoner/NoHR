package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OWLOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class QLOntologyTranslation extends OWLOntologyTranslation {

	private final QLDoubleAxiomsTranslator doubleAxiomsTranslator;

	private final TBoxDigraph graph;

	private final boolean hasDisjunctions;

	private final QLNormalizedOntology normalizedOntology;

	private final QLOriginalAxiomsTranslator originalAxiomsTranslator;

	public QLOntologyTranslation(OWLOntology ontology) throws UnsupportedAxiomsException {
		super(ontology);
		normalizedOntology = new QLNormalizedOntologyImpl(ontology);
		graph = new TBoxDigraphImpl(normalizedOntology);
		hasDisjunctions = normalizedOntology.hasDisjunctions();
		originalAxiomsTranslator = new QLOriginalAxiomsTranslator();
		doubleAxiomsTranslator = new QLDoubleAxiomsTranslator();
		translate();
	}

	@Override
	protected void computeNegativeHeadsPredicates() {
		final Variable X = Model.var("X");
		final Variable Y = Model.var("Y");
		for (final OWLClassExpression b : normalizedOntology.getSubConcepts())
			negativeHeadsPredicates.add(originalAxiomsTranslator.negTr(b, X).getFunctor());
		for (final OWLClassExpression b : normalizedOntology.getDisjointConcepts())
			negativeHeadsPredicates.add(originalAxiomsTranslator.negTr(b, X).getFunctor());
		for (final OWLPropertyExpression<?, ?> q : normalizedOntology.getSubRoles())
			negativeHeadsPredicates.add(originalAxiomsTranslator.negTr(q, X, Y).getFunctor());
		for (final OWLPropertyExpression<?, ?> q : normalizedOntology.getDisjointRoles())
			negativeHeadsPredicates.add(originalAxiomsTranslator.negTr(q, X, Y).getFunctor());
		for (final OWLEntity e : graph.getUnsatisfiableEntities())
			if (e instanceof OWLClass)
				negativeHeadsPredicates.add(originalAxiomsTranslator.negTr((OWLClass) e, X).getFunctor());
			else if (e instanceof OWLProperty)
				negativeHeadsPredicates.add(originalAxiomsTranslator.negTr((OWLProperty<?, ?>) e, X, Y).getFunctor());
		for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
			negativeHeadsPredicates.add(originalAxiomsTranslator.negTr(p, X, Y).getFunctor());
	}

	@Override
	protected void computeRules() {
		final boolean hasDisjunctions = normalizedOntology.hasDisjunctions();
		computeNegativeHeadsPredicates();
		RuntimesLogger.start("ontology translation");
		rules.addAll(getTranslation(originalAxiomsTranslator));
		if (hasDisjunctions)
			rules.addAll(getTranslation(doubleAxiomsTranslator));
		RuntimesLogger.stop("ontology translation", "loading");
		RuntimesLogger.start("ontology classification");
		for (final OWLEntity e : graph.getUnsatisfiableEntities())
			if (e instanceof OWLClass)
				rules.add(doubleAxiomsTranslator.translateUnsatisfaible((OWLClass) e));
			else if (e instanceof OWLProperty)
				rules.add(doubleAxiomsTranslator.translateUnsatisfaible((OWLProperty<?, ?>) e));
		for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
			rules.add(doubleAxiomsTranslator.translateIrreflexive(p));
		RuntimesLogger.stop("ontology classification", "loading");
		for (final Rule rule : rules)
			addTabledPredicates(rule);
	}

	@Override
	public Profile getProfile() {
		return Profile.OWL2_QL;
	}

	// TODO optimize translatin: (e) can be discarded for roles for which there
	// aren't assertions
	private Set<Rule> getTranslation(QLAxiomsTranslator axiomsTranslator) {
		final Set<Rule> result = new HashSet<Rule>();
		for (final OWLClassAssertionAxiom f : normalizedOntology.getConceptAssertions())
			result.addAll(axiomsTranslator.translate(f));
		for (final OWLObjectPropertyAssertionAxiom f : normalizedOntology.getRoleAssertions())
			result.addAll(axiomsTranslator.translate(f));
		for (final OWLDataPropertyAssertionAxiom f : normalizedOntology.getDataAssertions())
			result.addAll(axiomsTranslator.translate(f));
		for (final OWLSubClassOfAxiom s : normalizedOntology.getConceptSubsumptions())
			result.addAll(axiomsTranslator.translate(s));
		for (final OWLDisjointClassesAxiom d : normalizedOntology.getConceptDisjunctions())
			result.addAll(axiomsTranslator.translate(d));
		for (final OWLSubPropertyAxiom<?> s : normalizedOntology.getRoleSubsumptions())
			if (s instanceof OWLSubObjectPropertyOfAxiom) {
				result.addAll(axiomsTranslator.translate(s));
				final OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom) s;
				final OWLObjectPropertyExpression ope1 = axiom.getSubProperty();
				final OWLObjectPropertyExpression ope2 = axiom.getSuperProperty();
				final OWLObjectPropertyExpression invOpe1 = ope1.getInverseProperty().getSimplified();
				final OWLObjectPropertyExpression invOpe2 = ope2.getInverseProperty().getSimplified();
				if ((normalizedOntology.isSuper(some(ope1)) || normalizedOntology.isSuper(ope1))
						&& (normalizedOntology.isSub(some(ope2)) || normalizedOntology.isSub(ope2)))
					result.add(axiomsTranslator.translateDomain(axiom));
				if ((normalizedOntology.isSuper(some(invOpe1)) || normalizedOntology.isSuper(invOpe1))
						&& (normalizedOntology.isSub(some(invOpe2)) || normalizedOntology.isSub(invOpe2)))
					result.add(axiomsTranslator.translateRange(axiom));
			} else if (s instanceof OWLSubDataPropertyOfAxiom)
				result.addAll(axiomsTranslator.translate(s));
		for (final OWLNaryPropertyAxiom<?> d : normalizedOntology.getRoleDisjunctions())
			if (d instanceof OWLDisjointObjectPropertiesAxiom)
				result.addAll(axiomsTranslator.translate((OWLDisjointObjectPropertiesAxiom) d));
			else if (d instanceof OWLDisjointDataPropertiesAxiom)
				result.addAll(axiomsTranslator.translate((OWLDisjointDataPropertiesAxiom) d));

		for (final OWLPropertyExpression<?, ?> ope : normalizedOntology.getRoles())
			if (ope instanceof OWLObjectPropertyExpression) {
				final OWLObjectProperty p = ((OWLObjectPropertyExpression) ope).getNamedProperty();
				final OWLObjectPropertyExpression invP = p.getInverseProperty();
				if (normalizedOntology.isSub(some(p)) || normalizedOntology.isSub(p))
					result.add(axiomsTranslator.translateDomain(p));
				if (normalizedOntology.isSub(some(invP)) || normalizedOntology.isSub(invP))
					result.add(axiomsTranslator.translateRange(p));
			}
		return result;
	}

	@Override
	public boolean hasDisjunctions() {
		return hasDisjunctions;
	}

	private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression ope) {
		final OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		return df.getOWLObjectSomeValuesFrom(ope, df.getOWLThing());
	}

}
