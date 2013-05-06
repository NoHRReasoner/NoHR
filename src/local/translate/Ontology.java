package local.translate;

import java.io.*;
//import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import javax.print.attribute.DateTimeSyntax;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;
//import org.semanticweb.owlapi.model.AxiomType;
//import org.semanticweb.owlapi.model.OWLAxiom;
//import org.semanticweb.owlapi.model.OWLClass;
//import org.semanticweb.owlapi.model.OWLClassExpression;
//import org.semanticweb.owlapi.model.OWLIndividual;
//import org.semanticweb.owlapi.model.OWLObjectProperty;
//import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
//import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//import org.semanticweb.owlapi.model.OWLOntologyManager;
//import org.semanticweb.owlapi.model.OWLOntologyStorageException;
//import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
//import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
//import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
//import org.semanticweb.owlapi.util.ShortFormProvider;
//import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

import javax.swing.*;


/**
 * The Class Ontology.
 */
public class Ontology {

    /** The _fileaddress. */
    private String _fileaddress;

    private File _ontologyFile;

    /** The _ontology manager. */
    private OWLOntologyManager _ontologyManager;

    /** The _ontology. */
    private static OWLOntology _ontology;

    private OWLDataFactory _ontologyDataFactory;

    private OWLAnnotationProperty _ontologyLabel;


//	OWLReasonerFactory reasonerFactory;
    /** The _reasoner. */
//	private static OWLReasoner _reasoner;
//	ShortFormProvider shortFormProvider;
//	static BidirectionalShortFormProvider mapper;
    /** The _owl classes. */
    private static Set<OWLClass> _owlClasses;

    /** The _object properties. */
    private static Set<OWLObjectProperty> _objectProperties;

//    private OWLDataFactory _owlDataFactory;


    /** The _outfile. */
//	private FileWriter _outfile;
//    private FileWriter _outTopfile;

    /** The _outer. */
//	private static PrintWriter _outer;
//    private static PrintWriter _outerTop;

//    private List<String> tabledOntologies = new ArrayList<String>(500000);
//    private HashSet<String> tabledOntologies = new HashSet<String>();
    //    private List<String> translatedOntologies = new ArrayList<String>(10000000);
    private HashSet<String> translatedOntologies = new HashSet<String>();
    //    private List<String> appendedRules = new ArrayList<String>(1000000);
    private HashSet<String> appendedRules = new HashSet<String>();
    /**Set of the predicates to table them in the end 
     * */
    private HashSet<String> tablePredicates = new HashSet<String>();
    
    private String _delimeter="#";
    private String _altDelimeter=":";
    private String _negation="tnot";
    private String _searchNegation="not";
    private String _eq=":-";

    private List<String> _existsOntology;
    private List<String> _existsRules;

    private boolean debug=false;
    private boolean isAnyDisjointStatement = false;
    private String _currentRule;

    public List<String> _prohibitedNames = Arrays.asList("table","attribute","true","false","halt");

    private List<String> prologCommands = Arrays.asList(":- abolish_all_tables.",":- set_prolog_flag(unknown,fail).");

    private String tempDirProp = "java.io.tmpdir";
    private String _tempDir="";
//    private String _proresult = "ontologies_to_rules_proresult.p";
    private String _result = "result.p";//"ontologies_to_rules_result.p";
    private String _mergedOntologies = "ontologies_to_rules_merged.owl";

    private boolean _isLog = true;
    private JTextArea _textArea = null;
//    private boolean isTranslated = false;

    public boolean isOntologyChanged = true;
    /**
     * Instantiates a new ontology.
     *
     * @param filePath the file path
     * @throws OWLOntologyCreationException the oWL ontology creation exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws OWLOntologyStorageException
     */
    public Ontology(String filePath) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        /** Initializing a OntologyManager */
        Date date1=new Date();
        _ontologyManager=OWLManager.createOWLOntologyManager();
        _fileaddress=filePath;
        _ontologyFile=new File(_fileaddress);
        if(_ontologyFile.exists())
            _ontologyFile.createNewFile();
        _ontology=_ontologyManager.loadOntologyFromOntologyDocument(_ontologyFile);
//        _owlDataFactory = OWLManager.getOWLDataFactory();
        getDiffTime(date1, "Initializing is done, it took:");
        date1=new Date();
        initELK();
        getDiffTime(date1, "ELK reasoner finished it's work, it took:");
        date1=new Date();
        mergeOntologies();
        getDiffTime(date1, "Merger finished it's work, it took:");
        getOWL();
        _existsRules = new ArrayList<String>(5000000);
        _existsOntology = new ArrayList<String>(5000000);
    }
//    private void startOuters(boolean isAppend) throws IOException{
		/*if(_outfile!=null)
			_outfile.close();
		_outfile = new FileWriter(_tempDir+_proresult, isAppend);
		if(_outer!=null)
			_outer.close();
		_outer = new PrintWriter(_outfile);
		if(_outTopfile!=null)
			_outTopfile.close();
		_outTopfile=new FileWriter(_tempDir+_result, isAppend);
		printLog(_tempDir+_result);
		if(_outerTop!=null)
			_outerTop.close();
		_outerTop=new PrintWriter(_outTopfile);*/
