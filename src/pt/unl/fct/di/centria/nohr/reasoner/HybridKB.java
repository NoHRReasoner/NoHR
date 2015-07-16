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

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Visitor;
import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;
import pt.unl.fct.di.centria.nohr.plugin.Rules;
import pt.unl.fct.di.centria.nohr.reasoner.translation.EscapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.RuleTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.UnescapeVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.UnsupportedAxiomTypeException;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import pt.unl.fct.di.centria.runtimeslogger.RuntimesLogger;

public class HybridKB implements OWLOntologyChangeListener {

    private static final String TRANSLATION_FILE_NAME = "nohrtr.P";

    private boolean hasChanges;

    private boolean hasDisjunctions;

    private final OWLOntology ontology;

    private OntologyTranslation ontologyTranslation;

    private final QueryProcessor queryProcessor;

    private Set<String> rulesTranslation;

    private final RuleTranslator ruleTranslator;

    private final XSBDatabase xsbDatabase;

    public HybridKB(final OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	hasChanges = true;
	this.ontology = ontology;
	xsbDatabase = new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
	queryProcessor = new QueryProcessor(xsbDatabase);
	rulesTranslation = new HashSet<String>();
	ruleTranslator = new RuleTranslator();
	this.ontology.getOWLOntologyManager().addOntologyChangeListener(this);
	Rules.hasChanges = true;
	ontology.getOWLOntologyManager().addOntologyChangeListener(this);
    }

    private Set<String> asStringSet(Set<Predicate> set) {
	final Set<String> result = new HashSet<String>(set.size());
	for (final Predicate pred : set)
	    result.add(pred.getName());
	return result;
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
	final Set<String> tabled = new HashSet<String>();
	tabled.addAll(asStringSet(ontologyTranslation.getTabledPredicates()));
	tabled.addAll(ruleTranslator.getTabledPredicates());
	for (final String predicate : tabled) {
	    writer.write(":- table " + predicate + " as subsumptive.");
	    writer.newLine();
	}
	for (final Rule rule : ontologyTranslation.getTranslation()) {
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
	for (final OWLOntologyChange change : changes)
	    if (change.getOntology() == ontology) {
		hasChanges = true;
		return;
	    }
    }

    private void preprocess() throws OWLOntologyCreationException,
    OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
    CloneNotSupportedException, UnsupportedAxiomTypeException {
	if (hasChanges) {
	    RuntimesLogger.start("ontology processing");
	    ontologyTranslation = AbstractOntologyTranslation
		    .createOntologyTranslation(ontology);
	    RuntimesLogger.stop("ontology processing", "loading");
	}
	if (Rules.hasChanges
		|| ontologyTranslation.hasDisjunctions() != hasDisjunctions) {
	    RuntimesLogger.start("rules parsing");
	    rulesTranslation = new HashSet<String>();
	    ruleTranslator.reset();
	    for (final String rule : Rules.getRules())
		rulesTranslation.addAll(ruleTranslator.proceedRule(rule,
			ontologyTranslation.hasDisjunctions(),
			asStringSet(ontologyTranslation
				.getNegativeHeadsPredicates())));
	    RuntimesLogger.stop("rules parsing", "loading");
	    Rules.hasChanges = false;
	}
	RuntimesLogger.start("file writing");
	final File xsbFile = generateTranslationFile();
	RuntimesLogger.stop("file writing", "loading");
	RuntimesLogger.start("xsb loading");
	xsbDatabase.clear();
	xsbDatabase.load(xsbFile);
	RuntimesLogger.stop("xsb loading", "loading");
	hasChanges = false;
	hasDisjunctions = ontologyTranslation.hasDisjunctions();
    }

    public Collection<Answer> queryAll(Query query)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
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
}
