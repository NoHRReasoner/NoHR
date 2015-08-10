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
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypesVisitor;

/**
 * @author nunocosta
 *
 */
public class RulesDuplication {

    public static Rule[] duplicate(Rule rule, boolean negateHead) {
	final Rule[] result = new Rule[2];
	final ModelVisitor originalEncoder = new PredicateTypesVisitor(
		PredicateType.ORIGINAL);
	final ModelVisitor doubleEncoder = new PredicateTypesVisitor(PredicateType.DOUBLE);
	final Atom head = rule.getHead();
	final List<Atom> positiveBody = rule.getPositiveBody();
	final List<Literal> negativeBody = rule.getNegativeBody();
	final Literal[] originalBody = new Literal[rule.getBody().size()];
	final Literal[] doubleBody = new Literal[rule.getBody().size()
	                                         + (negateHead ? 1 : 0)];
	int i = 0;
	for (final Literal literal : positiveBody) {
	    originalBody[i] = literal.acept(originalEncoder);
	    doubleBody[i] = literal.acept(doubleEncoder);
	    i++;
	}
	for (final Literal literal : negativeBody) {
	    originalBody[i] = literal.acept(doubleEncoder);
	    doubleBody[i] = literal.acept(originalEncoder);
	    i++;
	}
	if (negateHead) {
	    final ModelVisitor negativeEncoder = new PredicateTypesVisitor(
		    PredicateType.NEGATIVE);
	    doubleBody[i] = Model.negLiteral(head.acept(negativeEncoder));
	}
	result[0] = Model.rule(head.acept(originalEncoder), originalBody);
	result[1] = Model.rule(head.acept(doubleEncoder), doubleBody);
	return result;
    }
}
