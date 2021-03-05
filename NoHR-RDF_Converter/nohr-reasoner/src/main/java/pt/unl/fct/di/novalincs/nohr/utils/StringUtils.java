package pt.unl.fct.di.novalincs.nohr.utils;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.List;

/**
 * Provides some utility methods for string manipulation.
 *
 * @author Nuno Costa
 */
public class StringUtils {

    /**
     * Concatenates the string representations (see
     * {@link java.lang.Object#toString()} ) of objects from a list, separating
     * them with a specified string.
     *
     * @param sep the separator.
     * @param objs the list of objects.
     * @return
     */
    public static String concat(String sep, List<?> objs) {
        final StringBuffer sb = new StringBuffer();

        String sepToken = "";

        for (final Object obj : objs) {
            sb.append(sepToken).append(obj.toString());
            sepToken = sep;
        }

        return new String(sb);
    }

    /**
     * Concatenates the string representations (see
     * {@link java.lang.Object#toString()} ) of objects from an array,
     * separating them with a specified string.
     *
     * @param sep the separator.
     * @param objs the array of objects.
     * @return
     */
    public static String concat(String sep, Object[] objs) {
        final StringBuffer sb = new StringBuffer();

        String sepToken = "";

        for (final Object obj : objs) {
            sb.append(sepToken).append(obj.toString());
            sepToken = sep;
        }

        return new String(sb);
    }

    public static String escapeSymbol(String symbol) {
        if (!symbol.matches("[A-Za-z][A-Za-z0-9_]*")) {
            return "'" + symbol.replace("'", "''") + "'";
        }

        return symbol;
    }

    public static String escapeXsbSymbol(String symbol) {
        if (!symbol.matches("[a-z][A-Za-z0-9_]*")) {
            return "'" + symbol.replace("'", "''") + "'";
        }

        return symbol;
    }

    public static String quote(String symbol) {
        return "'" + symbol + "'";
    }

    public static String simplifySymbol(String symbol) {
        if (symbol.startsWith("'")) {
            return symbol.substring(1, symbol.length() - 1).replace("''", "'");
        } else {
            return symbol;
        }
    }
}
