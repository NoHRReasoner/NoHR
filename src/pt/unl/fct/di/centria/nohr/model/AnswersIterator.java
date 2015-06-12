/**
 *
 */
package pt.unl.fct.di.centria.nohr.model;

import java.util.Iterator;

/**
 * @author nunocosta
 *
 */
public interface AnswersIterator extends Iterator<Answer> {

    public void cancel();

}
