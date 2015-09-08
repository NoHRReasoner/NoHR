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
 * Represents an negative literal <i><b>not</b> P(t<sub>1</sub>, ..., t <sub>n</sub>)</i>, where <i>P</i> is a predicate, <i>t<sub>i</sub></i>, with
 * <i>1&le;i&le;n</i> terms and <i><b>not</b></i> the default negation operator.
 *
 * @author Nuno Costa
 * @see Predicate
 * @see Term
 * @see Literal
 */

public interface NegativeLiteral extends Literal {

	@Override
	public NegativeLiteral accept(ModelVisitor visitor);

	public boolean isExistentiallyNegative();

}
