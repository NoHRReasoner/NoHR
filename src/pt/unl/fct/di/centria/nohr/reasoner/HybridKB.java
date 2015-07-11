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
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Visitor;
import pt.unl.fct.di.centria.nohr.plugin.Rules;
import pt.unl.fct.di.centria.nohr.reasoner.translation.EscapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.RuleTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.UnescapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class HybridKB implements OWLOntologyChangeListener {

    private static final String TRANSLATION_FILE_NAME = "nohrtr.P";

    private boolean hasChanges;

    private boolean hasDisjunctions;

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

    private Set<Rule> ontologyTranslation;

    private OntologyTranslator ontologyTranslator;

    private final QueryProcessor queryProcessor;

    private Set<String> rulesTranslation;

    private final RuleTranslator ruleTranslator;

    private final XSBDatabase xsbDatabase;

    public HybridKB(final OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	this(OWLManager.createOWLOntologyManager(), ontology);
    }

    public HybridKB(final OWLOntologyManager ontologyManager,
	    final OWLOntology ontology) throws OWLOntologyCreationException,
	    OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
	    CloneNotSupportedException, UnsupportedAxiomTypeException {
	hasChanges = true;
	this.ontology = ontology;
	this.ontologyManager = ontologyManager;
	ontologyTranslation = new HashSet<Rule>();
	ontologyTranslator = AbstractOntologyTranslator
		.createOntologyTranslator(ontologyManager, ontology);
	xsbDatabase = new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
	queryProcessor = new QueryProcessor(xsbDatabase);
	rulesTranslation = new HashSet<String>();
	ruleTranslator = new RuleTranslator();
	this.ontologyManager.addOntologyChangeListener(this);
	Rules.hasChanges = true;
    }

    private File generateTranslationFile() throws IOException {
	final File file = FileSystems.getDefault()
		.getPath(TRANSLATION_FILE_NAME).toAbsolutePath().toFile();
	final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	final Set<String> tabled = new HashSet<String>();
	tabled.addAll(ontologyTranslator.getTabledPredicates());
	tabled.addAll(ruleTranslator.getTabledPredicates());
	for (final String predicate : tabled) {
	    writer.write(":- table " + predicate + ".");
	    writer.newLine();
	}
	for (final Rule rule : ontologyTranslation) {
	    writer.write(rule + ".");
	    writer.newLine();
	}
	for (String rule : rulesTranslation) {
	    if (!rule.endsWith("."))
		rule += ".";
	    writer.write(rule);
	    writer.newLine();
	}
	writer.close();
	return file;
    }

    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
	    throws OWLException {
	try {
	    ontologyTranslator = AbstractOntologyTranslator
		    .createOntologyTranslator(ontologyManager, ontology);
	    for (final OWLOntologyChange change : changes)
		if (change.getOntology() == ontology) {
		    hasChanges = true;
		    Rules.dispose();
		    break;
		}
	} catch (IOException | CloneNotSupportedException
		| UnsupportedOWLProfile e) {
	    e.printStackTrace();
	} catch (final UnsupportedAxiomTypeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void preprocess() throws OWLOntologyCreationException,
    OWLOntologyStorageException, UnsupportedOWLProfile, IOException {
	if (hasChanges) {
	    pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger
	    .start("ontology processing");
	    ontologyTranslation = new HashSet<Rule>();
	    ontologyTranslation = ontologyTranslator.getTranslation();
	    pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger.stop(
		    "ontology processing", "loading");
	}
	if (Rules.hasChanges
		|| ontologyTranslator.hasDisjunctions() != hasDisjunctions) {
	    pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger
	    .start("rules parsing");
	    rulesTranslation = new HashSet<String>();
	    ruleTranslator.reset();
	    for (final String rule : Rules.getRules())
		rulesTranslation.addAll(ruleTranslator.proceedRule(rule,
			ontologyTranslator.hasDisjunctions(),
			ontologyTranslator.getNegatedPredicates()));
	    pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger.stop(
		    "rules parsing", "loading");
	    Rules.hasChanges = false;
	}
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger
	.start("file writing");
	final File xsbFile = generateTranslationFile();
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger.stop(
		"file writing", "loading");
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger
	.start("xsb loading");
	xsbDatabase.clear();
	xsbDatabase.load(xsbFile);
	pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger.stop("xsb loading",
		"loading");
	hasChanges = false;
	hasDisjunctions = ontologyTranslator.hasDisjunctions();
    }

    public Collection<Answer> queryAll(Query query)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException {
	if (hasChanges || Rules.hasChanges)
	    preprocess();
	final Visitor escapeVisitor = new EscapeVisitor();
	final Visitor unescapeVisitor = new UnescapeVisitor();
	RuntimesLogger.start("query");
	final Collection<Answer> answers = queryProcessor.queryAll(
		query.acept(escapeVisitor), hasDisjunctions);
	RuntimesLogger.stop("query", "queries");
	final Collection<Answer> result = new LinkedList<Answer>();
	for (final Answer ans : answers)
	    result.add(ans.acept(unescapeVisitor));
	return result;
    }

    public void shutdown() {
	xsbDatabase.shutdown();
    }
}
