package pt.unl.fct.di.novalincs.nohr.plugin;

import org.protege.editor.core.Disposable;
import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

class DisposableHybridKB extends NoHRHybridKB implements Disposable {

    public DisposableHybridKB(final NoHRHybridKBConfiguration configuration, OWLOntology ontology, Program program, DBMappingSet dbMappingsSet,
            Vocabulary vocabularyMapping) throws OWLProfilesViolationsException, UnsupportedAxiomsException,
            PrologEngineCreationException {
        super(configuration, ontology, program, dbMappingsSet, vocabularyMapping, null);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
