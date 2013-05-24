package local.translate;
import org.semanticweb.owlapi.model.*;

public class RuleCreator {

    private CollectionsManager cm;
    private String currentRule;
    private OntologyLabel ontologyLabel;

    public RuleCreator(CollectionsManager c, OntologyLabel ol) {
        cm = c;
        ontologyLabel = ol;
    }

    /**
            * (a1). for each C(a) ∈ A: C(a) ← and Cd(a) ← notNC(a).
            * @param member
    * @param entity
    */
    public void writeRuleA1(OWLIndividual member, OWLClass entity){
        currentRule = "%A1";
        String a = ontologyLabel.getLabel(member, 1);
        String C = ontologyLabel.getLabel(entity, 1);
        writeLineToFile("a"+C + "(a" + a + ").");
        cm.addTabledPredicateOntology("a"+C + "/1");
        if(cm.isAnyDisjointStatement()){
            String rule = "d"+C + "(a" + a + ")";
            cm.addTabledPredicateOntology("d"+C + "/1");
            if(isPredicateAppearedInHeadUnderNunderscore("n"+C+"/1"))
                rule += Utils.getEqForRule() + Config.negation + " n" + C + "(a" + a + ")";
            writeLineToFile(rule + ".");
        }
    }

    /**
     * (a2). for each R(a, b) ∈ A: R(a, b) ← and Rd(a, b) ← not NR(a, b).
     * @param entity
     */
    public void writeRuleA2(OWLAxiom entity){
        currentRule = "%A2";
        String R= ontologyLabel.getLabel(entity, 1),
                a= ontologyLabel.getLabel(entity, 2),
                b= ontologyLabel.getLabel(entity, 3);
        writeLineToFile("a"+R + "(a" + a + ", a" + b + ").");
        cm.addTabledPredicateOntology("a"+R + "/2");
        if(cm.isAnyDisjointStatement()){
            String rule = "d"+R + "(a" + a + ", a" + b + ")";
            cm.addTabledPredicateOntology("d"+R + "/2");
            if(isPredicateAppearedInHeadUnderNunderscore("n"+R+"/2"))
                rule += Utils.getEqForRule()+Config.negation + " n" + R + "(a" + a + ", a" + b + ")";
            writeLineToFile(rule + ".");
        }
    }

    /**
     * (i2). for each C1 ⊓ C2 ⊑⊥∈ T : NC2(x) ← C1(x) and NC1(x) ← C2(x).
     * @param expression
     */
    public void writeRuleI2(OWLClassExpression expression){
        currentRule = "%I2";
        String C2= ontologyLabel.getLabel(expression, 1);
        String C1= ontologyLabel.getLabel(expression, 2);
        writeLineToFile("n" + C2 + "(X) :- a" + C1 + "(X).");
        cm.addTabledPredicateOntology("n" + C2 + "/1");
        addPredicateToSetPredicatesAppearedUnderNunderscore("n"+C2+"/1");
        writeLineToFile("n" + C1 + "(X) :- a" + C2 + "(X).");
        cm.addTabledPredicateOntology("n" + C1 + "/1");
        addPredicateToSetPredicatesAppearedUnderNunderscore("n"+C1+"/1");

    }

    /**
     * (i3). for each ∃R.C ⊑⊥∈ T : NC(y) ← R(x,y) and NR(x,y) ← C(y) .
     * @param expression
     */
    public void writeRuleI3(OWLClassExpression expression){
        currentRule = "%I3";
        String C= ontologyLabel.getLabel(expression, 2);
        String R= ontologyLabel.getLabel(expression, 1);
        writeLineToFile("n" + C + "(Y) :- a" + R + "(X,Y).");
        cm.addTabledPredicateOntology("n" + C + "/1");
        addPredicateToSetPredicatesAppearedUnderNunderscore("n"+C+"/1");
        writeLineToFile("n" + R + "(X,Y) :- a" + C + "(Y).");
        cm.addTabledPredicateOntology("n" + R + "/2");
    }

