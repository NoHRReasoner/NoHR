package pt.unl.fct.di.centria.nohr.xsb;

import static pt.unl.fct.di.centria.nohr.model.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.Model.list;
import static pt.unl.fct.di.centria.nohr.model.Model.var;

import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.TruthValue;

import com.declarativa.interprolog.TermModel;

public class TermModelAdapter {

    public static Literal getLiteral(TermModel termModel) {
	// TODO implment
	return null;
    };

    public static Rule getRule(TermModel termModel) {
	// TODO implment
	return null;
    }

    public static Term getTerm(TermModel termModel) {
	if (termModel.isAtom())
	    return cons(unquote(termModel.toString()));
	else if (termModel.isNumber()) {
	    final Number number = termModel.intValue();
	    return cons(number);
	} else if (termModel.isVar())
	    return var(termModel.toString());
	else if (termModel.isList()) {
	    final List<Term> termList = new LinkedList<Term>();
	    for (final TermModel tm : termModel.flatList())
		termList.add(getTerm(tm));
	    return list(termList);
	} else
	    throw new ClassCastException();
    }

    public static TruthValue getTruthValue(TermModel termModel) {
	final String valStr = termModel.toString();
	if (valStr.equals("true"))
	    return TruthValue.TRUE;
	else if (valStr.equals("undefined"))
	    return TruthValue.UNDEFINED;
	else if (valStr.equals("false"))
	    return TruthValue.FALSE;
	else
	    throw new ClassCastException();
    }

    public static String unquote(String str) {
	return str.replaceAll("''", "'");
    }
}