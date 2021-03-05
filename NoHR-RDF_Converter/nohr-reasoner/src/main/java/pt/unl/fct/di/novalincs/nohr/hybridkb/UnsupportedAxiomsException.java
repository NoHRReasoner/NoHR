/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Represents the presence of axioms of an unsupported type.
 *
 * @author Nuno Costa
 */
public class UnsupportedAxiomsException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1487281045709804735L;

    /**
     * The set of unsupported axioms.
     */
    private final Set<OWLAxiom> unsupportedAxioms;

    /**
     * Constructs an {@link UnsupportedAxiomsException} from a given set of
     * unsupported axioms.
     *
     * @param unsupportedAxioms the set of unsupported axioms.
     */
    public UnsupportedAxiomsException(Set<OWLAxiom> unsupportedAxioms) {
        super();
        this.unsupportedAxioms = unsupportedAxioms;
    }

    /**
     * Returns the set of unsupported axioms.
     *
     * @return the set of unsupported axioms.
     */
    public Set<OWLAxiom> getUnsupportedAxioms() {
        return unsupportedAxioms;
    }

}
