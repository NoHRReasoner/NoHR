package local.translate.ql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BasicLazyGraphClosure<T> implements GraphClosure<T> {

	Map<T, Set<T>> markedAncestors;  // M
	Map<T, Set<T>> unmarkedAncestors;// U

	public BasicLazyGraphClosure(Map<T, Set<T>> predecessors) {
		this.markedAncestors = new HashMap<T, Set<T>>();
		this.unmarkedAncestors = new HashMap<T, Set<T>>(predecessors);
	}

	@Override
	public Set<T> getAncestors(T v) {
		Set<T> uv = unmarkedAncestors.get(v);
		if (uv == null)
			return null;
		Set<T> mv = markedAncestors.get(v);
		if (mv == null) {
			mv = new HashSet<T>();
			markedAncestors.put(v, mv);
		}
		while (!uv.isEmpty()) {
			T u = uv.iterator().next();
			Set<T> mu = markedAncestors.get(u);
			Set<T> uu = unmarkedAncestors.get(u);
			if (mu != null)
				mv.addAll(mu);
			mv.add(u);
			if (uu != null)
				uv.addAll(uu);
			uv.removeAll(mv);
		}
		return new HashSet<T>(mv);
	}

}
