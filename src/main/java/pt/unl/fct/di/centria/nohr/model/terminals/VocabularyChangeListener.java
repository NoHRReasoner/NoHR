package pt.unl.fct.di.centria.nohr.model.terminals;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Predicate;

public interface VocabularyChangeListener {

	void constantChanged(Constant constant);

	void predicateChanged(Predicate predicate);

}
