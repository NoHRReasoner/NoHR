package pt.unl.fct.di.novalincs.nohr.plugin;

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