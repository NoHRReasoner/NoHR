package xsb;

import java.util.Arrays;
import java.util.List;

import local.translate.Utils;

import com.declarativa.interprolog.TermModel;

public class Rule{
	protected static final String IF_SYMBOL = ":-";
	
	protected TermModel head;
	protected List<TermModel> body;

	public Rule(TermModel head, TermModel... body) {		
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