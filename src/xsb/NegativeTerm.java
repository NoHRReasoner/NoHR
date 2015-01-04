package xsb;

import com.declarativa.interprolog.TermModel;

public class NegativeTerm extends TermModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2834198211159007085L;

	public NegativeTerm(TermModel [] term) {
		super(new TermModel("tnot"), term);
	}

}