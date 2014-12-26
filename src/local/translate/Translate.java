package local.translate;

import org.apache.log4j.Level;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.*;
import java.util.*;

import local.translate.ql.OntologyProceederQL;
import local.translate.ql.RuleCreatorQL;

/**
 * The Class local.translate.Translate. The entrance to core of magic.
 */
public class Translate {

    /** The ontology file. */
    private File ontologyFile;

    /** The ontology manager. */
    private final OWLOntologyManager ontologyManager;

    /** The ontology. */
    private static OWLOntology ontology;

    /** The ontologies. */
    private List<OWLOntology> ontologies = new ArrayList<OWLOntology>();

    /** The _ontology label. */
    private OWLAnnotationProperty _ontologyLabel;

    /** The reasoner. */
    private OWLReasoner reasoner;

    /** The collections manager. */
    public static CollectionsManager collectionsManager;

    /** The prolog commands. */
    private final List<String> prologCommands = Arrays.asList(":- abolish_all_tables.", ":- set_prolog_flag(unknown,fail).");

    /** The temp dir prop. */
    private final String tempDirProp = "java.io.tmpdir";

    /** The temp dir. */
    private String tempDir = "";

    /** The result file name. */
    private String resultFileName = "result.p";
    /** The ontology label. */
    private OntologyLabel ontologyLabel;

    /** The rule creator. */
    private RuleCreator ruleCreator;

    /** The rule translator. */
    private RuleTranslator ruleTranslator;

    /** The cm. */
    private CollectionsManager cm;

    /** The query. */
    private Query query;

    /**
     * all processing about ontology
     */
    private OntologyProceeder ontologyProceeder;

	private boolean isOwl2elProfile;

	private boolean isOwl2qlProfile;

