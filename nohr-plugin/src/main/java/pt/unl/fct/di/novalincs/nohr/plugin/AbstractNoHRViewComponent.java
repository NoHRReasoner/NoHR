/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.HashSetDBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.HashSetProgram;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;

/**
 * An abstract NoHR {@link ViewComponent}. Provides methods to access the
 * components underlying to the NoHR plugin: the considered {@link OWLOntology
 * ontology}, the considered {@link Program program}, the
 * {@link HybridKB Hybrid KB}, and the {@link NoHRParser parser}.
 *
 * @author Nuno Costa
 */
public abstract class AbstractNoHRViewComponent extends AbstractOWLViewComponent {

    class DisposableProgram extends HashSetProgram implements Disposable {

        public DisposableProgram() {
            this(Collections.<Rule>emptySet());
        }

        DisposableProgram(Set<Rule> rules) {
            super(rules);
        }

        @Override
        public void dispose() throws Exception {
            super.clear();
        }

    }
    
    class DisposableDBMappingSet extends HashSetDBMappingSet implements Disposable {

        public DisposableDBMappingSet() {
            this(Collections.<DBMapping>emptySet());
        }

        DisposableDBMappingSet(Set<DBMapping> dbMappings) {
            super(dbMappings);
        }

        @Override
        public void dispose() throws Exception {
            super.clear();
        }

    }
    

    protected static final Logger LOG = Logger.getLogger(AbstractNoHRViewComponent.class);

    private static final long serialVersionUID = -2850791395194206722L;

    public AbstractNoHRViewComponent() {
        super();
    }

    /**
     * Returns the {@link HybridKB} if it was started.
     *
     * @return the {@link HybridKB}.
     * @throws NullPointerException if the {@link HybridKB} hasn't been started
     * yet.
     */
    protected HybridKB getHybridKB() {
        if (!NoHRInstance.getInstance().isStarted()) {
            throw new NullPointerException();
        }

        return NoHRInstance.getInstance().getHybridKB();
    }

    /**
     * Returns the considered ontology, i.e. the ontology component of the
     * {@link HybridKB}.
     *
     * @return the considered ontology.
     */
    protected OWLOntology getOntology() {
        return getOWLModelManager().getActiveOntology();
    }

    /**
     * Returns the {@link NoHRParser}.
     *
     * @return the {@link NoHRParser}.
     */
    protected NoHRParser getParser() {
        DisposableObject<NoHRParser> disposableObject = getOWLModelManager().get(NoHRParser.class);

        if (disposableObject == null) {
            disposableObject = new DisposableObject<NoHRParser>(new NoHRRecursiveDescentParser(getVocabulary()));
            getOWLModelManager().put(NoHRParser.class, disposableObject);
        }

        return disposableObject.getObject();
    }

    /**
     * Returns the considered {@link Program program}, i.e. the program
     * component of the {@link HybridKB}.
     *
     * @return the considered program.
     */
    protected Program getProgram() {
        DisposableProgram program = getOWLModelManager().get(Program.class);

        if (program == null) {
            program = new DisposableProgram();
            getOWLModelManager().put(Program.class, program);
        }

        return program;
    }
    
    /**
     * Returns the considered {@link DBMappingSet dbMappingSet}, i.e. the program
     * component of the {@link HybridKB}.
     *
     * @return the considered program.
     */
    protected DBMappingSet getDBMappingSet() {
        DisposableDBMappingSet dbMappingSet = getOWLModelManager().get(DBMappingSet.class);

        if (dbMappingSet == null) {
        	dbMappingSet = new DisposableDBMappingSet();
            getOWLModelManager().put(DBMappingSet.class, dbMappingSet);
        }

        return dbMappingSet;
    }
    

    /**
     * Returns the {@link ProgramPersistenceManager}.
     *
     * @return the {@link ProgramPersistenceManager}.
     */
    protected ProgramPersistenceManager getProgramPersistenceManager() {
        DisposableObject<ProgramPersistenceManager> disposableObject = getOWLModelManager().get(ProgramPersistenceManager.class);

        if (disposableObject == null) {
            disposableObject = new DisposableObject<>(new ProgramPersistenceManager(getVocabulary()));
            getOWLModelManager().put(ProgramPersistenceManager.class, disposableObject);
        }

        return disposableObject.getObject();
    }
    
    /**
     * Returns the {@link ProgramPersistenceManager}.
     *
     * @return the {@link ProgramPersistenceManager}.
     */
    protected DBMappingSetPersistenceManager getDBMappingSetPersistenceManager() {
        DisposableObject<DBMappingSetPersistenceManager> disposableObject = getOWLModelManager().get(DBMappingSetPersistenceManager.class);

        if (disposableObject == null) {
            disposableObject = new DisposableObject<>(new DBMappingSetPersistenceManager(getVocabulary()));
            getOWLModelManager().put(DBMappingSetPersistenceManager.class, disposableObject);
        }

        return disposableObject.getObject();
    }

    protected Vocabulary getVocabulary() {
        DisposableVocabulary vocabulary = getOWLModelManager().get(Vocabulary.class);

        if (vocabulary == null) {
            vocabulary = new DisposableVocabulary(getOntology());
            getOWLModelManager().put(Vocabulary.class, vocabulary);
        }

        return vocabulary;
    }

    /**
     * Checks whether the NoHR was started.
     */
    protected boolean isNoHRStarted() {
        return NoHRInstance.getInstance().isStarted();
    }

    protected void reset() {
        LOG.info("Resetting NoHR");

        getOWLModelManager().put(Vocabulary.class, new DisposableVocabulary(getOntology()));
        getParser().setVocabulary(getVocabulary());
        getProgramPersistenceManager().setVocabulary(getVocabulary());
    }

    /**
     * Starts the NoHR, creating a {@link NoHRHybridKB}.
     */
    protected void startNoHR() {
        NoHRHybridKBConfiguration config = NoHRPreferences.getInstance().getConfiguration();

        if (config.getXsbDirectory() == null) {
            Messages.xsbBinDirectoryNotSet(this);
            return;
        }

        if (config.getRdf2xDirectory() == null){
            Messages.rdfDirectoryNotSet(this);
            return;
        }


        try {
            NoHRInstance.getInstance().start(config, getOntology(), getProgram(), getDBMappingSet(), getVocabulary());
        } catch (final OWLProfilesViolationsException e) {
            LOG.warn("Violations to " + e.getReports());
            Messages.violations(this, e);
        } catch (final UnsupportedAxiomsException e) {
            LOG.warn("unsupported axioms: " + e.getUnsupportedAxioms());
            Messages.violations(this, e);
        } catch (final PrologEngineCreationException e) {
            LOG.error("can't create a xsb instance" + System.lineSeparator() + e.getCause() + System.lineSeparator() + Arrays.toString(e.getCause().getStackTrace()));
            Messages.xsbDatabaseCreationProblems(this, e);
        } catch (final RuntimeException e) {
            LOG.debug("Exception caught when trying to create the Hybrid KB", e);
        }
    }
}
