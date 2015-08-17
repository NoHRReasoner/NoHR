/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.ql;

import static pt.unl.fct.di.centria.nohr.model.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.Model.var;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.domPred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.negPred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.ranPred;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Variable;

/**
 * Provides DL-Lite<sub>R</sub> axiom translation operations according to
 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
public abstract class QLAxiomsTranslator {

	/**
	 * An variable <i>x</i>.
	 */
	protected static final Variable X = var("X");

	/**
	 * An variable <i>y</i>.
	 */
	protected static final Variable Y = var("Y");

	/**
	 * Translate the classic negation of a given concept to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param b
	 *            a basic concept <i>B</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>NA(x)</i> if <i>B</i> is an atomic concept <i>A</i>; <br>
	 *         <i>NP(x,_)</i> if <i>B</i> is an existential <i>&exist;P</i>; <br>
	 *         <i>NP(_,x)</i> if <i>B</i> is an existential <i>&exist;P<sup>-</sup></i>.
	 * @throws IllegalArgumentException
	 *             if <i>B</i> isn't a basic concept.
	 */
	protected Atom negTr(OWLClassExpression b, Variable x) {
		if (b instanceof OWLClass)
			return atom(negPred((OWLClass) b), x);
		else if (b instanceof OWLObjectSomeValuesFrom) {
			final OWLObjectSomeValuesFrom b1 = (OWLObjectSomeValuesFrom) b;
			if (!b1.getFiller().isOWLThing())
				throw new IllegalArgumentException("b: must be a basic concept, can't be: " + b);
			return negTr(b1.getProperty(), x, var());
		} else
			throw new IllegalArgumentException("b: must be a basic concept, can't be: " + b);
	}

	/**
	 * Translate the classic negation of a given role to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param q
	 *            a basic role <i>Q</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param y
	 *            a variable <i>y</i>.
	 * @return <i>NP(x,y)</i> if <i>Q</i> is an atomic role <i>P</i>; <br>
	 *         <i>NP(y,x)</i> if <i>Q</i> is an inverse role <i>P<sup>-</sup></i>.
	 */
	protected Atom negTr(OWLPropertyExpression<?, ?> q, Variable x, Variable y) {
		if (q instanceof OWLObjectProperty)
			return atom(negPred(q), x, y);
		else if (q instanceof OWLDataProperty)
			return atom(negPred(q), x, y);
		else
			return atom(negPred(q), y, x);
	}

	/**
	 * Translate the classic negation of the existential formed with a given basic role to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param q
	 *            a basic role <i>Q</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @return <i>NP(x,_)</i> if <i>Q</i> is an atomic role <i>P</i>; <br>
	 *         <i>NP(_,y)</i> if <i>Q</i> in an inverse role <i>P</i>.
	 */
	protected Atom negTrExist(OWLObjectPropertyExpression q, Variable x) {
		if (q instanceof OWLObjectProperty)
			return atom(negPred(q), x, var());
		else
			return atom(negPred(q), var(), x);
	}

	/**
	 * Translate a given concept to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param c
	 *            a concept <i>C</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param doub
	 *            specifies whether the returned atom is double (i.e. have a double meta-predicate functor).
	 * @return <i>A(x)</i> if <i>C</i> is an atomic concept <i>A</i>; <br>
	 *         <i>DP(x)</i> if <i>C</i> in an existential <i>&exist;P</i>; <br>
	 *         <i>RP(x)</i> if <i>C</i> is an existential <i>&exist;P<sup>-</sup>; <br>
	 *         <i>NA(x)</i> if <i>C</i> is the complement of an atomic atom <i>&not;A</i>; <br>
	 *         <i>tr(&not;Q, x, _)</i> if <i>C</i> is the complement of an existential <i>&not;&exist;Q</i>; <br>
	 *         and the corresponding double atoms if {@code doub} is true.
	 * @throws IllegalArgumentException
	 *             if <i>C</i> isn't a DL-Lite<sub>R</sub> concept.
	 */
	protected Atom tr(OWLClassExpression c, Variable x, boolean doub) {
		if (c instanceof OWLClass)
			return atom(pred((OWLClass) c, doub), x);
		else if (c instanceof OWLObjectSomeValuesFrom) {
			final OWLObjectPropertyExpression q = ((OWLObjectSomeValuesFrom) c).getProperty();
			if (q instanceof OWLObjectProperty)
				return atom(domPred(q, doub), x);
			else
				return atom(ranPred(q, doub), x);
		} else if (c instanceof OWLObjectComplementOf) {
			final OWLClassExpression b1 = ((OWLObjectComplementOf) c).getOperand();
			if (b1 instanceof OWLClass)
				return atom(negPred((OWLClass) b1), x);
			else if (b1 instanceof OWLObjectSomeValuesFrom)
				return negTr(((OWLObjectSomeValuesFrom) b1).getProperty(), x, var("_"));
		}
		throw new IllegalArgumentException("c: must be an DL-LiteR concept, but was: " + c);
	}

	/**
	 * Translate a given role to an atom (see <b>Definition 5.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>}).
	 *
	 * @param r
	 *            a role <i>R</i>.
	 * @param x
	 *            a variable <i>x</i>.
	 * @param y
	 *            a variable <i>y</i>.
	 * @param doub
	 *            specifies whether the returned atom is double (i.e. have a double meta-predicate functor).
	 * @return <i>P(x, y)</i> if <i>R</i> is an atomic role <i>P</i>; <br>
	 *         <i>P(y, x)</i> if <i>R</i> is an inverse role <i>P<sup>-</sup></i>; <br>
	 *         and the respective double atoms if {@code doub} is true.
	 */
	protected Atom tr(OWLPropertyExpression<?, ?> r, Variable x, Variable y, boolean doub) {
		if (r instanceof OWLObjectProperty)
			return atom(pred(r, doub), x, y);
		else if (r instanceof OWLDataProperty)
			return atom(pred(r, doub), x, y);
		else
			return atom(pred(r, doub), y, x);
	}

	/**
	 * Partially (depending on the concrete {@link QLAxiomsTranslator} used) translate a concept assertion to a set of rules according to <b>(a1)</b>
	 * of <b>Definition 9.</b> of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL
	 * 2 QL</a>}.
	 *
	 * @param assertion
	 *            an assertion
	 */
	public abstract Set<Rule> translateAssertion(OWLClassAssertionAxiom assertion);

	/**
	 * Partially (depending on the concrete {@link QLAxiomsTranslator} used) translate a concept assertion to a set of rules according to <b>(a1)</b>
	 * of <b>Definition 9.</b> of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL
	 * 2 QL</a>}.
	 *
	 * @param assertion
	 *            an assertion
	 */
	public abstract Set<Rule> translateAssertion(OWLPropertyAssertionAxiom<?, ?> assertion);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> negative concept
	 * subsumption axiom to a set of rules according to <b>(n1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param b1
	 *            a DL-Lite<sub>R</sub> basic concept operand, <i>B<sub>1</sub></i>.
	 * @param b2
	 *            a DL-Lite<sub>R</sub> basic concept operand, <i>B<sub>2</sub></i>.
	 * @return the translation of <i>B<sub>1</sub>&sqsube;&not;B<sub>2</sub></i> according to <b>(n1)</b> and given the the implementing class
	 *         criteria.
	 */
	public abstract Set<Rule> translateDisjunction(OWLClassExpression b1, OWLClassExpression b2);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> negative role
	 * subsumption axiom to a set of rules according to <b>(n2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            a DL-Lite<sub>R</sub> basic role operand, <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            a DL-Lite<sub>R</sub> basic role operand, <i>Q<sub>2</sub></i>.
	 * @return the translation of <i>Q<sub>1</sub>&sqsube;&not;Q<sub>2</sub></i> according to <b>(n2)</b> and given the the implementing class
	 *         criteria.
	 */
	public abstract Set<Rule> translateDisjunction(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2);

	public abstract Rule translateDomain(OWLObjectProperty p);

	public abstract Rule translateDomain(OWLSubObjectPropertyOfAxiom alpha);

	public abstract Rule translateRange(OWLObjectProperty p);

	public abstract Rule translateRange(OWLSubObjectPropertyOfAxiom alpha);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> concept subsumption
	 * axiom to a set of rules according to <b>(s1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param b1
	 *            the subsumed DL-Lite<sub>R</sub> basic concept <i>B<sub>1</sub></i>.
	 * @param b2
	 *            the subsuming DL-Lite<sub>R</sub> basic concept <i>B<sub>2</sub></i>.
	 * @return the translation of <i>B<sub>1</sub>&sqsube;&not;B<sub>2</sub></i> according to <b>(s1)</b> given the implementing class criteria.
	 * @throws IllegalArgumentException
	 *             if <i>B<sub>1</sub></i> or <i>B<sub>2</sub></i> aren't DL-Lite<sub>R</sub> basic concepts.
	 */
	public abstract Set<Rule> translateSubsumption(OWLClassExpression b1, OWLClassExpression b2);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> role subsumption axiom
	 * to a set of rules according to <b>(s2)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param q1
	 *            the subsumed basic role <i>Q<sub>1</sub></i>.
	 * @param q2
	 *            the subsuming basic role <i>Q<sub>2</sub></i>.
	 * @return translation of <i>Q<sub>1</sub>&sqsube;&not;Q<sub>2</sub></i> according to <b>(s1)</b> given the implementing class criteria.
	 * @throws IllegalArgumentException
	 *             <i>Q<sub>1</sub></i> or <i>Q<sub>2</sub></i> aren't a basic DL-Lite<sub>R</sub> roles.
	 */
	public abstract Set<Rule> translateSubsumption(OWLPropertyExpression<?, ?> q1, OWLPropertyExpression<?, ?> q2);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> concept unsatisfiability
	 * to a set of rules according to <b>(i1)</b> of <b>Definition 9.</b> of
	 * {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL 2 QL</a>} .
	 *
	 * @param a
	 *            the unsatisfiable concept <i>A</i>.
	 * @return translation of the unsatisfiability of <i>A</i> according to <b>(i1)</b> given the implementing class criteria.
	 */
	public abstract Set<Rule> translateUnsatisfaible(OWLClass a);

	/**
	 * Partially (according to some {@link QLAxiomsTranslator} implementing class's criteria) translate a DL-Lite<sub>R</sub> role unsatisfiability
	 * according to <b>(i2)</i> of {@link <a href="http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next Step for NoHR: OWL
	 * 2 QL</a>} .
	 *
	 * @param p
	 *            the unsatisfiable role <i>P</i>.
	 * @return translation of the unsatisfiability of <i>P</i> according to <b>(i2)</b> given the implementing class criteria.
	 */
	public abstract Set<Rule> translateUnsatisfaible(OWLProperty<?, ?> p);

}
