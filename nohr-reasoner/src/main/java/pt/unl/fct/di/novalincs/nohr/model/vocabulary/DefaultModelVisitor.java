/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.ModelElement;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Variable;

/**
 * An implementation of {@link ModelVisitor} that simply returns {@code element} in each {@code visit(E element)} method if {@code E} is an leaf type
 * (i.e. {@link Constant}, {@link Predicate}, or {@link Variable}}), and {@code element.accept(this)} otherwise. Extend this class if you want create
 * a {@link ModelVisitor} that manipulates that only some model types.
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
	public HybridConstant visit(HybridConstant constant) {
		return constant;
	}

	@Override
	public Predicate visit(HybridPredicate hybridPredicate) {
		return hybridPredicate;
	}

	@Override
	public Predicate visit(MetaPredicate metaPredicate) {
		return metaPredicate.accept(this);
	}

	@Override
	public Predicate visit(Predicate predicate) {
		return predicate;
	}

	@Override
	public Variable visit(Variable variable) {
		return variable;
	}

}
