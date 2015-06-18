/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Visitor;
import pt.unl.fct.di.centria.nohr.plugin.Rules;
import pt.unl.fct.di.centria.nohr.reasoner.translation.AbstractOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.DeHashVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.HashVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.RuleTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import utils.Tracer;

import com.declarativa.interprolog.util.IPException;

public class HybridKB implements OWLOntologyChangeListener {

    private boolean isOntologyChanged;

    private static OntologyTranslator translator;

    private boolean hasDisjunction;

    private OWLOntologyManager ontologyManager;

    private OWLOntology ontology;

    private int queryCount;

    private QueryProcessor queryProcessor;

    private XSBDatabase xsbDatabase;

    public OWLReasoner reasoner;

    private final ArrayList<String> variablesList = new ArrayList<String>();

    private File xsbFile;

    private Set<String> translation;

    private Set<String> rules;

    private RuleTranslator ruleTranslator;

    private final List<String> prologCommands = Arrays.asList(
	    ":- abolish_all_tables.", ":- set_prolog_flag(unknown,fail).");

    private String resultFileName = "nohrtr.P";

    public HybridKB(OWLOntology ontology) throws OWLOntologyCreationException,
    OWLOntologyStorageException, UnsupportedOWLProfile, IOException,
    CloneNotSupportedException {
	this(OWLManager.createOWLOntologyManager(), ontology);
    }

    public HybridKB(OWLOntologyManager ontologyManager, OWLOntology ontology)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile, IOException, CloneNotSupportedException {
	this.ontologyManager = ontologyManager;
	this.ontology = ontology;
	xsbDatabase = xsbDatabase();
	isOntologyChanged = true;
	translator = AbstractOntologyTranslator.createOntologyTranslator(
		ontologyManager, ontology);
	ruleTranslator = new RuleTranslator();
	queryProcessor = new QueryProcessor(xsbDatabase);
	this.ontologyManager.addOntologyChangeListener(this);
	Rules.isRulesOntologyChanged = true;
	queryCount = 1;
    }

    public void appendRules(ArrayList<String> _rules) throws Exception {
	rules = new HashSet<String>();
	ruleTranslator.reset();
	for (String rule : _rules)
	    rules.addAll(ruleTranslator.proceedRule(rule,
		    translator.hasDisjunctions(),
		    translator.getNegatedPredicates()));
    }

    public File Finish() throws IOException {
	File file = FileSystems.getDefault().getPath(resultFileName)
		.toAbsolutePath().toFile();
	FileWriter writer = new FileWriter(file);
	HashSet<String> tabled = new HashSet<String>();
	tabled.addAll(translator.getTabledPredicates());
	tabled.addAll(ruleTranslator.getTabledPredicates());
	for (String str : prologCommands)
	    writer.write(str + "\n");
	for (String str : tabled)
	    writer.write(":- table " + str + ".\n");
	for (String rule : translation)
	    writer.write(rule + "\n");
	for (String str : rules)
	    writer.write(str + "\n");
	writer.close();
	return file;
    }

    // TODO remove
    private String generateDetermenisticGoal(String command) {
	String detGoal = "findall(myTuple(TV";
	if (variablesList.size() > 0) {
	    detGoal += ", ";
	    detGoal += variablesList.toString().replace("[", "")
		    .replace("]", "");
	}
	// detGoal+="), call_tv(("+command+"), TV), List), buildTermModel(List,TM)";
	detGoal += "), call_tv((" + command
		+ "), TV), List), buildInitiallyFlatTermModel(List,TM)";
	return detGoal;
    }

    private void loadRulesInXSB(File file) throws IPException, Exception {
	utils.Tracer.start("xsb loading");
	xsbDatabase.clear();
	// boolean loaded =
	xsbDatabase.load(file);
	// if (!loaded)
	// throw new IPException("unsuccessful XSB loading");
	// initQuery???
	xsbDatabase.deterministicGoal(generateDetermenisticGoal("initQuery"));
	utils.Tracer.stop("xsb loading", "loading");
    }

    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
	    throws OWLException {
	try {
	    translator = AbstractOntologyTranslator.createOntologyTranslator(
		    ontologyManager, ontology);
	    for (final OWLOntologyChange change : changes)
		if (change.getOntology() == ontology) {
		    isOntologyChanged = true;
		    Rules.dispose();
		    break;
		}
	} catch (IOException | CloneNotSupportedException
		| UnsupportedOWLProfile e) {
	    e.printStackTrace();
	}
    }

    private void preprocessKB() throws Exception {
	try {
	    final boolean hasDisjunctions = hasDisjunction;
	    utils.Tracer.start("translator initialization");
	    utils.Tracer.stop("translator initialization", "loading");
	    if (isOntologyChanged) {
		utils.Tracer.start("ontology proceeding");
		translation = new HashSet<String>();
		translator.translate(translation);
		utils.Tracer.stop("ontology proceeding", "loading");
	    }
	    if (Rules.isRulesOntologyChanged
		    || translator.hasDisjunctions() != hasDisjunctions) {
		utils.Tracer.start("rules parsing");
		appendRules(Rules.getRules());
		utils.Tracer.stop("rules parsing", "loading");
		Rules.isRulesOntologyChanged = false;
	    }
	    utils.Tracer.start("file writing");
	    xsbFile = Finish();
	    utils.Tracer.stop("file writing", "loading");
	    loadRulesInXSB(xsbFile);
	    isOntologyChanged = false;
	    Rules.isRulesOntologyChanged = false;
	    hasDisjunction = translator.hasDisjunctions();
	} catch (final OWLOntologyCreationException e) {
	    e.printStackTrace();
	} catch (final OWLOntologyStorageException e) {
	    e.printStackTrace();
	} catch (final IOException e) {
	    e.printStackTrace();
	} catch (final ParserException e) {
	    e.printStackTrace();
	} catch (final Exception e) {
	    e.printStackTrace();
	} finally {

	}
    }

    public Collection<Answer> queryAll(
	    pt.unl.fct.di.centria.nohr.model.Query query) throws Exception {
	Tracer.info("query: " + query.toString());
	if (isOntologyChanged || Rules.isRulesOntologyChanged)
	    preprocessKB();
	final Visitor hashVisitor = new HashVisitor();
	final Visitor deHashVisitor = new DeHashVisitor();
	Tracer.start("query" + queryCount);
	final Collection<Answer> answers = queryProcessor.queryAll(
		query.acept(hashVisitor), hasDisjunction);
	Tracer.stop("query" + queryCount++, "queries");
	final Collection<Answer> res = new LinkedList<Answer>();
	for (final Answer ans : answers)
	    res.add(ans.acept(deHashVisitor));
	return res;
    }

    private XSBDatabase xsbDatabase() {
	return new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
    }
}