//    }
    public Ontology(OWLModelManager owlModelManager, JTextArea textArea, boolean isLog) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        _ontologyManager = owlModelManager.getOWLOntologyManager();
        _ontology = owlModelManager.getActiveOntology();
        _tempDir = System.getProperty(tempDirProp);
        //_textArea.append("OS current temporary directory is " + tempDir);
        _isLog = isLog;
        _textArea = textArea;
        _existsRules = new ArrayList<String>(5000000);
        _existsOntology = new ArrayList<String>(5000000);
    }      
    public boolean PrepareForTranslating() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        //startOuters(false);
//        tabledOntologies = new HashSet<String>();// new ArrayList<String>(500000);
        translatedOntologies = new HashSet<String>();//new ArrayList<String>(10000000);
        tablePredicates = new HashSet<String>();
        initELK();
        mergeOntologies();
        getOWL();
//        isTranslated=true;
        return true;
    }
    /**
     * Main function
     * @throws ParserException
     */
    public void proceed() throws ParserException{
        Date date1=new Date();
//        fillExistsOntologiesAndRules();
//        getDiffTime(date1, "PreProcessing and finish All I rules finished it's work, it took:");
//        date1=new Date();
//        autoTable();
//        getDiffTime(date1, "AutoTabling finished it's work, it took:");
        date1=new Date();
        loopThrowAllClasses();
        getDiffTime(date1, "All I rules, Rules C1, C2, C3 and A1 are finished it's work, it took:");
        date1=new Date();
        loopThrowAllProperties();
        getDiffTime(date1, "Rules R1, R2 and A2 are finished it's work, it took:");
    }

    /**
     * Read a rules from file and proceed them
     * @param filePath path of rule's file
     * @throws IOException
     */
    public void appendRules(String filePath) throws IOException{
        writeLineToAppendedRules("%Inserting rules");
        File rules=new File(filePath);
        if(rules!=null){
            FileInputStream fstream = new FileInputStream(rules);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if(strLine.length()>0)
                    proceedRule(strLine);
            }
            in.close();
        }
    }
    public void appendRules(ArrayList<String> _rules) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, ParserException {
        //startOuters(true);
        appendedRules = new HashSet<String>();//new ArrayList<String>(1000000);
		/*if(!isTranslated){
			PrepareForTranslating();
			proceed();
		}*/
        for(String rule : _rules){
            proceedRule(rule);
        }

    }
    protected void printLog(String message){
        if(_isLog){
            if(_textArea !=null){
                _textArea.append(message+"\n");
            }else{
                System.out.println(message);
            }
        }
    }
    protected void proceedRule(String rule){
        if(rule.startsWith(_eq))
            return;
        rule=rule.substring(0, rule.length()-1);
        rule = rule.replace(".", "");
        rule = ruleToLowerCase(rule);//processRule(rule);
        String[] arrayRule = rule.split(_eq);
        String leftSideRule = replaceSymbolsInRule(arrayRule[0].trim());
        String rightSideRule = null;
        if(arrayRule.length>1 && arrayRule[1]!=null)
            rightSideRule = replaceSymbolsInWholeRule(arrayRule[1].trim());
        writeArule(leftSideRule, rightSideRule);
        tablePredicateFromRule(leftSideRule);
        if(isAnyDisjointStatement)
            writeBrule(leftSideRule, rightSideRule);
    }
    protected void writeArule(String leftSide, String rightSide){
        if(rightSide==null){
            writeLineToAppendedRules(leftSide + ".");
        }else{
            String result = leftSide+getEqForRule();
            for (String rule : rightSide.split("\\)\\s*,")) {
                rule = rule.trim();
                if(rule.contains("(") && !rule.contains(")"))
                    rule+=")";
                if(rule.startsWith(_searchNegation)){
                    rule = rule.replaceFirst(_searchNegation, _negation);
                }
                if(rule.startsWith(_negation)){
                    result+=getSubRule(rule)+", ";
                }else{
                    result+=rule+", ";
                }
            }
            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }
    protected void writeBrule(String leftSide, String rightSide){
        if(rightSide==null){
            writeLineToAppendedRules(getSubRule(leftSide) + getEqForRule() + getNegRule(leftSide) + ".");
        }else{
            String result = getSubRule(leftSide)+getEqForRule();
            for (String rule : rightSide.split("\\)\\s*,")) {
                rule = rule.trim();
                if(rule.contains("(") && !rule.contains(")"))
                    rule+=")";
                if(rule.startsWith(_searchNegation)){
                    rule = rule.replaceFirst(_searchNegation, _negation);
                }
                if(rule.startsWith(_negation)){
                    result+=rule+", ";
                }else{
                    result+=getSubRule(rule)+", ";
                }
            }
            //String predicate = leftSide.split("\\(")[0];
            if(isAnyDisjointStatement)//if(isExistRule(predicate) || isExistOntology(predicate))
                result += getNegRule(leftSide)+", ";
            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }
    protected void tablePredicateFromRule(String rule){
        rule = rule.trim();
        if(rule.startsWith(_searchNegation))
            rule = rule.replaceFirst(_searchNegation, _negation);
        if(rule.startsWith(_negation))
            rule = rule.replaceFirst(_negation+" ", "");
        String[] _ = rule.split("\\(");
        String predicate = _[0];
        //if(isAnyDisjointStatement){//if(!(isExistOntology(predicate) || isExistRule(predicate))){
        int len = 0;
        if(_.length>1 && _[1]!=null){
            _ = _[1].split("\\)");
            _ = _[0].split(",");
            len = _.length;
        }
//        writeLineToTopFile(":- table "+predicate+"/"+len+".");
        addPredicateToTableIt(predicate+"/"+len);
        if(isAnyDisjointStatement)
//            writeLineToTopFile(":- table "+predicate+"_d/"+len+".");
        	addPredicateToTableIt(predicate+"_d/"+len);
//    	}
    }
    protected String getEqForRule(){
        return " "+_eq+" ";
    }
    protected String getSubRule(String rule){
        rule = rule.trim();
        if(rule.contains("("))
            rule= rule.replaceFirst("\\(","_d(");
        else
            rule= rule+"_d";
//        printLog(rule);
        return rule;
    }
    protected String getNegRule(String rule){
        return _negation+" n_"+rule.trim();
    }
    protected String getNameFromRule(String rule){
        String[] _rule=rule.split("\\(");
        return _rule[0];
    }
    public File Finish() throws IOException {
        Date date1 = new Date();
        FileWriter writer = new FileWriter(_tempDir+_result/*, isTranslated*/);
        for(String str: prologCommands){
            writer.write(str+"\n");
        }
//        if(!isTranslated){
//        for(String str: tabledOntologies) {
//            writer.write(str+"\n");
//        }
        for(String str: tablePredicates){
        	writer.write(":- table "+str+".\n");
        }
        for(String str: translatedOntologies) {
            writer.write(str+"\n");
        }
//	        isTranslated = true;
//        }
        for(String str: appendedRules) {
            writer.write(str+"\n");
        }
        writer.close();

        getDiffTime(date1,"Finishing, it took:");
        return new File(_tempDir+_result);
    }
    protected void getOWL() throws OWLOntologyCreationException{
        _owlClasses=_ontology.getClassesInSignature();
        _objectProperties=_ontology.getObjectPropertiesInSignature();
        _ontologyDataFactory = _ontologyManager.getOWLDataFactory();
        _ontologyLabel = _ontologyDataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
    }
    protected void getDiffTime(Date startDate, String message){
        Date stoped=new Date();
        long diff=stoped.getTime() - startDate.getTime();
		printLog(message+" "+diff+" milisec");
    }
    /**
     * During preprocessing append to array ontologies
     * @param ontology
     */
    protected void insertIntoExistOntology(String ontology){
        if(!_existsOntology.contains(ontology))
            _existsOntology.add(ontology);
    }
    /**
     * During preprocessing append to array rules
     * @param rule
     */
    protected void insertIntoExistRules(String rule){
        if(!_existsRules.contains(rule))
            _existsRules.add(rule);
    }
    /**
     * Checking if giving ontology exist
     * @param className
     * @return
     */
    protected boolean isExistOntology(String className){
        return _existsOntology.contains(className);
    }
    /**
     * Checking if giving rule are exist
     * @param rule
     * @return
     */
    protected boolean isExistRule(String rule){
        return _existsRules.contains(rule);
    }
    protected void initELK() throws OWLOntologyCreationException{
        Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
//		Logger logger = Logger.getRootLogger();
        Date date1 = new Date();
        OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(_ontology);
        printLog("Reasoner created");
        /** Classify the ontology. */
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        printLog("Precomputed inference");
        /**To generate an inferred ontology we use implementations of
         inferred axiom generators */
        ArrayList<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());

        /** Put the inferred axioms into a fresh empty ontology. */
        OWLOntology infOnt=_ontologyManager.createOntology();
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        iog.fillOntology(_ontologyManager,infOnt);
//		printLog("Reasoner finished work");
        getDiffTime(date1,"Reasoner finished work:");
    }
    protected void mergeOntologies() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        printLog("start merging");
        OWLOntologyMerger ontologyMerger= new OWLOntologyMerger(_ontologyManager);
        File mergedOntologyFile = null;

        mergedOntologyFile = new File(_tempDir+_mergedOntologies);
        if(!mergedOntologyFile.exists())
            mergedOntologyFile.createNewFile();
        //if(!mergedOntologyFile.createNewFile() || !mergedOntologyFile.canWrite())
        //	printLog("couldn't create owl file for merged ontologies");
        OWLOntology mergedOntology = ontologyMerger.createMergedOntology(_ontologyManager, IRI.create(mergedOntologyFile));
        //_ontologyManager.saveOntology(mergedOntology,IRI.create(mergedOntologyFile));
        _ontology=mergedOntology;
    }
    private void writeLineToFile(String string){
        //if(!string.startsWith("thing"))
        string += debug ? _currentRule : "";
//		_outer.println(string);
        translatedOntologies.add(string);
        //return;
    }
    /*private void writeLineToTopFile(String string){
        string += debug ? _currentRule : "";
//        _outerTop.println(string);
        tabledOntologies.add(string);
        //return;
    }*/
    private void writeLineToAppendedRules(String string){
        string += debug ? _currentRule : "";
//        _outerTop.println(string);
        appendedRules.add(string);
        //return;
    }


    private String getRuleFromString(String rule, int numInList){
        String result="";
        if(rule.contains(_delimeter))
            result=(rule.split(_delimeter)[numInList]).split(">")[0];
        else if (rule.contains(_altDelimeter)) {
            result=(rule.split(_altDelimeter)[numInList]).split(">")[0];
        }
        else
            result="";
        
        return replaceSymbolsInRule(result);
        
        //return result;
    }
    private String getRuleFromString(OWLObjectPropertyExpression property, int numInList) {
        return getRuleFromString(property.asOWLObjectProperty(), numInList);
    }
    private String getRuleFromString(OWLObjectProperty objectProperty, int numInList) {
        return getRuleFromString(objectProperty.getAnnotations(_ontology, _ontologyLabel), objectProperty.toString(), numInList);
    }
    private String getRuleFromString(OWLIndividual member, int numInList) {
//        return getRuleFromString(member.get  getAnnotations(_ontologyLabel), entity.toString(), numInList);
        if(member instanceof OWLNamedIndividual)                      {
            for(OWLEntity entity: member.getSignature()){
                return getRuleFromString(entity, 1);
            }
        }
        return getRuleFromString(((OWLClass)member), numInList);
    }

    private String getRuleFromString(OWLEntity entity, int numInList) {
        return getRuleFromString(entity.getAnnotations(_ontology, _ontologyLabel), entity.toString(), numInList);
    }


    private String getRuleFromString(OWLAxiom entity, int numInList) {
        if(entity instanceof OWLSubPropertyChainAxiomImpl){
            List<OWLObjectPropertyExpression> properties = ((OWLSubPropertyChainAxiomImpl) entity).getPropertyChain();
            if(properties!=null){
                if(properties.size()>=numInList)
                    return getRuleFromString(properties.get(numInList-1),numInList);
                else
                    return getRuleFromString(((OWLSubPropertyChainAxiomImpl) entity).getSuperProperty(),numInList);
            }
        }else if(entity instanceof OWLObjectPropertyAssertionAxiomImpl){
            switch (numInList){
                case 1:
                    return getRuleFromString(((OWLObjectPropertyAssertionAxiomImpl) entity).getProperty(), numInList);
                case 2:
                    return getRuleFromString(((OWLObjectPropertyAssertionAxiomImpl) entity).getSubject(),numInList);
                case 3:
                    return getRuleFromString(((OWLObjectPropertyAssertionAxiomImpl) entity).getObject(),numInList);
            }
        }
        return getRuleFromString(entity.toString(),numInList);
        //return getRuleFromString(entity.getAnnotations(_ontologyLabel), entity.toString(), numInList);
    }
    private String getRuleFromString(OWLClass owlClass, int numInList) {
        return getRuleFromString(owlClass.getAnnotations(_ontology, _ontologyLabel), owlClass.toString(), numInList);
    }
    private String getRuleFromString(OWLClassExpression owlClass, int numInList) {
        return getRuleFromString(owlClass.asOWLClass(), numInList);
    }
    private String getRuleFromString(Set<OWLAnnotation> annotations, String label, int numInList) {
        String message="";
        if(annotations!=null && annotations.size()>0)   {
            for (OWLAnnotation annotation : annotations) {
                message += annotation.getValue();
                        /*if (annotation.getValue() instanceof OWLLiteral) {
                            OWLLiteral val = (OWLLiteral) annotation.getValue();
                            message += val.getLiteral();
                        }*/
            }
        }
        if(message.length()>0)
            return replaceSymbolsInRule(message);//  message.replaceAll("'","").replaceAll("\"","'");
        return getRuleFromString(label, numInList);
    }
    /**
     * (a1). for each C(a) ∈ A: C(a) ← and Cd(a) ← notNC(a).
     * @param member
     * @param entity
     */
    private void writeRuleA1(OWLIndividual member, OWLClass entity){
        _currentRule = "%A1";
        String a = getRuleFromString(member, 1);
        String C = getRuleFromString(entity, 1);
        writeLineToFile(C + "(" + a + ").");
        addPredicateToTableIt(C+"/1");
        if(isAnyDisjointStatement){//if(isExistOntology(C)){
            String rule = getEqForRule() +_negation + " n_" + C + "(" + a + ")";
            addPredicateToTableIt(C+"_d/1");
            writeLineToFile(C + "_d(" + a + ")" + rule + ".");
        }
    }

    /**
     * (a2). for each R(a, b) ∈ A: R(a, b) ← and Rd(a, b) ← not NR(a, b).
     * @param entity
     */
    private void writeRuleA2(OWLAxiom entity){
        _currentRule = "%A2";
        String R= getRuleFromString(entity, 1),
                a= getRuleFromString(entity, 2),
                b= getRuleFromString(entity, 3);
        writeLineToFile(R + "(" + a + "," + b + ").");
        addPredicateToTableIt(R+"/2");
        if(isAnyDisjointStatement){//if(isExistRule(R)){
            String rule =  getEqForRule()+_negation + " n_" + R + "(" + a + "," + b + ")";
            addPredicateToTableIt(R+"_d/2");
            writeLineToFile(R + "_d(" + a + "," + b + ")" + rule + ".");
        }
    }

    /**
     * (i2). for each C1 ⊓ C2 ⊑⊥∈ T : NC2(x) ← C1(x) and NC1(x) ← C2(x).
     * @param expression
     */
    private void writeRuleI2(OWLClassExpression expression){
        _currentRule = "%I2";
        String C2= getRuleFromString(expression, 1);
        String C1= getRuleFromString(expression, 2);
        //if(!C1.equals(C2)){
//        insertIntoExistOntology(C2);
//        insertIntoExistOntology(C1);
        writeLineToFile("n_" + C2 + "(X) :- " + C1 + "(X).");
        addPredicateToTableIt("n_"+C2+"/1");
        writeLineToFile("n_" + C1 + "(X) :- " + C2 + "(X).");
        addPredicateToTableIt("n_"+C1+"/1");
        //}
    }
    /**
     * (c2). foreach C1 ⊓ C2 ⊑ D ∈ T : D(x) ← C1(x) , C2(x) and Dd(x) ← C1d(x), C2d(x), not ND(x).
     * @param expression
     * @param superclass
     */
	/*private void writeRuleC2(String expression, String superclass){
        _currentRule = "%C2";
		String C2= getRuleFromString(expression, 2);
		String C1= getRuleFromString(expression, 1);
		String D= getRuleFromString(superclass, 1);
		writeLineToFile(D + "(X)" + getEqForRule() + C1 + "(X), " + C2 + "(X).");
        String rule = /*isExistOntology(D)*//*isAnyDisjointStatement ? ", " + _negation + " n_" + D + "(X)" : "";
		writeLineToFile(D + "_d(X)" + getEqForRule() + C1 + "_d(X), " + C2 + "_d(X)" + rule + ".");
	}*/
    /**
     * (i3). for each ∃R.C ⊑⊥∈ T : NC(y) ← R(x,y) and NR(x,y) ← C(y) .
     * @param expression
     */
    private void writeRuleI3(OWLClassExpression expression){
        _currentRule = "%I3";
        String C= getRuleFromString(expression, 2);
        String R= getRuleFromString(expression, 1);
        writeLineToFile("n_" + C + "(Y) :- " + R + "(X,Y).");
        addPredicateToTableIt("n_"+C+"/1");
        writeLineToFile("n_" + R + "(X,Y) :- " + C + "(Y).");
        addPredicateToTableIt("n_"+R+"/2");
        //insertIntoExistRules(R);
        //insertIntoExistOntology(C);
    }
    /**
     * (c3). for each ∃R.C ⊑ D ∈ T : D(x) ← R(x,y),C(y) and Dd(x) ← Rd(x, y), Cd(y), not ND(x).
     * @param expression                                                                                ≤
     * @param superclass
     */
	/*private void writeRuleC3(String expression, String superclass){
        _currentRule = "%C3";
		String C= getRuleFromString(expression, 2);
		String R= getRuleFromString(expression, 1);
		String D= getRuleFromString(superclass, 1);
		writeLineToFile(D + "(X)" + getEqForRule() + R + "(X,Y), " + C + "(Y).");
        String rule = /*isExistOntology(D)*//*isAnyDisjointStatement ? ", " + _negation + " n_" + D + "(X)" : "";
		writeLineToFile(D + "_d(X)" + getEqForRule() + R + "_d(X,Y), " + C + "_d(Y)" + rule + ".");
	}*/
    /**
     * (i1). for each C ⊑⊥∈ T : NC(x) ←.
     * @param expression
     */
    private void writeRuleI1(OWLClassExpression expression){
        _currentRule = "%I1";
        String C= getRuleFromString(expression, 1);
        writeLineToFile("n_" + C + "(X).");
        addPredicateToTableIt("n_"+C+"/1");
//        insertIntoExistOntology(C);
    }
    /**
     * (c1). foreach GCI C ⊑ D ∈ T: D(x)←C(x) and Dd(x) ← Cd(x), not ND(x).
     * @param expression
     * @param superclass
     */
    private void writeRuleC1(OWLClassExpression expression, OWLClass superclass, boolean lastIndex){
        _currentRule = "%C1";
        String D= getRuleFromString(superclass, 1);
        String C= getRuleFromString(expression, lastIndex ? -1 : 1);
        if(!C.equals(D)){
            writeLineToFile(D + "(X)" + getEqForRule() + C + "(X).");
            addPredicateToTableIt(D+"/1");
        }
        String rule = /*isExistOntology(D)*/isAnyDisjointStatement ? ", " + _negation + " n_" + D + "(X)" : "";
        if(isAnyDisjointStatement && !(C.equals(D) && rule.length()==0) /*isExistOntology(D)*/){
            writeLineToFile(D + "_d(X)" + getEqForRule() + C + "_d(X)" + rule + ".");
            addPredicateToTableIt(D+"_d/1");
        }
    }
    /**
     * (r1). foreach RI R⊑S ∈ T: S(x,y)←R(x,y) and Sd(x, y) ← Rd(x, y), not NS(x, y).
     * @param expression
     * @param superclass
     */
    private void writeRuleR1(OWLObjectPropertyExpression expression, OWLObjectProperty superclass){
        _currentRule = "%R1";
        String S= getRuleFromString(superclass, 1);
        String R= getRuleFromString(expression, 1);
        if(!R.equals(S)){
            writeLineToFile(S + "(X,Y)" + getEqForRule() + R + "(X,Y).");
            addPredicateToTableIt(S+"/2");
        }
        String rule = isAnyDisjointStatement/*isExistOntology(S)*/ ? ", " + _negation + " n_" + S + "(X,Y)":"";
        if(isAnyDisjointStatement && !(R.equals(S) && rule.length()==0)/*isExistRule(R)*/){
            writeLineToFile(S + "_d(X,Y)" + getEqForRule() + R + "_d(X,Y)" + rule + ".");
            addPredicateToTableIt(S+"_d/2");
        }
    }
    /**
     * (r2). foreach R◦S ⊑ T ∈ T: T(x,z)←R(x,y),S(y,z) and Td(x,z) ← Rd(x,y),Sd(y,z),notNT(x,z).
     * @param axiom
     */
    private void writeRuleR2(OWLAxiom axiom){
        _currentRule = "%R2";
        String S= getRuleFromString(axiom, 2);
        String R= getRuleFromString(axiom, 1);
        String T= getRuleFromString(axiom, 3);
        writeLineToFile(T + "(X,Z)" + getEqForRule() + R + "(X,Y), " + S + "(Y,Z).");
        addPredicateToTableIt(T+"/2");
        if(isAnyDisjointStatement){//if(isExistRule(T)){
            String rule = isAnyDisjointStatement/*isExistOntology(T)*/ ? ", " + _negation + " n_" + T + "(X,Z)":"";
            writeLineToFile(T + "_d(X,Z)" + getEqForRule() + R + "_d(X,Y), " + S + "_d(Y,Z)" + rule + ".");
            addPredicateToTableIt(T+"_d/2");
        }
    }

    private void writeEquivalentRule(OWLClass owlClass, OWLClassExpression rightPartOfRule){
        _currentRule="%EquivalentRule";
        EquivalentClass rightSideOfRule = getRuleFromEquivalentClasses(rightPartOfRule, 1, 1);
        String ruleHead = getRuleFromString(owlClass,1); 
        String rule= ruleHead+"(X1) "+_eq+" "+rightSideOfRule.getFinalRule();
        writeLineToFile(rule);
        addPredicateToTableIt(ruleHead+"/1");
    }

    private void writeNegEquivalentRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        _currentRule="%NegEquivalentRule";
