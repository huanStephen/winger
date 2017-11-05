package org.eocencle.winger.ognl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListPropertyAccessor extends ObjectPropertyAccessor implements PropertyAccessor {
	public Object getProperty(Map context, Object target, Object name) throws OgnlException {
		List list = (List) target;
		if (!(name instanceof String)) {
			if (name instanceof Number) {
				return list.get(((Number) name).intValue());
			} else {
				if (name instanceof DynamicSubscript) {
					int len1 = list.size();
					switch (((DynamicSubscript) name).getFlag()) {
					case 0:
						return len1 > 0 ? list.get(0) : null;
					case 1:
						return len1 > 0 ? list.get(len1 / 2) : null;
					case 2:
						return len1 > 0 ? list.get(len1 - 1) : null;
					case 3:
						return new ArrayList(list);
					}
				}

				throw new NoSuchPropertyException(target, name);
			}
		} else {
			Object len = null;
			if (name.equals("size")) {
				len = new Integer(list.size());
			} else if (name.equals("iterator")) {
				len = list.iterator();
			} else if (!name.equals("isEmpty") && !name.equals("empty")) {
				len = super.getProperty(context, target, name);
			} else {
				len = list.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
			}

			return len;
		}
	}

	public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
		if (name instanceof String && ((String) name).indexOf("$") < 0) {
			super.setProperty(context, target, name, value);
		} else {
			List list = (List) target;
			if (name instanceof Number) {
				list.set(((Number) name).intValue(), value);
			} else {
				if (name instanceof DynamicSubscript) {
					int len = list.size();
					switch (((DynamicSubscript) name).getFlag()) {
					case 0:
						if (len > 0) {
							list.set(0, value);
						}

						return;
					case 1:
						if (len > 0) {
							list.set(len / 2, value);
						}

						return;
					case 2:
						if (len > 0) {
							list.set(len - 1, value);
						}

						return;
					case 3:
						if (!(value instanceof Collection)) {
							throw new OgnlException("Value must be a collection");
						}

						list.clear();
						list.addAll((Collection) value);
						return;
					}
				}

				throw new NoSuchPropertyException(target, name);
			}
		}
	}

	public Class getPropertyClass(OgnlContext context, Object target, Object index) {
		if (index instanceof String) {
			String indexStr = (String) index;
			String key = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
			return key.equals("size") ? Integer.TYPE
					: (key.equals("iterator") ? Iterator.class
							: (!key.equals("isEmpty") && !key.equals("empty")
									? super.getPropertyClass(context, target, index) : Boolean.TYPE));
		} else {
			return index instanceof Number ? Object.class : null;
		}
	}

	public String getSourceAccessor(OgnlContext context, Object target, Object index) {
		String indexStr = index.toString();
		if (indexStr.indexOf(34) >= 0) {
			indexStr = indexStr.replaceAll("\"", "");
		}

		if (String.class.isInstance(index)) {
			if (indexStr.equals("size")) {
				context.setCurrentAccessor(List.class);
				context.setCurrentType(Integer.TYPE);
				return ".size()";
			}

			if (indexStr.equals("iterator")) {
				context.setCurrentAccessor(List.class);
				context.setCurrentType(Iterator.class);
				return ".iterator()";
			}

			if (indexStr.equals("isEmpty") || indexStr.equals("empty")) {
				context.setCurrentAccessor(List.class);
				context.setCurrentType(Boolean.TYPE);
				return ".isEmpty()";
			}
		}

		if (context.getCurrentObject() != null && !Number.class.isInstance(context.getCurrentObject())) {
			try {
				Method toString = OgnlRuntime.getReadMethod(target.getClass(), indexStr);
				if (toString != null) {
					return super.getSourceAccessor(context, target, index);
				}
			} catch (Throwable arg5) {
				throw OgnlOps.castToRuntime(arg5);
			}
		}

		context.setCurrentAccessor(List.class);
		if (!context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
			indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
		} else if (context.getCurrentObject() != null
				&& Number.class.isAssignableFrom(context.getCurrentObject().getClass())
				&& !context.getCurrentType().isPrimitive()) {
			String toString1 = String.class.isInstance(index) && context.getCurrentType() != Object.class ? ""
					: ".toString()";
			indexStr = "org.eocencle.winger.ognl.OgnlOps#getIntValue(" + indexStr + toString1 + ")";
		}

		context.setCurrentType(Object.class);
		return ".get(" + indexStr + ")";
	}

	public String getSourceSetter(OgnlContext context, Object target, Object index) {
		String indexStr = index.toString();
		if (indexStr.indexOf(34) >= 0) {
			indexStr = indexStr.replaceAll("\"", "");
		}

		if (context.getCurrentObject() != null && !Number.class.isInstance(context.getCurrentObject())) {
			try {
				Method toString = OgnlRuntime.getWriteMethod(target.getClass(), indexStr);
				if (toString != null || !context.getCurrentType().isPrimitive()) {
					System.out
							.println("super source setter returned: " + super.getSourceSetter(context, target, index));
					return super.getSourceSetter(context, target, index);
				}
			} catch (Throwable arg5) {
				throw OgnlOps.castToRuntime(arg5);
			}
		}

		context.setCurrentAccessor(List.class);
		if (!context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
			indexStr = indexStr + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
		} else if (context.getCurrentObject() != null
				&& Number.class.isAssignableFrom(context.getCurrentObject().getClass())
				&& !context.getCurrentType().isPrimitive()) {
			String toString1 = String.class.isInstance(index) && context.getCurrentType() != Object.class ? ""
					: ".toString()";
			indexStr = "org.eocencle.winger.ognl.OgnlOps#getIntValue(" + indexStr + toString1 + ")";
		}

		context.setCurrentType(Object.class);
		return ".set(" + indexStr + ", $3)";
	}
}
