package local.translate;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class CollectionsManager {

    private HashSet<String> tablePredicatesOntology = new HashSet<String>();
    private HashSet<String> tablePredicatesRules = new HashSet<String>();
    private HashSet<String> translatedOntologies = new HashSet<String>();
    private HashSet<String> appendedRules = new HashSet<String>();
    private HashSet<String> prediactesAppearedUnderNunderscore = new HashSet<String>();
    private HashMap<String, String> labels = new HashMap<String, String>();
    private HashMap<String, String> labelsValue = new HashMap<String, String>();
    private static final Logger log = Logger.getLogger(Ontology.class);
    private boolean isAnyDisjointStatement;
    public CollectionsManager(){
        isAnyDisjointStatement = false;
        log.setLevel(Config.logLevel);
    }

    public void clear(){
        tablePredicatesOntology = new HashSet<String>();
        tablePredicatesRules = new HashSet<String>();
        translatedOntologies = new HashSet<String>();
        appendedRules = new HashSet<String>();
        prediactesAppearedUnderNunderscore = new HashSet<String>();
    }

    public void clearRules(){
    	log.info("Clearing rules set");
        tablePredicatesRules = new HashSet<String>();
        appendedRules = new HashSet<String>();
    }

    public void clearOntology(){
    	log.info("Clearing ontology set");
        tablePredicatesOntology = new HashSet<String>();
        translatedOntologies = new HashSet<String>();
        prediactesAppearedUnderNunderscore = new HashSet<String>();
    }

    public void addTabledPredicateOntology(String title){
        tablePredicatesOntology.add(title);
    }
    public HashSet<String> getAllTabledPredicateOntology(){
        return tablePredicatesOntology;
    }

    public void addTabledPredicateRule(String title){
        tablePredicatesRules.add(title);
    }
    public HashSet<String> getAllTabledPredicateRule(){
        return tablePredicatesRules;
    }

    public void addTranslatedOntology(String rule){
        translatedOntologies.add(rule);
    }
    public HashSet<String> getTranslatedOntologies(){
        return translatedOntologies;
    }

    public void addTranslatedRule(String rule){
        appendedRules.add(rule);
    }

    public HashSet<String> getTranslatedRules(){
        return appendedRules;
    }

    public void addPrediactesAppearedUnderNunderscore(String predicate){
        prediactesAppearedUnderNunderscore.add(predicate);
    }
    public boolean isPrediactesAppearedUnderNunderscore(String predicate){
        return prediactesAppearedUnderNunderscore.contains(predicate);
    }
    public void setIsAnyDisjointStatement(boolean value){
        isAnyDisjointStatement = value;
    }
    public boolean isAnyDisjointStatement(){
        return isAnyDisjointStatement;
    }


    public String getLabelByHash(String hash){
    	String originalHash = hash;
    	hash = hash.substring(1, hash.length());
    	if(labels.containsKey(hash))
    		return labels.get(hash);
    	return originalHash;
    }

    public String getHashedLabel(String label){
        if(labelsValue.containsKey(label)){
            return labelsValue.get(label);
        }else{
            String hash = Utils.getHash(label);
            labels.put(hash, label);
            labelsValue.put(label, hash);
            return hash;
        }
    }

    public void printAllLabels(){

        FileWriter writer = null;
        try {
            writer = new FileWriter("labels.txt");
            Iterator it = labels.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                writer.write(pairs.getKey() + " = " + pairs.getValue()+"\n");
                //it.remove(); // avoids a ConcurrentModificationException
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
