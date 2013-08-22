package org.haferlib.util;

import java.util.Map;

/**
 * A class that holds a method that allows for convenient deep data viewing.
 * 
 * @author John Werner
 *
 */

public class DeepToString {

	/**
	 * A deep recursive toString method.
	 * Looks into arrays and maps to thoroughly output their contents.
	 * For other objects, this just returns toString().
	 * 
	 * @param obj The object to make a string from.
	 * @return A deep view of the contents of arrays and maps, or just obj.toString().
	 */
	public String deepToString(Object obj) {
		if (obj instanceof Object[]) {
			StringBuilder out = new StringBuilder();
			out.append('{');
			Object[] array = (Object[]) obj;
			for (int i = 0; i < array.length - 1; i++) {
				out.append(deepToString(array[i]));
				out.append(", ");
			}
			if (array.length > 0)
				out.append(deepToString(array[array.length - 1]));
			out.append('}');
			return out.toString();
		}
		else if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			StringBuilder out = new StringBuilder();
			out.append('<');
			if (map.size() > 0) {
				for (Map.Entry<?, ?> entry : map.entrySet()) {
					out.append(deepToString(entry.getKey()));
					out.append(": ");
					out.append(deepToString(entry.getValue()));
					out.append(", ");
				}
				out.delete(out.length() - 2, out.length());
			}
			out.append('>');
			return out.toString();
		}
		return obj.toString();
	}
}
