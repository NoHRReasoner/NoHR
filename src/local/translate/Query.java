package local.translate;

public class Query {

    private CollectionsManager cm;
    private OntologyLabel ontologyLabel;
    private ParsedRule parsedRule;
    public Query(CollectionsManager c, OntologyLabel ol){
        cm=c;
        ontologyLabel = ol;
    }

    public String prepareQuery(String q) {
    	String result = "";
        for (String s: Utils.getSubRulesFromRule(q)){
            parsedRule = new ParsedRule(s);
            result += parsedRule.getPlainHashedRule()+", ";
        }
        result= result.substring(0, result.length()-2);
        return result;
    }
}