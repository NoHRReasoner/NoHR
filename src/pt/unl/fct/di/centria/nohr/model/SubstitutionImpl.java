package pt.unl.fct.di.centria.nohr.model;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

public class SubstitutionImpl implements Substitution {

    private final Term[] values;
    private final SortedMap<Variable, Integer> variablesIndex;

    SubstitutionImpl(SortedMap<Variable, Integer> variablesIndex, Term[] values) {
	this.variablesIndex = variablesIndex;
	this.values = values;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof SubstitutionImpl))
	    return false;
	final SubstitutionImpl other = (SubstitutionImpl) obj;
	if (!Arrays.equals(values, other.values))
	    return false;
	if (variablesIndex == null) {
	    if (other.variablesIndex != null)
		return false;
	} else if (!variablesIndex.equals(other.variablesIndex))
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(values);
	return result;
    }

    @Override
    public String toString() {
	String result = "{";
	String sep = "";
	for (final Entry<Variable, Integer> entry : variablesIndex.entrySet()) {
	    result += sep + entry.getKey() + "=" + values[entry.getValue()];
	    sep = ",";
	}
	result += "}";
	return result;
    }

}
