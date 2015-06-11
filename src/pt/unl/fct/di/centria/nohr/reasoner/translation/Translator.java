package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import other.Config;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.reasoner.Query;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.CollectionsManager;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyLabel;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.OntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.TranslationAlgorithm;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELAxiomsTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.el.ELOntologyTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.INormalizedOntology;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.Normalizer;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLAxiomsTranslator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql.QLOntologyTranslator;
import pt.unl.fct.di.centria.nohr.xsb.XSBDatabase;
import utils.Tracer;

//TODO remove

/**
 * The Class local.translate.Translate. The entrance to core of magic.
 */
public class Translator {

    private INormalizedOntology normalizedOntology;

    /** The ontology file. */
    private File ontologyFile;

    /** The ontology manager. */
    private final OWLOntologyManager ontologyManager;

    /** The ontology. */
    private OWLOntology ontology;

    /** The ontologies. */
    private List<OWLOntology> ontologies = new ArrayList<OWLOntology>();

    /** The _ontology label. */
    private OWLAnnotationProperty _ontologyLabel;

    /** The reasoner. */
    private OWLReasoner reasoner;

    private XSBDatabase xsbDatabase;

    /** The collections manager. */
    public static CollectionsManager collectionsManager;

    /** The prolog commands. */
    private final List<String> prologCommands = Arrays.asList(
	    ":- abolish_all_tables.", ":- set_prolog_flag(unknown,fail).");

    /** The temp dir prop. */
    private final String tempDirProp = "java.io.tmpdir";

    /** The temp dir. */
    private String tempDir = "";

    /** The result file name. */
    private String resultFileName = "nohrtr.P";

    /** The ontology label. */
    private OntologyLabel ontologyLabel;
    /** The rule creator. */
    private ELAxiomsTranslator ruleCreator;

    /** The rule translator. */
    private RuleTranslator ruleTranslator;

    /** The cm. */
    private CollectionsManager cm;

    /** The query. */
    private Query query;
    /**
     * all processing about ontology
     */
    private OntologyTranslator ontologyProceeder;

    private Collection<Rule> translationContainer;

    private boolean isOwl2elProfile;

    private boolean isOwl2qlProfile;

    public Translator(OWLOntology owlOntology, XSBDatabase xsbDatabase)
	    throws OWLOntologyCreationException, OWLOntologyStorageException,
	    UnsupportedOWLProfile {
	this.xsbDatabase = xsbDatabase;
	ontologyManager = OWLManager.createOWLOntologyManager();
	ontology = owlOntology;
	// checkOwlProfile();
	// initCollections();
	// initELK();
    }

    /**
     * Instantiates a new translator.
     *
     * @param owlModelManager
     *            the owl model manager
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     * @throws OWLOntologyStorageException
     *             the oWL ontology storage exception
     * @throws CloneNotSupportedException
     *             the clone not supported exception
     * @throws UnsupportedOWLProfile
     */
    public Translator(OWLOntologyManager owlModelManager, OWLOntology ontology,
	    XSBDatabase xsbDatabase) throws IOException,
	    OWLOntologyCreationException, OWLOntologyStorageException,
	    CloneNotSupportedException, UnsupportedOWLProfile {
	ontologyManager = owlModelManager;
	this.xsbDatabase = xsbDatabase;
	this.ontology = ontology;
	tempDir = System.getProperty(tempDirProp);
	// checkOwlProfile();
	// initCollections();
	// if (getTranslationAlgorithm() == TranslationAlgorithm.EL)
	// getInferredDataFromReasoner(owlReasoner);
    }

    /**
     * Instantiates a new translator.
     *
     * @param filePath
     *            the file path
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws OWLOntologyStorageException
     *             the oWL ontology storage exception
     * @throws UnsupportedOWLProfile
     */
    public Translator(String filePath) throws OWLOntologyCreationException,
	    IOException, OWLOntologyStorageException, UnsupportedOWLProfile {
	/** Initializing a OntologyManager */
	// Date dateStart = new Date();
	ontologyManager = OWLManager.createOWLOntologyManager();

	ontologyFile = new File(filePath);
	if (ontologyFile.exists())
	    ontologyFile.createNewFile();
	ontology = ontologyManager
		.loadOntologyFromOntologyDocument(ontologyFile);
	// Utils.getDiffTime(dateStart, "Initializing is done, it took:");
	// checkOwlProfile();
	// initCollections();
	// initELK();
    }

