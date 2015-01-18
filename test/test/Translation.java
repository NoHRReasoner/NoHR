package test;

import java.util.HashSet;
import java.util.Set;

import local.translate.CollectionsManager;
import local.translate.Translate;

import org.junit.Assert;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

class Translation {

	Set<String> arules;
	Set<String> drules;
	Set<String> nrules;

	KB kb;
	OWLAxiom axiom;
	OWLAxiom disjunction;
	String r1;
	String r2;

	public Translation(KB kb, OWLAxiom axiom, Rule... rules)
			throws OWLOntologyCreationException {
		this.axiom = axiom;
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
	}

	public void test() throws OWLOntologyCreationException,
			OWLOntologyStorageException, ParserException {
		kb.clear();
		kb.add(axiom);
		Translate tr = new Translate(kb.getOntology());
		tr.proceed();
		CollectionsManager cm = tr.getCollectionsManager();
		Set<String> result = cm.getTranslatedOntologies();
		Set<String> expected = new HashSet<String>(arules);
		expected.addAll(nrules);
		Assert.assertEquals(axiom.toString(), expected, result);
		if (!hasDisjunction()) {
			kb.add(disjunction);
			tr.proceed();
			expected.addAll(drules);
			expected.add(r1);
			expected.add(r2);
			result = cm.getTranslatedOntologies();
			Assert.assertEquals(axiom.toString() + " with inverses", expected,
					result);
		}
	}

	private boolean isComplement() {
		if (axiom instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom s = (OWLSubClassOfAxiom) axiom;
			return s.getSuperClass() instanceof OWLObjectIntersectionOf;
		}
		return true;
	}

	private boolean hasDisjunction() {
		AxiomType<?> type = axiom.getAxiomType();
		return isComplement() || type == AxiomType.DISJOINT_CLASSES
				|| type == AxiomType.DISJOINT_OBJECT_PROPERTIES
				|| type == AxiomType.IRREFLEXIVE_OBJECT_PROPERTY
				|| type == AxiomType.ASYMMETRIC_OBJECT_PROPERTY;
	}

}