package nohr.model;

public interface NegativeLiteral extends Literal {
	
	public NegativeLiteral acept(Visitor visitor);
	
}
