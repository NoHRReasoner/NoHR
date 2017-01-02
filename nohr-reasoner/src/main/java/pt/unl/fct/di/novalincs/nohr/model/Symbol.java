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
/**
 * Represents a terminal symbol of the abstract syntax.
 *
 * @author Nuno Costa
 */
public interface Symbol extends ModelElement<Symbol> {

    /**
     * Returns the string representation of this symbol. That representation
     * must univocally identify the symbol, i.e. the following property must be
     * satisfied, where {@code s} and {@code r} are two
     * {@link Symbol symbols}: {@code s.asString().equals(r.asString())} iff
     * {@code s.equals(r)}.
     * @return 
     */
    String asString();

}
