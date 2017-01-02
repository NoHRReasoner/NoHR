package pt.unl.fct.di.novalincs.nohr.model;

import java.util.List;

public interface ListTerm extends Term {

    List<Term> getHead();

    Term getTail();
}
