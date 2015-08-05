package pt.unl.fct.di.centria.nohr;

import java.util.List;

public class Utils {

    public static String concat(String sep, List<?> objs) {
	final StringBuffer sb = new StringBuffer();
	String sepToken = "";
	for (final Object obj : objs) {
	    sb.append(sepToken + obj.toString());
	    sepToken = sep;
	}
	return new String(sb);
    }

    public static String concat(String sep, Object[] objs) {
	final StringBuffer sb = new StringBuffer();
	String sepToken = "";
	for (final Object obj : objs) {
	    sb.append(sepToken + obj.toString());
	    sepToken = sep;
	}
	return new String(sb);
    }

    public static String escapeAtom(String atom) {
        return atom.replaceAll("'", "''");
    }

    public static String unescapeAtom(String atom) {
        return atom.replaceAll("''", "'");
    }
}
