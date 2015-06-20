/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class MetaPredicateImpl extends PredicateImpl implements MetaPredicate {

    protected PredicateType type;

    protected static char prefix;

    public MetaPredicateImpl(String symbol, int arity, PredicateType type) {
	super(symbol, arity);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (!(obj instanceof MetaPredicateImpl))
	    return false;
	MetaPredicateImpl other = (MetaPredicateImpl) obj;
	if (type != other.type)
	    return false;
	return true;
    }

    public PredicateType getType() {
	return type;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + (type == null ? 0 : type.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return prefix + symbol;
    }
}
