package pt.unl.fct.di.novalincs.nohr.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrologSyntax {

    private static final Set<String> OPERATORS;
    public static final String OPERATORS_REGEX = "[a-z\\\\\\+\\-\\*/^<>=?@]+";
    private static final Map<Integer, Set<String>> PREDICATES;
    public static final String PREDICATES_REGEX = "[a-z]+[A-Za-z0-9]*|[\\\\\\+\\-\\*/^<>=?@]+";

    static {
        OPERATORS = new HashSet<>();
        PREDICATES = new HashMap<>();

        addAggregatePredicates();
        addArithmeticEvaluationOperators();
        addArithmeticEvaluationPredicates();
        addUnificationAndComparisonOperators();
        addUnificationAndComparisonPredicates();
    }

    private static void addAggregatePredicates() {
        addPredicate("setof", 3);
        addPredicate("bagof", 3);
        addPredicate("findall", 3);
        addPredicate("excess_vars", 4);
        addPredicate("find_n", 4);
    }

    private static void addArithmeticEvaluationOperators() {
        addOperator("is");
        addOperator("+");
        addOperator("-");
        addOperator("*");
        addOperator("/");
        addOperator("//");
        addOperator("/\\");
        addOperator("\\/");
        addOperator("<<");
        addOperator(">>");
        addOperator("xor");
        addOperator("><");
        addOperator("mod");
        addOperator("rem");
        addOperator("^");
        addOperator("**");
    }

    private static void addArithmeticEvaluationPredicates() {
        addPredicate("is", 2);
        addPredicate("+", 2);
        addPredicate("-", 2);
        addPredicate("*", 2);
        addPredicate("div", 2);
        addPredicate("/", 2);
        addPredicate("//", 2);
        addPredicate("-", 1);
        addPredicate("/\\", 2);
        addPredicate("\\/", 2);
        addPredicate("<<", 2);
        addPredicate(">>", 2);
        addPredicate("xor", 2);
        addPredicate("><", 2);
        addPredicate("min", 2);
        addPredicate("max", 2);
        addPredicate("ceiling", 1);
        addPredicate("float", 1);
        addPredicate("floor", 1);
        addPredicate("mod", 2);
        addPredicate("rem", 2);
        addPredicate("round", 1);
        addPredicate("^", 2);
        addPredicate("**", 2);
        addPredicate("sqrt", 1);
        addPredicate("truncate", 1);
        addPredicate("sign", 1);
        addPredicate("pi", 0);
        addPredicate("e", 0);
        addPredicate("epsilon", 0);
        addPredicate("cos", 1);
        addPredicate("sin", 1);
        addPredicate("tan", 1);
        addPredicate("acos", 1);
        addPredicate("asin", 1);
        addPredicate("atan", 1);
        addPredicate("log", 1);
        addPredicate("log10", 1);
        addPredicate("atan", 2);
    }

    private static void addOperator(String symbol) {
        OPERATORS.add(symbol);
    }

    private static void addPredicate(String symbol, int arity) {
        PREDICATES.putIfAbsent(arity, new HashSet<String>());
        PREDICATES.get(arity).add(symbol);
    }

    private static void addUnificationAndComparisonOperators() {
        addOperator("=");
        addOperator("==");
        addOperator("\\=");
        addOperator("\\==");
        addOperator("?=");
        addOperator("@<");
        addOperator("@>");
        addOperator("@=<");
        addOperator("@>=");
        addOperator("@=");
        addOperator("<");
        addOperator(">");
        addOperator("=<");
        addOperator(">=");
    }

    private static void addUnificationAndComparisonPredicates() {
        addPredicate("=", 2);
        addPredicate("unify_with_occurs_check", 2);
        addPredicate("==", 2);
        addPredicate("\\=", 2);
        addPredicate("\\==", 2);
        addPredicate("?=", 2);
        addPredicate("unifiable", 3);
        addPredicate("@<", 2);
        addPredicate("@>", 2);
        addPredicate("@=<", 2);
        addPredicate("@>=", 2);
        addPredicate("@=", 2);
        addPredicate("<", 2);
        addPredicate(">", 2);
        addPredicate("=<", 2);
        addPredicate(">=", 2);
        addPredicate("compare", 3);
        addPredicate("ground", 1);
        addPredicate("ground_and_acyclic", 1);
        addPredicate("ground_or_cyclic", 1);
        addPredicate("subsumes", 2);
        addPredicate("subsumes_chk", 2);
        addPredicate("subsumes_term", 2);
        addPredicate("variant", 2);
        addPredicate("check_variant", 1);
        addPredicate("check_variant", 2);
        addPredicate("sort", 2);
        addPredicate("keysort", 2);
        addPredicate("parsort", 4);
    }

    public static boolean validOperator(String name) {
        return OPERATORS.contains(name.trim());
    }

    public static boolean validPredicate(String name, int arity) {
        final Set<String> preds = PREDICATES.get(arity);

        if (preds == null) {
            return false;
        }

        return preds.contains(name.trim());
    }

}
