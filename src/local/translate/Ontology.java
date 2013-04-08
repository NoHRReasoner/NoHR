package local.translate;

import java.io.*;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
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
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;

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
	
    private List<String> tabledOntologies = new ArrayList<String>();
    private List<String> translatedOntologies = new ArrayList<String>();
    private List<String> appendedRules = new ArrayList<String>();
    
	private String _delimeter="#";
	private String _altDelimeter=":";
    private String _negation="tnot";
    private String _searchNegation="not";
    private String _eq=":-";

    private List<String> _existsOntology;
    private List<String> _existsRules;

    private boolean debug=false;
    private String _currentRule;

    private List<String> _prohibitedNames = Arrays.asList("table","attribute");
    
    private String tempDirProp = "java.io.tmpdir";
    private String _tempDir="";
    private String _proresult = "ontologies_to_rules_proresult.p";
    private String _result = "ontologies_to_rules_result.p";//"r.p";//"ontologies_to_rules_result.p";
    private String _mergedOntologies = "ontologies_to_rules_merged.owl";
    
    private boolean _isLog = true;
    private JTextArea _textArea = null;
    private boolean isTranslated = false;
	
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
		//startOuters(false);
//        writeLineToFile(":- auto_table.");
        writeLineToTopFile(":- set_prolog_flag(unknown,fail).");
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
        _existsRules = new ArrayList<String>();
        _existsOntology = new ArrayList<String>();
    }
	private void startOuters(boolean isAppend) throws IOException{
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
	}
    public Ontology(OWLModelManager owlModelManager, JTextArea textArea, boolean isLog) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		_ontologyManager = owlModelManager.getOWLOntologyManager();
		_ontology = owlModelManager.getActiveOntology();
        _tempDir = System.getProperty(tempDirProp);
        //_textArea.append("OS current temporary directory is " + tempDir);
        _isLog = isLog;
        _textArea = textArea;
        _existsRules = new ArrayList<String>();
        _existsOntology = new ArrayList<String>();
	}
    public boolean PrepareForTranslating() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
    	//startOuters(false);
    	writeLineToTopFile(":- set_prolog_flag(unknown,fail).");
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


        fillExistsOntologiesAndRules();
        getDiffTime(date1, "PreProcessing and finish All I rules finished it's work, it took:");
        date1=new Date();
        autoTable();
        getDiffTime(date1, "AutoTabling finished it's work, it took:");
        date1=new Date();
        loopThrowAllClasses();
        getDiffTime(date1, "Rules C1, C2, C3 and A1 are finished it's work, it took:");
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
	public void appendRules(List<String> _rules) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, ParserException {
		//startOuters(true);
		if(!isTranslated){
			PrepareForTranslating();
			proceed();
		}
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
        String leftSideRule;
        String rightSideRule;
        String[] arrayRule;
        String result;
        if(rule.contains(_searchNegation)){
            arrayRule=rule.split(_eq);
            leftSideRule=arrayRule[0].trim();
            rightSideRule=arrayRule[1].trim();
            result=leftSideRule+getEqForRule();
            arrayRule=rightSideRule.split(", ");
            for(String _rule : arrayRule){
                if(_rule.contains(_searchNegation))
                    result+=getSubRule(_rule)+", ";
                else
                    result+=_rule+", ";
            }
            result=result.substring(0,result.length()-2)+".";
            writeLineToAppendedRules(result);

            result= getSubRule(leftSideRule)+getEqForRule();
            for(String _rule : arrayRule){
                if(_rule.contains(_searchNegation))
                    result+=_rule+", ";
                else
                    result+=getSubRule(_rule)+", ";
            }
            result+= isExistOntology(getNameFromRule(rule)) ? getNegRule(leftSideRule) : leftSideRule;
            writeLineToAppendedRules(result + ".");

        }else if(rule.contains(_eq)){
        	writeLineToAppendedRules(rule + ".");
            arrayRule=rule.split(_eq);
            leftSideRule=arrayRule[0].trim();
            rightSideRule=arrayRule[1].trim();
            result=getSubRule(leftSideRule)+getEqForRule();
            arrayRule=rightSideRule.split(", ");
            for(String _rule : arrayRule){
                result+=getSubRule(_rule)+", ";
            }
            result+= isExistOntology(getNameFromRule(rule)) ? getNegRule(leftSideRule) : leftSideRule;
            writeLineToAppendedRules(result + ".");

        }else{
        	writeLineToAppendedRules(rule + ".");
            if(isExistOntology(getNameFromRule(rule))){
            	writeLineToAppendedRules(getSubRule(rule) + getEqForRule() + getNegRule(rule) + ".");
            }
        }
    }
    protected String getEqForRule(){
        return " "+_eq+" ";
    }
    protected String getSubRule(String rule){
        return rule.replace("(","_d(").trim();
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
//		_outer.close();
        /*BufferedReader in = new BufferedReader(new FileReader(_tempDir+_proresult));
        String str;
        while ((str = in.readLine()) != null) {
            writeLineToTopFile(str);
        }
        in.close();*/
//        _outerTop.close();
//        _outer.close();
//        _outfile.close();
//        _outTopfile.close();
        
//        _outTopfile.
        
        
        FileWriter writer = new FileWriter(_tempDir+_result, isTranslated);
        if(!isTranslated){
	        for(String str: tabledOntologies) {
	        	writer.write(str+"\n");
	        }
	        for(String str: translatedOntologies) {
	        	writer.write(str+"\n");
		    }
	        isTranslated = true;
        }
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
		printLog("Reasoner finished work");
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
    private void writeLineToTopFile(String string){
        string += debug ? _currentRule : "";
//        _outerTop.println(string);
        tabledOntologies.add(string);
        //return;
    }
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
        result=result.trim().replace("-","").toLowerCase();
        if(_prohibitedNames.contains(result))
            result+="_";
		return result;
	}
    /**
     * (a1). for each C(a) ∈ A: C(a) ← and Cd(a) ← notNC(a).
     * @param member
     * @param entity
     */
	private void writeRuleA1(String member, String entity){
        _currentRule = "%A1";
        String a = getRuleFromString(member, 1);
        String C = getRuleFromString(entity, 1);
        writeLineToFile(C + "(" + a + ").");
        if(isExistOntology(C)){
            String rule = getEqForRule() +_negation + " n_" + C + "(" + a + ")";
            writeLineToFile(C + "_d(" + a + ")" + rule + ".");
        }
    }
    /**
     * (a2). for each R(a, b) ∈ A: R(a, b) ← and Rd(a, b) ← not NR(a, b).
     * @param entity
     */
    private void writeRuleA2(String entity){
        _currentRule = "%A2";
		String R= getRuleFromString(entity, 1),
				a= getRuleFromString(entity, 2),
				b= getRuleFromString(entity, 3);
		writeLineToFile(R + "(" + a + "," + b + ").");
        if(isExistRule(R)){
            String rule =  getEqForRule()+_negation + " n_" + R + "(" + a + "," + b + ")";
            writeLineToFile(R + "_d(" + a + "," + b + ")" + rule + ".");
        }
	}
    /**
     * (i2). for each C1 ⊓ C2 ⊑⊥∈ T : NC2(x) ← C1(x) and NC1(x) ← C2(x).
     * @param expression
     */
	private void writeRuleI2(String expression){
        _currentRule = "%I2";
		String C2= getRuleFromString(expression, 1);
		String C1= getRuleFromString(expression, 2);
        //if(!C1.equals(C2)){
        insertIntoExistOntology(C2);
        insertIntoExistOntology(C1);
        writeLineToFile("n_" + C2 + "(X) :- " + C1 + "(X).");
        writeLineToFile("n_" + C1 + "(X) :- " + C2 + "(X).");
        //}
	}
    /**
     * (c2). foreach C1 ⊓ C2 ⊑ D ∈ T : D(x) ← C1(x) , C2(x) and Dd(x) ← C1d(x), C2d(x), not ND(x).
     * @param expression
     * @param superclass
     */
	private void writeRuleC2(String expression, String superclass){
        _currentRule = "%C2";
		String C2= getRuleFromString(expression, 2);
		String C1= getRuleFromString(expression, 1);
		String D= getRuleFromString(superclass, 1);
		writeLineToFile(D + "(X)" + getEqForRule() + C1 + "(X), " + C2 + "(X).");
        String rule = isExistOntology(D) ? ", " + _negation + " n_" + D + "(X)" : "";
		writeLineToFile(D + "_d(X)" + getEqForRule() + C1 + "_d(X), " + C2 + "_d(X)" + rule + ".");
	}
    /**
     * (i3). for each ∃R.C ⊑⊥∈ T : NC(y) ← R(x,y) and NR(x,y) ← C(y) .
     * @param expression
     */
	private void writeRuleI3(String expression){
        _currentRule = "%I3";
		String C= getRuleFromString(expression, 2);
		String R= getRuleFromString(expression, 1);
		writeLineToFile("n_" + C + "(Y) :- " + R + "(X,Y).");
		writeLineToFile("n_" + R + "(X,Y) :- " + C + "(Y).");
        insertIntoExistRules(R);
        insertIntoExistOntology(C);
	}
    /**
     * (c3). for each ∃R.C ⊑ D ∈ T : D(x) ← R(x,y),C(y) and Dd(x) ← Rd(x, y), Cd(y), not ND(x).
     * @param expression                                                                                ≤
     * @param superclass
     */
	private void writeRuleC3(String expression, String superclass){
        _currentRule = "%C3";
		String C= getRuleFromString(expression, 2);
		String R= getRuleFromString(expression, 1);
		String D= getRuleFromString(superclass, 1);
		writeLineToFile(D + "(X)" + getEqForRule() + R + "(X,Y), " + C + "(Y).");
        String rule = isExistOntology(D) ? ", " + _negation + " n_" + D + "(X)" : "";
		writeLineToFile(D + "_d(X)" + getEqForRule() + R + "_d(X,Y), " + C + "_d(Y)" + rule + ".");
	}
    /**
     * (i1). for each C ⊑⊥∈ T : NC(x) ←.
     * @param expression
     */
	private void writeRuleI1(String expression){
        _currentRule = "%I1";
        String C= getRuleFromString(expression, 1);
		writeLineToFile("n_" + C + "(X).");
        insertIntoExistOntology(C);
	}
    /**
     * (c1). foreach GCI C ⊑ D ∈ T: D(x)←C(x) and Dd(x) ← Cd(x), not ND(x).
     * @param expression
     * @param superclass
     */
	private void writeRuleC1(String expression, String superclass, boolean lastIndex){
        _currentRule = "%C1";
		String D= getRuleFromString(superclass, 1);
		String C= getRuleFromString(expression, lastIndex ? -1 : 1);
        if(!C.equals(D))
		    writeLineToFile(D + "(X)" + getEqForRule() + C + "(X).");
        String rule = isExistOntology(D) ? ", " + _negation + " n_" + D + "(X)" : "";
        if(!(C.equals(D) && rule.length()==0) && isExistOntology(D))
		    writeLineToFile(D + "_d(X)" + getEqForRule() + C + "_d(X)" + rule + ".");
	}
    /**
     * (r1). foreach RI R⊑S ∈ T: S(x,y)←R(x,y) and Sd(x, y) ← Rd(x, y), not NS(x, y).
     * @param expression
     * @param superclass
     */
	private void writeRuleR1(String expression, String superclass){
        _currentRule = "%R1";
		String S= getRuleFromString(superclass, 1);
		String R= getRuleFromString(expression, 1);
        if(!R.equals(S))
		    writeLineToFile(S + "(X,Y)" + getEqForRule() + R + "(X,Y).");
        String rule = isExistOntology(S) ? ", " + _negation + " n_" + S + "(X,Y)":"";
        if(!(R.equals(S) && rule.length()==0) && isExistRule(R))
		    writeLineToFile(S + "_d(X,Y)" + getEqForRule() + R + "_d(X,Y)" + rule + ".");
	}
    /**
     * (r2). foreach R◦S ⊑ T ∈ T: T(x,z)←R(x,y),S(y,z) and Td(x,z) ← Rd(x,y),Sd(y,z),notNT(x,z).
     * @param axiom
     */
	private void writeRuleR2(String axiom){
        _currentRule = "%R2";
        String S= getRuleFromString(axiom, 2);
        String R= getRuleFromString(axiom, 1);
        String T= getRuleFromString(axiom, 3);
        writeLineToFile(T + "(X,Z)" + getEqForRule() + R + "(X,Y), " + S + "(Y,Z).");
        if(isExistRule(T)){
            String rule = isExistOntology(T) ? ", " + _negation + " n_" + T + "(X,Z)":"";
            writeLineToFile(T + "_d(X,Z)" + getEqForRule() + R + "_d(X,Y), " + S + "_d(Y,Z)" + rule + ".");
        }
	}

    private void writeEquivalentRule(String owlClass, OWLClassExpression rightPartOfRule){
        _currentRule="%EquivalentRule";
        EquivalentClass rightSideOfRule = getRuleFromEquivalentClasses(rightPartOfRule, 1, 1);
        String rule= getRuleFromString(owlClass,1)+"(X1) "+_eq+" "+rightSideOfRule.getFinalRule();
        writeLineToFile(rule);
    }

    private void writeNegEquivalentRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        _currentRule="%NegEquivalentRule";
        EquivalentClass rules= getRuleFromEquivalentClasses(classExpression, 1, 1);
        if(!(owlClass.isOWLThing() || owlClass.isOWLNothing()))
            rules.addRule(getRuleFromString(owlClass.toString(),1),1,1, EquivalentClass.OntologyType.ONTOLOGY);
        for(String rule : rules.getNegRules()){
            writeLineToFile(rule);
        }
        for (String className : rules.listClassNames){
            insertIntoExistOntology(className);
        }
        for(String ruleName : rules.listRuleNames){
            insertIntoExistRules(ruleName);
        }
    }

    private void writeDoubledRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        _currentRule="%DoubledRule";
        EquivalentClass rules = getRuleFromEquivalentClasses(classExpression, 1, 1);
        String _owlClass=getRuleFromString(owlClass.toString(), 1);
        writeLineToFile(_owlClass+"(X1)"+ getEqForRule()+rules.getFinalRule());
        if(isExistOntology(_owlClass)){
            String rule=_owlClass+"_d(X1)"+getEqForRule()+rules.getDoubledRules()+", " + _negation + " n_" + _owlClass + "(X1).";
            writeLineToFile(rule);
        }
    }

    private void autoTable(){
        String name;
        for(OWLClass owlClass : _owlClasses){
            if(!(owlClass.isOWLThing() || owlClass.isOWLNothing())){
                name=getRuleFromString(owlClass.toString(),1);
                writeLineToTopFile(":- table " + name + "/1.");
                if(isExistOntology(name))
                    writeLineToTopFile(":- table d_"+name+"/1.");
            }
        }
        for(OWLObjectProperty objectProperty : _objectProperties){
            name=getRuleFromString(objectProperty.toString(),1);
            writeLineToTopFile(":- table " + name + "/2.");
            if(isExistRule(name))
                writeLineToTopFile(":- table d_"+name+"/2.");
        }
    }


    /**
     * Going throw all ontologies and preprocess them.
     */
    private void fillExistsOntologiesAndRules(){
        String _owlExpression;
        boolean isTopClass;
        ClassExpressionType expressionType;



        //Init loop throw all ontologies
        for(OWLClass owlClass : _owlClasses){
            //Going into loop throw all Disjoint classes
            for(OWLClassExpression owlClassExpression : owlClass.getDisjointClasses(_ontology)){
//                OWLClassExpression hasPartSomeNose = _owlDataFactory.getOWLObjectSomeValuesFrom(_owlDataFactory.getOWLObjectProperty(owlClassExpression.), owlClass);
                isTopClass=owlClass.isOWLThing() || owlClass.isOWLNothing();
                expressionType=owlClassExpression.getClassExpressionType();
                _owlExpression=owlClassExpression.toString();
                if(expressionType==ClassExpressionType.OBJECT_SOME_VALUES_FROM && isTopClass){
                    writeRuleI3(_owlExpression);
                }else if(expressionType==ClassExpressionType.OBJECT_INTERSECTION_OF && isTopClass){
                    writeRuleI2(_owlExpression);
                }else{// if(isTopClass){
                    if(expressionType==ClassExpressionType.OWL_CLASS && isTopClass)
                        writeRuleI1(_owlExpression);
                    else
                        writeNegEquivalentRules(owlClassExpression, owlClass);
                }
            }
        }
    }
    /**
     * Going into loop of all classes
     */
    private void loopThrowAllClasses(){
        String _owlClass;
        String _owlExpression;
        boolean isTopClass;
        ClassExpressionType expressionType;
        OWLClassExpression leftPartOfRule;
        OWLClassExpression rightPartOfRule;
        String leftPartOfRuleString;
        String rightPartOfRuleString;
        List<OWLClassExpression> equivalentClasses;
        for(OWLClass owlClass : _owlClasses){
            equivalentClasses = new ArrayList<OWLClassExpression>();
            _owlClass=owlClass.toString();
            for(OWLIndividual individual : owlClass.getIndividuals(_ontology)){
                writeRuleA1(individual.toString(), _owlClass);
            }

            for(OWLClassExpression owlClassExpression : owlClass.getSubClasses(_ontology)){
                isTopClass=owlClass.isOWLThing() || owlClass.isOWLNothing();
//                expressionType=owlClassExpression.getClassExpressionType();
//                _owlExpression=owlClassExpression.toString();
                if(!isTopClass)
                    writeDoubledRules(owlClassExpression, owlClass);

                /*
                if(expressionType==ClassExpressionType.OBJECT_SOME_VALUES_FROM){// && !isTopClass){
                    writeRuleC3(_owlExpression, _owlClass);
                }else if(expressionType==ClassExpressionType.OBJECT_INTERSECTION_OF){// && !isTopClass){
                    writeRuleC2(_owlExpression, _owlClass);
                }else{// if(!isTopClass){
                    if(expressionType==ClassExpressionType.OWL_CLASS)
                        writeRuleC1(_owlExpression, _owlClass, false);
                    else
                        writeEquivalentRule(_owlClass, owlClassExpression);
                }
                */
            }

            for(OWLEquivalentClassesAxiom equivalentClassesAxiom : _ontology.getEquivalentClassesAxioms(owlClass)){
                List<OWLClassExpression> list = equivalentClassesAxiom.getClassExpressionsAsList();
                for(int i=0; i<list.size(); i++){
                    equivalentClasses.add(list.get(i));
                }
            }
            if(equivalentClasses.size()>0){
                equivalentClasses = removeDuplicates(equivalentClasses);
                for(int i=0; i<equivalentClasses.size(); i++){
                    rightPartOfRule=equivalentClasses.get(i);
                    rightPartOfRuleString=rightPartOfRule.toString();
                    if(rightPartOfRule.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                        writeRuleC1(rightPartOfRuleString, _owlClass, false);
                    }else{
                        writeEquivalentRule(_owlClass, rightPartOfRule);
                    }
                }
            }
        }
    }
    private void loopThrowAllProperties(){
        for(OWLObjectProperty objectProperty : _objectProperties){
            for(OWLAxiom axiom : objectProperty.getReferencingAxioms(_ontology)){
                if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_ASSERTION){
                    writeRuleA2(axiom.toString());
                }
            }
            for(OWLObjectPropertyExpression objectPropertyExpression : objectProperty.getSubProperties(_ontology)){
                writeRuleR1(objectPropertyExpression.toString(), objectProperty.toString());
            }
        }
        for(OWLAxiom axiom : _ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)){
            writeRuleR2(axiom.toString());
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
        switch (owlClassExpression.getClassExpressionType()){
            case OWL_CLASS:{
                equivalentClass.addRule(
                        getRuleFromString(owlClassExpression.toString(),1),
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

                equivalentClass.addRule(getRuleFromString(property.toString(),1), localIterator, equivalentClass.incrementIterator(), EquivalentClass.OntologyType.RULE);
                equivalentClass.updateClass(getRuleFromEquivalentClasses(classExpression, ++localIterator, equivalentClass.getVariableIterator()));
                break;
            }

        }
        return equivalentClass;
    }

	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 * @throws ParserException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, IOException, ParserException, OWLOntologyStorageException{
		
		String currentDir=new java.io.File(".").getCanonicalPath();
		JFileChooser file = new JFileChooser(currentDir);
		file.showDialog(null, "Choose ontology");
		Date date1=new Date();
		Ontology ontology = new Ontology(file.getSelectedFile().getAbsolutePath());
//        Ontology ontology = new Ontology("/Users/vadimivanov/Desktop/equival1.owl");
		ontology.proceed();
		Date date2=new Date();
		long diff=date2.getTime() - date1.getTime();
		int val = file.showDialog(null, "Choose rules");
        date1=new Date();
        if(val==JFileChooser.APPROVE_OPTION){
            ontology.appendRules(file.getSelectedFile().getAbsolutePath());
        }

        ontology.Finish();
		date2=new Date();
		diff+=date2.getTime() - date1.getTime();
		System.out.println("I'm done. it took "+diff+" milisec");
	}

	
}

