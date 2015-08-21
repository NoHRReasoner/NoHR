/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.rulebase.RuleBase;

/**
 * @author Nuno Costa
 */
public interface HybridKB {

	boolean addAxiom(OWLAxiom axiom);

	List<Answer> allAnswers(Query query) throws OWLProfilesViolationsException, UnsupportedAxiomsException, IOException;

	List<Answer> allAnswers(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws IOException, OWLProfilesViolationsException, UnsupportedAxiomsException;

	void dispose();

	/**
	 * @return the ruleBase
	 */
	RuleBase getRuleBase();

	VocabularyMapping getVocabularyMapping();

	boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, IOException, UnsupportedAxiomsException;

	Answer oneAnswer(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException;

	Answer oneAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLOntologyCreationException, OWLOntologyStorageException, OWLProfilesViolationsException,
			IOException, CloneNotSupportedException, UnsupportedAxiomsException;

	boolean removeAxiom(OWLAxiom axiom);

}