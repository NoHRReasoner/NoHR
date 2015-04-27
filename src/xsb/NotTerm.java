package xsb;

import com.declarativa.interprolog.TermModel;

public class NotTerm extends TermModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2834198211159007085L;

	public NotTerm(TermModel term) {
		super(new TermModel("tnot"), new TermModel[]{term});
	}

}