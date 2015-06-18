package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;

public interface OntologyTranslator {

    public Set<String> getNegatedPredicates();

    public Set<String> getTabledPredicates();

    /**
     * Main function.
     *
     * @return
     *
     * @throws org.semanticweb.owlapi.expression.ParserException
     *             the parser exception
     */
    public boolean proceed(Set<String> translationContainer)
	    throws ParserException;

}