package org.eocencle.winger.ognl;

import java.lang.reflect.Member;
import java.util.Map;

public class DefaultTypeConverter implements TypeConverter {
	public Object convertValue(Map context, Object value, Class toType) {
		return OgnlOps.convertValue(value, toType);
	}

	public Object convertValue(Map context, Object target, Member member, String propertyName, Object value,
			Class toType) {
		return this.convertValue(context, value, toType);
	}
}
