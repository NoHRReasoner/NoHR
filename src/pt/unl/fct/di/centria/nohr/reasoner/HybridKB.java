/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Visitor;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.translation.EscapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.RulesDuplication;
import pt.unl.fct.di.centria.nohr.reasoner.translation.UnescapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class HybridKB implements OWLOntologyChangeListener {

    private static final String TRANSLATION_FILE_NAME = "nohrtr.P";

    private boolean hasDisjunctions;

    private boolean hasOntologyChanges;

    private final OWLOntology ontology;

    private OntologyTranslation ontologyTranslation;

    private final QueryProcessor queryProcessor;

    private final RuleBase ruleBase;

    private final Set<Rule> rulesDuplication;

    private final XSBDatabase xsbDatabase;

    public HybridKB(final OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	this(ontology, new RuleBase());
    }

    public HybridKB(final OWLOntology ontology, final RuleBase ruleBase)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	hasOntologyChanges = true;
	this.ontology = ontology;
	xsbDatabase = new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
	queryProcessor = new QueryProcessor(xsbDatabase);
	this.ruleBase = ruleBase;
	rulesDuplication = new HashSet<Rule>();
	this.ontology.getOWLOntologyManager().addOntologyChangeListener(this);
    }

    public HybridKB(final RuleBase ruleBase)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	this(OWLManager.createOWLOntologyManager().createOntology(),
		new RuleBase());
    }

    public void dispose() {
	xsbDatabase.dispose();
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

    private File generateTranslationFile() throws IOException,
    OWLOntologyCreationException, OWLOntologyStorageException,
    UnsupportedOWLProfile {
	final File file = FileSystems.getDefault()
		.getPath(TRANSLATION_FILE_NAME).toAbsolutePath().toFile();
	final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	final Set<Predicate> tabled = new HashSet<Predicate>();
	tabled.addAll(ontologyTranslation.getTabledPredicates());
	tabled.addAll(rulesTabledPredicates());
	for (final Predicate predicate : tabled) {
	    writer.write(":- table " + predicate.getName() + " as subsumptive.");
	    writer.newLine();
	}
	for (final Rule rule : ontologyTranslation.getTranslation()) {
	    writer.write(rule + ".");
	    writer.newLine();
	}
	for (final Rule rule : rulesDuplication) {
	    writer.write(rule + ".");
	    writer.newLine();
	}
	writer.close();
	return file;
    }

    /**
     * @return the ruleBase
     */
    public RuleBase getRuleBase() {
	return ruleBase;
    }

    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
	    throws OWLException {
	for (final OWLOntologyChange change : changes)
	    if (change.getOntology() == ontology) {
		hasOntologyChanges = true;
		return;
	    }
    }

    private void preprocess() throws OWLOntologyCreationException,
	    OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
	    CloneNotSupportedException, UnsupportedAxiomTypeException {
	if (hasOntologyChanges) {
	    RuntimesLogger.start("ontology processing");
	    ontologyTranslation = AbstractOntologyTranslation
		    .createOntologyTranslation(ontology);
	    RuntimesLogger.stop("ontology processing", "loading");
	}
	System.out.println(ruleBase);
	System.out.println(ruleBase.hasChanges());
	System.out.println(ruleBase.getRules());
	if (ruleBase.hasChanges(true)
		|| ontologyTranslation.hasDisjunctions() != hasDisjunctions) {
	    RuntimesLogger.start("rules parsing");
	    rulesDuplication.clear();
	    for (final Rule rule : ruleBase.getRules())
		Collections.addAll(rulesDuplication,
			RulesDuplication.duplicate(rule, true));
	    RuntimesLogger.stop("rules parsing", "loading");
	}
	RuntimesLogger.start("file writing");
	final File xsbFile = generateTranslationFile();
	RuntimesLogger.stop("file writing", "loading");
	RuntimesLogger.start("xsb loading");
	xsbDatabase.clear();
	xsbDatabase.load(xsbFile);
	RuntimesLogger.stop("xsb loading", "loading");
	hasOntologyChanges = false;
	hasDisjunctions = ontologyTranslation.hasDisjunctions();
    }

    public Answer query(Query query) throws OWLOntologyCreationException,
    OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
    CloneNotSupportedException, UnsupportedAxiomTypeException {
	return query(query, true, true, true);
    }

    public Answer query(Query query, boolean trueAnswer,
	    boolean undefinedAnswers, boolean inconsistentAnswers)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	if (hasOntologyChanges || ruleBase.hasChanges())
	    preprocess();
	final Visitor escapeVisitor = new EscapeVisitor();
	final Visitor unescapeVisitor = new UnescapeVisitor();
	RuntimesLogger.start("query");
	final Query escapedQuery = query.acept(escapeVisitor);
	final Answer answer = queryProcessor.query(escapedQuery,
		hasDisjunctions, trueAnswer, undefinedAnswers,
		hasDisjunctions ? inconsistentAnswers : false);
	RuntimesLogger.stop("query", "queries");
	return answer.acept(unescapeVisitor);
    }

    public Collection<Answer> queryAll(Query query)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	return queryAll(query, true, true, true);
    }

    public Collection<Answer> queryAll(Query query, boolean trueAnswer,
	    boolean undefinedAnswers, boolean inconsistentAnswers)
		    throws OWLOntologyCreationException, OWLOntologyStorageException,
		    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
		    UnsupportedAxiomTypeException {
	if (hasOntologyChanges || ruleBase.hasChanges())
	    preprocess();
	final Visitor escapeVisitor = new EscapeVisitor();
	final Visitor unescapeVisitor = new UnescapeVisitor();
	RuntimesLogger.start("query");
	final Query escapedQuery = query.acept(escapeVisitor);
	final Collection<Answer> answers = queryProcessor.queryAll(
		escapedQuery, hasDisjunctions, trueAnswer, undefinedAnswers,
		hasDisjunctions ? inconsistentAnswers : false);
	RuntimesLogger.stop("query", "queries");
	final Collection<Answer> result = new LinkedList<Answer>();
	for (final Answer ans : answers)
	    result.add(ans.acept(unescapeVisitor));
	return result;
    }

    private Set<Predicate> rulesTabledPredicates() {
	final Set<Predicate> result = new HashSet<Predicate>();
	for (final Rule rule : rulesDuplication)
	    for (final Literal literal : rule.getNegativeBody())
		result.add(literal.getPredicate());
	return result;
    }

}
