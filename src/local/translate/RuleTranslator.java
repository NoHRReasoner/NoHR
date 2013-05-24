package local.translate;
public class RuleTranslator {

    private CollectionsManager cm;
    private ParsedRule parsedRule;
    private String currentRule;

    public RuleTranslator(CollectionsManager c) {
        cm = c;
    }


    public void proceedRule(String rule) throws Exception {
        if(rule.startsWith(Config.eq))
            return;
        if(rule.endsWith("."))
            rule = rule.substring(0, rule.length()-1);

        String[] arrayRule = rule.split(Config.eq);

        ParsedRule leftSideRule = new ParsedRule(arrayRule[0]);
        String rightSideRule = null;
        if(arrayRule.length>1 && arrayRule[1]!=null)
            rightSideRule = arrayRule[1].trim();
        tablePredicateFromRule(leftSideRule);

        if(cm.isAnyDisjointStatement()){
            writeArule(leftSideRule, rightSideRule);
            writeBrule(leftSideRule, rightSideRule);
        }else{
            writePlainRule(leftSideRule, rightSideRule);
        }

    }

    private void writePlainRule(ParsedRule leftSide, String rightSide){
        currentRule = "%writePlainRule";
        if(rightSide==null){
            writeLineToAppendedRules(leftSide.getRule() + ".");
        }else{
            String result = leftSide.getRule()+Utils.getEqForRule();
            for(String subRule : Utils.getSubRulesFromRule(rightSide)){
                parsedRule = new ParsedRule(subRule);
                if(parsedRule.isUnderTnot()){
                    tablePredicateFromRule(parsedRule);
                    result += parsedRule.getNegPlainRule()+", ";
                }else
                    result += parsedRule.getRule()+", ";
            }

            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }
    private void writeArule(ParsedRule leftSide, String rightSide){
        currentRule = "%writeArule";
        if(rightSide==null){
            writeLineToAppendedRules(leftSide.getRule() + ".");
        }else{
            String result = leftSide.getRule()+Utils.getEqForRule();

            for(String subRule : Utils.getSubRulesFromRule(rightSide)){
                parsedRule = new ParsedRule(subRule);
                if(parsedRule.isUnderTnot()){
                    tablePredicateFromRule(parsedRule);
                    result += parsedRule.getNegSubRule()+", ";
                }else{
                    result += parsedRule.getRule()+", ";
                }
            }
            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }
    private void writeBrule(ParsedRule leftSide, String rightSide){
        currentRule = "%writeBrule";
        if(rightSide==null){
            String rule = leftSide.getSubRule();
            if(cm.isPrediactesAppearedUnderNunderscore(leftSide.getTabledNegRule()))
                rule +=Utils.getEqForRule() + leftSide.getNegRule();
            writeLineToAppendedRules(rule + ".");
        }else{
            String result = leftSide.getSubRule()+Utils.getEqForRule();

            for(String subRule : Utils.getSubRulesFromRule(rightSide)){
                parsedRule = new ParsedRule(subRule);
                if(parsedRule.isUnderTnot()){
                    tablePredicateFromRule(parsedRule);
                    result += parsedRule.getNegPlainRule()+", ";
                }else{
                    result += parsedRule.getSubRule()+", ";
                }
            }
            if(cm.isPrediactesAppearedUnderNunderscore(leftSide.getTabledNegRule())){
                result += leftSide.getNegRule()+", ";
            }
            result=result.substring(0,result.length()-2);
            writeLineToAppendedRules(result + ".");
        }
    }

    private void writeLineToAppendedRules(String string){
        string += Config.ruleCreationDebug ? currentRule : "";
        cm.addTranslatedRule(string);
    }

    private void tablePredicateFromRule(ParsedRule rule){

        cm.addTabledPredicateRule(rule.getTabledRule());
        if(cm.isAnyDisjointStatement()){
            cm.addTabledPredicateRule(rule.getTabledDoubledRule());
        }
    }

}