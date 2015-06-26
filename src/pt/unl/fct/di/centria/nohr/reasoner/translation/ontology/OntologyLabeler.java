package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import other.Config;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

//TODO remove

/**
 * The Class OntologyLabel. Parsing the properties to get label
 */
public class OntologyLabeler {
    /** The _ontology. */
    private static OWLOntology ontology;

    /** The _ontology ID. */
    private static String ontologyID;

    public static String escapeAtom(String atom) {
	return atom.replaceAll("'", "''");
    }

    public static String unescapeAtom(String atom) {
	return atom.replaceAll("''", "'");
    }

    /** The ontology annotation property. */
    private final OWLAnnotationProperty ontologyLabel;

    /**
     * Instantiates a new ontology label.
     *
     * @param ont
     *            the ont
     * @param annotationProperty
     *            the annotation property
     * @param manager
     *            the manager
     */
    public OntologyLabeler(OWLOntology ont,
	    OWLAnnotationProperty annotationProperty) {
	ontology = ont;
	ontologyLabel = annotationProperty;
	ontologyID = getOntologyID();
    }

    /**
     * Gets the label.
     *
     * @param entity
     *            the axiom
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLAxiom entity, int numInList) {
	if (entity instanceof OWLSubPropertyChainAxiomImpl) {
	    final List<OWLObjectPropertyExpression> properties = ((OWLSubPropertyChainAxiomImpl) entity)
		    .getPropertyChain();
	    if (properties != null)
		if (properties.size() >= numInList)
		    return getLabel(properties.get(numInList - 1), 1);
		else
		    return getLabel(
			    ((OWLSubPropertyChainAxiomImpl) entity)
			    .getSuperProperty(),
			    1);
	} else if (entity instanceof OWLObjectPropertyAssertionAxiomImpl)
	    switch (numInList) {
	    case 1:
		return getLabel(
			((OWLObjectPropertyAssertionAxiomImpl) entity)
			.getProperty(),
			1);
	    case 2:
		return getLabel(
			((OWLObjectPropertyAssertionAxiomImpl) entity)
			.getSubject(),
			1);
	    case 3:
		return getLabel(
			((OWLObjectPropertyAssertionAxiomImpl) entity)
			.getObject(),
			1);
	    }
	return getLabel(entity.toString(), numInList);
    }

    /**
     * Gets the label.
     *
     * @param owlClass
     *            the owl class
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLClass owlClass, int numInList) {
	return getLabel(owlClass.getAnnotations(ontology, ontologyLabel),
		owlClass.toString(), numInList);
    }

    /**
     * Gets the label.
     *
     * @param owlClass
     *            the owl class expression
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLClassExpression owlClass, int numInList) {
	return getLabel(owlClass.asOWLClass(), numInList);
    }

    /**
     *
     * @param dataProperty
     * @param numInList
     * @return (hashed) label of the data property
     */
    public String getLabel(OWLDataProperty dataProperty, int numInList) {
	return getLabel(dataProperty.getAnnotations(ontology, ontologyLabel),
		dataProperty.toString(), numInList);
    }

    /**
     * Gets the label.
     *
     * @param entity
     *            the ontology entity
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLEntity entity, int numInList) {
	return getLabel(entity.getAnnotations(ontology, ontologyLabel),
		entity.toString(), numInList);
    }

    /**
     * Gets the label.
     *
     * @param member
     *            the owl individual
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLIndividual member, int numInList) {
	if (member instanceof OWLNamedIndividual)
	    for (final OWLEntity entity : member.getSignature())
		return getLabel(entity, 1);
	return getLabel((OWLClass) member, numInList);
    }

    /**
     * Gets the label.
     *
     * @param objectProperty
     *            the object property
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLObjectProperty objectProperty, int numInList) {
	return getLabel(objectProperty.getAnnotations(ontology, ontologyLabel),
		objectProperty.toString(), numInList);
    }

    /**
     * Gets the label.
     *
     * @param property
     *            the object property expression
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(OWLObjectPropertyExpression property, int numInList) {
	return getLabel(property.asOWLObjectProperty(), numInList);
    }

    /**
     * Gets the label.
     *
     * @param annotations
     *            the annotations
     * @param label
     *            the label
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(Set<OWLAnnotation> annotations, String label,
	    int numInList) {
	String message = "";
	if (annotations != null && annotations.size() > 0)
	    for (final OWLAnnotation annotation : annotations)
		message += annotation.getValue();
	if (message.length() > 0)
	    return getLabel(message);// message;//replaceSymbolsInRule("\""+message.replace("^^xsd:string","").replace(",","").replace(":-","").replace("'","").replace("\"","")+"\"");//
	// message.replaceAll("'","").replaceAll("\"","'");
	return getLabel(label, numInList);
    }

    /**
     * Gets the label.
     *
     * @param originalLabel
     *            the original label
     * @return the label
     */
    private String getLabel(String originalLabel) {
	originalLabel = originalLabel.replace("^^xsd:string", "");
	if (originalLabel.startsWith("\"") && originalLabel.endsWith("\""))
	    originalLabel = originalLabel.substring(1,
		    originalLabel.length() - 1);
	return escapeAtom(originalLabel);
    }

