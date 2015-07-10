/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.predicates;

/**
 * @author nunocosta
 *
 */
public class MetaPredicateImpl extends PredicateImpl implements MetaPredicate {

    protected final char prefix;
    protected PredicateType type;

    MetaPredicateImpl(String symbol, int arity, PredicateType type, char prefix) {
	super(symbol, arity);
	this.prefix = prefix;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!super.equals(obj))
	    return false;
	if (!(obj instanceof MetaPredicateImpl))
	    return false;
	final MetaPredicateImpl other = (MetaPredicateImpl) obj;
	if (type != other.type)
	    return false;
	return true;
    }

    @Override
    public String getName() {
	return "'" + prefix + symbol + "'/" + arity;
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
	return "'" + prefix + symbol + "'";
    }
}
