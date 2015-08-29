package pt.unl.fct.di.centria.nohr.model;

import java.util.Objects;

/**
 * Implementation of a numeric {@link Constant}.
 *
 * @author Nuno Costa
 */
public class NumericConstantImpl implements NumericConstant {

	/** The number that this constant represents */
	private final Number number;

	/**
	 * Constructs a numeric constant with a specified number.
	 *
	 * @param number
	 *            the number.
	 */
	NumericConstantImpl(Number number) {
		Objects.requireNonNull(number);
		final double dval = number.doubleValue();
		if (number.shortValue() == dval)
			number = number.shortValue();
		else if (number.intValue() == dval)
			number = number.intValue();
		else if (number.longValue() == dval)
			number = number.longValue();
		else if (number.floatValue() == dval)
			number = number.floatValue();
		else
			number = number.doubleValue();
		this.number = number;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Constant accept(ModelVisitor visit) {
		return visit.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NumericConstantImpl other = (NumericConstantImpl) obj;
		if (number.doubleValue() != other.number.doubleValue())
			return false;
		return true;
	}

	@Override
	public Number getNumber() {
		return number;
	}

	@Override
	public String getSymbol() {
		throw new ClassCastException();
	}

	@Override
	public int hashCode() {
		return number.hashCode();
	}

	@Override
	public String toString() {
		return number.toString();
	}
}
