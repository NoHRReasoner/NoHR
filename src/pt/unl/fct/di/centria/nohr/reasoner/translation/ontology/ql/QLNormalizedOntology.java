package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;

/**
 * Represents an OWL 2 QL ontology normalized in order to fit in DL-Lite<sub>R</sub>, i.e. a DL-Lite<sub>R</sub> ontology that entails exactly the
 * same axioms that a certain QL ontology (see <b>Appendix D</b> of
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
 *
 * @author Nuno Costa
 */
public interface QLNormalizedOntology {

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concept assertions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concept assertions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLClassAssertionAxiom> getConceptAssertions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concept disjunctions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concept disjunctions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLDisjointClassesAxiom> getConceptDisjunctions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concept subsumptions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concept subsumptions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLSubClassOfAxiom> getConceptSubsumptions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> (data) role assertions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> (data) role assertions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLDataPropertyAssertionAxiom> getDataAssertions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts that occurring in some disjunction of this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts that occurring in some disjunction of this {@link QLNormalizedOntology}.
	 */
	public Set<OWLClassExpression> getDisjointConcepts();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> roles occurring in some disjunction of this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> roles occurring in some disjunction of this {@link QLNormalizedOntology}.
	 */
	public Set<OWLProperty<?, ?>> getDisjointRoles();

	/**
	 * The ontology of which this {@link QLOntologyTranslation} is normalization.
	 *
	 * @return the ontology of which this {@link QLOntologyTranslation} is normalization.
	 */
	public OWLOntology getOriginalOntology();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> role assertions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> role assertions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getRoleAssertions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> role disjunctions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> role disjunctions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLNaryPropertyAxiom<?>> getRoleDisjunctions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> roles occurring in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> roles occurring in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLObjectProperty> getRoles();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> role subsumptions in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> role subsumptions in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLSubPropertyAxiom<?>> getRoleSubsumptions();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts occurring as subsumed concept in some subsumption in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts occurring as subsumed concept in some subsumption in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLClassExpression> getSubConcepts();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> roles occurring as subsumed roles in some subsumption in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> role occurring as subsumed roles in some subsumption in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLProperty<?, ?>> getSubRoles();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts occurring as subsuming concept in some subsumption in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts occurring as subsuming concept in some subsumption in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLClassExpression> getSuperConcepts();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts occurring as subsuming role in some subsumption in this {@link QLNormalizedOntology}.
	 *
	 * @return the set of DL-Lite<sub>R</sub> concepts occurring as subsuming role in some subsumption in this {@link QLNormalizedOntology}.
	 */
	public Set<OWLProperty<?, ?>> getSuperRoles();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> concepts <i>B</i> that occur in some axiom <i>B&sqsube;&bot;</i>,<i>B&sqsube;&not;&top;</i>,
	 * <i>&top;&sqsube;&not;B</i> or </i> B&sqsube;&not;B</i> {@link QLNormalizedOntology}.
	 *
	 * @returns returns the set of DL-Lite<sub>R</sub> concepts <i>B</i> that occur in some axiom <i>B&sqsube;&bot;</i>,<i>B&sqsube;&not;&top;</i>,
	 *          <i>&top;&sqsube;&not;B</i> or </i> B&sqsube;&not;B</i> {@link QLNormalizedOntology}.
	 */
	public Set<OWLClassExpression> getUnsatisfiableConcepts();

	/**
	 * Returns the set of DL-Lite<sub>R</sub> roles <i>Q</i> that occur in some axiom <i>Q&sqsube;&bot;</i>,<i>Q&sqsube;&not;&top;</i>,
	 * <i>&top;&sqsube;&not;Q</i> or </i>Q&sqsube;&not;Q</i> {@link QLNormalizedOntology}.
	 *
	 * @returns returns the set of DL-Lite<sub>R</sub> concepts <i>Q</i> that occur in some axiom <i>Q&sqsube;&bot;</i>,<i>Q&sqsube;&not;&top;</i>,
	 *          <i>&top;&sqsube;&not;Q</i> or </i> Q&sqsube;&not;Q</i> {@link QLNormalizedOntology}.
	 */
	public Set<OWLPropertyExpression<?, ?>> getUnsatisfiableRoles();

	/**
	 * Returns true iff this {@link QLNormalizedOntology} has disjunctions.
	 *
	 * @return true iff this {@link QLNormalizedOntology} has disjunctions.
	 */
	public boolean hasDisjunctions();

	/**
	 * Checks whether a given concept occur as subsumed concept in some subsumption of this {@link QLNormalizedOntology}.
	 *
	 * @param b
	 *            a concept.
	 * @return true iff {@code b} occur as subsumed concept in some subumption of this {@link QLNormalizedOntology}.
	 */
	public boolean isSub(OWLClassExpression b);

	/**
	 * Checks whether a given concept occur as subsumed role in some subsumption of this {@link QLNormalizedOntology}.
	 *
	 * @param q
	 *            a concept.
	 * @return true iff {@code q} occur as subsumed role in some subumption of this {@link QLNormalizedOntology}.
	 */
	public boolean isSub(OWLPropertyExpression<?, ?> q);

	/**
	 * Checks whether a given concept occur as subsuming concept in some subsumption of this {@link QLNormalizedOntology}.
	 *
	 * @param c
	 *            a concept.
	 * @return true iff {@code c} occur as subsuming concept in some subumption of this {@link QLNormalizedOntology}.
	 */
	public boolean isSuper(OWLClassExpression c);

	/**
	 * Checks whether a given concept occur as subsuming role in some subsumption of this {@link QLNormalizedOntology}.
	 *
	 * @param c
	 *            a concept.
	 * @return true iff {@code r} occur as subsuming role in some subumption of this {@link QLNormalizedOntology}.
	 */
	public boolean isSuper(OWLPropertyExpression<?, ?> r);
}