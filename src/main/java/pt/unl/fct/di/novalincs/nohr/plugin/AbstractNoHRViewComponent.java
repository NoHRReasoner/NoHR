/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.HybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.HashSetProgram;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ProgramPresistenceManager;

/**
 * An abstract NoHR {@link ViewComponent}. Provides methods to access the components underlying to the NoHR plugin: the considered {@link OWLOntology
 * ontology}, the considered {@link Program program}, the {@link HybridKB Hybrid KB}, and the {@link NoHRParser parser}.
 *
 * @author Nuno Costa
 */
public abstract class AbstractNoHRViewComponent extends AbstractOWLViewComponent implements OWLModelManagerListener {

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

	protected class DisposableObject<T> implements Disposable {

		private T object;

		public DisposableObject(T object) {
			this.object = object;
		}

		@Override
		public void dispose() throws Exception {
			object = null;
		}

		public T getObject() {
			return object;
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

	class DisposableVocabulary extends DefaultVocabulary implements Disposable {

		public DisposableVocabulary(OWLOntology ontology) {
			super(ontology);
		}

	}

	private static final Logger log = Logger.getLogger(AbstractNoHRViewComponent.class);

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
	 * Returns the {@link ProgramPresistenceManager}.
	 *
	 * @return the {@link ProgramPresistenceManager}.
	 */
	protected ProgramPresistenceManager getProgramPresistenceManager() {
		DisposableObject<ProgramPresistenceManager> disposableObject = getOWLModelManager()
				.get(ProgramPresistenceManager.class);
		if (disposableObject == null) {
			disposableObject = new DisposableObject<ProgramPresistenceManager>(
					new ProgramPresistenceManager(getVocabulary()));
			getOWLModelManager().put(ProgramPresistenceManager.class, disposableObject);
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
		if (getVocabulary().getOntology().equals(getOntology()))
			return;
		log.info("Resetting...");
		getOWLModelManager().put(Vocabulary.class, new DisposableVocabulary(getOntology()));
		getOWLModelManager().put(Program.class, new DisposableProgram());
		getOWLModelManager().put(NoHRParser.class,
				new DisposableObject<NoHRParser>(new NoHRRecursiveDescentParser(getVocabulary())));
		getOWLModelManager().put(ProgramPresistenceManager.class,
				new DisposableObject<ProgramPresistenceManager>(new ProgramPresistenceManager(getVocabulary())));
	}

	/**
	 * Starts the NoHR, creating a {@link NoHRHybridKB}.
	 */
	protected void startHybridKB() {
		log.info("starting NoHR");
		final File xsbBinDirectory = NoHRPreferences.getInstance().getXSBBinDirectory();
		if (xsbBinDirectory == null)
			Messages.xsbBinDirectoryNotSet(this);
		DisposableHybridKB disposableHybridKB = null;
		try {
			disposableHybridKB = new DisposableHybridKB(xsbBinDirectory, getOntology(), getProgram(), getVocabulary());
		} catch (final UnsupportedAxiomsException e) {
			Messages.violations(this, e);
		} catch (final PrologEngineCreationException e) {
			Messages.xsbDatabaseCreationProblems(this, e);
		} catch (final RuntimeException e) {
			if (log.isDebugEnabled())
				log.debug("Exception caught when trying to create the Hybrid KB", e);
		}
		if (disposableHybridKB != null)
			getOWLModelManager().put(HybridKB.class, disposableHybridKB);
	}

}
