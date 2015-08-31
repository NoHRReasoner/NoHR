package pt.unl.fct.di.centria.nohr.model.concrete;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import pt.unl.fct.di.centria.nohr.StringUtils;
import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.FormatVisitor;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.HybridPredicate;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * Implementation of {@link Atom}
 *
 * @author Nuno Costa
 */
public class AtomImpl implements Atom {

	/**
	 * The list of arguments.
	 */
	private final List<Term> arguments;

	/**
	 * The functor predicate.
	 */
	private final HybridPredicate predicate;

	/**
	 * Constructs an atom with a specified predicate as functor and list of terms as arguments.
	 *
	 * @param predicate
	 *            the functor predicate.
	 * @param arguments
	 *            the arguments list. Can be null, in which case the atom is treated has having a empty arguments list.
	 * @throws IllegalArgumentException
	 *             if the size of {@code arguments} is different from the predicate arity.
	 */
	AtomImpl(HybridPredicate predicate, List<Term> arguments) {
		Objects.requireNonNull(predicate);
		if (arguments == null && predicate.getArity() > 0)
			throw new IllegalArgumentException("arguments must have a size equal to the predicate arity");
		if (arguments != null)
			if (predicate.getArity() != arguments.size())
				throw new IllegalArgumentException("arguments must have a size equal to the predicate arity");
		this.predicate = predicate;
		this.arguments = arguments;
	}

	@Override
	public String accept(FormatVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Atom accept(ModelVisitor visitor) {
		final HybridPredicate pred = predicate.accept(visitor);
		final List<Term> args = new LinkedList<Term>();
		if (arguments == null)
			return new AtomImpl(pred, null);
		for (final Term term : arguments)
			args.add(term.accept(visitor));
		return new AtomImpl(pred, args);
	}

	@Override
	public Atom apply(Map<Variable, Term> substitution) {
		final List<Term> args = new LinkedList<Term>(arguments);
		final ListIterator<Term> argsIt = args.listIterator();
		while (argsIt.hasNext()) {
			final Term t = argsIt.next();
			if (substitution.containsKey(t)) {
				argsIt.remove();
				argsIt.add(substitution.get(t));
			}
		}
		return new AtomImpl(predicate, args);
	}

	@Override
	public Atom apply(Variable var, Term term) {
		final List<Term> args = new LinkedList<Term>(arguments);
		final ListIterator<Term> argsIt = args.listIterator();
		while (argsIt.hasNext()) {
			final Term t = argsIt.next();
			if (t.equals(var)) {
				argsIt.remove();
				argsIt.add(term);
			}
		}
		return new AtomImpl(predicate, args);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AtomImpl))
			return false;
		final AtomImpl other = (AtomImpl) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}

	@Override
	public List<Term> getArguments() {
		return arguments;
	}

	@Override
	public int getArity() {
		return predicate.getArity();
	}

	@Override
	public Atom getAtom() {
		return this;
	}

	@Override
	public HybridPredicate getFunctor() {
		return predicate;
	}

	@Override
	public List<Variable> getVariables() {
		final List<Variable> result = new LinkedList<Variable>();
		for (final Term arg : arguments)
			if (arg instanceof Variable && !result.contains(arg))
				result.add((Variable) arg);
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (arguments == null ? 0 : arguments.hashCode());
		result = prime * result + (predicate == null ? 0 : predicate.hashCode());
		return result;
	}

	@Override
	public boolean isGrounded() {
		for (final Term term : arguments)
			if (term instanceof Variable)
				return false;
		return true;
	}

	@Override
	public String toString() {
		return predicate + (getArity() > 0 ? "(" + StringUtils.concat(",", arguments) + ")" : "");
	}

}
