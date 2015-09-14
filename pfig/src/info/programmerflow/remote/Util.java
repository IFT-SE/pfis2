package info.programmerflow.remote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Utility class for miscellaneous functionality.
 * @author jalawran
 *
 */
public class Util {
	public static Pattern camelCase = Pattern.compile("_|\\W+|\\s+|(?<=[a-z]+)(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[a-zA-Z]+)(?=[0-9]+)|(?<=[0-9]+)(?=[a-zA-Z]+)");

	/**
	 * Remove camel case
	 * @param s
	 * @return
	 */
	public static String deCamel(String s) {
		String[] ts = camelCase.split(s);
		StringBuffer b = new StringBuffer();
		for (String t : ts) {
			b.append(" ");
			b.append(t.toLowerCase());
		}
		return b.toString();
	}

	/**
	 * Invoke a private method on an object.
	 * @param object The object
	 * @param method The private method
	 * @param args The arguments for the private method
	 * @return The result of calling the private method
	 */
	public static Object call(Object object, String method, Object[] args) {
		if (args == null) {
			args = new Object[] {};
		}
		Class[] argTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				argTypes[i] = args[i].getClass();
			}
		}
		Class c = object.getClass();
		try {
			Method m = c.getDeclaredMethod(method, argTypes);
			if (m != null) {
				m.setAccessible(true);
				return m.invoke(object, args);
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	public static Object call(Object object, String method) {
		return call(object, method, null);
	}

}
