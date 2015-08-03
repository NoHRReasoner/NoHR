package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLOntologyTranslation;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public abstract class AbstractOntologyTranslation implements OntologyTranslation {

    public static Profiles profile = null;

    public static OntologyTranslation createOntologyTranslation(OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
	    CloneNotSupportedException, UnsupportedAxiomTypeException {
	final OWLAnnotationProperty labelAnnotation = ontology.getOWLOntologyManager().getOWLDataFactory()
		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	final OntologyLabeler ontologyLabel = new OntologyLabeler(ontology, labelAnnotation);
	switch (getTranslationProfile(ontology)) {
	case OWL2_QL:
	    System.out.println("OWL2_QL");
	    return new QLOntologyTranslation(ontology, ontologyLabel);
	case OWL2_EL:
	    System.out.println("OWL2_EL");
	    return new ELOntologyTranslation(ontology, ontologyLabel);
	default:
	    return null;
	}
    }

    public static Profiles getTranslationProfile(OWLOntology ontology) throws UnsupportedOWLProfile {
	if (AbstractOntologyTranslation.profile != null)
	    return AbstractOntologyTranslation.profile;
	final OWL2ELProfile elProfile = new OWL2ELProfile();
	final OWL2QLProfile qlProfile = new OWL2QLProfile();
	final OWLProfileReport elReport = elProfile.checkOntology(ontology);
	final OWLProfileReport qlRerport = qlProfile.checkOntology(ontology);
	final boolean isEL = elReport.isInProfile();
	final boolean isQL = qlRerport.isInProfile();
	if (!isEL && !isQL)
	    throw new UnsupportedOWLProfile(qlRerport.getViolations());
	RuntimesLogger.logBool("OWL EL", isEL);
	RuntimesLogger.logBool("OWL QL", isQL);
	if (AbstractOntologyTranslation.profile != null)
	    return AbstractOntologyTranslation.profile;
	if (isQL)
	    return Profiles.OWL2_QL;
	else if (isEL)
	    return Profiles.OWL2_EL;
	return null;
    }

    protected final Set<Predicate> negativeHeadsPredicates;

    protected final OWLOntology ontology;

    protected final Set<Rule> rules;

    protected final Set<Predicate> tabledPredicates;

    public AbstractOntologyTranslation(OWLOntology ontology) throws IOException, OWLOntologyCreationException,
	    OWLOntologyStorageException, CloneNotSupportedException, UnsupportedOWLProfile {
	this.ontology = ontology;
	rules = new HashSet<Rule>();
	tabledPredicates = new HashSet<Predicate>();
	negativeHeadsPredicates = new HashSet<Predicate>();
    }

    protected void addTabledPredicates(Rule rule) {
	final Predicate headPred = rule.getHead().getPredicate();
	tabledPredicates.add(headPred);
	for (final Literal negLiteral : rule.getNegativeBody())
	    tabledPredicates.add(negLiteral.getPredicate());
    }

    protected abstract void computeNegativeHeadsPredicates();

    protected abstract void computeRules();

    private void computeTabledPredicates() {
	for (final Rule rule : rules)
	    addTabledPredicates(rule);
    }

    @Override
    public Set<Predicate> getNegativeHeadsPredicates() {
	return negativeHeadsPredicates;
    }

    @Override
    public Set<Predicate> getTabledPredicates() {
	return tabledPredicates;
    }

    @Override
    public Set<Rule> getTranslation() {
	return rules;
    }

    protected void translate() {
	if (hasDisjunctions())
	    computeNegativeHeadsPredicates();
	computeRules();
	computeTabledPredicates();
    }

}