    /**
     * Append rules.
     *
     * @param _rules
     *            the _rules
     * @throws Exception
     *             the exception
     */
    public void appendRules(ArrayList<String> _rules) throws Exception {
	cm.clearRules();
	ruleTranslator = new RuleTranslator(cm);
	for (String rule : _rules)
	    ruleTranslator.proceedRule(rule);
    }

    /**
     * Read a rules from file and proceed them.
     *
     * @param filePath
     *            path of rule's file
     * @throws Exception
     *             the exception
     */
    public void appendRules(String filePath) throws Exception {
	ruleTranslator = new RuleTranslator(cm);
	File rules = new File(filePath);
	if (rules != null) {
	    FileInputStream fstream = new FileInputStream(rules);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    // Read File Line By Line
	    while ((strLine = br.readLine()) != null)
		if (strLine.length() > 0)
		    ruleTranslator.proceedRule(strLine);
	    in.close();
	}
    }

    private void checkAndPartiallyNormalizeOntology()
	    throws OWLOntologyCreationException, OWLOntologyStorageException {
	ELOntologyTranslator proceeder = (ELOntologyTranslator) ontologyProceeder;
	if (proceeder.isOntologyNeedToBeNormalized(ontology))
	    ontology = proceeder.normalizeOntology(ontology, null);
    }

    // TODO: throw an exception if the ontology is not in a supported profile
    private void checkOwlProfile() throws UnsupportedOWLProfile {

	if (Config.translationAlgorithm != null)
	    return;

	OWL2ELProfile owl2elProfile = new OWL2ELProfile();
	OWL2QLProfile owl2qlProfile = new OWL2QLProfile();
	OWLProfileReport owl2elReport = owl2elProfile.checkOntology(ontology);
	OWLProfileReport owl2qlRerport = owl2qlProfile.checkOntology(ontology);
	isOwl2elProfile = owl2elReport.isInProfile();
	isOwl2qlProfile = owl2qlRerport.isInProfile();

	if (!isOwl2elProfile && !isOwl2qlProfile)
	    throw new UnsupportedOWLProfile(owl2qlRerport.getViolations());

	// if (!isOwl2elProfile && !isOwl2qlProfile)
	// throw new ImportsClosureNotInProfileException(new OWL2QLProfile());
	Tracer.logBool("OWL EL", isOwl2elProfile);
	Tracer.logBool("OWL QL", isOwl2qlProfile);
    }

    /**
     * Clear.
     */
    public void clear() {
	// owlClasses = new HashSet<OWLClass>();
	// objectProperties = new HashSet<OWLObjectProperty>();
	if (cm != null)
	    cm.clear();
	if (reasoner != null)
	    reasoner.dispose();
    }

    /**
     * Finish.
     *
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public File Finish() throws IOException {
	// Date dateStart = new Date();
	File file = FileSystems.getDefault().getPath(tempDir, resultFileName)
		.toAbsolutePath().toFile();
	FileWriter writer = new FileWriter(file);
	HashSet<String> tabled = new HashSet<String>();
	tabled.addAll(cm.getAllTabledPredicateOntology());
	tabled.addAll(cm.getAllTabledPredicateRule());
	for (String str : prologCommands)
	    writer.write(str + "\n");
	for (String str : tabled)
	    writer.write(":- table " + str + ".\n");
	for (String str : cm.getTranslatedOntologies())
	    writer.write(str + "\n");
	for (String str : cm.getTranslatedRules())
	    writer.write(str + "\n");
	writer.close();

	// Utils.getDiffTime(dateStart, "Writing XSB file: ");
	return file;
    }

    public CollectionsManager getCollectionsManager() {
	return collectionsManager;
    }

    /**
     * Gets the inferred data.
     *
     * @return the inferred data
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     */
    private void getInferredData() throws OWLOntologyCreationException {
	new Date();
	ontologies = new ArrayList<OWLOntology>();
	ontologies.add(ontology);
	/**
	 * To generate an inferred ontology we use implementations of inferred
	 * axiom generators
	 */
	ArrayList<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
	gens.add(new InferredSubClassAxiomGenerator());
	gens.add(new InferredEquivalentClassAxiomGenerator());
	gens.add(new InferredClassAssertionAxiomGenerator());
	new Date();

	OWLOntologyManager outputOntologyManager = OWLManager
		.createOWLOntologyManager();
	// Put the inferred axioms into a fresh empty ontology.
	OWLOntology infOnt = outputOntologyManager.createOntology();

	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
		gens);
	// iog.fillOntology(ontologyManager, ontology);
	iog.fillOntology(outputOntologyManager, infOnt);
	iog = null;

