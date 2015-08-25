/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import org.protege.editor.core.Disposable;

import pt.unl.fct.di.centria.nohr.model.ProgramImpl;

/**
 * @author nunocosta
 */
public class DisposableRuleBase extends ProgramImpl implements Disposable {

	/**
	 *
	 */
	public DisposableRuleBase() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protege.editor.core.Disposable#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		clear();
	}

}