    /** The Constant log. */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Translate.class);

    /**
     * Instantiates a new translator.
     *
     * @param owlModelManager the owl model manager
     * @throws IOException                  Signals that an I/O exception has occurred.
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     * @throws OWLOntologyStorageException  the oWL ontology storage exception
     * @throws CloneNotSupportedException   the clone not supported exception
     */
    public Translate(OWLModelManager owlModelManager) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, CloneNotSupportedException {
        ontologyManager = owlModelManager.getOWLOntologyManager();
        ontology = owlModelManager.getActiveOntology();
        tempDir = System.getProperty(tempDirProp);
        checkOwlProfile();
        initCollections();
        if (isOwl2elProfile)
        	getInferredDataFromReasoner(owlModelManager.getReasoner());
        log.setLevel(Config.logLevel);
    }

	private void checkOwlProfile() {
		OWL2ELProfile owl2elProfile = new OWL2ELProfile();
		OWL2QLProfile owl2qlProfile = new OWL2QLProfile();
		isOwl2elProfile = owl2elProfile.checkOntology(ontology).isInProfile();
		isOwl2qlProfile = owl2qlProfile.checkOntology(ontology).isInProfile();
 	    Logger.logBool("OWL EL",  isOwl2elProfile);
	    Logger.logBool("OWL QL", isOwl2qlProfile);
	}

	/**
     * Instantiates a new translator.
     *
     * @param filePath the file path
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     * @throws IOException                  Signals that an I/O exception has occurred.
     * @throws OWLOntologyStorageException  the oWL ontology storage exception
     */
    public Translate(String filePath) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        /** Initializing a OntologyManager */
        Date dateStart = new Date();
        ontologyManager = OWLManager.createOWLOntologyManager();

        ontologyFile = new File(filePath);
        if (ontologyFile.exists()) {
            ontologyFile.createNewFile();
        }
        ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
        Utils.getDiffTime(dateStart, "Initializing is done, it took:");
        checkOwlProfile();
        initCollections();
        initELK();
        log.setLevel(Config.logLevel);
    }

    /**
     * Append rules.
     *
     * @param _rules the _rules
     * @throws Exception the exception
     */
    public void appendRules(ArrayList<String> _rules) throws Exception {
        ruleTranslator = new RuleTranslator(cm);
        for (String rule : _rules) {
            ruleTranslator.proceedRule(rule);
        }
    }

    /**
     * Read a rules from file and proceed them.
     *
     * @param filePath path of rule's file
     * @throws Exception the exception
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
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() > 0) {
                    ruleTranslator.proceedRule(strLine);
                }
            }
            in.close();
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        // owlClasses = new HashSet<OWLClass>();
        // objectProperties = new HashSet<OWLObjectProperty>();
        cm.clear();
        reasoner.dispose();
    }

    /**
     * Finish.
     *
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public File Finish() throws IOException {
        Date dateStart = new Date();
        FileWriter writer = new FileWriter(tempDir + resultFileName);
        HashSet<String> tabled = new HashSet<String>();
        log.info("tabled ontology count: " + cm.getAllTabledPredicateOntology().size());
        tabled.addAll(cm.getAllTabledPredicateOntology());
        log.info("tabled rules count: " + cm.getAllTabledPredicateRule().size());
        tabled.addAll(cm.getAllTabledPredicateRule());

        for (String str : prologCommands) {
            writer.write(str + "\n");
        }
        for (String str : tabled) {
            writer.write(":- table " + str + ".\n");
        }
        log.info("ontology count: " + cm.getTranslatedOntologies().size());
        for (String str : cm.getTranslatedOntologies()) {
            writer.write(str + "\n");
        }
        log.info("rule count: " + cm.getTranslatedRules().size());
        for (String str : cm.getTranslatedRules()) {
            writer.write(str + "\n");
        }
        writer.close();

        Utils.getDiffTime(dateStart, "Writing XSB file: ");
        return new File(tempDir + resultFileName);
    }

    /**
     * Gets the inferred data.
     *
     * @return the inferred data
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     */
    private void getInferredData() throws OWLOntologyCreationException {
        Date dateStart = new Date();
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
        Utils.getDiffTime(dateStart, "Generating inferred ontology: ");
        dateStart = new Date();

        OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
        // Put the inferred axioms into a fresh empty ontology.
        OWLOntology infOnt = outputOntologyManager.createOntology();

        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        // iog.fillOntology(ontologyManager, ontology);
        iog.fillOntology(outputOntologyManager, infOnt);
        iog = null;

        ontologies.add(infOnt);
        // reasoner.dispose();
        Utils.getDiffTime(dateStart, "Retrieving inferred information: ");
        ontologyProceeder.setOntologiesToProceed(ontologies);
    }

    /**
     * Gets the inferred data from reasoner.
     *
     * @param owlReasoner the owl reasoner
     * @return the inferred data from reasoner
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     */
    private void getInferredDataFromReasoner(OWLReasoner owlReasoner) throws OWLOntologyCreationException {
        boolean isNeedToInitLocalElk = true;
        if (owlReasoner != null && owlReasoner.getReasonerName().equals("ELK Reasoner")) {
            reasoner = owlReasoner;
            getInferredData();
            isNeedToInitLocalElk = false;
        }
        if (isNeedToInitLocalElk) {
            initELK();
        }

    }

    /**
     * Gets the label by hash.
     *
     * @param hash the hash
     * @return the label by hash
     */
    public String getLabelByHash(String hash) {
        return cm.getLabelByHash(hash);
    }

    /**
     * Inits the collections.
     */
    private void initCollections() throws OWLOntologyCreationException, OWLOntologyStorageException {
        _ontologyLabel = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        cm = new CollectionsManager();
        collectionsManager = cm;
        ontologyLabel = new OntologyLabel(ontology, _ontologyLabel, cm);
        query = new Query(cm);
        if (isOwl2elProfile) {
        	ruleCreator = new RuleCreator(cm, ontologyLabel);
        	ontologyProceeder = new OntologyProceeder(cm, ruleCreator);
        }
        else if (isOwl2qlProfile) {
        	ruleCreator = new RuleCreatorQL(cm, ontologyLabel);
        	ontologyProceeder = new OntologyProceederQL(cm, ruleCreator);
        }
        checkAndPartiallyNormalizeOntology();
    }

    private void checkAndPartiallyNormalizeOntology() throws OWLOntologyCreationException, OWLOntologyStorageException {
        if (ontologyProceeder.isOntologyNeedToBeNormalized(ontology)) {
            ontology = ontologyProceeder.normalizeOntology(ontology, null);
        }
    }

    /**
     * Inits the elk.
     *
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     */
    private void initELK() throws OWLOntologyCreationException {
    	if (isOwl2elProfile) {
    		org.apache.log4j.Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
    		Date dateStart = new Date();
    		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
    		reasoner = reasonerFactory.createReasoner(ontology);
    		/** Classify the ontology. */
    		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

    		Utils.getDiffTime(dateStart, "Running ELK Reasoner: ");

    		getInferredData();
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
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     * @throws OWLOntologyStorageException  the oWL ontology storage exception
     * @throws IOException                  Signals that an I/O exception has occurred.
     */
    public boolean PrepareForTranslating() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
    	checkOwlProfile();
    	initELK();
        cm.clearOntology();
        return true;
    }
    public void proceed() throws ParserException {
        ontologyProceeder.proceed();
    }

    /**
     * Prepare query.
     *
     * @param q the q
     * @return the string
     */
    public String prepareQuery(String q) {
        return query.prepareQuery(q, isAnyDisjointWithStatement());
    }

    /**
     * Sets the result file name.
     *
     * @param path the new result file name
     */
    public void setResultFileName(String path) {
        resultFileName = path;
    }
}