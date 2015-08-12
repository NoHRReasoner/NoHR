/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation;

import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypeVisitor;

/**
 * Provides an auxiliary method that double a given rule according to
 * <b>Definition 3.1</b> of
 * {@link <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i>Query-driven
 * Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
 *
 * @author Nuno Costa
 *
 */
public class RulesDoubling {

    /**
     * Double a given rule according to <b>Definition 3.1</b> of
     * {@link <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i>
     * Query-driven Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
     *
     * @param rule
     *            a rule.
     * 
     * @return the pair of rules corresponding to the doubling of {@code rule}.
     */

    public static Rule[] doubleRule(Rule rule) {
	final Rule[] result = new Rule[2];
	final ModelVisitor originalEncoder = new PredicateTypeVisitor(PredicateType.ORIGINAL);
	final ModelVisitor doubleEncoder = new PredicateTypeVisitor(PredicateType.DOUBLE);
	final Atom head = rule.getHead();
	final List<Atom> positiveBody = rule.getPositiveBody();
	final List<Literal> negativeBody = rule.getNegativeBody();
	final Literal[] originalBody = new Literal[rule.getBody().size()];
	final Literal[] doubleBody = new Literal[rule.getBody().size()
		+ (head.getFunctor().isConcept() || head.getFunctor().isRole() ? 1 : 0)];
	int i = 0;
	for (final Literal literal : positiveBody) {
	    originalBody[i] = literal.accept(originalEncoder);
	    doubleBody[i] = literal.accept(doubleEncoder);
	    i++;
	}
	for (final Literal literal : negativeBody) {
	    originalBody[i] = literal.accept(doubleEncoder);
	    doubleBody[i] = literal.accept(originalEncoder);
	    i++;
	}
	if (head.getFunctor().isConcept() || head.getFunctor().isRole()) {
	    final ModelVisitor negativeEncoder = new PredicateTypeVisitor(PredicateType.NEGATIVE);
	    doubleBody[i] = Model.negLiteral(head.accept(negativeEncoder));
	}
	result[0] = Model.rule(head.accept(originalEncoder), originalBody);
	result[1] = Model.rule(head.accept(doubleEncoder), doubleBody);
	return result;
    }
}
