package local.translate;

public class Query {

    private ParsedRule parsedRule;
    private CollectionsManager cm;
    public Query(CollectionsManager collectionsManager){
        cm = collectionsManager;
    }

    public String prepareQuery(String q, boolean isAnyDisjointWithStatement) {
    	String result = "";
    	String tabledRule="";
    	boolean isAvailable;
        for (String s: Utils.getSubRulesFromRule(q)){
            parsedRule = new ParsedRule(s);
            isAvailable = true;
            if(parsedRule.isUnderTnot()){
            	tabledRule = isAnyDisjointWithStatement ? parsedRule.getTabledDoubledRule() : parsedRule.getTabledRule();
            	isAvailable =cm.isTabled(tabledRule); 
            }
            if(isAvailable)
            	result += (isAnyDisjointWithStatement ? parsedRule.getHashedRuleForQuery() : parsedRule.getPlainHashedRule()) +", ";
        }
        if(result.endsWith(", "))
        	result= result.substring(0, result.length()-2);
        return result;
    }
    
}