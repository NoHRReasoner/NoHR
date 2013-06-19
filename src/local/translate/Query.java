package local.translate;

public class Query {

    private ParsedRule parsedRule;
    public Query(){
        
    }

    public String prepareQuery(String q) {
    	String result = "";
        for (String s: Utils.getSubRulesFromRule(q)){
            parsedRule = new ParsedRule(s);
            result += parsedRule.getHashedRuleForQuery()+", ";
        }
        result= result.substring(0, result.length()-2);
        return result;
    }
}