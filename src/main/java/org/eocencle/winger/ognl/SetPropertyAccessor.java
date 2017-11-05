package org.eocencle.winger.ognl;

import java.util.Map;
import java.util.Set;

public class SetPropertyAccessor extends ObjectPropertyAccessor implements PropertyAccessor {
	public Object getProperty(Map context, Object target, Object name) throws OgnlException {
		Set set = (Set) target;
		if (name instanceof String) {
			Object result;
			if (name.equals("size")) {
				result = new Integer(set.size());
			} else if (name.equals("iterator")) {
				result = set.iterator();
			} else if (name.equals("isEmpty")) {
				result = set.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
			} else {
				result = super.getProperty(context, target, name);
			}

			return result;
		} else {
			throw new NoSuchPropertyException(target, name);
		}
	}
}
