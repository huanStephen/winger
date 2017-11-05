package org.eocencle.winger.ognl;

import java.util.HashMap;
import java.util.Map;

public class DefaultClassResolver implements ClassResolver {
	private Map classes = new HashMap(101);

	public Class classForName(String className, Map context) throws ClassNotFoundException {
		Class result = null;
		if ((result = (Class) this.classes.get(className)) == null) {
			try {
				result = Class.forName(className);
			} catch (ClassNotFoundException arg4) {
				if (className.indexOf(46) == -1) {
					result = Class.forName("java.lang." + className);
					this.classes.put("java.lang." + className, result);
				}
			}

			this.classes.put(className, result);
		}

		return result;
	}
}
