/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

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
import org.semanticweb.owlapi.expression.ParserException;
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
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.plugin.Rules;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.UnescapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.EscapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.RuleTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import utils.Tracer;

public class HybridKB implements OWLOntologyChangeListener {

    private static final String TRANSLATION_FILE_NAME = "nohrtr.P";

    private boolean hasChanges;

    private boolean hasDisjunctions;

    private final OWLOntology ontology;

    private final OWLOntologyManager ontologyManager;

    private Set<String> ontologyTranslation;

    private OntologyTranslator ontologyTranslator;

    private int queryCount;

    private final QueryProcessor queryProcessor;

    private Set<String> rulesTranslation;

    private final RuleTranslator ruleTranslator;

    private final XSBDatabase xsbDatabase;

    public HybridKB(final OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException {
	this(OWLManager.createOWLOntologyManager(), ontology);
    }

    public HybridKB(final OWLOntologyManager ontologyManager,
	    final OWLOntology ontology) throws OWLOntologyCreationException,
	    OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
	    CloneNotSupportedException {
	hasChanges = true;
	this.ontology = ontology;
	this.ontologyManager = ontologyManager;
	ontologyTranslation = new HashSet<String>();
	ontologyTranslator = AbstractOntologyTranslator
		.createOntologyTranslator(ontologyManager, ontology);
	queryCount = 1;
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
	final FileWriter writer = new FileWriter(file);
	for (final String predicate : ontologyTranslator.getTabledPredicates())
	    writer.write(":- table " + predicate + ".\n");
	for (final String predicate : ruleTranslator.getTabledPredicates())
	    writer.write(":- table " + predicate + ".\n");
	for (String rule : ontologyTranslation) {
	    if (!rule.endsWith("."))
		rule += ".";
	    writer.write(rule + "\n");
	}
	for (String rule : rulesTranslation) {
	    if (!rule.endsWith("."))
		rule += ".";
	    writer.write(rule + "\n");
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
	}
    }

    private void preprocess() throws OWLOntologyCreationException,
	    OWLOntologyStorageException, ParserException,
	    UnsupportedOWLProfile, IOException {
	if (hasChanges) {
	    utils.Tracer.start("ontology proceeding");
	    ontologyTranslation = new HashSet<String>();
	    ontologyTranslator.translate(ontologyTranslation);
	    utils.Tracer.stop("ontology proceeding", "loading");
	}
	if (Rules.hasChanges
		|| ontologyTranslator.hasDisjunctions() != hasDisjunctions) {
	    utils.Tracer.start("rules parsing");
	    rulesTranslation = new HashSet<String>();
	    ruleTranslator.reset();
	    for (final String rule : Rules.getRules())
		rulesTranslation.addAll(ruleTranslator.proceedRule(rule,
			ontologyTranslator.hasDisjunctions(),
			ontologyTranslator.getNegatedPredicates()));
	    utils.Tracer.stop("rules parsing", "loading");
	    Rules.hasChanges = false;
	}
	utils.Tracer.start("file writing");
	final File xsbFile = generateTranslationFile();
	utils.Tracer.stop("file writing", "loading");
	utils.Tracer.start("xsb loading");
	xsbDatabase.clear();
	xsbDatabase.load(xsbFile);
	utils.Tracer.stop("xsb loading", "loading");
	hasChanges = false;
	hasDisjunctions = ontologyTranslator.hasDisjunctions();
    }

    public Collection<Answer> queryAll(Query query)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    ParserException, UnsupportedOWLProfile, IOException {
	if (hasChanges || Rules.hasChanges)
	    preprocess();
	final Visitor escapeVisitor = new EscapeVisitor();
	final Visitor unescapeVisitor = new UnescapeVisitor();
	Tracer.start("query" + queryCount);
	final Collection<Answer> answers = queryProcessor.queryAll(
		query.acept(escapeVisitor), hasDisjunctions);
	Tracer.stop("query" + queryCount++, "queries");
	final Collection<Answer> result = new LinkedList<Answer>();
	for (final Answer ans : answers)
	    result.add(ans.acept(unescapeVisitor));
	return result;
    }
}
