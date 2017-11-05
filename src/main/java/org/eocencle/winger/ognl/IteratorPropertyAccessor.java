package org.eocencle.winger.ognl;

import java.util.Iterator;
import java.util.Map;

public class IteratorPropertyAccessor extends ObjectPropertyAccessor implements PropertyAccessor {
	public Object getProperty(Map context, Object target, Object name) throws OgnlException {
		Iterator iterator = (Iterator) target;
		Object result;
		if (name instanceof String) {
			if (name.equals("next")) {
				result = iterator.next();
			} else if (name.equals("hasNext")) {
				result = iterator.hasNext() ? Boolean.TRUE : Boolean.FALSE;
			} else {
				result = super.getProperty(context, target, name);
			}
		} else {
			result = super.getProperty(context, target, name);
		}

		return result;
	}

	public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
		throw new IllegalArgumentException("can\'t set property " + name + " on Iterator");
	}
}
