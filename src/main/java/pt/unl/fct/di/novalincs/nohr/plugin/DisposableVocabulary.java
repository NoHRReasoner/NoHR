package pt.unl.fct.di.novalincs.nohr.plugin;

import org.protege.editor.core.Disposable;
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;

class DisposableVocabulary extends DefaultVocabulary implements Disposable {

	public DisposableVocabulary(OWLOntology ontology) {
		super(ontology);
	}

}