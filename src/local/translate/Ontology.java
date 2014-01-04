package local.translate;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.*;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class local.translate.Ontology.
 */
public class Ontology {

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
    private final List<String> prologCommands = Arrays.asList(
	    ":- abolish_all_tables.", ":- set_prolog_flag(unknown,fail).");

    /** The temp dir prop. */
    private final String tempDirProp = "java.io.tmpdir";

    /** The temp dir. */
    private String tempDir = "";

    /** The result file name. */
    private String resultFileName = "result.p";
    // private ParsedRule parsedRule;
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

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(Ontology.class);

    /** The is ontology changed. */
    public boolean isOntologyChanged = true;

    /**
     * Instantiates a new ontology.
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
     */
    public Ontology(OWLModelManager owlModelManager) throws IOException,
	    OWLOntologyCreationException, OWLOntologyStorageException,
	    CloneNotSupportedException {
	ontologyManager = owlModelManager.getOWLOntologyManager();
	ontology = owlModelManager.getActiveOntology();
	tempDir = System.getProperty(tempDirProp);
	getInferredDataFromReasoner(owlModelManager.getReasoner());
	initCollections();
	log.setLevel(Config.logLevel);
    }

    /**
     * Instantiates a new ontology.
     * 
     * @param filePath
     *            the file path
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws OWLOntologyStorageException
     *             the oWL ontology storage exception
     */
    public Ontology(String filePath) throws OWLOntologyCreationException,
	    IOException, OWLOntologyStorageException {
	/** Initializing a OntologyManager */
	Date date1 = new Date();
	ontologyManager = OWLManager.createOWLOntologyManager();
	ontologyFile = new File(filePath);
	if (ontologyFile.exists()) {
	    ontologyFile.createNewFile();
	}
	ontology = ontologyManager
		.loadOntologyFromOntologyDocument(ontologyFile);
	getDiffTime(date1, "Initializing is done, it took:");
	date1 = new Date();
	initELK();
	initCollections();
	log.setLevel(Config.logLevel);
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
	ruleTranslator = new RuleTranslator(cm);
	for (String rule : _rules) {
	    ruleTranslator.proceedRule(rule);
	}

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
     * Going throw all ontologies and preprocess them.
     */
    private void fillExistsOntologiesAndRules() {
	boolean isTopClass;
	ClassExpressionType expressionType;
	cm.setIsAnyDisjointStatement(false);

	for (OWLOntology ont : ontologies) {
	    for (OWLClass owlClass : ont.getClassesInSignature()) {

		isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();

		// Going into loop throw all Disjoint classes
		for (OWLClassExpression owlClassExpression : owlClass
			.getDisjointClasses(ont)) {
		    cm.setIsAnyDisjointStatement(true);
		    expressionType = owlClassExpression
			    .getClassExpressionType();
		    if ((expressionType == ClassExpressionType.OWL_CLASS)
			    && (owlClassExpression.isOWLThing() || owlClassExpression
				    .isOWLNothing())) {
			break;
		    }
		    if ((expressionType == ClassExpressionType.OBJECT_SOME_VALUES_FROM)
			    && isTopClass) {
			ruleCreator.writeRuleI3(owlClassExpression);
		    } else if ((expressionType == ClassExpressionType.OBJECT_INTERSECTION_OF)
			    && isTopClass) {
			ruleCreator.writeRuleI2(owlClassExpression);
		    } else {
			if ((expressionType == ClassExpressionType.OWL_CLASS)
				&& isTopClass) {
			    ruleCreator.writeRuleI1(owlClassExpression);
			} else if (!isTopClass) {
			    ruleCreator.writeNegEquivalentRules(
				    owlClassExpression, owlClass);
			}
		    }
		}
	    }

	    for (OWLClassAxiom owlClassAxiom : ont.getGeneralClassAxioms()) {
		if (owlClassAxiom.getAxiomType() == AxiomType.DISJOINT_CLASSES) {
		    cm.setIsAnyDisjointStatement(true);
		    ruleCreator
			    .writeGeneralClassAxiomsWithComplexAssertions(owlClassAxiom);
		}
	    }
	}

    }

    /**
     * Finish.
     * 
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public File Finish() throws IOException {
	Date date1 = new Date();
	FileWriter writer = new FileWriter(tempDir + resultFileName/*
								    * ,
								    * isTranslated
								    */);
	HashSet<String> tabled = new HashSet<String>();
	log.info("tabled ontology count: "
		+ cm.getAllTabledPredicateOntology().size());
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

