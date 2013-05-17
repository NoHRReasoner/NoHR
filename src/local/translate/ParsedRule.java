package local.translate;

public class ParsedRule {
	
	private String rule;
	private int len;
	private String predicate;
	
	public ParsedRule(String r){
		rule = r;
		parse();
	}
	private void parse(){
		rule = rule.trim();
		if(rule.startsWith(Config.searchNegation))
            rule = rule.replaceFirst(Config.searchNegation, Config.negation);
        if(rule.startsWith(Config.negation))
            rule = rule.replaceFirst(Config.negation+" ", "");
        len = 0;
        predicate="";
        String[] _;
        if(rule.startsWith("'")){
            int index = rule.lastIndexOf("'");
            predicate = rule.substring(0, index)+"'";
            _ = rule.substring(index+1, rule.length()).split("\\(");
        }else{
            _ = rule.split("\\(");
            predicate = _[0];

        }
        if(_.length>1 && _[1]!=null){
            _ = _[1].split("\\)");
            _ = _[0].split(",");
            len = _.length;
        }
	}
	
	public String getPredicate(){
		return predicate;
	}
	public int getCountVariables(){
		return len;
	}
	public String getTabledRule(){
		return predicate+"/"+len;
	}
	public String getTabledDoubledRule(){
		return predicate+"_d/"+len;
	}
	public String getTabledNegRule(){
		return "n_"+predicate+"/"+len;
	}
}