//        if(!(classExpression.isOWLThing() || classExpression.isOWLNothing())){
	        EquivalentClass rules= getRuleFromEquivalentClasses(classExpression, 1, 1);
	        if(!(owlClass.isOWLThing() || owlClass.isOWLNothing()))
	            rules.addRule(getRuleFromString(owlClass,1),1,1, EquivalentClass.OntologyType.ONTOLOGY);
	        for(String rule : rules.getNegRules()){
	            writeLineToFile(rule);
	        }
	        for (String rule : rules.getNegRulesHeadForTabling()) {
	        	addPredicateToTableIt(rule);
			}
//        }
        /*
        for (String className : rules.listClassNames){
            insertIntoExistOntology(className);
        }
        for(String ruleName : rules.listRuleNames){
            insertIntoExistRules(ruleName);
        }
        */
    }


    private void writeDoubledRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        _currentRule="%DoubledRule";
        EquivalentClass rules = getRuleFromEquivalentClasses(classExpression, 1, 1);
        String _owlClass=getRuleFromString(owlClass, 1);
        writeLineToFile(_owlClass+"(X1)"+ getEqForRule()+rules.getFinalRule());
        addPredicateToTableIt(_owlClass+"/1");
        if(isAnyDisjointStatement){//if(isExistOntology(_owlClass)){
            String rule=_owlClass+"_d(X1)"+getEqForRule()+rules.getDoubledRules()+", " + _negation + " n_" + _owlClass + "(X1).";
            writeLineToFile(rule);
            addPredicateToTableIt(_owlClass+"_d/1");
        }
    }
    private void addPredicateToTableIt(String title){
    	tablePredicates.add(title);
    }
    /*private void autoTable(){
        String name;

        for(OWLClass owlClass : _owlClasses){
            if(!(owlClass.isOWLThing() || owlClass.isOWLNothing())){
                name=getRuleFromString(owlClass,1);
//                printLog(owlClass.get +owlClass.getIRI().toString());
                //String message = ((OWLAnnotation)owlClass.getAnnotations(_ontology, label)[0]).getValue().toString();//"";

//                for (OWLAnnotation annotation : owlClass.getAnnotations(_ontology, label)) {
//                	message += annotation.getValue();
                    /*if (annotation.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) annotation.getValue();
                        message += val.getLiteral();
                    }*/
