package local.translate;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

public class OntologyLabel{
	/** The _ontology ID. */
    private static String ontologyID;
    private OWLAnnotationProperty ontologyLabel;
    /** The _ontology. */
    private static OWLOntology ontology;

    private CollectionsManager cm;
	public OntologyLabel(OWLOntology ont, OWLAnnotationProperty annotationProperty, CollectionsManager manager){
		ontology = ont;
		ontologyLabel = annotationProperty;
        cm = manager;
		ontologyID = getOntologyID();
	}
	
	public String getLabel(String rule, int numInList){
        if(ontologyID.length()>0)
            rule = rule.replace(ontologyID,"");
        try{
            String result;
            if(rule.contains(Config.delimeter))
                result=(rule.split(Config.delimeter)[numInList]).split(">")[0];
            else if (rule.contains(Config.altDelimeter))
                result=(rule.split(Config.altDelimeter)[numInList]).split(">")[0];
            else if(rule.startsWith("<"))
                result = rule.replaceFirst("<","").replace(">","");
            else
                result="";

            return getLabel(result);//result;//replaceSymbolsInRule(result);
        }catch (Exception e){
            printLog("------------------------------------------------------------------------");
            printLog(rule);
            //printLog(_currentRule);
            printLog(Integer.toString(numInList));
            printLog("------------------------------------------------------------------------");
            printLog(e.toString());
        }
        return getLabel(rule);
    }
	public String getLabel(OWLObjectPropertyExpression property, int numInList) {
        return getLabel(property.asOWLObjectProperty(), numInList);
    }
	public String getLabel(OWLObjectProperty objectProperty, int numInList) {
        return getLabel(objectProperty.getAnnotations(ontology, ontologyLabel), objectProperty.toString(), numInList);
    }
	public String getLabel(OWLIndividual member, int numInList) {
//        return getLabel(member.get  getAnnotations(_ontologyLabel), entity.toString(), numInList);
        if(member instanceof OWLNamedIndividual)                      {
            for(OWLEntity entity: member.getSignature()){
                return getLabel(entity, 1);
            }
        }
        return getLabel(((OWLClass) member), numInList);
    }
	public String getLabel(OWLEntity entity, int numInList) {
        return getLabel(entity.getAnnotations(ontology, ontologyLabel), entity.toString(), numInList);
    }
	public String getLabel(OWLAxiom entity, int numInList) {
        if(entity instanceof OWLSubPropertyChainAxiomImpl){
            List<OWLObjectPropertyExpression> properties = ((OWLSubPropertyChainAxiomImpl) entity).getPropertyChain();
            if(properties!=null){
                if(properties.size()>=numInList)
                    return getLabel(properties.get(numInList - 1), 1);
                else
                    return getLabel(((OWLSubPropertyChainAxiomImpl) entity).getSuperProperty(), 1);
            }
        }else if(entity instanceof OWLObjectPropertyAssertionAxiomImpl){
            switch (numInList){
                case 1:
                    return getLabel(((OWLObjectPropertyAssertionAxiomImpl) entity).getProperty(), 1);
                case 2:
                    return getLabel(((OWLObjectPropertyAssertionAxiomImpl) entity).getSubject(), 1);
                case 3:
                    return getLabel(((OWLObjectPropertyAssertionAxiomImpl) entity).getObject(), 1);
            }
        }
        return getLabel(entity.toString(), numInList);
        //return getLabel(entity.getAnnotations(_ontologyLabel), entity.toString(), numInList);
    }
	public String getLabel(OWLClass owlClass, int numInList) {
        return getLabel(owlClass.getAnnotations(ontology, ontologyLabel), owlClass.toString(), numInList);
    }
	public String getLabel(OWLClassExpression owlClass, int numInList) {
        return getLabel(owlClass.asOWLClass(), numInList);
    }
	public String getLabel(Set<OWLAnnotation> annotations, String label, int numInList) {
        String message="";
        if(annotations!=null && annotations.size()>0)   {
            for (OWLAnnotation annotation : annotations) {
                message += annotation.getValue();
                        /*if (annotation.getValue() instanceof OWLLiteral) {
                            OWLLiteral val = (OWLLiteral) annotation.getValue();
                            message += val.getLiteral();
                        }*/
            }
        }
        if(message.length()>0)
            return getLabel(message);//message;//replaceSymbolsInRule("\""+message.replace("^^xsd:string","").replace(",","").replace(":-","").replace("'","").replace("\"","")+"\"");//  message.replaceAll("'","").replaceAll("\"","'");
        return getLabel(label, numInList);
    }
    private String getLabel(String originalLabel){
        originalLabel =originalLabel.replace("^^xsd:string","");
        if(originalLabel.startsWith("\"") && originalLabel.endsWith("\""))
            originalLabel = originalLabel.substring(1, originalLabel.length()-1);
        return cm.getHashedLabel(originalLabel);
    }

    public EquivalentClass getLabelEquivalentClasses(OWLClassExpression owlClassExpression, int localIterator, int iterator){
        EquivalentClass equivalentClass = new EquivalentClass(iterator);
        //OWLLabelAnnotation a = (OWLLabelAnnotation)owlClassExpression;
        switch (owlClassExpression.getClassExpressionType()){
            case OWL_CLASS:{
                if(!(owlClassExpression.isOWLThing() || owlClassExpression.isOWLNothing()))
                    equivalentClass.addRule(
                            getLabel(owlClassExpression, 1),
                            localIterator,
                            iterator,
                            EquivalentClass.OntologyType.ONTOLOGY
                    );
                break;
            }
            case OBJECT_INTERSECTION_OF:{
                List<OWLClassExpression> operands = ((OWLObjectIntersectionOf) owlClassExpression).getOperandsAsList();
                for (OWLClassExpression operand : operands) {
                    equivalentClass.updateClass(getLabelEquivalentClasses(operand, localIterator, equivalentClass.getVariableIterator()));
                }
                break;
            }
            case OBJECT_SOME_VALUES_FROM:{
                OWLClassExpression classExpression = ((OWLObjectSomeValuesFromImpl) owlClassExpression).getFiller();
                OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFromImpl) owlClassExpression).getProperty();

                equivalentClass.addRule(getLabel(property, 1), localIterator, equivalentClass.incrementIterator(), EquivalentClass.OntologyType.RULE);
                equivalentClass.updateClass(getLabelEquivalentClasses(classExpression, ++localIterator, equivalentClass.getVariableIterator()));
                break;
            }
            default:
                break;

        }
        return equivalentClass;
    }

    private String getOntologyID(){
    	try{
    		String _ = ontology.getOntologyID().getOntologyIRI().toString();
            return _.contains("/") ? _.substring(0, _.lastIndexOf("/")) + "/" : "";
            
    	}catch(NullPointerException e){
    		return "";
    	}
    	
    }
	public void printLog(String log){
		System.out.println(log);
	}


}
