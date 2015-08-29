package pt.unl.fct.di.centria.nohr.deductivedb;

import static pt.unl.fct.di.centria.nohr.model.concrete.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.var;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.TermModel;

import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;
import pt.unl.fct.di.centria.nohr.model.VocabularyMapping;

/**
 * Convert {@link TermModel term models} obtained from a {@link PrologEngine Prolog engine} to {@link Terms terms}.
 *
 * @author Nuno Costa
 */
public class TermModelConverter {

	private static String unquote(String str) {
		return str.replaceAll("''", "'");
	}

	private final VocabularyMapping vocabularyMapping;

	public TermModelConverter(VocabularyMapping vocabularyMapping) {
		this.vocabularyMapping = vocabularyMapping;
	}

	/**
	 * Convert a given {@link TermModel} to the corresponding {@link Term}.
	 *
	 * @param termModel
	 *            the term model.
	 * @return the {@link Term term} corresponding to {@code termModel}.
	 */
	public Term term(TermModel termModel) {
		if (termModel.isAtom())
			return cons(unquote(termModel.toString()), vocabularyMapping);
		else if (termModel.isNumber()) {
			final Number number = termModel.intValue();
			return cons(number);
		} else if (termModel.isVar())
			return var(termModel.toString());
		else
			throw new ClassCastException();

	}

	/**
	 * Convert a given {@link TermModel} to the corresponding {@link TruthValue} if it represents a {@link TruthValue truth value}.
	 *
	 * @param termModel
	 *            the term model.
	 * @return the {@link Term term} corresponding to {@code termModel}.
	 * @throws ClassCastException
	 *             if {@code termModel} doesn't represent a {@link TruthValue truth value}.
	 */
	public TruthValue truthValue(TermModel termModel) {
		final String valStr = termModel.toString();
		if (valStr.equals("true"))
			return TruthValue.TRUE;
		else if (valStr.equals("undefined"))
			return TruthValue.UNDEFINED;
		else
			throw new ClassCastException();
	}
}