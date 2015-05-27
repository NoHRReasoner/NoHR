package pt.unl.fct.di.centria.nohr.model;

public interface NegativeLiteral extends Literal {
	
	public NegativeLiteral acept(Visitor visitor);
	
}
