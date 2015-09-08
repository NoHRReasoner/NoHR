package pt.unl.fct.di.novalincs.nohr.plugin;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import org.protege.editor.core.Disposable;

class DisposableObject<T> implements Disposable {

	private T object;

	public DisposableObject(T object) {
		this.object = object;
	}

	@Override
	public void dispose() throws Exception {
		object = null;
	}

	public T getObject() {
		return object;
	}

}