    /**
     * (i1). for each C ⊑⊥∈ T : NC(x) ←.
     * @param expression
     */
    public void writeRuleI1(OWLClassExpression expression){
        currentRule = "%I1";
        String C= ontologyLabel.getLabel(expression, 1);
        writeLineToFile("n" + C + "(X).");
        cm.addTabledPredicateOntology("n" + C + "/1");
        addPredicateToSetPredicatesAppearedUnderNunderscore("n"+C+"/1");
    }
    /**
     * (c1). foreach GCI C ⊑ D ∈ T: D(x)←C(x) and Dd(x) ← Cd(x), not ND(x).
     * @param expression
     * @param superclass
     */
    public void writeRuleC1(OWLClassExpression expression, OWLClass superclass, boolean lastIndex){
        currentRule = "%C1";
        String D= ontologyLabel.getLabel(superclass, 1);
        String C= ontologyLabel.getLabel(expression, lastIndex ? -1 : 1);
        writeLineToFile("a"+D + "(X)" + Utils.getEqForRule() + "a"+C + "(X).");
        cm.addTabledPredicateOntology("a"+D + "/1");
        if(cm.isAnyDisjointStatement()){
            String rule = "d"+D + "(X)" + Utils.getEqForRule() + "d"+C + "(X)" ;
            if(isPredicateAppearedInHeadUnderNunderscore("n"+D+"/1"))
                rule +=", " + Config.negation + " n" + D + "(X)";
            writeLineToFile(rule + ".");
            cm.addTabledPredicateOntology("d"+D + "/1");
        }
    }
    /**
     * (r1). foreach RI R⊑S ∈ T: S(x,y)←R(x,y) and Sd(x, y) ← Rd(x, y), not NS(x, y).
     * @param expression
     * @param superclass
     */
    public void writeRuleR1(OWLObjectPropertyExpression expression, OWLObjectProperty superclass){
        currentRule = "%R1";
        if(expression==superclass)
            return;
        String S= ontologyLabel.getLabel(superclass, 1);
        String R= ontologyLabel.getLabel(expression, 1);
        writeLineToFile("a"+S + "(X,Y)" + Utils.getEqForRule() + "a"+R + "(X,Y).");
        cm.addTabledPredicateOntology("a"+S + "/2");
        if(cm.isAnyDisjointStatement()){
            String rule = "d"+S + "(X,Y)" + Utils.getEqForRule() + "d"+R + "(X,Y)";
            cm.addTabledPredicateOntology("d"+S + "/2");
            if(isPredicateAppearedInHeadUnderNunderscore("n"+S+"/2"))
                rule +=", " + Config.negation + " n" + S + "(X,Y)";
            writeLineToFile(rule + ".");
        }
    }
    /**
     * (r2). foreach R◦S ⊑ T ∈ T: T(x,z)←R(x,y),S(y,z) and Td(x,z) ← Rd(x,y),Sd(y,z),notNT(x,z).
     * @param axiom
     */
    public void writeRuleR2(OWLAxiom axiom){
        currentRule = "%R2";
        String S= ontologyLabel.getLabel(axiom, 2);
        String R= ontologyLabel.getLabel(axiom, 1);
        String T= ontologyLabel.getLabel(axiom, 3);
        writeLineToFile("a"+T + "(X,Z)" + Utils.getEqForRule() + "a"+R + "(X,Y), " + "a"+S + "(Y,Z).");
        cm.addTabledPredicateOntology("a"+T + "/2");
        if(cm.isAnyDisjointStatement()){//if(isExistRule(T)){
            String rule = "d"+T + "(X,Z)" + Utils.getEqForRule() + "d"+R + "(X,Y), " + "d"+S + "(Y,Z)";
            cm.addTabledPredicateOntology("d"+T + "/2");
            if(isPredicateAppearedInHeadUnderNunderscore("n"+T+"/2"))
                rule +=", " + Config.negation + " n" + T + "(X,Z)";
            writeLineToFile(rule + ".");
        }
    }

    public void writeEquivalentRule(OWLClass owlClass, OWLClassExpression rightPartOfRule){
        currentRule ="%EquivalentRule";
        EquivalentClass rightSideOfRule = ontologyLabel.getLabelEquivalentClasses(rightPartOfRule, 1, 1);
        String ruleHead = "a"+ontologyLabel.getLabel(owlClass, 1);
        String rule= ruleHead+"(X1) "+Config.eq+" "+rightSideOfRule.getFinalRule();
        writeLineToFile(rule);
        cm.addTabledPredicateOntology(ruleHead + "/1");
    }

    public void writeNegEquivalentRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        currentRule ="%NegEquivalentRule";
        EquivalentClass rules= ontologyLabel.getLabelEquivalentClasses(classExpression, 1, 1);
        if(!(owlClass.isOWLThing() || owlClass.isOWLNothing()))
            rules.addRule(ontologyLabel.getLabel(owlClass, 1),1,1, EquivalentClass.OntologyType.ONTOLOGY);
        for(String rule : rules.getNegRules()){
            writeLineToFile(rule);
        }
        for (String rule : rules.getNegRulesHeadForTabling()) {
            cm.addTabledPredicateOntology(rule);
            addPredicateToSetPredicatesAppearedUnderNunderscore(rule);
        }

    }


    public void writeDoubledRules(OWLClassExpression classExpression, OWLClassExpression owlClass){
        currentRule ="%DoubledRule";
        EquivalentClass rules = ontologyLabel.getLabelEquivalentClasses(classExpression, 1, 1);
        String _owlClass=ontologyLabel.getLabel(owlClass, 1);
        writeLineToFile("a"+_owlClass+"(X1)"+ Utils.getEqForRule()+rules.getFinalRule());
        cm.addTabledPredicateOntology("a"+_owlClass + "/1");
        if(cm.isAnyDisjointStatement()){//if(isExistOntology(_owlClass)){
            String rule="d"+_owlClass+"(X1)"+Utils.getEqForRule()+rules.getDoubledRules();
            cm.addTabledPredicateOntology("d"+_owlClass + "/1");
            if(isPredicateAppearedInHeadUnderNunderscore("n"+_owlClass+"/1"))
                rule+=", " + Config.negation + " n" + _owlClass + "(X1)";
            writeLineToFile(rule+".");
        }
    }
    private void addPredicateToSetPredicatesAppearedUnderNunderscore(String s){
        cm.addPrediactesAppearedUnderNunderscore(s);
    }
    private boolean isPredicateAppearedInHeadUnderNunderscore(String predicate){
        return cm.isPrediactesAppearedUnderNunderscore(predicate);
    }
    private void writeLineToFile(String string){
        string += Config.ruleCreationDebug ? currentRule : "";
        cm.addTranslatedOntology(string);
    }

}
