package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;

/**
 * {@link Vocabulary} changes listener.
 */
public interface VocabularyChangeListener {

	/**
	 * Called when an constant was changed.
	 *
	 * @param constant
	 *            the constant that changed.
	 */
	void constantChanged(Constant constant);

	/**
	 * Called when a predicate was changed.
	 *
	 * @param constant
	 *            the constant that changed.
	 */
	void predicateChanged(Predicate predicate);

}
