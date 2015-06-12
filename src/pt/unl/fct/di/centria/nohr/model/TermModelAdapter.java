package pt.unl.fct.di.centria.nohr.model;

import java.util.LinkedList;
import java.util.List;

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

    public static Term getTerm(TermModel termModel) throws ModelException {
	if (termModel.isAtom())
	    return new ConstantImpl(termModel.toString());
	else if (termModel.isNumber()) {
	    Number number = termModel.intValue();
	    return new NumericConstantImpl(number);
	} else if (termModel.isVar())
	    return new VariableImpl(termModel.toString());
	else if (termModel.isList()) {
	    List<Term> termList = new LinkedList<Term>();
	    for (TermModel tm : termModel.flatList())
		termList.add(getTerm(tm));
	    return new ListTermImpl(termList);
	} else
	    throw new ModelException();
    }

    public static TruthValue getTruthValue(TermModel termModel)
	    throws ModelException {
	String valStr = termModel.toString();
	if (valStr.equals("true"))
	    return TruthValue.TRUE;
	else if (valStr.equals("undefined"))
	    return TruthValue.UNDEFINED;
	else if (valStr.equals("false"))
	    return TruthValue.FALSE;
	else
	    throw new ModelException();
    }
}