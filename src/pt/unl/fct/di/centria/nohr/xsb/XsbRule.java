package pt.unl.fct.di.centria.nohr.xsb;

import java.util.Arrays;
import java.util.List;

import other.Utils;

import com.declarativa.interprolog.TermModel;

public class XsbRule{
	protected static final String IF_SYMBOL = ":-";
	
	protected TermModel head;
	protected List<TermModel> body;

	public XsbRule(TermModel head, TermModel... body) {		
		this.head = head;
		this.body = Arrays.asList(body);
	}

	@Override
	public String toString() {
		if (body.size() > 0)
			return head + IF_SYMBOL + Utils.concat(",", body) + ".";
		else
			return head.toString() + ".";
	}

}