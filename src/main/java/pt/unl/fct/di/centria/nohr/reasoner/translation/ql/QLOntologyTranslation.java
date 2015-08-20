package pt.unl.fct.di.centria.nohr.reasoner.translation.ql;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
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

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link OntologyTranslator} for the {@link Profile#OWL2_QL QL} profile, according to
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
public class QLOntologyTranslation extends OntologyTranslatorImplementor {

	/** The {@link QLAxiomsTranslator} that obtain the double rules of this {@link OntologyTranslator}. */
	private final QLDoubleAxiomsTranslator doubleAxiomsTranslator;

	/** The {@link TBoxDigraph digraph} of the TBox of the ontology of which this {@link OntologyTranslator} is translation. */
	private final TBoxDigraph graph;

	/** The {@link QLOntologyNormalization normalization} of the ontology of which this {@link OntologyTranslator} is translation. */
	private final QLOntologyNormalization ontologyNormalization;

	/** The {@link QLAxiomsTranslator} that obtain the original rules of this {@link OntologyTranslator}. */
	private final QLOriginalAxiomsTranslator originalAxiomsTranslator;

	/**
	 * Constructs an {@link OntologyTranslator} of a given OWL 2 QL ontology.
	 *
	 * @param ontology
	 *            an OWL 2 QL ontology.
	 * @throws UnsupportedAxiomsException
	 *             if {@code ontology} contains some axioms of unsupported types.
	 */
	public QLOntologyTranslation(OWLOntology ontology, DeductiveDatabaseManager dedutiveDatabase)
			throws UnsupportedAxiomsException {
		super(ontology, dedutiveDatabase);
		ontologyNormalization = new QLOntologyNormalizationImpl(ontology);
		graph = new TBoxDigraphImpl(ontologyNormalization);
		originalAxiomsTranslator = new QLOriginalAxiomsTranslator();
		doubleAxiomsTranslator = new QLDoubleAxiomsTranslator();
		translate();
	}

	@Override
	protected void computeNegativeHeadFunctors() {
		final Variable X = Model.var("X");
		final Variable Y = Model.var("Y");
		for (final OWLClassExpression b : ontologyNormalization.getSubConcepts())
			negativeHeadFunctors.add(originalAxiomsTranslator.negTr(b, X).getFunctor());
		for (final OWLPropertyExpression<?, ?> q : ontologyNormalization.getSubRoles())
			negativeHeadFunctors.add(originalAxiomsTranslator.negTr(q, X, Y).getFunctor());
		for (final OWLEntity e : graph.getUnsatisfiableEntities())
			if (e instanceof OWLClass)
				negativeHeadFunctors.add(originalAxiomsTranslator.negTr((OWLClass) e, X).getFunctor());
			else if (e instanceof OWLProperty)
				negativeHeadFunctors.add(originalAxiomsTranslator.negTr((OWLProperty<?, ?>) e, X, Y).getFunctor());
		for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
			negativeHeadFunctors.add(originalAxiomsTranslator.negTr(p, X, Y).getFunctor());
	}

	@Override
	protected void computeRules() {
		final boolean hasDisjunctions = ontologyNormalization.hasDisjunctions();
		computeNegativeHeadFunctors();
		RuntimesLogger.start("ontology translation");
		rules.addAll(translation(originalAxiomsTranslator));
		if (hasDisjunctions) {
			rules.addAll(translation(doubleAxiomsTranslator));
			rules.addAll(disjunctionsTranslation());
			RuntimesLogger.stop("ontology translation", "loading");
			RuntimesLogger.start("ontology classification");
			for (final OWLEntity e : graph.getUnsatisfiableEntities())
				if (e instanceof OWLClass)
					rules.addAll(doubleAxiomsTranslator.unsatisfiabilityTranslation((OWLClass) e));
				else if (e instanceof OWLProperty)
					rules.addAll(doubleAxiomsTranslator.unsatisfiabilityTranslation((OWLProperty<?, ?>) e));
			for (final OWLObjectProperty p : graph.getIrreflexiveRoles())
				rules.add(doubleAxiomsTranslator.unreflexivityTranslation(p));
			RuntimesLogger.stop("ontology classification", "loading");
		}
		RuntimesLogger.stop("ontology translation", "loading");
	}

	/**
	 * Translate the negative axioms of the ontology that this ontology refers with a given {@link QLLAxiomsTranslator}.
	 *
	 * @retrun the translation of the negative axioms.
	 */
	private Set<Rule> disjunctionsTranslation() {
		final Set<Rule> result = new HashSet<Rule>();
		for (final OWLDisjointClassesAxiom disjunction : ontologyNormalization.getConceptDisjunctions()) {
			final List<OWLClassExpression> concepts = disjunction.getClassExpressionsAsList();
			assert concepts.size() <= 2;
			result.addAll(doubleAxiomsTranslator.disjunctionTranslation(concepts.get(0), concepts.get(1)));
		}
		for (final OWLDisjointObjectPropertiesAxiom disjunction : ontologyNormalization.getRoleDisjunctions()) {
			final List<OWLObjectPropertyExpression> roles = new LinkedList<>(disjunction.getProperties());
			assert roles.size() <= 2;
			result.addAll(doubleAxiomsTranslator.disjunctionTranslation(roles.get(0), roles.get(1)));
		}
		for (final OWLDisjointDataPropertiesAxiom disjunction : ontologyNormalization.getDataDisjunctions()) {
			final List<OWLDataPropertyExpression> roles = new LinkedList<>(disjunction.getProperties());
			assert roles.size() <= 2;
			result.addAll(doubleAxiomsTranslator.disjunctionTranslation(roles.get(0), roles.get(1)));
		}
		return result;
	}

	@Override
	public Profile getProfile() {
		return Profile.OWL2_QL;
	}

	@Override
	public boolean hasDisjunctions() {
		return ontologyNormalization.hasDisjunctions();
	}

	private OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression ope) {
		final OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		return df.getOWLObjectSomeValuesFrom(ope, df.getOWLThing());
	}

	/**
	 * Translate the positive axioms (including assertions) of the ontology that this ontology refers with a given {@link QLLAxiomsTranslator}.
	 *
	 * @param axiomTranslator
	 *            the {@link QLAxiomsTranslator} that will be used.
	 * @retrun the translation.
	 */
	// TODO optimize translatin: (e) can be discarded for roles for which there
	// aren't assertions
	private Set<Rule> translation(QLAxiomsTranslator axiomsTranslator) {
		final Set<Rule> result = new HashSet<Rule>();
		for (final OWLClassAssertionAxiom assertion : ontologyNormalization.getConceptAssertions())
			result.addAll(axiomsTranslator.assertionTranslation(assertion));
		for (final OWLObjectPropertyAssertionAxiom assertion : ontologyNormalization.getRoleAssertions())
			result.addAll(axiomsTranslator.assertionTranslation(assertion));
		for (final OWLDataPropertyAssertionAxiom assertion : ontologyNormalization.getDataAssertions())
			result.addAll(axiomsTranslator.assertionTranslation(assertion));
		for (final OWLSubClassOfAxiom subsumption : ontologyNormalization.getConceptSubsumptions())
			result.addAll(
					axiomsTranslator.subsumptionTranslation(subsumption.getSubClass(), subsumption.getSuperClass()));
		for (final OWLSubPropertyAxiom<?> subsumption : ontologyNormalization.getRoleSubsumptions())
			if (subsumption instanceof OWLSubObjectPropertyOfAxiom) {
				result.addAll(axiomsTranslator.subsumptionTranslation(subsumption.getSubProperty(),
						subsumption.getSuperProperty()));
				final OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom) subsumption;
				final OWLObjectPropertyExpression ope1 = axiom.getSubProperty();
				final OWLObjectPropertyExpression ope2 = axiom.getSuperProperty();
				final OWLObjectPropertyExpression invOpe1 = ope1.getInverseProperty().getSimplified();
				final OWLObjectPropertyExpression invOpe2 = ope2.getInverseProperty().getSimplified();
				if ((ontologyNormalization.isSuper(some(ope1)) || ontologyNormalization.isSuper(ope1))
						&& (ontologyNormalization.isSub(some(ope2)) || ontologyNormalization.isSub(ope2)))
					result.add(axiomsTranslator.domainSubsumptionTranslation(axiom.getSubProperty(),
							axiom.getSuperProperty()));
				if ((ontologyNormalization.isSuper(some(invOpe1)) || ontologyNormalization.isSuper(invOpe1))
						&& (ontologyNormalization.isSub(some(invOpe2)) || ontologyNormalization.isSub(invOpe2)))
					result.add(axiomsTranslator.rangeSubsumptionTranslation(invOpe1, invOpe2));
			} else if (subsumption instanceof OWLSubDataPropertyOfAxiom)
				result.addAll(axiomsTranslator.subsumptionTranslation(subsumption.getSubProperty(),
						subsumption.getSuperProperty()));
		for (final OWLPropertyExpression<?, ?> ope : ontologyNormalization.getRoles())
			if (ope instanceof OWLObjectPropertyExpression) {
				final OWLObjectProperty p = ((OWLObjectPropertyExpression) ope).getNamedProperty();
				final OWLObjectPropertyExpression invP = p.getInverseProperty();
				if (ontologyNormalization.isSub(some(p)) || ontologyNormalization.isSub(p))
					result.add(axiomsTranslator.domainTranslation(p));
				if (ontologyNormalization.isSub(some(invP)) || ontologyNormalization.isSub(invP))
					result.add(axiomsTranslator.rangeTranslation(p));
			}
		return result;
	}

}
