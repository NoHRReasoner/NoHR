package pt.unl.fct.di.novalincs.nohr.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrologSyntax {

    private static final Set<String> OPERATORS;
    public static final String OPERATORS_REGEX;
    private static final Map<Integer, Set<String>> PREDICATES;
    public static final String PREDICATES_REGEX;

    static {
        StringBuilder builder;

        OPERATORS = new HashSet<>();
        builder = new StringBuilder();

        addOperator("==", builder);
        addOperator("=", builder);

        OPERATORS_REGEX = "(" + builder.substring(1) + ")";

        PREDICATES = new HashMap<>();
        builder = new StringBuilder();

        // Arity 1
        // Arity 2
        addPredicate("==", 2, builder);
        addPredicate("=", 2, builder);

        // Arity 3       
        addPredicate("compare", 3, builder);

        PREDICATES_REGEX = builder.substring(1);
        System.out.println(PREDICATES_REGEX);
    }

    private static void addOperator(String symbol, StringBuilder builder) {
        OPERATORS.add(symbol);
        builder.append("|").append(symbol);
    }

    private static void addPredicate(String symbol, int arity, StringBuilder builder) {
        PREDICATES.putIfAbsent(arity, new HashSet<String>());
        PREDICATES.get(arity).add(symbol);
        builder.append("|").append(symbol);
    }

    public static boolean validOperator(String name) {
        return OPERATORS.contains(name);
    }

    public static boolean validPredicate(String name, int arity) {
        final Set<String> preds = PREDICATES.get(arity);

        if (preds == null) {
            return false;
        }

        return preds.contains(name);
    }

}
