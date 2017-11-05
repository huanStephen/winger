package org.eocencle.winger.ognl;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Map;

public class ArrayPropertyAccessor extends ObjectPropertyAccessor implements PropertyAccessor {
	public Object getProperty(Map context, Object target, Object name) throws OgnlException {
		Object result = null;
		if (name instanceof String) {
			if (name.equals("length")) {
				result = new Integer(Array.getLength(target));
			} else {
				result = super.getProperty(context, target, name);
			}
		} else {
			Object index = name;
			int i;
			if (name instanceof DynamicSubscript) {
				i = Array.getLength(target);
				switch (((DynamicSubscript) name).getFlag()) {
				case 0:
					index = new Integer(i > 0 ? 0 : -1);
					break;
				case 1:
					index = new Integer(i > 0 ? i / 2 : -1);
					break;
				case 2:
					index = new Integer(i > 0 ? i - 1 : -1);
					break;
				case 3:
					result = Array.newInstance(target.getClass().getComponentType(), i);
					System.arraycopy(target, 0, result, 0, i);
				}
			}

			if (result == null) {
				if (!(index instanceof Number)) {
					throw new NoSuchPropertyException(target, index);
				}

				i = ((Number) index).intValue();
				result = i >= 0 ? Array.get(target, i) : null;
			}
		}

		return result;
	}

	public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
		boolean isNumber = name instanceof Number;
		if (!isNumber && !(name instanceof DynamicSubscript)) {
			if (!(name instanceof String)) {
				throw new NoSuchPropertyException(target, name);
			}

			super.setProperty(context, target, name, value);
		} else {
			TypeConverter converter = ((OgnlContext) context).getTypeConverter();
			Object convertedValue = converter.convertValue(context, target, (Member) null, name.toString(), value,
					target.getClass().getComponentType());
			int len;
			if (isNumber) {
				len = ((Number) name).intValue();
				if (len >= 0) {
					Array.set(target, len, convertedValue);
				}
			} else {
				len = Array.getLength(target);
				switch (((DynamicSubscript) name).getFlag()) {
				case 0:
					new Integer(len > 0 ? 0 : -1);
					break;
				case 1:
					new Integer(len > 0 ? len / 2 : -1);
					break;
				case 2:
					new Integer(len > 0 ? len - 1 : -1);
					break;
				case 3:
					System.arraycopy(target, 0, convertedValue, 0, len);
					return;
				}
			}
		}

	}

	public String getSourceAccessor(OgnlContext context, Object target, Object index) {
		String indexStr = index.toString();
		if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive()
				&& Number.class.isAssignableFrom(context.getCurrentType())) {
			indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
		} else if (context.getCurrentObject() != null
				&& Number.class.isAssignableFrom(context.getCurrentObject().getClass())
				&& !context.getCurrentType().isPrimitive()) {
			String toString = String.class.isInstance(index) && context.getCurrentType() != Object.class ? ""
					: ".toString()";
			indexStr = "org.eocencle.winger.ognl.OgnlOps#getIntValue(" + indexStr + toString + ")";
		}

		context.setCurrentAccessor(target.getClass());
		context.setCurrentType(target.getClass().getComponentType());
		return "[" + indexStr + "]";
	}

	public String getSourceSetter(OgnlContext context, Object target, Object index) {
		String indexStr = index.toString();
		if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive()
				&& Number.class.isAssignableFrom(context.getCurrentType())) {
			indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
		} else if (context.getCurrentObject() != null
				&& Number.class.isAssignableFrom(context.getCurrentObject().getClass())
				&& !context.getCurrentType().isPrimitive()) {
			String type = String.class.isInstance(index) && context.getCurrentType() != Object.class ? ""
					: ".toString()";
			indexStr = "org.eocencle.winger.ognl.OgnlOps#getIntValue(" + indexStr + type + ")";
		}

		Class type1 = target.getClass().isArray() ? target.getClass().getComponentType() : target.getClass();
		context.setCurrentAccessor(target.getClass());
		context.setCurrentType(target.getClass().getComponentType());
		if (type1.isPrimitive()) {
			Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(type1);
			return "[" + indexStr + "]=((" + wrapClass.getName() + ")ognl.OgnlOps.convertValue($3,"
					+ wrapClass.getName() + ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass);
		} else {
			return "[" + indexStr + "]=ognl.OgnlOps.convertValue($3," + type1.getName() + ".class)";
		}
	}
}
