/**
 *
 */
package pt.unl.fct.di.centria.nohr.model.terminals;

import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.DOUBLE;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.DOUBLED_RANGE;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.DOUBLE_DOMAIN;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.NEGATIVE;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.ORIGINAL;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.ORIGINAL_DOMAIN;
import static pt.unl.fct.di.centria.nohr.model.terminals.PredicateType.ORIGINAL_RANGE;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.reasoner.translation.DLUtils;

/**
 * A mapping between symbols (strings) and the concepts, roles, or individuals, of a specified set of ontologies, that they represent.
 *
 * @author Nuno Costa
 */
public interface Vocabulary {

	/**
	 * Get the concept represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the concept represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any concept.
	 */
	public Predicate getConcept(String symbol);

	/**
	 * Get the individual represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the individual represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any individual.
	 */
	public Constant getIndividual(String symbol);

	/**
	 * The ontologies whose concepts, roles and individuals this {@link Vocabulary} mapps.
	 *
	 * @return
	 */
	public Set<OWLOntology> getOntologies();

	/**
	 * Get the role represented by a given symbol.
	 *
	 * @param symbol
	 *            a symbol
	 * @return the role represented by {@code symbol}; or {@code null} if {@code symbol} doesn't represent any role.
	 */
	public Predicate getRole(String symbol);

	Predicate ranPred(OWLPropertyExpression<?, ?> role, boolean doub);

	Predicate pred(String symbol, int arity, Vocabulary vocabularyMapping);

	Predicate pred(String symbol, int arity, PredicateType type);

	Predicate pred(String symbol, int arity, boolean doub);

	Predicate pred(String symbol, int arity);

	MetaPredicate pred(Predicate predicate, PredicateType type);

	Predicate pred(OWLPropertyExpression<?, ?> role, PredicateType type);

	Predicate pred(OWLPropertyExpression<?, ?> role, boolean doub);

	Predicate pred(OWLPropertyExpression<?, ?> role);

	MetaPredicate pred(OWLClass concept, PredicateType type);

	Predicate pred(OWLClass concept, boolean doub);

	Predicate pred(OWLClass concept);

	Predicate origRanPred(OWLPropertyExpression<?, ?> role);

	Predicate origPred(String symbol, int arity);

	Predicate origPred(OWLPropertyExpression<?, ?> role);

	Predicate origPred(OWLClass concept);

	Predicate origDomPred(OWLPropertyExpression<?, ?> role);

	Predicate negPred(String symbol, int arity);

	Predicate negPred(Predicate predicate);

	Predicate negPred(OWLPropertyExpression<?, ?> role);

	Predicate negPred(OWLClass concept);

	Predicate doubRanPred(OWLPropertyExpression<?, ?> role);

	Predicate doubPred(String symbol, int arity);

	Predicate doubPred(OWLPropertyExpression<?, ?> role);

	Predicate doubPred(OWLClass concept);

	Predicate doubDomPred(OWLPropertyExpression<?, ?> role);

	Predicate domPred(OWLPropertyExpression<?, ?> role, boolean doub);

	Constant cons(String symbol, Vocabulary vocabularyMapping);

	Constant cons(String symbol);

	Constant cons(OWLPropertyAssertionObject object);

	Constant cons(OWLLiteral literal);

	Constant cons(OWLIndividual individual);

	Constant cons(Number n);
}
