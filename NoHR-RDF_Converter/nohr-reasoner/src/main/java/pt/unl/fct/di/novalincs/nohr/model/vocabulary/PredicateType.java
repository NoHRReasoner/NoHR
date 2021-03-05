package pt.unl.fct.di.novalincs.nohr.model.vocabulary;

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
 * The possible types of a {@link MetaPredicate}: original, of <i>P</i>; double,
 * of <i>P<sup>d</sup></i>; classical negation, of <i>NP</i>; original domain,
 * of <i>DP</i>; original range, of <i>RP</i>; double domain, of <i>DP
 * <sup>d</sup></i>; and double range, of <i>RP<sup>d</sup></i> (see
 * {@link <a href= "http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">
 * <i>Next Step for NoHR: OWL 2 QL</i></a>,
 * <b>Definition 5.</b>}). Each type has a marker char to prefix the symbols of
 * meta-predicates of that type: {@code 'a', 'd', 'n', 'e', 'f', 'g'}, and
 * {@code 'h'}, respectively. Note that the meta-predicates of the types that
 * represent existential (original domain, original range, double domain and
 * double range) have always arity {@literal 1}, unlike the other
 * meta-predicates which have the same arity that the predicate that they refer
 * - {@link #isQuantification()} allows distinguish between the two cases.
 *
 * @author Nuno Costa
 */
public enum PredicateType {
    /**
     * The type of an original meta-predicate, <i>P</i>. Has the maker
     * {@code 'a'}.
     */
    ORIGINAL('a'), /**
     * The type of a double meta-predicate, <i>P<sup>d</sup></i>. Has the marker
     * {@code 'd'}.
     */
    DOUBLE('d', true), /**
     * The type of a meta-predicate, <i>NP
     * </p>
     * , representing a classic negation. Has the marker {@code 'n'}.
     */
    NEGATIVE('n'), /**
     * The type of an original meta-predicate, <i>DP</i>, representing an
     * existential quantification of an atomic role, <i>P</i>. Has the maker
     * {@code 'e'}.
     */
    ORIGINAL_DOMAIN('e', false, true), /**
     * The type of an original meta-predicate, <i>RP</i>, representing an
     * existential quantification of an atomic role, <i>P</i>. Has the marker
     * {@code 'f'}.
     */
    ORIGINAL_RANGE('f', false, true), /**
     * The type of a double meta-predicate, <i>DP<sup>d</sup></i> , representing
     * an existential quantification of an atomic role, <i>P</i>. Has the marker
     * {@code 'g'}.
     */
    DOUBLE_DOMAIN('g', true, true), /**
     * The type of a double meta-predicate, <i>RP <sup>d</sup></i>, representing
     * a existential quantification of an inverse role, <i>P</i>. Has the marker
     * {@code 'g'}.
     */
    DOUBLED_RANGE('h', true, true);

    /**
     * The char to prefix the symbols of meta-predicates of this type.
     */
    private final char marker;

    /**
     * Specifies whether this type represents an quantification.
     */
    private final boolean isQuantification;

    /**
     * Specifies whether this type represents a double type.
     */
    private final boolean isDouble;

    PredicateType(char marker) {
        this(marker, false, false);
    }

    PredicateType(char marker, boolean isDouble) {
        this(marker, isDouble, false);
    }

    PredicateType(char marker, boolean isDouble, boolean isQuantification) {
        this.marker = marker;
        this.isDouble = isDouble;
        this.isQuantification = isQuantification;
    }

    /**
     * Returns true iff this type represents a double type.
     */
    public boolean isDouble() {
        return isDouble;
    }

    /**
     * Returns true iff this type represents an quantification, in which case
     * the arity of the correspondent meta-predicate is {@literal 1}.
     */
    public boolean isQuantification() {
        return isQuantification;
    }

    /**
     * Returns the marker of this type.
     *
     * @return the marker of this type.
     */
    public char marker() {
        return marker;
    }
}
