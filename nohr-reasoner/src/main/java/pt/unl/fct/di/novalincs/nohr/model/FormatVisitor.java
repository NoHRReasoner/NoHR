/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.model;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.HybridConstant;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.MetaPredicate;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PrologPredicate;

/**
 * A model visitor (see {@link
 * <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>}
 * ) to support different model formats, i.e. string representation of the model
 * elements. Implement this interface if you want to support a new model
 * formant, returning the desired string representation of each model element,
 * in the corresponding {@code visit} method.
 *
 * @see ModelElement
 * @author nunocosta
 */
public interface FormatVisitor {

    /**
     * @param answer
     * @return the string representation of the answer {@code answer}.
     */
    public String visit(Answer answer);

    /**
     * @param atom
     * @return the string representation of the atom {@code atom}.
     */
    public String visit(Atom atom);

    public String visit(AtomOperator atomOp);

    public String visit(AtomOperatorTerm term);
    
    public String visit(AtomTerm atomTerm);

    public String visit(ListTerm list);

    public String visit(HybridConstant constant);

    /**
     * @param metaPredicate
     * @return the string representation of the meta-predicate
     * {@code metaPredicate}.
     */
    public String visit(MetaPredicate metaPredicate);

    /**
     * @param negativeLiteral
     * @return the string representation of the negative literal
     * {@code negativeLiteral}.
     */
    public String visit(NegativeLiteral negativeLiteral);

    public String visit(ParenthesisTerm paren);

    public String visit(PrologPredicate predicate);

    /**
     * @param query
     * @return the string representation of the query {@code query}.
     */
    public String visit(Query query);

    /**
     * @param rule
     * @return the string representation of the rule {@code rule}.
     */
    public String visit(Rule rule);

    public String visit(Symbol symbolic);

    /**
     * @param variable
     * @return the string representation of the variable {@code variable}.
     */
    public String visit(Variable variable);

}