	getDiffTime(date1, "Writing XSB file: ");
	return new File(tempDir + resultFileName);
    }

    /**
     * Gets the diff time.
     * 
     * @param startDate
     *            the start date
     * @param message
     *            the message
     * @return the diff time
     */
    protected void getDiffTime(Date startDate, String message) {
	Date stoped = new Date();
	long diff = stoped.getTime() - startDate.getTime();
	OntologyLogger.log(message + " " + diff + " milisec");
    }

    /**
     * Gets the inferred data.
     * 
     * @return the inferred data
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     */
    private void getInferredData() throws OWLOntologyCreationException {
	Date date1 = new Date();
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
	getDiffTime(date1, "Generating inferred ontology: ");
	date1 = new Date();

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
	getDiffTime(date1, "Retrieving inferred information: ");

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
	if ((owlReasoner != null)
		&& owlReasoner.getReasonerName().equals("ELK Reasoner")) {
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
     * @param hash
     *            the hash
     * @return the label by hash
     */
    public String getLabelByHash(String hash) {
	return cm.getLabelByHash(hash);
    }

    /**
     * Inits the collections.
     */
    private void initCollections() {
	_ontologyLabel = ontologyManager.getOWLDataFactory()
		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	cm = new CollectionsManager();
	collectionsManager = cm;
	ontologyLabel = new OntologyLabel(ontology, _ontologyLabel, cm);
	query = new Query(cm);
	ruleCreator = new RuleCreator(cm, ontologyLabel);
    }

    /**
     * Inits the elk.
     * 
     * @throws OWLOntologyCreationException
     *             the oWL ontology creation exception
     */
    protected void initELK() throws OWLOntologyCreationException {
	Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
	Date date1 = new Date();
	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	reasoner = reasonerFactory.createReasoner(ontology);
	/** Classify the ontology. */
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

	getDiffTime(date1, "Running ELK Reasoner: ");
	date1 = new Date();

	getInferredData();
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
     * Going into loop of all classes.
     */
    private void loopThrowAllClasses() {
	boolean isTopClass;
	OWLClassExpression rightPartOfRule;
	List<OWLClassExpression> equivalentClasses;
	OWLClassExpression equivalentClass;

	for (OWLOntology ont : ontologies) {
	    for (OWLClass owlClass : ont.getClassesInSignature()) {
		isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();
		equivalentClasses = new ArrayList<OWLClassExpression>();

		if (!isTopClass) {
		    for (OWLIndividual individual : owlClass
			    .getIndividuals(ont)) {
			ruleCreator.writeRuleA1(individual, owlClass);
		    }

		    for (OWLClassExpression owlClassExpression : owlClass
			    .getSubClasses(ont)) {
			ruleCreator.writeDoubledRules(owlClassExpression,
				owlClass);
		    }
		}

		for (OWLEquivalentClassesAxiom equivalentClassesAxiom : ont
			.getEquivalentClassesAxioms(owlClass)) {
		    List<OWLClassExpression> list = equivalentClassesAxiom
			    .getClassExpressionsAsList();
		    for (int i = 0; i < list.size(); i++) {
			equivalentClass = list.get(i);
			if (!((equivalentClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) && (equivalentClass
				.isOWLThing() || equivalentClass.isOWLNothing()))) {
			    equivalentClasses.add(equivalentClass);
			}
		    }
		}
		if (equivalentClasses.size() > 0) {
		    equivalentClasses = removeDuplicates(equivalentClasses);
		    for (int i = 0; i < equivalentClasses.size(); i++) {
			rightPartOfRule = equivalentClasses.get(i);
			// rightPartOfRuleString=rightPartOfRule.toString();
			if (!isTopClass) {
			    if (rightPartOfRule.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
				if (!owlClass.equals(rightPartOfRule)) {
				    ruleCreator.writeRuleC1(rightPartOfRule,
					    owlClass, false);
				}
			    } else {
				ruleCreator.writeEquivalentRule(owlClass,
					rightPartOfRule);
			    }
			}
		    }
		}
	    }
	    for (OWLClassAxiom owlClassAxiom : ont.getGeneralClassAxioms()) {
		if (owlClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
		    ruleCreator
			    .writeGeneralClassAxiomsSubClasses(owlClassAxiom);
		}
		if (owlClassAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
		    ruleCreator
			    .writeGeneralClassAxiomsEquivClasses(owlClassAxiom);
		}
	    }
	}
    }

    /**
     * Loop throw all properties.
     */
    private void loopThrowAllProperties() {
	for (OWLOntology ont : ontologies) {
	    for (OWLObjectProperty objectProperty : ont
		    .getObjectPropertiesInSignature()) {

		for (OWLAxiom axiom : objectProperty.getReferencingAxioms(ont)) {
		    if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
			ruleCreator.writeRuleA2(axiom);
		    }
		}
		for (OWLObjectPropertyExpression objectPropertyExpression : objectProperty
			.getSubProperties(ont)) {
		    ruleCreator.writeRuleR1(objectPropertyExpression,
			    objectProperty);
		}
	    }
	    for (OWLAxiom axiom : ont
		    .getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
		ruleCreator.writeRuleR2(axiom);
	    }
	}
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
     */
    public boolean PrepareForTranslating() throws OWLOntologyCreationException,
	    OWLOntologyStorageException, IOException {
	initELK();
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

    /**
     * Prints the all labels.
     */
    public void printAllLabels() {
	cm.printAllLabels();
    }

    /**
     * Main function.
     * 
     * @throws ParserException
     *             the parser exception
     */
    public void proceed() throws ParserException {

	Date date1 = new Date();
	// setProgressLabelText("Rule translation");
	fillExistsOntologiesAndRules();
	getDiffTime(date1, "Preprocessing DisjointWith axioms: ");
	date1 = new Date();
	loopThrowAllClasses();
	getDiffTime(date1, "Processing classes: ");
	date1 = new Date();
	loopThrowAllProperties();
	getDiffTime(date1, "Processing properties: ");
    }

    /**
     * Process rule.
     * 
     * @param rule
     *            the rule
     * @return the string
     */
    public String processRule(String rule) {
	String[] parts = rule.split("\\(");
	String[] subParts;
	for (String string : parts) {
	    subParts = string.split(" ");
	    string = subParts[subParts.length];
	    rule.replace(" " + string + "(", " " + string.toLowerCase() + "(");
	}
	return rule;
    }

    /**
     * Removes the duplicates.
     * 
     * @param list
     *            the list
     * @return the list
     */
    private List<OWLClassExpression> removeDuplicates(
	    List<OWLClassExpression> list) {
	HashSet<OWLClassExpression> h = new HashSet<OWLClassExpression>(list);
	list.clear();
	list.addAll(h);

	return list;
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