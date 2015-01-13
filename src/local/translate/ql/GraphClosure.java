package local.translate.ql;

import java.util.Set;

public interface GraphClosure<T> {

	public Set<T> getAncestors(T v);

}