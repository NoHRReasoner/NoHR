package local.translate;
import java.util.ArrayList;
import java.util.List;

public class EquivalentClass {
    protected int _iterator;
    protected String _rule;
    protected List<EquivalentRules> rulesList= new ArrayList<EquivalentRules>();
    protected List<String> listClassNames= new ArrayList<String>();
    protected List<String> listRuleNames= new ArrayList<String>();

    public EquivalentClass(int iterator){
        _iterator = iterator;
        _rule="";

    }
    public void updateClass(EquivalentClass equivalentClass){
        _iterator=equivalentClass.getVariableIterator();
        updateRule(equivalentClass.getListOfRules());
    }
    public int getVariableIterator(){
        return _iterator;
    }
    public void addRule(String name, int localIterator, int iterator, OntologyType type){
        rulesList.add(new EquivalentRules(name, localIterator, iterator, type));

    }
    public void updateRule(List<EquivalentRules> rules){
        for(EquivalentRules rule : rules){
            rulesList.add(rule);
        }
    }
    public int incrementIterator(){
        return ++_iterator;
    }
    public String getFinalRule(){
        _rule="";
        if(rulesList.size()>0){
            for(EquivalentRules rule : rulesList){
                _rule+="a"+rule.name+rule.getArguments()+", ";
            }
            _rule=_rule.substring(0,_rule.length()-2)+".";
        }
        return _rule;
    }

    public List<String> getNegRules(){
        List<String> result = new ArrayList<String>();
        String _rule="";
        EquivalentRules r;
        for(int i=0; i<rulesList.size();i++){
            r=rulesList.get(i);
            _rule="n"+r.name+r.getArguments()+" :- ";
            for(int j=0; j<rulesList.size();j++){
                if(j!=i){
                    r=rulesList.get(j);
                    _rule+="a"+r.name+r.getArguments()+", ";
                }
            }
            switch (r.ontologyType){
                case ONTOLOGY:{
                    listClassNames.add(r.name);
                    break;
                }
                case RULE:{
                    listRuleNames.add(r.name);
                    break;
                }
            }
            _rule=_rule.substring(0,_rule.length()-2)+".";
            result.add(_rule);
        }
        return result;
    }
    public List<String> getNegRulesHeadForTabling(){
        List<String> result = new ArrayList<String>();
        String _rule="";
        EquivalentRules r;
        for(int i=0; i<rulesList.size();i++){
            r=rulesList.get(i);
            _rule="n"+r.name+"/";
            switch (r.ontologyType){
                case ONTOLOGY:{
                    _rule+="1";
                    break;
                }
                case RULE:{
                    _rule+="2";
                    break;
                }
            }
            result.add(_rule);
        }
        return result;
    }
    public List<EquivalentRules> getListOfRules(){
        return rulesList;
    }

    public String getDoubledRules() {
        _rule="";
        if(rulesList.size()>0){
            for(EquivalentRules rule : rulesList){
                _rule+="d"+rule.name+rule.getArguments()+", ";
            }
            _rule=_rule.substring(0,_rule.length()-2);
        }
        return _rule;
    }

    protected class EquivalentRules{
        public String name;
        public int localIterator;
        public int iterator;
        protected String _variableName="X";
        public OntologyType ontologyType;

        public EquivalentRules(String _name, int _localIterator, int _iterator, OntologyType _type){
            name=_name;
            localIterator=_localIterator;
            iterator=_iterator;
            ontologyType=_type;
        }
        public String getArguments(){
            switch (ontologyType){
                case ONTOLOGY:
                    return "("+_variableName+iterator+")";
                case RULE:
                    return "("+_variableName+localIterator+", "+_variableName+iterator+")";
                default:
                    return "";
            }
        }

    }
    public enum OntologyType {
        RULE,
        ONTOLOGY
    }
}

