/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;

/**
 * A model element, i.e a symbol (terminal or non terminal) of the abstract syntax of the Hybrid Knowledge Bases. Can accept an {@link FormatVisitor}
 * or a {@link ModelVisitor} (see {@link <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>}).
 *
 * @see FormatVisitor
 * @see ModelVisitor
 * @author Nuno Costa
 */
public interface ModelElement<T extends ModelElement<T>> {

	public String accept(FormatVisitor visitor);

	public T accept(ModelVisitor visitor);

}
