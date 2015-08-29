/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin;

import java.io.File;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import pt.unl.fct.di.centria.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.centria.nohr.model.DefaultVocabularyMapping;
import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;
import pt.unl.fct.di.centria.nohr.model.concrete.Model;
import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;
import pt.unl.fct.di.centria.nohr.parsing.NoHRRecursiveDescentParser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKBImpl;
import pt.unl.fct.di.centria.nohr.reasoner.OWLProfilesViolationsException;
import pt.unl.fct.di.centria.nohr.reasoner.UnsupportedAxiomsException;

/**
 * An abstract NoHR {@link ViewComponent}. Provides methods to access the components underlying to the NoHR plugin: the considered {@link OWLOntology
 * ontology}, the considered {@link Program program}, the {@link HybridKB Hybrid KB}, and the {@link NoHRParser parser}.
 *
 * @author Nuno Costa
 */
public abstract class AbstractNoHRViewComponent extends AbstractOWLViewComponent implements OWLModelManagerListener {

	protected class DisposableHybridKB extends HybridKBImpl implements Disposable {

		private final OWLModelManagerListener modelManagerListener;

		public DisposableHybridKB(File xsbBinDirectory, OWLOntology ontology, Program program,
				VocabularyMapping vocabularyMapping, OWLModelManagerListener modelListener)
						throws OWLProfilesViolationsException, UnsupportedAxiomsException,
						PrologEngineCreationException {
			super(xsbBinDirectory, ontology, program, vocabularyMapping, null);
			modelManagerListener = modelListener;
		}

		@Override
		public void dispose() {
			super.dispose();
			getOWLModelManager().removeListener(modelManagerListener);
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

	private static final Logger log = Logger.getLogger(AbstractNoHRViewComponent.class);

	private static final long serialVersionUID = -2850791395194206722L;

	public AbstractNoHRViewComponent() {
		super();
	}

	@Override
	protected void disposeOWLView() {

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
			disposableObject = new DisposableObject<NoHRParser>(new NoHRRecursiveDescentParser(getVocabularyMapping()));
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
		DisposableObject<Program> program = getOWLModelManager().get(Program.class);
		if (program == null) {
			program = new DisposableObject<Program>(Model.program());
			getOWLModelManager().put(Program.class, program);
		}
		return program.getObject();
	}

	protected VocabularyMapping getVocabularyMapping() {
		DisposableObject<VocabularyMapping> disposableObject = getOWLModelManager().get(VocabularyMapping.class);
		if (disposableObject == null) {
			disposableObject = new DisposableObject<VocabularyMapping>(new DefaultVocabularyMapping(getOntology()));
			getOWLModelManager().put(VocabularyMapping.class, disposableObject);
		}
		return disposableObject.getObject();
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
			getOWLModelManager().put(VocabularyMapping.class,
					new DisposableObject<VocabularyMapping>(new DefaultVocabularyMapping(getOntology())));
			getOWLModelManager().put(NoHRParser.class,
					new DisposableObject<NoHRParser>(new NoHRRecursiveDescentParser(getVocabularyMapping())));
			startNoHR();
		}

	}

	/**
	 * Checks whether the NoHR was started.
	 */
	protected boolean isNoHRStarted() {
		return getOWLModelManager().get(HybridKB.class) != null;
	}

	/**
	 * Starts the NoHR, creating a {@link HybridKBImpl}.
	 */
	protected void startNoHR() {
		final File xsbBinDirectory = NoHRPreferences.getInstance().getXSBBinDirectory();
		if (xsbBinDirectory == null)
			Messages.xsbBinDirectoryNotSet(this);
		DisposableHybridKB disposableHybridKB = null;
		try {
			disposableHybridKB = new DisposableHybridKB(xsbBinDirectory, getOntology(), getProgram(),
					getVocabularyMapping(), this);
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
