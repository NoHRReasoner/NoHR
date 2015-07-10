package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLOntologyTranslator;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public abstract class AbstractOntologyTranslator implements OntologyTranslator {

    public static TranslationAlgorithm translationAlgorithm = null;

    public static OntologyTranslator createOntologyTranslator(
	    OWLOntologyManager ontologyManager, OWLOntology ontology)
		    throws OWLOntologyCreationException, OWLOntologyStorageException,
		    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	final OWLAnnotationProperty labelAnnotation = ontologyManager
		.getOWLDataFactory().getOWLAnnotationProperty(
			OWLRDFVocabulary.RDFS_LABEL.getIRI());
	final OntologyLabeler ontologyLabel = new OntologyLabeler(ontology,
		labelAnnotation);
	switch (getTranslationAlgorithm(ontology)) {
	case DL_LITE_R:
	    return new QLOntologyTranslator(ontology,
		    ontologyManager.getOWLDataFactory(), ontologyManager,
		    ontologyLabel);
	case EL:
	    return new ELOntologyTranslator(ontologyManager, ontology,
		    ontologyLabel);
	default:
	    return null;
	}
    }

    private static TranslationAlgorithm getTranslationAlgorithm(
	    OWLOntology ontology) throws UnsupportedOWLProfile {
	if (AbstractOntologyTranslator.translationAlgorithm != null)
	    return AbstractOntologyTranslator.translationAlgorithm;
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
	if (AbstractOntologyTranslator.translationAlgorithm != null)
	    return AbstractOntologyTranslator.translationAlgorithm;
	if (isQL)
	    return TranslationAlgorithm.DL_LITE_R;
	else if (isEL)
	    return TranslationAlgorithm.EL;
	return null;
    }

    protected final Set<String> negatedPredicates;

    protected final OWLOntology ontology;

    protected final OWLOntologyManager ontologyManager;

    protected final Set<String> tabled;

    public AbstractOntologyTranslator(OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException {
	this(OWLManager.createOWLOntologyManager(), ontology);
    }

    public AbstractOntologyTranslator(OWLOntologyManager ontologyManager,
	    OWLOntology ontology) throws IOException,
	    OWLOntologyCreationException, OWLOntologyStorageException,
	    CloneNotSupportedException, UnsupportedOWLProfile {
	this.ontologyManager = ontologyManager;
	this.ontology = ontology;
	tabled = new HashSet<String>();
	negatedPredicates = new HashSet<String>();
    }

    protected void addTabledPredicates(Rule rule) {
	final Predicate headPred = rule.getHead().getPredicate();
	tabled.add(headPred.getName());
	for (final Literal negLiteral : rule.getNegativeBody())
	    tabled.add(negLiteral.getPredicate().getName());
    }

    @Override
    public Set<String> getNegatedPredicates() {
	return negatedPredicates;
    }

    @Override
    public Set<String> getTabledPredicates() {
	return tabled;
    }

}