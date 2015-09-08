/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.deductivedb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Atom;
import pt.unl.fct.di.novalincs.nohr.model.DefaultFormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.FormatVisitor;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.NegativeLiteral;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Symbol;
import pt.unl.fct.di.novalincs.nohr.model.Variable;

/**
 * An {@link FormatVisitor} to format the {@link Rule rules} that are sent to a XSB Prolog engine, according to the XSB syntax.
 *
 * @author Nuno Costa
 */
public class XSBFormatVisitor extends DefaultFormatVisitor {

	private String quoted(String str) {
		return "'" + str.replaceAll("'", "''") + "'";
	}

	@Override
	public String visit(Answer answer) {
		return Model.concat(answer.apply(), this, ",");
	}

	@Override
	public String visit(Atom atom) {
		final String pred = atom.getFunctor().accept(this);
		final String args = Model.concat(atom.getArguments(), this, ",");
		if (atom.getArity() == 0)
			return pred;
		return pred + "(" + args + ")";
	}

	@Override
	public String visit(NegativeLiteral literal) {
		final String format = literal.isExistentiallyNegative() ? "not_exists(%s)" : "tnot(%s)";
		return String.format(format, literal.getAtom().accept(this));
	}

	@Override
	public String visit(Query query) {
		return Model.concat(query.getLiterals(), this, ",");
	}

	@Override
	public String visit(Rule rule) {
		final String head = rule.getHead().accept(this);
		final String body = Model.concat(rule.getBody(), this, ",");
		if (rule.isFact())
			return head + ".";
		else
			return head + ":-" + body + ".";
	}

	@Override
	public String visit(Symbol symbolic) {
		return quoted(symbolic.asString());
	}

	@Override
	public String visit(Variable variable) {
		return variable.asString();
	}

}
