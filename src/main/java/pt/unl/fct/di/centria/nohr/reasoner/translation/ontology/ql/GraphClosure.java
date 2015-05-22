package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import java.util.Set;

public interface GraphClosure<T> {

	public Set<T> getAncestors(T v);

}