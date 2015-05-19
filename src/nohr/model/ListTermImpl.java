package nohr.model;

import java.util.List;

import other.Utils;

public class ListTermImpl implements Term {
	
	private List<Term> termList;

	public ListTermImpl(List<Term> termList) {
		this.termList = termList;
	}

	@Override
	public Constant asConstant() throws ModelException {
		throw new ModelException();
	}

	@Override
	public List<Term> asList() {
		return termList;
	}

	@Override
	public Variable asVariable() throws ModelException {
		throw new ModelException();
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isList() {
		return true;
	}

	@Override
	public boolean isVariable() {
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + Utils.concat(",", termList) + "]";
	}

}
