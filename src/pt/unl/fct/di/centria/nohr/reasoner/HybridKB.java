/*
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import pt.unl.fct.di.centria.nohr.reasoner.translation.DeHashVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.HashVisitor;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Translator;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import utils.Tracer;

import com.declarativa.interprolog.util.IPException;

public class HybridKB implements OWLOntologyChangeListener {

    private boolean isOntologyChanged;

    private static Translator translator;

    private boolean hasDisjunction;

    private OWLOntologyManager om;

    private OWLOntology ontology;

    private int queryCount;

    private QueryProcessor queryProcessor;

    private XSBDatabase xsbDatabase;

    public OWLReasoner reasoner;

    private final ArrayList<String> variablesList = new ArrayList<String>();

    private File xsbFile;

    public HybridKB(OWLOntology ontology) throws IPException {
	try {
	    om = OWLManager.createOWLOntologyManager();
	    this.ontology = ontology;
	    isOntologyChanged = true;
	    Rules.isRulesOntologyChanged = true;

	    xsbDatabase = xsbDatabase();

	    queryProcessor = new QueryProcessor(xsbDatabase);

	    translator = new Translator(ontology, xsbDatabase);

	    queryCount = 1;

	} catch (final IPException e) {
	    throw e;
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public HybridKB(OWLOntologyManager ontologyManager, OWLOntology ontolgy)
	    throws Exception {
	om = ontologyManager;
	ontology = ontolgy;
	om.addOntologyChangeListener(this);
	isOntologyChanged = true;
	Rules.isRulesOntologyChanged = true;

	xsbDatabase = xsbDatabase();

	queryProcessor = new QueryProcessor(xsbDatabase);

	translator = new Translator(ontology, xsbDatabase);

	queryCount = 1;
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
	    translator = new Translator(om, ontology, xsbDatabase);
	    for (final OWLOntologyChange change : changes)
		if (change.getOntology() == ontology) {
		    isOntologyChanged = true;
		    Rules.dispose();
		    if (HybridKB.translator != null)
			HybridKB.translator.clear();
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
		translator.proceed();
		utils.Tracer.stop("ontology proceeding", "loading");
	    }
	    if (Rules.isRulesOntologyChanged
		    || translator.isAnyDisjointWithStatement() != hasDisjunctions) {
		utils.Tracer.start("rules parsing");
		translator.appendRules(Rules.getRules());
		utils.Tracer.stop("rules parsing", "loading");
		Rules.isRulesOntologyChanged = false;
	    }
	    utils.Tracer.start("file writing");
	    xsbFile = translator.Finish();
	    utils.Tracer.stop("file writing", "loading");
	    loadRulesInXSB(xsbFile);
	    isOntologyChanged = false;
	    Rules.isRulesOntologyChanged = false;
	    hasDisjunction = translator.isAnyDisjointWithStatement();
	    translator.getCollectionsManager().getLabels();
	    new Query(translator.getCollectionsManager());

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
	final Visitor hashVisitor = new HashVisitor(
		translator.getCollectionsManager());
	final Visitor deHashVisitor = new DeHashVisitor(
		translator.getCollectionsManager());
	Tracer.start("query" + queryCount);
	final Collection<Answer> answers = queryProcessor.queryAll(
		query.acept(hashVisitor), hasDisjunction);
	Tracer.stop("query" + queryCount++, "queries");
	final Collection<Answer> res = new LinkedList<Answer>();
	for (final Answer ans : answers)
	    res.add(ans.acept(deHashVisitor));
	return res;
    }

    private XSBDatabase xsbDatabase() throws Exception {
	return new XSBDatabase(FileSystems.getDefault().getPath(
		System.getenv("XSB_BIN_DIRECTORY"), "xsb"));
    }
}
