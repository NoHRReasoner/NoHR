package pt.unl.fct.di.centria.nohr;

import java.util.List;

//TODO remove

/**
 * The Class Utils.
 */
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
}
