/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Model;
import pt.unl.fct.di.centria.nohr.model.ModelVisitor;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.predicates.ConceptPredicate;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateType;
import pt.unl.fct.di.centria.nohr.model.predicates.PredicateTypeVisitor;
import pt.unl.fct.di.centria.nohr.model.predicates.RolePredicate;

/**
 * Provides an auxiliary method that double a given rule according to <b>Definition 3.1</b> of
 * {@link <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i>Query-driven Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
 *
 * @author Nuno Costa
 */
public class ProgramDoubling {

	/**
	 * Double a given rule according to <b>Definition 3.1</b> of {@link <a href="http://tocl.acm.org/accepted/464knorr.pdf"><i> Query-driven
	 * Procedures for Hybrid MKNF Knowledge Bases</i></a>}.
	 *
	 * @param rule
	 *            a rule.
	 * @return the pair of rules corresponding to the doubling of {@code rule}.
	 */

	public static List<Rule> doubleRule(Rule rule) {
		final List<Rule> result = new ArrayList<>(2);
		final ModelVisitor originalEncoder = new PredicateTypeVisitor(PredicateType.ORIGINAL);
		final ModelVisitor doubleEncoder = new PredicateTypeVisitor(PredicateType.DOUBLE);
		final Atom head = rule.getHead();
		final List<Atom> positiveBody = rule.getPositiveBody();
		final List<Literal> negativeBody = rule.getNegativeBody();
		final Literal[] originalBody = new Literal[rule.getBody().size()];
		final Literal[] doubleBody = new Literal[rule.getBody().size()
				+ (head.getFunctor() instanceof ConceptPredicate || head.getFunctor() instanceof RolePredicate ? 1
						: 0)];
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
		if (head.getFunctor() instanceof ConceptPredicate || head.getFunctor() instanceof RolePredicate) {
			final ModelVisitor negativeEncoder = new PredicateTypeVisitor(PredicateType.NEGATIVE);
			doubleBody[i] = Model.negLiteral(head.accept(negativeEncoder));
		}
		result.add(Model.rule(head.accept(originalEncoder), originalBody));
		result.add(Model.rule(head.accept(doubleEncoder), doubleBody));
		return result;
	}
}
