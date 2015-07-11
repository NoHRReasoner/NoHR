package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

//TODO remove

/**
 * The Class OntologyLabel. Parsing the properties to get label
 */
public class OntologyLabeler {
    /** The alt delimeter. */
    public static String altDelimeter = ":";

    /** The delimeter. */
    public static String delimeter = "#";

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
	final Set<OWLAnnotation> classAnnotations = labelAnnotations(owlClass);
	return getLabel(classAnnotations, owlClass.toString(), numInList);
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
	final Set<OWLAnnotation> dataPropertyAnnotations = labelAnnotations(dataProperty);
	return getLabel(dataPropertyAnnotations, dataProperty.toString(),
		numInList);
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
	final Set<OWLAnnotation> entityAnnotatoions = labelAnnotations(entity);
	return getLabel(entityAnnotatoions, entity.toString(), numInList);
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
	final Set<OWLAnnotation> opAnnots = labelAnnotations(objectProperty);
	return getLabel(opAnnots, objectProperty.toString(), numInList);
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
	    if (rule.contains(OntologyLabeler.delimeter))
		result = rule.split(OntologyLabeler.delimeter)[numInList]
			.split(">")[0];
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
	    } else if (rule.contains(OntologyLabeler.altDelimeter))
		result = rule.split(OntologyLabeler.altDelimeter)[numInList]
			.split(">")[0];
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

    private Set<OWLAnnotation> labelAnnotations(OWLEntity subject) {
	final Set<OWLAnnotation> result = new HashSet<OWLAnnotation>();
	for (final OWLAnnotationAssertionAxiom annotationAssertion : ontology
		.getAnnotationAssertionAxioms(subject.getIRI()))
	    if (annotationAssertion.getProperty().equals(ontologyLabel))
		result.add(annotationAssertion.getAnnotation());
	return result;
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