//                }
//                printLog(message);
               /* writeLineToTopFile(":- table " + name + "/1.");
                if(isAnyDisjointStatement)//if(isExistOntology(name))
                    writeLineToTopFile(":- table "+name+"_d/1.");
            }
        }
        for(OWLObjectProperty objectProperty : _objectProperties){
            name=getRuleFromString(objectProperty,1);
            writeLineToTopFile(":- table " + name + "/2.");
            if(isAnyDisjointStatement)//if(isExistRule(name))
                writeLineToTopFile(":- table "+name+"_d/2.");
        }
    }*/



    /**
     * Going throw all ontologies and preprocess them.
     */
    /*private void fillExistsOntologiesAndRules(){
//        String _owlExpression;
        boolean isTopClass;
        ClassExpressionType expressionType;
//        _ontology.getDisjointClassesAxioms(arg0)
        //Init loop throw all ontologies
        for(OWLClass owlClass : _owlClasses){
            //Going into loop throw all Disjoint classes
            for(OWLClassExpression owlClassExpression : owlClass.getDisjointClasses(_ontology)){
                isAnyDisjointStatement = true;
//                OWLClassExpression hasPartSomeNose = _owlDataFactory.getOWLObjectSomeValuesFrom(_owlDataFactory.getOWLObjectProperty(owlClassExpression.), owlClass);
                isTopClass=owlClass.isOWLThing() || owlClass.isOWLNothing();
                expressionType=owlClassExpression.getClassExpressionType();
//                _owlExpression=owlClassExpression.toString();

                if(expressionType==ClassExpressionType.OBJECT_SOME_VALUES_FROM && isTopClass){
                    writeRuleI3(owlClassExpression);
                }else if(expressionType==ClassExpressionType.OBJECT_INTERSECTION_OF && isTopClass){
                    writeRuleI2(owlClassExpression);
                }else if(expressionType==ClassExpressionType.OWL_CLASS && isTopClass){
                    writeRuleI1(owlClassExpression);
                }else if(!isTopClass){
                    writeNegEquivalentRules(owlClassExpression, owlClass);
                }
            }
        }
    }*/
    /**
     * Going into loop of all classes
     */
    private void loopThrowAllClasses(){
        boolean isTopClass;
        OWLClassExpression rightPartOfRule;
        ClassExpressionType expressionType;
        List<OWLClassExpression> equivalentClasses;
        OWLClassExpression equivalentClass;
        for(OWLClass owlClass : _owlClasses){
        	isTopClass=owlClass.isOWLThing() || owlClass.isOWLNothing();
        	for(OWLClassExpression owlClassExpression : owlClass.getDisjointClasses(_ontology)){
        		expressionType = owlClassExpression.getClassExpressionType();
                if(expressionType==ClassExpressionType.OWL_CLASS && (owlClassExpression.isOWLThing() || owlClassExpression.isOWLNothing()))
                	break;
				if(expressionType==ClassExpressionType.OBJECT_SOME_VALUES_FROM && isTopClass){
                    writeRuleI3(owlClassExpression);
                }else if(expressionType==ClassExpressionType.OBJECT_INTERSECTION_OF && isTopClass){
                    writeRuleI2(owlClassExpression);
                }else{
                	if(expressionType==ClassExpressionType.OWL_CLASS && isTopClass){
	                    writeRuleI1(owlClassExpression);
	                }else if (!isTopClass){
	                    writeNegEquivalentRules(owlClassExpression, owlClass);
	                }
                }
            }
//        	if(isTopClass)
//        		break;
        	
            equivalentClasses = new ArrayList<OWLClassExpression>();
            for(OWLIndividual individual : owlClass.getIndividuals(_ontology)){
                writeRuleA1(individual, owlClass);
            }
            if(!isTopClass)
	            for(OWLClassExpression owlClassExpression : owlClass.getSubClasses(_ontology)){
	                writeDoubledRules(owlClassExpression, owlClass);
	            }

            
            for(OWLEquivalentClassesAxiom equivalentClassesAxiom : _ontology.getEquivalentClassesAxioms(owlClass)){
                List<OWLClassExpression> list = equivalentClassesAxiom.getClassExpressionsAsList();
                for(int i=0; i<list.size(); i++){
                	equivalentClass = list.get(i);
                	if(!(equivalentClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS && (equivalentClass.isOWLThing() || equivalentClass.isOWLNothing())))
                		equivalentClasses.add(equivalentClass);
                }
            }
            if(equivalentClasses.size()>0){
                equivalentClasses = removeDuplicates(equivalentClasses);
                for(int i=0; i<equivalentClasses.size(); i++){
                    rightPartOfRule=equivalentClasses.get(i);
                    //rightPartOfRuleString=rightPartOfRule.toString();
                    if(!isTopClass){
	                    if(rightPartOfRule.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
	                        writeRuleC1(rightPartOfRule, owlClass, false);
	                    }else{
	                        writeEquivalentRule(owlClass, rightPartOfRule);
	                    }
                    }
                }
            }
        }
    }
    private void loopThrowAllProperties(){
        for(OWLObjectProperty objectProperty : _objectProperties){
            for(OWLAxiom axiom : objectProperty.getReferencingAxioms(_ontology)){
                if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_ASSERTION){
                    writeRuleA2(axiom);
                }
            }
            for(OWLObjectPropertyExpression objectPropertyExpression : objectProperty.getSubProperties(_ontology)){
                writeRuleR1(objectPropertyExpression, objectProperty);
            }
        }
        for(OWLAxiom axiom : _ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)){
            writeRuleR2(axiom);
        }
    }



    private List<OWLClassExpression> removeDuplicates(List<OWLClassExpression> list){
        HashSet<OWLClassExpression> h = new HashSet<OWLClassExpression>(list);
        list.clear();
        list.addAll(h);

        return list;
    }


    private EquivalentClass getRuleFromEquivalentClasses(OWLClassExpression owlClassExpression, int localIterator, int iterator){
        EquivalentClass equivalentClass = new EquivalentClass(iterator);
        //OWLLabelAnnotation a = (OWLLabelAnnotation)owlClassExpression;
        switch (owlClassExpression.getClassExpressionType()){
            case OWL_CLASS:{
                equivalentClass.addRule(
                        getRuleFromString(owlClassExpression,1),
                        localIterator,
                        iterator,
                        EquivalentClass.OntologyType.ONTOLOGY
                );
                break;
            }
            case OBJECT_INTERSECTION_OF:{
                List<OWLClassExpression> operands = ((OWLObjectIntersectionOf) owlClassExpression).getOperandsAsList();
                for (OWLClassExpression operand : operands) {
                    equivalentClass.updateClass(getRuleFromEquivalentClasses(operand, localIterator, equivalentClass.getVariableIterator()));
                }
                break;
            }
            case OBJECT_SOME_VALUES_FROM:{
                OWLClassExpression classExpression = ((OWLObjectSomeValuesFromImpl) owlClassExpression).getFiller();
                OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFromImpl) owlClassExpression).getProperty();

                equivalentClass.addRule(getRuleFromString(property,1), localIterator, equivalentClass.incrementIterator(), EquivalentClass.OntologyType.RULE);
                equivalentClass.updateClass(getRuleFromEquivalentClasses(classExpression, ++localIterator, equivalentClass.getVariableIterator()));
                break;
            }
		default:
			break;

        }
        return equivalentClass;
    }

    public String processRule(String rule) {
		String[] parts = rule.split("\\(");
		String[] subParts;
    	for (String string : parts) {
    		subParts = string.split(" ");
    		string = subParts[subParts.length];
			rule.replace(" "+string+"(", " "+string.toLowerCase()+"(");
		}
    	return rule;
	}
    
    /**
     * @param args
     * @throws OWLOntologyCreationException
     * @throws IOException
     * @throws ParserException
     * @throws OWLOntologyStorageException
     */
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, ParserException, OWLOntologyStorageException{

//        String currentDir=new java.io.File(".").getCanonicalPath();
//        JFileChooser file = new JFileChooser(currentDir);
//        file.showDialog(null, "Choose ontology");
        Date date1=new Date();
//        Ontology ontology = new Ontology(file.getSelectedFile().getAbsolutePath());
        Ontology ontology = new Ontology("/Users/vadimivanov/Documents/University/Ontologies/really_short_SnomedFunctSyn.owl");
        ontology.proceed();
        Date date2=new Date();
        long diff=date2.getTime() - date1.getTime();
        date1=new Date();
//        int val = file.showDialog(null, "Choose rules");
//        if(val==JFileChooser.APPROVE_OPTION){
//            ontology.appendRules(file.getSelectedFile().getAbsolutePath());
//        }
        ontology.appendRules("/Users/vadimivanov/Downloads/rules.p");


        ontology.Finish();
        date2=new Date();
        diff+=date2.getTime() - date1.getTime();
        System.out.println("I'm done. it took "+diff+" milisec");
    }

    
    public String ruleToLowerCase(String rule) {
//    	System.out.println(rule);
    	try {
//    		Matcher m = Pattern.compile("\\w*\\(").matcher(rule);
    		Matcher m = Pattern.compile("\\w*\\b\\(?(?![,|\\)])").matcher(rule);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(sb, m.group().toLowerCase());
            }
            m.appendTail(sb);
            rule = sb.toString();
            sb.setLength(0);
            return rule;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	return rule;
        
	}
    
    private String replaceSymbolsInRule(String rule) {
    	rule = rule.trim().replaceAll("'","").replaceAll("\"","'").replaceAll("-", "").toLowerCase();
    	if(_prohibitedNames.contains(rule))
            rule+="_";
        /*if(rule.contains("/")){
            String[] _ = rule.split("/");
            rule = _[_.length-1];
        }*/
		return rule;
	}
    public String replaceSymbolsInWholeRule(String rule) {
    	rule = rule.trim().replaceAll("'","").replaceAll("\"","'").replaceAll("-", "");
    	if(_prohibitedNames.contains(rule)){
    		rule+="_";
    	}else{
	    	for (String name : _prohibitedNames) {
				rule.replaceAll(name,name+"_");
			}
    	}
    	/*
    	if(rule.contains("/")){
            String[] _ = rule.split("/");
            rule = _[_.length-1];
        }*/
		return rule;
	}

}

































