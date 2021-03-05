/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.deductivedb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

/**
 * The {@link Exception} thrown when an Prolog engine creation timed out.
 *
 * @author Nuno Costa
 */
public class PrologEngineCreationException extends Exception {

	private static final long serialVersionUID = -5376164449185180022L;

	private final Throwable cause;

	public PrologEngineCreationException(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

}
