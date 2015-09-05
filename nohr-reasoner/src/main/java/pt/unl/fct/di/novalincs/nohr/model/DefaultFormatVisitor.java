package pt.unl.fct.di.novalincs.nohr.model;

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.MetaPredicate;

public abstract class DefaultFormatVisitor implements FormatVisitor {

	@Override
	public String visit(MetaPredicate metaPredicate) {
		return visit((Symbol) metaPredicate);
	}

	@Override
	public abstract String visit(Symbol symbolic);

	@Override
	public String visit(Variable variable) {
		return visit((Symbol) variable);
	}

}
