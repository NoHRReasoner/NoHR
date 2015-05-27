package helpers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedOWLProfile;
import pt.unl.fct.di.centria.nohr.reasoner.translation.Translator;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.CollectionsManager;

public class Translation {

    boolean full;

    Set<String> arules;
    Set<String> drules;
    Set<String> nrules;

    KB kb;
    Set<OWLAxiom> axioms;

    OWLAxiom disjunction;
    String r1;
    String r2;

    boolean hasDisjunction;

    public Translation(KB kb, OWLAxiom axiom, Set<Rule> rules)
	    throws OWLOntologyCreationException {
	this(kb, new HashSet<OWLAxiom>(), true, rules);
	axioms = new HashSet<OWLAxiom>();
	axioms.add(axiom);
    }

    public Translation(KB kb, Set<OWLAxiom> axioms, boolean full,
	    Set<Rule> rules) throws OWLOntologyCreationException {
	this.axioms = axioms;
	this.full = full;
	this.kb = kb;
	arules = new HashSet<String>();
	drules = new HashSet<String>();
	nrules = new HashSet<String>();
	arules.add("dom(P)(X):-P(X,_).");
	arules.add("ran(P)(X):-P(_,X).");
	OWLClass a1 = kb.getConcept();
	OWLClass a2 = kb.getConcept();
	disjunction = kb.getDataFactory().getOWLDisjointClassesAxiom(a1, a2);
	r1 = kb.getRule("n%s(X):-a%s(X).", a1, a2);
	r2 = kb.getRule("n%s(X):-a%s(X).", a2, a1);
	for (Rule rule : rules) {
	    String r = kb.getRule(rule);
	    if (r.startsWith("a") || r.startsWith("dom(a")
		    || r.startsWith("ran(a"))
		arules.add(r);
	    else if (r.startsWith("d") || r.startsWith("dom(d")
		    || r.startsWith("ran(d"))
		drules.add(r);
	    else if (r.startsWith("n"))
		nrules.add(r);
	}
	hasDisjunction = hasDisjunction();
    }

    public Translation(KB kb, Set<OWLAxiom> axioms, Set<Rule> rules)
	    throws OWLOntologyCreationException {
	this(kb, axioms, true, rules);
    }

    private void check(String msg, Set<String> expected, Set<String> actual) {
	if (full)
	    Assert.assertEquals(msg, expected, actual);
	else
	    Assert.assertTrue(msg, actual.containsAll(expected));

    }

    private boolean hasDisjunction() {
	Iterator<OWLAxiom> axiomsIt = axioms.iterator();
	while (axiomsIt.hasNext()) {
	    OWLAxiom axiom = axiomsIt.next();
	    AxiomType<?> type = axiom.getAxiomType();
	    if (isComplement(axiom) || type == AxiomType.DISJOINT_CLASSES
		    || type == AxiomType.DISJOINT_OBJECT_PROPERTIES
		    || type == AxiomType.IRREFLEXIVE_OBJECT_PROPERTY
		    || type == AxiomType.ASYMMETRIC_OBJECT_PROPERTY)
		return true;
	}
	return false;
    }

    private boolean isComplement(OWLAxiom axiom) {
	if (axiom instanceof OWLSubClassOfAxiom) {
	    OWLSubClassOfAxiom s = (OWLSubClassOfAxiom) axiom;
	    return s.getSuperClass() instanceof OWLObjectComplementOf;
	}
	return false;
    }

    public void test() throws OWLOntologyCreationException,
    OWLOntologyStorageException, ParserException, UnsupportedOWLProfile {
	kb.clear();
	kb.add(axioms);
	Translator tr = new Translator(kb.getOntology(), null);
	tr.proceed();
	CollectionsManager cm = tr.getCollectionsManager();
	Set<String> result = cm.getTranslatedOntologies();
	Set<String> expected = new HashSet<String>(arules);
	if (hasDisjunction)
	    expected.addAll(drules);
	expected.addAll(nrules);
	check(axioms.toString(), expected, result);
	if (!hasDisjunction) {
	    kb.add(disjunction);
	    tr = new Translator(kb.getOntology(), null);
	    cm = tr.getCollectionsManager();
	    result = cm.getTranslatedOntologies();
	    cm = tr.getCollectionsManager();
	    result = cm.getTranslatedOntologies();
	    tr.proceed();
	    expected.addAll(drules);
	    expected.add(r1);
	    expected.add(r2);
	    result = cm.getTranslatedOntologies();
	    check(axioms.toString() + " with inverses", expected, result);
	}
    }

}