	ontologies.add(infOnt);
	// reasoner.dispose();
	// Utils.getDiffTime(dateStart, "Retrieving inferred information: ");
	((ELOntologyTranslator) ontologyProceeder)
		.setOntologiesToProceed(ontologies);
    }

    /**
     * Gets the inferred data from reasoner.
     *
     * @param owlReasoner
     *            the owl reasoner
     * @return the inferred data from reasoner
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     */
    private void getInferredDataFromReasoner(OWLReasoner owlReasoner)
	    throws OWLOntologyCreationException {
	boolean isNeedToInitLocalElk = true;
	if (owlReasoner != null
		&& owlReasoner.getReasonerName().equals("ELK Reasoner")) {
	    reasoner = owlReasoner;
	    getInferredData();
	    isNeedToInitLocalElk = false;
	}
	if (isNeedToInitLocalElk)
	    initELK();

    }

    /**
     * Gets the label by hash.
     *
     * @param hash
     *            the hash
     * @return the label by hash
     */
    public String getLabelByHash(String hash) {
	return cm.getLabelByHash(hash);
    }

    public TranslationAlgorithm getTranslationAlgorithm() {
	if (Config.translationAlgorithm != null)
	    return Config.translationAlgorithm;
	if (isOwl2qlProfile)
	    return TranslationAlgorithm.DL_LITE_R;
	else if (isOwl2elProfile)
	    return TranslationAlgorithm.EL;
	return null;
    }

    /**
     * Inits the collections.
     */
    private void initCollections() throws OWLOntologyCreationException,
	    OWLOntologyStorageException {
	_ontologyLabel = ontologyManager.getOWLDataFactory()
		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	cm = new CollectionsManager();
	collectionsManager = cm;
	ontologyLabel = new OntologyLabel(ontology, _ontologyLabel, cm);
	query = new Query(cm);
	switch (getTranslationAlgorithm()) {
	case DL_LITE_R:
	    utils.Tracer.start("ontology normalization");
	    normalizedOntology = new Normalizer(ontology);
	    utils.Tracer.stop("ontology normalization", "loading");
	    QLAxiomsTranslator ruleCreatorQL = new QLAxiomsTranslator(cm,
		    ontologyLabel, normalizedOntology, ontologyManager);
	    ontologyProceeder = new QLOntologyTranslator(cm, ruleCreatorQL,
		    normalizedOntology, ontologyManager.getOWLDataFactory(),
		    ontologyManager);
	    break;
	case EL:
	    ELAxiomsTranslator ruleCreator = new ELAxiomsTranslator(cm,
		    ontologyLabel);
	    ontologyProceeder = new ELOntologyTranslator(cm, ruleCreator);
	    checkAndPartiallyNormalizeOntology();
	}
    }

    /**
     * Inits the elk.
     *
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     */
    private void initELK() throws OWLOntologyCreationException {
	if (getTranslationAlgorithm() == TranslationAlgorithm.EL) {
	    Tracer.start("ontology classification");
	    new Date();
	    OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	    reasoner = reasonerFactory.createReasoner(ontology);
	    /** Classify the ontology. */
	    reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

	    // Utils.getDiffTime(dateStart, "Running ELK Reasoner: ");

	    getInferredData();
	    Tracer.stop("ontology classification", "loading");
	}
    }

    /**
     * Checks if is any disjoint with statement.
     *
     * @return true, if is any disjoint with statement
     */
    public boolean isAnyDisjointWithStatement() {
	return cm.isAnyDisjointStatement();
    }

    /**
     * Prepare for translating.
     *
     * @return true, if successful
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     * @throws OWLOntologyStorageException
     *             the oWL ontology storage exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws UnsupportedOWLProfile
     */
    public boolean PrepareForTranslating() throws OWLOntologyCreationException,
	    OWLOntologyStorageException, IOException, UnsupportedOWLProfile {
	checkOwlProfile();
	initELK();
	// TODO normalize and initialize graph
	cm.clearOntology();
	return true;
    }

    /**
     * Prepare query.
     *
     * @param q
     *            the q
     * @return the string
     */
    public String prepareQuery(String q) {
	return query.prepareQuery(q, isAnyDisjointWithStatement());
    }

    public void proceed() throws ParserException, UnsupportedOWLProfile,
	    OWLOntologyCreationException, OWLOntologyStorageException {
	checkOwlProfile();
	initCollections();
	if (getTranslationAlgorithm() == TranslationAlgorithm.EL)
	    initELK();
	cm.clearOntology();
	ontologyProceeder.proceed();
    }

    /**
     * Sets the result file name.
     *
     * @param path
     *            the new result file name
     */
    public void setResultFileName(String path) {
	resultFileName = path;
    }
}