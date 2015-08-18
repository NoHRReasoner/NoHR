package pt.unl.fct.di.centria.nohr;

import java.util.List;

/**
 * Provides some utility methods for string manipulation.
 *
 * @author Nuno Costa
 */
public class StringUtils {

	/**
	 * Concatenates the string representations (see {@link java.lang.Object#toString()} ) of objects from a list, separating them with a specified
	 * string.
	 *
	 * @param sep
	 *            the separator.
	 * @param objs
	 *            the list of objects.
	 */
	public static String concat(String sep, List<?> objs) {
		final StringBuffer sb = new StringBuffer();
		String sepToken = "";
		for (final Object obj : objs) {
			sb.append(sepToken + obj.toString());
			sepToken = sep;
		}
		return new String(sb);
	}

	/**
	 * Concatenates the string representations (see {@link java.lang.Object#toString()} ) of objects from an array, separating them with a specified
	 * string.
	 *
	 * @param sep
	 *            the separator.
	 * @param objs
	 *            the array of objects.
	 */
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
