package pt.unl.fct.di.centria.nohr.model;

public interface NegativeLiteral extends Literal {

    @Override
    public NegativeLiteral acept(Visitor visitor);

}