    /**
     * Gets the label.
     *
     * @param rule
     *            the rule
     * @param numInList
     *            the num in list
     * @return the label
     */
    public String getLabel(String rule, int numInList) {
	if (ontologyID.length() > 0)
	    rule = rule.replace(ontologyID, "");
	try {
	    String result;
	    if (rule.contains(Config.delimeter))
		result = rule.split(Config.delimeter)[numInList].split(">")[0];
	    else if (rule.contains("<http://")) {
		// String[] split = rule.split("/");
		// result = split[split.length-1];
		result = rule.replaceFirst("<http://", "");
		result = result.replaceAll(">", "");
		result = result.replaceAll("/", "");
		result = result.replaceAll("\\.", "");
		if (Character.isUpperCase(result.charAt(0)))
		    result = result.substring(0, 1).toLowerCase()
		    + result.substring(1);
	    } else if (rule.contains(Config.altDelimeter))
		result = rule.split(Config.altDelimeter)[numInList].split(">")[0];
	    else if (rule.startsWith("<"))
		result = rule.replaceFirst("<", "").replace(">", "");
	    else {
		result = "";
		throw new ParseException(rule, 0);
	    }

	    return getLabel(result);// result;//replaceSymbolsInRule(result);
	} catch (final Exception e) {
	    printLog("------------------------------------------------------------------------");
	    printLog(rule);
	    // printLog(_currentRule);
	    printLog(Integer.toString(numInList));
	    printLog("------------------------------------------------------------------------");
	    printLog(e.toString());
	    e.printStackTrace();
	}
	return getLabel(rule);
    }

    /**
     * Gets the label equivalent classes.
     *
     * @param owlClassExpression
     *            the owl class expression
     * @param localIterator
     *            the local iterator
     * @param iterator
     *            the iterator
     * @return the label equivalent classes
     */
    public EquivalentClass getLabelEquivalentClasses(
	    OWLClassExpression owlClassExpression, int localIterator,
	    int iterator) {
	final EquivalentClass equivalentClass = new EquivalentClass(iterator);
	switch (owlClassExpression.getClassExpressionType()) {
	case OWL_CLASS: {
	    if (!(owlClassExpression.isOWLThing() || owlClassExpression
		    .isOWLNothing()))
		equivalentClass.addRule(getLabel(owlClassExpression, 1),
			localIterator, iterator,
			EquivalentClass.OntologyType.ONTOLOGY);
	    break;
	}
	case OBJECT_INTERSECTION_OF: {
	    final List<OWLClassExpression> operands = ((OWLObjectIntersectionOf) owlClassExpression)
		    .getOperandsAsList();
	    for (final OWLClassExpression operand : operands)
		equivalentClass.updateClass(getLabelEquivalentClasses(operand,
			localIterator, equivalentClass.getVariableIterator()));
	    break;
	}
	case OBJECT_SOME_VALUES_FROM: {
	    final OWLClassExpression classExpression = ((OWLObjectSomeValuesFromImpl) owlClassExpression)
		    .getFiller();
	    final OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFromImpl) owlClassExpression)
		    .getProperty();

	    equivalentClass.addRule(getLabel(property, 1), localIterator,
		    equivalentClass.incrementIterator(),
		    EquivalentClass.OntologyType.RULE);
	    equivalentClass.updateClass(getLabelEquivalentClasses(
		    classExpression, ++localIterator,
		    equivalentClass.getVariableIterator()));
	    break;
	}
	default:
	    break;

	}
	return equivalentClass;
    }

    /**
     * Gets the ontology id.
     *
     * @return the ontology id
     */
    private String getOntologyID() {
	try {
	    final String _ = ontology.getOntologyID().getOntologyIRI()
		    .toString();
	    return _.contains("/") ? _.substring(0, _.lastIndexOf("/")) + "/"
		    : "";

	} catch (final NullPointerException e) {
	    return "";
	}

    }

    /**
     * Prints the log.
     *
     * @param log
     *            the log
     */
    public void printLog(String log) {
	System.out.println(log);
    }

}
