package local.translate;


import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

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
    /** The _ontology ID. */
    private static String _ontologyID;


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
    private HashSet<String> tablePredicatesOntology = new HashSet<String>();
    private HashSet<String> tablePredicatesRules = new HashSet<String>();
    private HashSet<String> setPrediactesAppearedUnderNunderscore = new HashSet<String>();

    private HashSet<String> _existsClasses = new HashSet<String>();
    private HashSet<String> _existsProperties = new HashSet<String>();

    private boolean debug=false;
    private boolean isAnyDisjointStatement = false;
    private String _currentRule;

    public List<String> _prohibitedNames = Arrays.asList("table","attribute","true","false","halt", "sleep", "tab","display");

    private List<String> prologCommands = Arrays.asList(":- abolish_all_tables.",":- set_prolog_flag(unknown,fail).");

    private String tempDirProp = "java.io.tmpdir";
    private String _tempDir="";
    //    private String _proresult = "ontologies_to_rules_proresult.p";
    private String _result = "result.p";//"ontologies_to_rules_result.p";
    private String _mergedOntologies = "ontologies_to_rules_merged.owl";
    private ParsedRule parsedRule;
    
    private boolean _isLog = true;
    private JTextArea _textArea = null;
    private JLabel progressLabel;
//    private Task task;
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
        getDiffTime(date1, "ELK reasoner finished, it took:");
//        date1=new Date();
//        mergeOntologies();
//        getDiffTime(date1, "Merger finished it's work, it took:");
        getOWL();
        _existsProperties = new HashSet<String>();
        _existsClasses = new HashSet<String>();
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
    public Ontology(OWLModelManager owlModelManager, JTextArea textArea, JLabel label, boolean isLog) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        _ontologyManager = owlModelManager.getOWLOntologyManager();
        _ontology = owlModelManager.getActiveOntology();
        _tempDir = System.getProperty(tempDirProp);
        //_textArea.append("OS current temporary directory is " + tempDir);
        _isLog = isLog;
        _textArea = textArea;
        progressLabel = label;
        _existsProperties  = new HashSet<String>();
        _existsClasses = new HashSet<String>();
    }
    
    public boolean PrepareForTranslating() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        //startOuters(false);
//        tabledOntologies = new HashSet<String>();// new ArrayList<String>(500000);
        translatedOntologies = new HashSet<String>();//new ArrayList<String>(10000000);
        tablePredicatesOntology = new HashSet<String>();
        setPrediactesAppearedUnderNunderscore = new HashSet<String>();
        setProgressLabelText("ELK reasoner");
        initELK();
//        mergeOntologies();
        getOWL();
