/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import static pt.unl.fct.di.centria.nohr.model.Model.prog;
import static pt.unl.fct.di.centria.nohr.model.Model.table;

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

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.TableDirective;
import pt.unl.fct.di.centria.nohr.prolog.XSBDedutiveDatabase;
import pt.unl.fct.di.centria.nohr.prolog.DatabaseCreationException;
import pt.unl.fct.di.centria.nohr.prolog.DedutiveDatabaseManager;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.OntologyTranslatorImpl;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Profile;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class HybridKB {

	private boolean hasDisjunctions;

	private boolean hasOntologyChanges;

	private final OWLOntology ontology;

	private final OntologyTranslator ontologyTranslator;

	private final QueryProcessor queryProcessor;

	private final RuleBase ruleBase;

	private final Set<Rule> rulesDuplication;

	private final DedutiveDatabaseManager dedutiveDatabaseManager;

	public HybridKB(final File xsbBinDirectory) throws OWLProfilesViolationsException, IOException,
			UnsupportedAxiomsException, IPException, DatabaseCreationException {
		this(xsbBinDirectory, Collections.<OWLAxiom> emptySet());
	}

	public HybridKB(final File xsbBinDirectory, final Profile profile) throws OWLProfilesViolationsException,
			IPException, UnsupportedAxiomsException, IOException, DatabaseCreationException {
		this(xsbBinDirectory, Collections.<OWLAxiom> emptySet(), new RuleBase(), profile);
	}

	public HybridKB(final File xsbBinDirectory, final RuleBase ruleBase) throws IOException,
			OWLProfilesViolationsException, UnsupportedAxiomsException, IPException, DatabaseCreationException {
		this(xsbBinDirectory, Collections.<OWLAxiom> emptySet(), new RuleBase(), null);
	}

	public HybridKB(final File xsbBinDirectory, final Set<OWLAxiom> axioms) throws IOException,
			OWLProfilesViolationsException, UnsupportedAxiomsException, IPException, DatabaseCreationException {
		this(xsbBinDirectory, axioms, new RuleBase(), null);
	}

	public HybridKB(final File xsbBinDirectory, final Set<OWLAxiom> axioms, Profile profile)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException, IOException,
			DatabaseCreationException {
		this(xsbBinDirectory, axioms, new RuleBase(), profile);
	}

	public HybridKB(final File xsbBinDirectory, final Set<OWLAxiom> axioms, final RuleBase ruleBase)
			throws OWLProfilesViolationsException, IPException, UnsupportedAxiomsException, IOException,
			DatabaseCreationException {
		this(xsbBinDirectory, axioms, ruleBase, null);
	}

	public HybridKB(final File xsbBinDirectory, final Set<OWLAxiom> axioms, final RuleBase ruleBase, Profile profile)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException, IPException, IOException,
			DatabaseCreationException {
		Objects.requireNonNull(xsbBinDirectory);
		try {
			ontology = OWLManager.createOWLOntologyManager().createOntology(axioms, IRI.generateDocumentIRI());
		} catch (final OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
		hasOntologyChanges = true;
		dedutiveDatabaseManager = new XSBDedutiveDatabase(xsbBinDirectory);
		ontologyTranslator = new OntologyTranslatorImpl(ontology, dedutiveDatabaseManager, profile);
		queryProcessor = new QueryProcessor(dedutiveDatabaseManager);
		this.ruleBase = ruleBase;
		rulesDuplication = new HashSet<Rule>();
		preprocess();
	}

	public boolean addAxiom(OWLAxiom axiom) {
		final List<OWLOntologyChange> changes = ontology.getOWLOntologyManager().addAxiom(ontology, axiom);
		if (!changes.isEmpty()) {
			hasOntologyChanges = true;
			return true;
		}
		return false;
	}

	public void dispose() {
		dedutiveDatabaseManager.clear();
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
	public RuleBase getRuleBase() {
		return ruleBase;
	}

	public boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLProfilesViolationsException, IOException, UnsupportedAxiomsException {
		if (hasOntologyChanges || ruleBase.hasChanges())
			preprocess();
		RuntimesLogger.start("query");
		final boolean hasAnswer = queryProcessor.hasAnswer(query, hasDisjunctions, trueAnswer, undefinedAnswers,
				hasDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		return hasAnswer;
	}

	private void preprocess() throws IOException, OWLProfilesViolationsException, UnsupportedAxiomsException {
		dedutiveDatabaseManager.clear();
		if (hasOntologyChanges) {
			RuntimesLogger.start("ontology processing");
			ontologyTranslator.translate();
			RuntimesLogger.stop("ontology processing", "loading");
		}
		if (ruleBase.hasChanges(true) || ontologyTranslator.hasDisjunctions() != hasDisjunctions) {
			RuntimesLogger.start("rules parsing");
			rulesDuplication.clear();
			for (final Rule rule : ruleBase.getRules())
				Collections.addAll(rulesDuplication, RulesDoubling.doubleRule(rule));
			dedutiveDatabaseManager.load(prog(ruleBase, rulesTabledPredicates(), rulesDuplication));
			RuntimesLogger.stop("rules parsing", "loading");
		}
		hasOntologyChanges = false;
		hasDisjunctions = ontologyTranslator.hasDisjunctions();
	}

	public Answer query(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
			OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException {
		return query(query, true, true, true);
	}

	public Answer query(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws OWLOntologyCreationException, OWLOntologyStorageException, OWLProfilesViolationsException,
			IOException, CloneNotSupportedException, UnsupportedAxiomsException {
		if (hasOntologyChanges || ruleBase.hasChanges())
			preprocess();
		RuntimesLogger.start("query");

		final Answer answer = queryProcessor.query(query, hasDisjunctions, trueAnswer, undefinedAnswers,
				hasDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		return answer;
	}

	public List<Answer> queryAll(Query query)
			throws OWLProfilesViolationsException, UnsupportedAxiomsException, IOException {
		return queryAll(query, true, true, true);
	}

	public List<Answer> queryAll(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
			throws IOException, OWLProfilesViolationsException, UnsupportedAxiomsException {
		if (hasOntologyChanges || ruleBase.hasChanges())
			preprocess();
		RuntimesLogger.start("query");
		RuntimesLogger.info("querying: " + query);
		final List<Answer> answers = queryProcessor.queryAll(query, hasDisjunctions, trueAnswer, undefinedAnswers,
				hasDisjunctions ? inconsistentAnswers : false);
		RuntimesLogger.stop("query", "queries");
		final List<Answer> result = new LinkedList<Answer>();
		for (final Answer ans : answers)
			result.add(ans);
		return result;
	}

	public boolean removeAxiom(OWLAxiom axiom) {
		final List<OWLOntologyChange> changes = ontology.getOWLOntologyManager().removeAxiom(ontology, axiom);
		if (!changes.isEmpty())
			hasOntologyChanges = true;
		return !changes.isEmpty();
	}

	private Set<TableDirective> rulesTabledPredicates() {
		final Set<TableDirective> result = new HashSet<>();
		for (final Rule rule : rulesDuplication)
			for (final Literal literal : rule.getNegativeBody())
				result.add(table(literal.getFunctor()));
		return result;
	}

}
