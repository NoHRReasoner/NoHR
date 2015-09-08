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

import pt.unl.fct.di.novalincs.nohr.model.vocabulary.MetaPredicate;

public abstract class DefaultFormatVisitor implements FormatVisitor {

	@Override
	public String visit(MetaPredicate metaPredicate) {
		return visit((Symbol) metaPredicate);
	}

	@Override
	public abstract String visit(Symbol symbolic);

	@Override
	public String visit(Variable variable) {
		return visit((Symbol) variable);
	}

}