//        isTranslated=true;
        return true;
    }

    public void clear(){
        _owlClasses = new HashSet<OWLClass>();
         _objectProperties = new HashSet<OWLObjectProperty>();
        translatedOntologies = new HashSet<String>();
        appendedRules = new HashSet<String>();
        tablePredicatesOntology = new HashSet<String>();
        setPrediactesAppearedUnderNunderscore = new HashSet<String>();
        tablePredicatesRules = new HashSet<String>();
        _existsClasses = new HashSet<String>();
        _existsProperties = new HashSet<String>();
    }
    /**
     * Main function
     * @throws ParserException
     */
    public void proceed() throws ParserException{
        Date date1=new Date();
//        setProgressLabelText("Rule translation");
        fillExistsOntologiesAndRules();
        getDiffTime(date1, "PreProcessing and axioms containing DisjointWith finished: ");
        date1=new Date();
//        autoTable();
//        getDiffTime(date1, "AutoTabling finished it's work, it took:");
        date1=new Date();
        loopThrowAllClasses();
        getDiffTime(date1, "Processing classes finished: ");
        date1=new Date();
        loopThrowAllProperties();
        getDiffTime(date1, "Processing properties finished: ");
    }


    /**
     * Read a rules from file and proceed them
     * @param filePath path of rule's file
     * @throws IOException
     */
    public void appendRules(String filePath) throws Exception {
        appendedRules = new HashSet<String>();//new ArrayList<String>(1000000);
        tablePredicatesRules = new HashSet<String>();
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
    public void appendRules(ArrayList<String> _rules) throws Exception {
        //startOuters(true);
        appendedRules = new HashSet<String>();//new ArrayList<String>(1000000);
        tablePredicatesRules = new HashSet<String>();
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
    protected void proceedRule(String rule) throws Exception {
        if(rule.startsWith(Config.eq))
            return;
//        System.out.println(rule);
//        rule = rule.substring(0, rule.length()-1);
        rule = rule.replace(".", "");
        rule = ruleToLowerCase(rule);//processRule(rule);
//        System.out.println(rule);
        String[] arrayRule = rule.split(Config.eq);
        String leftSideRule = replaceSymbolsInWholeRule(arrayRule[0].trim());
        String rightSideRule = null;
        if(arrayRule.length>1 && arrayRule[1]!=null)
            rightSideRule = replaceSymbolsInWholeRule(arrayRule[1].trim());

        tablePredicateFromRule(leftSideRule, false);
        if(isAnyDisjointStatement){
            writeArule(leftSideRule, rightSideRule);
            writeBrule(leftSideRule, rightSideRule);
        }else{
            writePlainRule(leftSideRule, rightSideRule);
            /*if(rightSideRule!=null)
                writeLineToAppendedRules(leftSideRule + getEqForRule()+ rightSideRule.replace(_searchNegation+" ", _negation+" ")+".");
            else
                writeLineToAppendedRules(leftSideRule+".");
                */
        }

    }

    protected void writePlainRule(String leftSide, String rightSide){
        if(rightSide==null){
            writeLineToAppendedRules(leftSide + ".");
        }else{
            String result = leftSide+getEqForRule();
            String tableRule;
            for (String rule : rightSide.split("\\)\\s*,")) {
                rule = rule.trim();
                if(rule.contains("(") && !rule.endsWith(")"))
                    rule+=")";
                if(rule.startsWith(Config.searchNegation)){
                    rule = rule.replaceFirst(Config.searchNegation, Config.negation);
                    tableRule = rule.replaceFirst(Config.negation,"").trim();
                    tablePredicateFromRule(tableRule, true);
                    
                }
                result+=rule+", ";

            }
            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }
    protected void writeArule(String leftSide, String rightSide){
        if(rightSide==null){
            writeLineToAppendedRules(leftSide + ".");
        }else{
            String result = leftSide+getEqForRule();
            for (String rule : rightSide.split("\\)\\s*,")) {
                rule = rule.trim();
                if(rule.contains("(") && !rule.endsWith(")"))
                    rule+=")";
                if(rule.startsWith(Config.searchNegation)){
                    rule = rule.replaceFirst(Config.searchNegation, Config.negation);
                }
                if(rule.startsWith(Config.negation)){
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
        	parsedRule = new ParsedRule(leftSide);
            writeLineToAppendedRules(getSubRule(leftSide) + getEqForRule() + getNegRule(leftSide) + ".");
            writeRuleForPredicateUnderTnot(parsedRule.getTabledNegRule());
            addPredicateToTableItRule(parsedRule.getTabledNegRule());
        }else{
            String result = getSubRule(leftSide)+getEqForRule();
            for (String rule : rightSide.split("\\)\\s*,")) {
                rule = rule.trim();
                if(rule.contains("(") && !rule.endsWith(")"))
                    rule+=")";
                if(rule.startsWith(Config.searchNegation)){
                    rule = rule.replaceFirst(Config.searchNegation, Config.negation);
                }
                if(rule.startsWith(Config.negation)){
                    result+=rule+", ";
                }else{
                    result+=getSubRule(rule)+", ";
                }
            }
            //String predicate = leftSide.split("\\(")[0];
//            if(isAnyDisjointStatement)//if(isExistRule(predicate) || isExistOntology(predicate))
            if(isRuleHeadAppears(leftSide)){
                result += getNegRule(leftSide)+", ";
                parsedRule = new ParsedRule(leftSide);
                writeRuleForPredicateUnderTnot(parsedRule.getTabledNegRule());
                addPredicateToTableItRule(parsedRule.getTabledNegRule());
            }
            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }
    
    protected void tablePredicateFromRule(String rule, boolean isAddToPredicateSet){
        parsedRule = new ParsedRule(rule);
        if(parsedRule.getPredicate().equals("'"))
            System.out.println(rule);
//        writeLineToTopFile(":- table "+predicate+"/"+len+".");
        addPredicateToTableItRule(parsedRule.getTabledRule());
        if(isAnyDisjointStatement){
//            writeLineToTopFile(":- table "+predicate+"_d/"+len+".");
            addPredicateToTableItRule(parsedRule.getTabledDoubledRule());
	        if(isAddToPredicateSet)
	        	writeRuleForPredicateUnderTnot(parsedRule.getTabledDoubledRule());
    	}
    }
    protected String getEqForRule(){
        return " "+Config.eq+" ";
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
        return Config.negation+" n_"+rule.trim();
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
        for(String str: tablePredicatesOntology){
            writer.write(":- table "+str+".\n");
        }
        for(String str: tablePredicatesRules){
        	if(!tablePredicatesOntology.contains(str))
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

        getDiffTime(date1,"Writing XSB file: ");
        return new File(_tempDir+_result);
    }
    protected void getOWL() throws OWLOntologyCreationException{
        _owlClasses=_ontology.getClassesInSignature();
        _objectProperties=_ontology.getObjectPropertiesInSignature();
        _ontologyDataFactory = _ontologyManager.getOWLDataFactory();
        _ontologyLabel = _ontologyDataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        String _ = _ontology.getOntologyID().getOntologyIRI().toString();
        _ontologyID = _.contains("/") ? _.substring(0, _.lastIndexOf("/")) + "/" : "";
    }
    protected void getDiffTime(Date startDate, String message){
        Date stoped=new Date();
        long diff=stoped.getTime() - startDate.getTime();
        printLog(message+" "+diff+" milisec");
    }
    /**
     * During preprocessing append to array ontologies
     * @param classname
     */
    protected void insertIntoExistClasses(String classname){
        _existsClasses.add(classname);
    }
    /**
     * During preprocessing append to array rules
     * @param property
     */
    protected void insertIntoExistProperties(String property){
        _existsProperties.add(property);
    }

    protected boolean isRuleHeadAppears(String head){
        head = head.trim();
        if(head.contains("\\(")){
            String[] _ = head.split("\\(");
            String name = _[0];
            int len = 0;
            if(_.length>1 && _[1]!=null){
                _ = _[1].split("\\)");
                _ = _[0].split(",");
                len = _.length;
            }
            if(len==1){
                return _existsClasses.contains(name);
            }else if(len==2){
                return _existsProperties.contains(name);
            }
        }

        return false;
    }
    protected void initELK() throws OWLOntologyCreationException{
        Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
//		Logger logger = Logger.getRootLogger();
        Date date1 = new Date();
        OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
//        reasonerFactory.
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
//        OWLOntology infOnt=_ontologyManager.createOntology();
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        iog.fillOntology(_ontologyManager,_ontology);
//		printLog("Reasoner finished work");
        getDiffTime(date1,"Reasoner finished: ");
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
    public void setResultFileName(String path){
        _result = path;
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
        if(_ontologyID.length()>0)
            rule = rule.replace(_ontologyID,"");
        try{
            String result;
            if(rule.contains(Config.delimeter))
                result=(rule.split(Config.delimeter)[numInList]).split(">")[0];
            else if (rule.contains(Config.altDelimeter))
                result=(rule.split(Config.altDelimeter)[numInList]).split(">")[0];
            else if(rule.startsWith("<"))
                result = rule.replaceFirst("<","").replace(">","");
            else
                result="";

            return replaceSymbolsInRule(result);
        }catch (Exception e){
            printLog("------------------------------------------------------------------------");
            printLog(rule);
            printLog(_currentRule);
            printLog(Integer.toString(numInList));
            printLog("------------------------------------------------------------------------");
            printLog(e.toString());
        }
        return rule;
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
                    return getRuleFromString(properties.get(numInList-1),1);
                else
                    return getRuleFromString(((OWLSubPropertyChainAxiomImpl) entity).getSuperProperty(),1);
            }
        }else if(entity instanceof OWLObjectPropertyAssertionAxiomImpl){
            switch (numInList){
                case 1:
                    return getRuleFromString(((OWLObjectPropertyAssertionAxiomImpl) entity).getProperty(), 1);
                case 2:
                    return getRuleFromString(((OWLObjectPropertyAssertionAxiomImpl) entity).getSubject(),1);
                case 3:
                    return getRuleFromString(((OWLObjectPropertyAssertionAxiomImpl) entity).getObject(),1);
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
            return replaceSymbolsInRule("\""+message.replace("^^xsd:string","").replace(",","").replace(":-","").replace("'","").replace("\"","")+"\"");//  message.replaceAll("'","").replaceAll("\"","'");
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
            String rule = getEqForRule() + Config.negation + " n_" + C + "(" + a + ")";
            addPredicateToTableIt(C+"_d/1");
            addPredicateToTableIt("n_"+C+"/1");
            writeRuleForPredicateUnderTnot("n_"+C+"/1");
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
            String rule =  getEqForRule()+Config.negation + " n_" + R + "(" + a + "," + b + ")";
            addPredicateToTableIt(R+"_d/2");
            addPredicateToTableIt("n_"+R+"/2");
            writeRuleForPredicateUnderTnot("n_"+R+"/2");
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
        insertIntoExistClasses(C2);
        insertIntoExistClasses(C1);
        writeLineToFile("n_" + C2 + "(X) :- " + C1 + "(X).");
        addPredicateToTableIt("n_"+C2+"/1");
        addPredicateToSetPredicatesAppearedUnderNunderscore("n_"+C2+"/1");
        writeLineToFile("n_" + C1 + "(X) :- " + C2 + "(X).");
        addPredicateToTableIt("n_"+C1+"/1");
        addPredicateToSetPredicatesAppearedUnderNunderscore("n_"+C1+"/1");
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
        addPredicateToSetPredicatesAppearedUnderNunderscore("n_"+C+"/1");
        writeLineToFile("n_" + R + "(X,Y) :- " + C + "(Y).");
        addPredicateToTableIt("n_"+R+"/2");
        insertIntoExistProperties(R);
        insertIntoExistClasses(C);
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
        addPredicateToSetPredicatesAppearedUnderNunderscore("n_"+C+"/1");
        insertIntoExistClasses(C);
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
        String rule = /*isExistOntology(D)*/isAnyDisjointStatement ? ", " + Config.negation + " n_" + D + "(X)" : "";
        if(isAnyDisjointStatement && !(C.equals(D) && rule.length()==0) /*isExistOntology(D)*/){
            writeLineToFile(D + "_d(X)" + getEqForRule() + C + "_d(X)" + rule + ".");
            addPredicateToTableIt(D+"_d/1");
            addPredicateToTableIt("n_"+D+"/1");
            writeRuleForPredicateUnderTnot("n_"+D+"/1");
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
        String rule = isAnyDisjointStatement/*isExistOntology(S)*/ ? ", " + Config.negation + " n_" + S + "(X,Y)":"";
        if(isAnyDisjointStatement && !(R.equals(S) && rule.length()==0)/*isExistRule(R)*/){
            writeLineToFile(S + "_d(X,Y)" + getEqForRule() + R + "_d(X,Y)" + rule + ".");
            addPredicateToTableIt(S+"_d/2");
            addPredicateToTableIt("n_"+S+"/2");
            writeRuleForPredicateUnderTnot("n_"+S+"/2");
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
            String rule = isAnyDisjointStatement/*isExistOntology(T)*/ ? ", " + Config.negation + " n_" + T + "(X,Z)":"";
            writeLineToFile(T + "_d(X,Z)" + getEqForRule() + R + "_d(X,Y), " + S + "_d(Y,Z)" + rule + ".");
            addPredicateToTableIt(T+"_d/2");
            addPredicateToTableIt("n_"+T+"/2");
            writeRuleForPredicateUnderTnot("n_"+T+"/1");
        }
    }

    private void writeEquivalentRule(OWLClass owlClass, OWLClassExpression rightPartOfRule){
        _currentRule="%EquivalentRule";
        EquivalentClass rightSideOfRule = getRuleFromEquivalentClasses(rightPartOfRule, 1, 1);
        String ruleHead = getRuleFromString(owlClass,1);
        String rule= ruleHead+"(X1) "+Config.eq+" "+rightSideOfRule.getFinalRule();
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
            addPredicateToSetPredicatesAppearedUnderNunderscore(rule);
        }
//        }

        for (String className : rules.listClassNames){
            insertIntoExistClasses(className);
        }
        for(String ruleName : rules.listRuleNames){
            insertIntoExistProperties(ruleName);
        }

    }


    private void writeDoubledRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        _currentRule="%DoubledRule";
        EquivalentClass rules = getRuleFromEquivalentClasses(classExpression, 1, 1);
        String _owlClass=getRuleFromString(owlClass, 1);
        writeLineToFile(_owlClass+"(X1)"+ getEqForRule()+rules.getFinalRule());
        addPredicateToTableIt(_owlClass+"/1");
        if(isAnyDisjointStatement){//if(isExistOntology(_owlClass)){
            String rule=_owlClass+"_d(X1)"+getEqForRule()+rules.getDoubledRules()+", " + Config.negation + " n_" + _owlClass + "(X1).";
            writeLineToFile(rule);
            addPredicateToTableIt(_owlClass+"_d/1");
            addPredicateToTableIt("n_"+_owlClass+"/1");
            writeRuleForPredicateUnderTnot("n_"+_owlClass+"/1");
        }
    }
    private void addPredicateToTableIt(String title){
    	tablePredicatesOntology.add(title);
    }
    private void addPredicateToTableItRule(String title){
    	tablePredicatesRules.add(title);
    }
    
    private void addPredicateToSetPredicatesAppearedUnderNunderscore(String s){
    	setPrediactesAppearedUnderNunderscore.add(s);
    }
    private boolean isPredicateAppearedInHeadUnderNunderscore(String predicate){
    	return setPrediactesAppearedUnderNunderscore.contains(predicate);
    }
    private void writeRuleForPredicateUnderTnot(String predicate){
//    	System.out.println("-----writeRuleForPredicateUnderTnot-------");
//    	System.out.println(predicate);
    	if(!isPredicateAppearedInHeadUnderNunderscore(predicate)){
    		int index = predicate.lastIndexOf("/");
    		String rule = predicate.substring(0, index);// +"(";
//    		System.out.println("rule:"+rule);
    		int limit = Integer.parseInt(predicate.substring(index+1,predicate.length()));
//    		System.out.println("limitRule:"+predicate.substring(index+1,predicate.length()));
    		if(limit>=1){
    			rule += "(";
	    		for(int i=1; i<=limit; i++){
	    			rule += "_,";
	    		}
	    		rule = rule.substring(0, rule.length()-1)+")";
    		}
    		rule+=" :- fail.";
    		writeLineToFile(rule);
    	}
//    	System.out.println("------------------------------------");
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
    private void fillExistsOntologiesAndRules(){
        boolean isTopClass;
        ClassExpressionType expressionType;
        for(OWLClass owlClass : _owlClasses){
        	
            isTopClass = owlClass.isOWLThing() || owlClass.isOWLNothing();

            //Going into loop throw all Disjoint classes
            for(OWLClassExpression owlClassExpression : owlClass.getDisjointClasses(_ontology)){
                isAnyDisjointStatement = true;
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
        }
    }
    /**
     * Going into loop of all classes
     */
    private void loopThrowAllClasses(){
        boolean isTopClass;
        OWLClassExpression rightPartOfRule;
//        ClassExpressionType expressionType;
        List<OWLClassExpression> equivalentClasses;
        OWLClassExpression equivalentClass;
//        Set<OWLClassExpression> disjClasses;
//        for(OWLClass owlClass : _owlClasses){
//        	disjClasses = owlClass.getDisjointClasses(_ontology); 
//        	if(disjClasses!=null && disjClasses.size()>0){
//        		isAnyDisjointStatement = true;
//        		break;
//        	}
//        }

        for(OWLClass owlClass : _owlClasses){
            isTopClass=owlClass.isOWLThing() || owlClass.isOWLNothing();

//        	if(isTopClass)
//        		break;

            equivalentClasses = new ArrayList<OWLClassExpression>();
            if(!isTopClass){
                for(OWLIndividual individual : owlClass.getIndividuals(_ontology)){
                    writeRuleA1(individual, owlClass);
                }

                for(OWLClassExpression owlClassExpression : owlClass.getSubClasses(_ontology)){
                    writeDoubledRules(owlClassExpression, owlClass);
                }
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

    public boolean isAnyDisjointWithStatement() {
        return isAnyDisjointStatement;
    }
    /**
     * @param args
     * @throws OWLOntologyCreationException
     * @throws IOException
     * @throws ParserException
     * @throws OWLOntologyStorageException
     */
    public static void main(String[] args) throws Exception {

//      String currentDir=new java.io.File(".").getCanonicalPath();
//      JFileChooser file = new JFileChooser(currentDir);
//      file.showDialog(null, "Choose ontology");
//      Ontology ontology = new Ontology(file.getSelectedFile().getAbsolutePath());
      Date timeStart;
      Date timeEnd;
      Date timeProceedStart;
      Date timeProceedEnd;

      timeStart = new Date();
/*
      Ontology ontology = new Ontology("/Users/vadimivanov/Downloads/mkn@fct.unl.pt - cities example/city.owl");
      ontology.proceed();
      ontology.appendRules("/Users/vadimivanov/Downloads/mkn@fct.unl.pt - cities example/city.p");
      
      ontology.Finish();
      ontology.clear();
      System.exit(0);
      */
      if(args.length==0){
          System.out.println("Please specify arguments, at least ontology file path");
          System.exit(0);
      }

      String ontologyPath = args[0];
      File file = new File(ontologyPath);
      if(!file.exists()){
          System.out.println("Please specify correct path for ontology file");
          System.exit(0);
      }
      System.out.println("Initialization started");
      Ontology ontology = new Ontology(ontologyPath);


      timeProceedStart = new Date();
      ontology.proceed();
//      Date date2=new Date();
      //long diff=date2.getTime() - date1.getTime();
      //date1=new Date();

      if(args.length>1 && args[1]!=null){
          String rulePath = args[1];
          file = new File(rulePath);
          if(file.exists()){
              ontology.appendRules(rulePath);
          }
          file =null;
      }
      timeProceedEnd = new Date();
      System.out.println("====================================================================================");
      System.out.println("our procedure time is "+(timeProceedEnd.getTime()-timeProceedStart.getTime())+" milisec");
      System.out.println("====================================================================================");
//      int val = file.showDialog(null, "Choose rules");
//      if(val==JFileChooser.APPROVE_OPTION){
//          ontology.appendRules(file.getSelectedFile().getAbsolutePath());
//      }
//      ontology.appendRules("/Users/vadimivanov/Documents/University/tests/rules/2.p");


      ontology.Finish();
      ontology.clear();
      ontology = null;
      timeEnd = new Date();
      System.out.println("Total time is "+(timeEnd.getTime()-timeStart.getTime())+" milisec");
      System.out.println("====================================================================================");
      System.exit(0);
      
    }


    public String ruleToLowerCase(String rule) {
//    	System.out.println("ruleToLowerCase: "+rule);
        try {
            rule = rule.replace("'","").replace("\"","'");
            StringBuffer sb = new StringBuffer();
            String _;
            //(?!\s)'(?:''|[^'])*'|(?!\s)[^',]+
            //(?!\s)'(?:''|[^'])*'|(?!\s)[^',\s]+(\(|\.)(?![,|\)])
            //(?!\s)['|"](?:''|[^'])*['|"]|(?!\s)[^',\s]+(\(|\.)(?![,|\)])
//    		Matcher m = Pattern.compile("\\w*\\(").matcher(rule);
//    		Matcher m = Pattern.compile("\\w*\\b\\(?(?![,|\\)])").matcher(rule);
            
            Matcher m = Pattern.compile("'([^']*)'").matcher(rule);
            while (m.find()) {
                _ = m.group().replace(",", "");
                m.appendReplacement(sb, _);
            }
            m.appendTail(sb);
            rule = sb.toString();
            sb.setLength(0);
            
            m = Pattern.compile("(?!\\s)['|\"](?:''|[^'])*['|\"]|(?!\\s)[^',\\s]+(\\(|\\.)(?![,|\\)])").matcher(rule);
            
            boolean addbracket;
            while (m.find()) {
                _ = m.group().toLowerCase();

                if(_.contains(" ")){
                    addbracket = _.endsWith("\\(");
                    if(addbracket)
                        _ = _.substring(0, _.length()-1);
                    if(!_.startsWith("'"))
                        _ = "'"+_+"'";

//	        		_.replaceAll("(", "[").replaceAll(")", "]");
                    if(addbracket)
                        _+="(";
                }
//            	System.out.println(rule);
                m.appendReplacement(sb, _);
            }
            m.appendTail(sb);
            rule = sb.toString();
            sb.setLength(0);

            return rule.replace("'","\"");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return rule;

    }
    
    public String _dAllrule(String rule){
    	String[] _ = rule.split("\\)\\s*,");
    	String result="";
//    	int index;
    	if(_.length==1)
    		result = getSubRule(rule);
    	else{
	    	for(String s:_){
	    		result += getSubRule(s)+"), ";
	    	}
	    	result = result.substring(0, result.length()-3);
    	}
    	return result;
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
    public String replaceSymbolsInWholeRule(String rule) throws Exception {
        rule = rule.trim().replaceAll("'","").replaceAll("\"","'").replaceAll("-", "");
        if(_prohibitedNames.contains(rule)){
            rule+="_";
        }else{
            for (String name : _prohibitedNames) {
                if(rule.startsWith(name))
                    rule = rule.replace(name,name+"_");
            }
        }
        /*
        if(rule.contains("/")){
            String[] _ = rule.split("/");
            rule = _[_.length-1];
        }*/
        return rule;
    }
    
    public void setProgressLabelText(String label) {
    	if(progressLabel!=null){
    		progressLabel.setText(label);
    	}
	}

	
}




























































