/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import pt.unl.fct.di.centria.nohr.model.predicates.MetaPredicate;

/**
 * An implementation of {@link ModelVisitor} that simply returns {@code element} in each {@code visit(E element)} method if {@code E} is an leaf type
 * (i.e. {@link Constant}, {@link HybridPredicate}, or {@link Variable}}), and {@code element.accept(this)} otherwise. Extend this class if you want
 * create a {@link ModelVisitor} that manipulates that only some model types.
 *
 * @author Nuno Costa
 */
public class DefaultModelVisitor implements ModelVisitor {

	/**
	 * Returns the string representation of a given model element.
	 *
	 * @param element
	 *            the model element.
	 * @return the representation of {@code element}.
	 */
	static String toString(FormatVisitor formatVisitor, ModelElement<?> element) {
		return element.accept(formatVisitor);
	}

	@Override
	public Constant visit(Constant constant) {
		return constant;
	}

	@Override
	public HybridPredicate visit(HybridPredicate hybridPredicate) {
		return hybridPredicate;
	}

	@Override
	public HybridPredicate visit(MetaPredicate metaPredicate) {
		return metaPredicate.accept(this);
	}

	@Override
	public Variable visit(Variable variable) {
		return variable;
	}

}
