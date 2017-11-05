package org.eocencle.winger.ognl;

import java.util.Enumeration;
import java.util.Map;

public class EnumerationPropertyAccessor extends ObjectPropertyAccessor implements PropertyAccessor {
	public Object getProperty(Map context, Object target, Object name) throws OgnlException {
		Enumeration e = (Enumeration) target;
		Object result;
		if (name instanceof String) {
			if (!name.equals("next") && !name.equals("nextElement")) {
				if (!name.equals("hasNext") && !name.equals("hasMoreElements")) {
					result = super.getProperty(context, target, name);
				} else {
					result = e.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE;
				}
			} else {
				result = e.nextElement();
			}
		} else {
			result = super.getProperty(context, target, name);
		}

		return result;
	}

	public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
		throw new IllegalArgumentException("can\'t set property " + name + " on Enumeration");
	}
}
