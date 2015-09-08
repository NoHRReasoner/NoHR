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
 * Represent an rule atom <i>P(t<sub>1</sub>, ..., t<sub>n</sub>)</i>, where <i>P</i> is a predicate and each <i> t<sub>i</sub>, with 1&le;i&le;n </i>
 * , a term.
 *
 * @see Predicate
 * @see Term
 * @author Nuno Costa
 */

public interface Atom extends Literal {

	@Override
	Atom accept(ModelVisitor model);
}
