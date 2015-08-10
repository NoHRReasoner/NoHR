package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLOntologyTranslation;

public abstract class AbstractOntologyTranslation implements OntologyTranslation {

    public static Profiles profile = null;

    public static OntologyTranslation createOntologyTranslation(OWLOntology ontology)
	    throws OWLProfilesViolationsException, UnsupportedAxiomsException {
	switch (getProfile(ontology)) {
	case OWL2_QL:
	    return new QLOntologyTranslation(ontology);
	case OWL2_EL:
	    return new ELOntologyTranslation(ontology);
	default:
	    return null;
	}
    }

    public static Profiles getProfile(OWLOntology ontology) throws OWLProfilesViolationsException {
	if (AbstractOntologyTranslation.profile != null)
	    return AbstractOntologyTranslation.profile;
	final OWL2ELProfile elProfile = new OWL2ELProfile();
	final OWL2QLProfile qlProfile = new OWL2QLProfile();
	final OWLProfileReport elReport = elProfile.checkOntology(ontology);
	final OWLProfileReport qlRerport = qlProfile.checkOntology(ontology);
	if (!qlRerport.isInProfile() && !elReport.isInProfile())
	    throw new OWLProfilesViolationsException(qlRerport, elReport);
	if (qlRerport.getViolations().size() <= elReport.getViolations().size())
	    return Profiles.OWL2_QL;
	else
	    return Profiles.OWL2_EL;
    }

    protected final Set<Predicate> negativeHeadsPredicates;

    protected final OWLOntology ontology;

    protected final Set<Rule> rules;

    protected final Set<Predicate> tabledPredicates;

    public AbstractOntologyTranslation(OWLOntology ontology) {
	this.ontology = ontology;
	rules = new HashSet<Rule>();
	tabledPredicates = new HashSet<Predicate>();
	negativeHeadsPredicates = new HashSet<Predicate>();
    }

    protected void addTabledPredicates(Rule rule) {
	final Predicate headPred = rule.getHead().getFunctor();
	tabledPredicates.add(headPred);
	for (final Literal negLiteral : rule.getNegativeBody())
	    tabledPredicates.add(negLiteral.getFunctor());
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