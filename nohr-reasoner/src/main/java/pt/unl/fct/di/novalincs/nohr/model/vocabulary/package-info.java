/**
 * Models the vocabulary elements of the Hybrid Knowledge Bases (predicates and constants) and provides components to manage that elements. The
 * predicates and constants of a given KB are managed by a dedicated {@link pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary} that construct
 * such elements as needed and returns them when given their possible concrete representations - e.g. a predicate corresponding to a OWL concept or
 * role can be represented by their IRI fragment and their labels.
 */
package pt.unl.fct.di.novalincs.nohr.model.vocabulary;