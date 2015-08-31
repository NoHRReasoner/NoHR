package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.terminals.MetaPredicate;

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
