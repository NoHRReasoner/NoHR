package local.translate;

public class ParsedRule {
	
	private String rule;
	private int len;
	private String predicate;
    private String variables;
    private boolean isUnderTnot = false;
    private CollectionsManager cm = Ontology.collectionsManager;
	public ParsedRule(String r){
		rule = r;
		parse();
	}
	private void parse(){
		rule = rule.trim();
		if(rule.startsWith(Config.searchNegation))
            rule = rule.replaceFirst(Config.searchNegation, Config.negation);
        if(rule.startsWith(Config.negation)) {
            rule = rule.replaceFirst(Config.negation+" ", "").trim();
            isUnderTnot = true;
        }
        len = 0;
        predicate="";
        variables="";
        String[] _;
        if(rule.startsWith("'")){
            int index = rule.lastIndexOf("'");
            predicate = rule.substring(0, index)+"'";
            _ = rule.substring(index+1, rule.length()).split("\\(");
        }else if(rule.startsWith("\"")){
            int index = rule.lastIndexOf("\"");
            predicate = rule.substring(0, index)+"\"";
            _ = rule.substring(index+1, rule.length()).split("\\(");
        }else{
            _ = rule.split("\\(");
            predicate = _[0];

        }
        if(_.length>1 && _[1]!=null){
            _ = _[1].split("\\)");
            _ = _[0].split(",");
            variables = "(";
            for(String argument : _){
            	if(Character.isLowerCase(argument.charAt(0)))
//            		variables+="c"+Utils.getHash(argument)+", ";
            		variables+="c"+cm.getHashedLabel(argument)+", ";
            	else 
            		variables+=argument+", ";
            }
            variables = variables.substring(0, variables.length()-2);
            variables +=")";
            len = _.length;
        }
//        predicate = Utils.getHash(Utils.replaceQuotes(predicate));
        predicate = cm.getHashedLabel(Utils.replaceQuotes(predicate));
	}

	public boolean isUnderTnot(){
        return isUnderTnot;
    }
	public String getTabledRule(){
		return "a"+predicate+"/"+len;
	}
	public String getTabledDoubledRule(){
		return "d"+predicate+"/"+len;
	}
	public String getTabledNegRule(){
		return "n"+predicate+"/"+len;
	}
    public String getRule(){
        return "a"+predicate+variables;
    }

    public String getNegRule(){
        return Config.negation+" n"+predicate+variables;
    }
    public String getNegPlainRule(){
        return Config.negation+" "+getRule();
    }
    public String getSubRule(){
        return "d"+predicate+variables;
    }
    public String getPlainSubRule(){
        if(isUnderTnot())
            return getNegSubRule();
        else
            return getSubRule();
    }
    public String getPlainHashedRule(){
        if(isUnderTnot())
            return getNegPlainRule();
        else
            return getRule();
    }
    public String getNegSubRule() {
        return Config.negation+" d"+predicate+variables;
    }
}

