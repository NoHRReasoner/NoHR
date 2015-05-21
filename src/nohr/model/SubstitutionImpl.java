package nohr.model;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

public class SubstitutionImpl implements Substitution {

    private SortedMap<Variable, Integer> variablesIndex;
    private Term[] values;

    public SubstitutionImpl(SortedMap<Variable, Integer> variablesIndex,
	    Term[] values) {
	this.variablesIndex = variablesIndex;
	this.values = values;
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Substitution))
	    return false;
	Substitution sub = (Substitution) obj;
	if (!sub.getVariables().equals(getVariables()))
	    return false;
	for (Variable var : sub.getVariables())
	    if (!sub.getValue(var).equals(getValue(var)))
		return false;
	return true;
    }

    @Override
    public Term getValue(Variable variable) {
	return values[variablesIndex.get(variable)];
    }

    @Override
    public Set<Variable> getVariables() {
	return variablesIndex.keySet();
    }

    @Override
    public int hashCode() {
	return toString().hashCode();
    }

    @Override
    public String toString() {
	String result = "{";
	String sep = "";
	for (Entry<Variable, Integer> entry : variablesIndex.entrySet()) {
	    result += sep + entry.getKey() + "=" + values[entry.getValue()];
	    sep = ",";
	}
	result += "}";
	return result;
    }

}
