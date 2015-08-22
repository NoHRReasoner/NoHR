/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.centria.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.centria.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.centria.nohr.deductivedb.XSBDeductiveDatabase;
import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.centria.nohr.rulebase.RuleBase;
import pt.unl.fct.di.centria.nohr.rulebase.RuleBaseImpl;
import pt.unl.fct.di.centria.nohr.rulebase.RuleBaseListener;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class HybridKBImpl implements HybridKB {

	private boolean hasDisjunctions;

	private boolean hasOntologyChanges;

	private boolean hasRuleChanges;

	private final OWLOntology ontology;

	private final OntologyTranslator ontologyTranslator;

	private final QueryProcessor queryProcessor;

	private final RuleBase ruleBase;

	private final DeductiveDatabase dedutiveDatabaseManager;

	private final VocabularyMapping vocabularyMapping;

	public HybridKBImpl(final File xsbBinDirectory) throws OWLProfilesViolationsException, IOException,
			UnsupportedAxiomsException, IPException, PrologEngineCreationException {
		this(xsbBinDirectory, Collections.<OWLAxiom> emptySet());
	}

	public HybridKBImpl(final File xsbBinDirectory, final Profile profile) throws OWLProfilesViolationsException,
			IPException, UnsupportedAxiomsException, IOException, PrologEngineCreationException {
		this(xsbBinDirectory, Collections.<OWLAxiom> emptySet(), new RuleBaseImpl(), profile);
	}

	public HybridKBImpl(final File xsbBinDirectory, final RuleBase ruleBase) throws IOException,
			OWLProfilesViolationsException, UnsupportedAxiomsException, IPException, PrologEngineCreationException {
		this(xsbBinDirectory, Collections.<OWLAxiom> emptySet(), new RuleBaseImpl(), null);
	}

	public HybridKBImpl(final File xsbBinDirectory, final Set<OWLAxiom> axioms) throws IOException,
			OWLProfilesViolationsException, UnsupportedAxiomsException, IPException, PrologEngineCreationException {
		this(xsbBinDirectory, axioms, new RuleBaseImpl(), null);
	}

	public HybridKBImpl(final File xsbBinDirectory, final Set<OWLAxiom> axioms, Profile profile)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException, IOException,
			PrologEngineCreationException {
		this(xsbBinDirectory, axioms, new RuleBaseImpl(), profile);
	}

	public HybridKBImpl(final File xsbBinDirectory, final Set<OWLAxiom> axioms, final RuleBase ruleBase)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException, IOException,
			PrologEngineCreationException {
		this(xsbBinDirectory, axioms, ruleBase, null);
	}

	public HybridKBImpl(final File xsbBinDirectory, final Set<OWLAxiom> axioms, final RuleBase ruleBase,
			Profile profile) throws OWLProfilesViolationsException, UnsupportedAxiomsException, IPException,
					IOException, PrologEngineCreationException {
		Objects.requireNonNull(xsbBinDirectory);
		try {
			ontology = OWLManager.createOWLOntologyManager().createOntology(axioms, IRI.generateDocumentIRI());
		} catch (final OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
		final Set<OWLOntology> ontologies = new HashSet<>();
		ontologies.add(ontology);
		vocabularyMapping = new VocabularyMappingImpl(ontologies);
		hasOntologyChanges = true;
		dedutiveDatabaseManager = new XSBDeductiveDatabase(xsbBinDirectory, vocabularyMapping);
		ontologyTranslator = new OntologyTranslatorImpl(ontology, dedutiveDatabaseManager, profile);
		queryProcessor = new QueryProcessor(dedutiveDatabaseManager);
		this.ruleBase = ruleBase;
		ruleBase.addListner(new RuleBaseListener() {

			@Override
			public void added(Rule rule) {
				hasRuleChanges = true;
			}

			@Override
			public void cleaned() {
				hasRuleChanges = true;
			}

			@Override
			public void removed(Rule rule) {
				hasRuleChanges = true;
			}

			@Override
			public void updated(Rule oldRule, Rule newRule) {
				hasRuleChanges = true;
			}
		});
		preprocess();
	}

	@Override
	public boolean addAxiom(OWLAxiom axiom) {
		final List<OWLOntologyChange> changes = ontology.getOWLOntologyManager().addAxiom(ontology, axiom);
		if (!changes.isEmpty()) {
			hasOntologyChanges = true;
			return true;
		}
		return false;
	}

	@Override
	public List<Answer> allAnswers(Query query)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException, IOException {
		return allAnswers(query, true, true, true);
	}

	@Override
	public List<Answer> allAnswers(Query query, boolean trueAnswer, boolean undefinedAnswers,
			boolean inconsistentAnswers)
					throws IOException, OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasRuleChanges)
			preprocess();
		RuntimesLogger.start("query");
		RuntimesLogger.info("querying: " + query);
		final List<Answer> answers = queryProcessor.allAnswers(query, hasDisjunctions, trueAnswer, undefinedAnswers,
				hasDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		final List<Answer> result = new LinkedList<Answer>();
		for (final Answer ans : answers)
			result.add(ans);
		return result;
	}

	@Override
	public void dispose() {
		dedutiveDatabaseManager.dipose();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		dispose();
	}

	/**
	 * @return the ruleBase
	 */
	@Override
	public RuleBase getRuleBase() {
		return ruleBase;
	}

	private String getRuleBaseProgramKey() {
		return "rulebase" + ruleBase.hashCode();
	}

	@Override
	public VocabularyMapping getVocabularyMapping() {
		return vocabularyMapping;
	}

	@Override
	public boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, IOException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasRuleChanges)
			preprocess();
		RuntimesLogger.start("query");
		final boolean hasAnswer = queryProcessor.hasAnswer(query, hasDisjunctions, trueAnswer, undefinedAnswers,
				hasDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		return hasAnswer;
	}

	@Override
	public Answer oneAnswer(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException {
		return oneAnswer(query, true, true, true);
	}

	@Override
	public Answer oneAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLOntologyCreationException, OWLOntologyStorageException, OWLProfilesViolationsException,
			IOException, CloneNotSupportedException, UnsupportedAxiomsException {
		if (hasOntologyChanges || hasRuleChanges)
			preprocess();
		RuntimesLogger.start("query");

		final Answer answer = queryProcessor.oneAnswer(query, hasDisjunctions, trueAnswer, undefinedAnswers,
				hasDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		return answer;
	}

	private void preprocess() throws IOException, OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges) {
			RuntimesLogger.start("ontology processing");
			ontologyTranslator.translate();
			RuntimesLogger.stop("ontology processing", "loading");
		}
		if (hasRuleChanges || ontologyTranslator.hasDisjunctions() != hasDisjunctions) {
			RuntimesLogger.start("rules parsing");
			dedutiveDatabaseManager.dispose(getRuleBaseProgramKey());
			for (final Rule rule : ruleBase)
				for (final Rule doublingRule : RulesDoubling.doubleRule(rule))
					dedutiveDatabaseManager.add(getRuleBaseProgramKey(), doublingRule);
			RuntimesLogger.stop("rules parsing", "loading");
		}
		hasOntologyChanges = false;
		hasRuleChanges = false;
		hasDisjunctions = ontologyTranslator.hasDisjunctions();
	}

	@Override
	public boolean removeAxiom(OWLAxiom axiom) {
		final List<OWLOntologyChange> changes = ontology.getOWLOntologyManager().removeAxiom(ontology, axiom);
		if (!changes.isEmpty())
			hasOntologyChanges = true;
		return !changes.isEmpty();
	}

}
