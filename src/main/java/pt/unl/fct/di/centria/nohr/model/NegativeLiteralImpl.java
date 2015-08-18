package pt.unl.fct.di.centria.nohr.model;

import java.util.List;
import java.util.Map;

import pt.unl.fct.di.centria.nohr.model.predicates.Predicate;

/**
 * Implementation of {@link NegativeLiteral}.
 *
 * @author Nuno Costa
 */
public class NegativeLiteralImpl extends LiteralImpl implements NegativeLiteral {

	// TODO check the need of this
	private final boolean existentially;

	/**
	 * Constructs a negative literal with a specified atom.
	 *
	 * @param atom
	 *            an atom <i>P(t<sub>1</sub>,...,t<sub>n</sub>)</i>.
	 * @return the literal <i><b>not</b> P(t<sub>1</sub>,..., t<sub>n</sub>)</i> .
	 */
	NegativeLiteralImpl(Atom atom) {
		this(atom, false);
	}

	NegativeLiteralImpl(Atom atom, boolean existentially) {
		super(atom);
		this.existentially = existentially;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public NegativeLiteral accept(ModelVisitor visitor) {
		return new NegativeLiteralImpl(visitor.visit(atom).getAtom());
	}

	@Override
	public Literal apply(Map<Variable, Term> substitution) {
		return new NegativeLiteralImpl(atom.apply(substitution).getAtom());
	}

	@Override
	public Literal apply(Variable var, Term term) {
		return new NegativeLiteralImpl(atom.apply(var, term).getAtom());
	}

	@Override
	public NegativeLiteral asNegativeLiteral() {
		return this;
	}

	@Override
	public Atom asPositiveLiteral() {
		throw new ClassCastException();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NegativeLiteralImpl))
			return false;
		final NegativeLiteralImpl other = (NegativeLiteralImpl) obj;
		if (atom == null) {
			if (other.atom != null)
				return false;
		} else if (!atom.equals(other.atom))
			return false;
		return true;
	}

	@Override
	public List<Term> getArguments() {
		return atom.getArguments();
	}

	@Override
	public int getArity() {
		return atom.getArity();
	}

	@Override
	public Predicate getFunctor() {
		return atom.getFunctor();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (atom == null ? 0 : atom.hashCode());
		return result;
	}

	@Override
	public boolean isExistentiallyNegative() {
		return existentially;
	}

	@Override
	public boolean isNegative() {
		return true;
	}

	@Override
	public boolean isPositive() {
		return false;
	}

	@Override
	public String toString() {
		return "not " + atom;
	}

}
