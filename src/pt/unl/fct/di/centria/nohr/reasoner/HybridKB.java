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
import pt.unl.fct.di.centria.nohr.plugin.Rules;
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

    private boolean hasRulesChanges;

    private final OWLOntology ontology;

    private OntologyTranslation ontologyTranslation;

    private final QueryProcessor queryProcessor;

    private final Set<Rule> rules;

    private final Set<Rule> rulesTranslation;

    private final XSBDatabase xsbDatabase;

    public HybridKB(final OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	this(ontology, new HashSet<Rule>());
    }

    public HybridKB(final OWLOntology ontology, final Set<Rule> rules)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	hasOntologyChanges = true;
	this.ontology = ontology;
	xsbDatabase = new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
	queryProcessor = new QueryProcessor(xsbDatabase);
	this.rules = new HashSet<Rule>(rules);
	rulesTranslation = new HashSet<Rule>();
	this.ontology.getOWLOntologyManager().addOntologyChangeListener(this);
	hasRulesChanges = true;
	Rules.hasChanges = true;
	ontology.getOWLOntologyManager().addOntologyChangeListener(this);
    }

    public void add(Rule rule) {
	rules.add(rule);
	hasRulesChanges = true;
    }

    public void addAll(Collection<Rule> rules) {
	rules.addAll(rules);
	hasRulesChanges = true;
    }

    public void clearRules() {
	rules.clear();
	hasRulesChanges = true;
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
	for (final Rule rule : rulesTranslation) {
	    writer.write(rule + ".");
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
	if (hasRulesChanges
		|| ontologyTranslation.hasDisjunctions() != hasDisjunctions) {
	    RuntimesLogger.start("rules parsing");
	    rulesTranslation.clear();
	    for (final Rule rule : rules)
		// final boolean negateHead = ontologyTranslation
		// .getNegativeHeadsPredicates().contains(
		// rule.getHead().getPredicate());
		Collections.addAll(rulesTranslation,
			RulesDuplication.duplicate(rule, true));
	    RuntimesLogger.stop("rules parsing", "loading");
	    hasRulesChanges = false;
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

    public Collection<Answer> queryAll(Query query)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException,
	    UnsupportedAxiomTypeException {
	if (hasOntologyChanges || Rules.hasChanges)
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

    public void remove(Rule rule) {
	rules.remove(rule);
	hasRulesChanges = true;
    }

    public void removeAll(Collection<Rule> rules) {
	rules.removeAll(rules);
	hasRulesChanges = true;
    }

    private Set<Predicate> rulesTabledPredicates() {
	final Set<Predicate> result = new HashSet<Predicate>();
	for (final Rule rule : rulesTranslation)
	    for (final Literal literal : rule.getNegativeBody())
		result.add(literal.getPredicate());
	return result;
    }

}
