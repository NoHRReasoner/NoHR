package pt.unl.fct.di.novalincs.nohr.plugin;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLEditorKitHook;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RuleListModel;

public class NoHREditorKitHook extends OWLEditorKitHook implements OWLModelManagerListener {

	protected static final Logger log = Logger.getLogger(NoHREditorKitHook.class);

	@Override
	public void dispose() throws Exception {
		getEditorKit().getOWLModelManager().removeListener(this);
		final RuleListModel ruleListModel = getRuleListModel();
		if (ruleListModel != null)
			ruleListModel.clear();
		log.info("NoHR disposed");
	}

	private RuleListModel getRuleListModel() {
		final DisposableObject<RuleListModel> disposableRuleListModel = getEditorKit().getOWLModelManager()
				.get(RuleListModel.class);
		if (disposableRuleListModel == null)
			return null;
		return disposableRuleListModel.getObject();
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent ev) {
		if (ev.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
			final RuleListModel ruleListModel = getRuleListModel();
			if (ruleListModel != null) {
				ruleListModel.clear();
				reset();
			}
		}
	}

	@Override
	public void initialise() throws Exception {
		getEditorKit().getOWLModelManager().addListener(this);
		log.info("NoHR initialised");
	}

	private void reset() {
		log.info("NoHR reseted");
		final OWLModelManager modelManager = getEditorKit().getOWLModelManager();
		final DisposableVocabulary vocabulary = new DisposableVocabulary(modelManager.getActiveOntology());
		modelManager.put(Vocabulary.class, vocabulary);
		final DisposableObject<NoHRParser> disposableParser = modelManager.get(NoHRParser.class);
		final DisposableObject<ProgramPresistenceManager> disposablePresistenceManager = modelManager
				.get(ProgramPresistenceManager.class);
		disposableParser.getObject().setVocabulary(vocabulary);
		disposablePresistenceManager.getObject().setVocabulary(vocabulary);
	}

}
