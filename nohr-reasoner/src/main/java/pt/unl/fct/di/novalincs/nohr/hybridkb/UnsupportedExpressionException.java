/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 * @author Nuno Costa
 */
public class UnsupportedExpressionException extends RuntimeException {

	private static final long serialVersionUID = 4306711165888446473L;

	private final OWLClassExpression expression;

	/**
	 *
	 */
	public UnsupportedExpressionException(OWLClassExpression expression) {
		this.expression = expression;
	}

	/**
	 * @return the expression
	 */
	public OWLClassExpression getExpression() {
		return expression;
	}

}
