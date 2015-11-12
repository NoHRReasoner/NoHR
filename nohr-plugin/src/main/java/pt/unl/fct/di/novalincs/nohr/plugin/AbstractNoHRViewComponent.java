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

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.HashSetProgram;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;

/**
 * An abstract NoHR {@link ViewComponent}. Provides methods to access the components underlying to the NoHR plugin: the considered {@link OWLOntology
 * ontology}, the considered {@link Program program}, the {@link HybridKB Hybrid KB}, and the {@link NoHRParser parser}.
 *
 * @author Nuno Costa
 */
public abstract class AbstractNoHRViewComponent extends AbstractOWLViewComponent {

	protected class DisposableHybridKB extends NoHRHybridKB implements Disposable {

		public DisposableHybridKB(File xsbBinDirectory, OWLOntology ontology, Program program,
				Vocabulary vocabularyMapping) throws OWLProfilesViolationsException, UnsupportedAxiomsException,
						PrologEngineCreationException {
			super(xsbBinDirectory, ontology, program, vocabularyMapping, null);
		}

		@Override
		public void dispose() {
			super.dispose();
		}

	}

	class DisposableProgram extends HashSetProgram implements Disposable {

		public DisposableProgram() {
			this(Collections.<Rule> emptySet());
		}

		DisposableProgram(Set<Rule> rules) {
			super(rules);
		}

		@Override
		public void dispose() throws Exception {
			super.clear();
		}

	}

	protected static final Logger log = Logger.getLogger(AbstractNoHRViewComponent.class);

	private static final long serialVersionUID = -2850791395194206722L;

	public AbstractNoHRViewComponent() {
		super();
	}

	/**
	 * Returns the {@link HybridKB} if it was started.
	 *
	 * @return the {@link HybridKB}.
	 * @throws NullPointerException
	 *             if the {@link HybridKB} hasn't been started yet.
	 */
	protected HybridKB getHybridKB() {
		final DisposableHybridKB hybridKB = getOWLModelManager().get(HybridKB.class);
		if (hybridKB == null)
			throw new NullPointerException();
		return hybridKB;
	}

	/**
	 * Returns the considered ontology, i.e. the ontology component of the {@link HybridKB}.
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
	 * Returns the considered {@link Program program}, i.e. the program component of the {@link HybridKB}.
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
	 * Returns the {@link ProgramPersistenceManager}.
	 *
	 * @return the {@link ProgramPersistenceManager}.
	 */
	protected ProgramPersistenceManager getProgramPersistenceManager() {
		DisposableObject<ProgramPersistenceManager> disposableObject = getOWLModelManager()
				.get(ProgramPersistenceManager.class);
		if (disposableObject == null) {
			disposableObject = new DisposableObject<ProgramPersistenceManager>(
					new ProgramPersistenceManager(getVocabulary()));
			getOWLModelManager().put(ProgramPersistenceManager.class, disposableObject);
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

	@Override
	protected void initialiseOWLView() throws Exception {

	}

	/**
	 * Checks whether the NoHR was started.
	 */
	protected boolean isNoHRStarted() {
		return getOWLModelManager().get(HybridKB.class) != null;
	}

	protected void reset() {
		log.info("Resetting...");
		getOWLModelManager().put(Vocabulary.class, new DisposableVocabulary(getOntology()));
		getParser().setVocabulary(getVocabulary());
		getProgramPersistenceManager().setVocabulary(getVocabulary());
	}

	/**
	 * Starts the NoHR, creating a {@link NoHRHybridKB}.
	 */
	protected void startNoHR() {
		log.info("starting NoHR");
		final File xsbBinDirectory = NoHRPreferences.getInstance().getXSBBinDirectory();
		if (xsbBinDirectory == null) {
			Messages.xsbBinDirectoryNotSet(this);
			return;
		}
		DisposableHybridKB disposableHybridKB = null;
		try {
			disposableHybridKB = new DisposableHybridKB(xsbBinDirectory, getOntology(), getProgram(), getVocabulary());
		} catch (final OWLProfilesViolationsException e) {
			log.warn("Violations to " + e.getReports());
			Messages.violations(this, e);
		} catch (final UnsupportedAxiomsException e) {
			log.warn("unsupported axioms: " + e.getUnsupportedAxioms());
			Messages.violations(this, e);
		} catch (final PrologEngineCreationException e) {
			log.error("can't create a xsb instance" + System.lineSeparator() + e.getCause() + System.lineSeparator()
					+ e.getCause().getStackTrace());
			Messages.xsbDatabaseCreationProblems(this, e);
		} catch (final RuntimeException e) {
			log.debug("Exception caught when trying to create the Hybrid KB", e);
		}
		if (disposableHybridKB != null)
			getOWLModelManager().put(HybridKB.class, disposableHybridKB);
	}

}
