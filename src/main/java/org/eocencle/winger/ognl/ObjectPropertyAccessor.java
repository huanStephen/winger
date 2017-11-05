package org.eocencle.winger.ognl;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ObjectPropertyAccessor implements PropertyAccessor {
	public Object getPossibleProperty(Map context, Object target, String name) throws OgnlException {
		OgnlContext ognlContext = (OgnlContext) context;

		try {
			Object result;
			if ((result = OgnlRuntime.getMethodValue(ognlContext, target, name, true)) == OgnlRuntime.NotFound) {
				result = OgnlRuntime.getFieldValue(ognlContext, target, name, true);
			}

			return result;
		} catch (IntrospectionException arg6) {
			throw new OgnlException(name, arg6);
		} catch (OgnlException arg7) {
			throw arg7;
		} catch (Exception arg8) {
			throw new OgnlException(name, arg8);
		}
	}

	public Object setPossibleProperty(Map context, Object target, String name, Object value) throws OgnlException {
		Object result = null;
		OgnlContext ognlContext = (OgnlContext) context;

		try {
			if (!OgnlRuntime.setMethodValue(ognlContext, target, name, value, true)) {
				result = OgnlRuntime.setFieldValue(ognlContext, target, name, value) ? null : OgnlRuntime.NotFound;
			}

			if (result == OgnlRuntime.NotFound) {
				Method ex = OgnlRuntime.getWriteMethod(target.getClass(), name);
				if (ex != null) {
					result = ex.invoke(target, new Object[] { value });
				}
			}

			return result;
		} catch (IntrospectionException arg7) {
			throw new OgnlException(name, arg7);
		} catch (OgnlException arg8) {
			throw arg8;
		} catch (Exception arg9) {
			throw new OgnlException(name, arg9);
		}
	}

	public boolean hasGetProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
		try {
			return OgnlRuntime.hasGetProperty(context, target, oname);
		} catch (IntrospectionException arg4) {
			throw new OgnlException("checking if " + target + " has gettable property " + oname, arg4);
		}
	}

	public boolean hasGetProperty(Map context, Object target, Object oname) throws OgnlException {
		return this.hasGetProperty((OgnlContext) context, target, oname);
	}

	public boolean hasSetProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
		try {
			return OgnlRuntime.hasSetProperty(context, target, oname);
		} catch (IntrospectionException arg4) {
			throw new OgnlException("checking if " + target + " has settable property " + oname, arg4);
		}
	}

	public boolean hasSetProperty(Map context, Object target, Object oname) throws OgnlException {
		return this.hasSetProperty((OgnlContext) context, target, oname);
	}

	public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
		Object result = null;
		String name = oname.toString();
		result = this.getPossibleProperty(context, target, name);
		if (result == OgnlRuntime.NotFound) {
			throw new NoSuchPropertyException(target, name);
		} else {
			return result;
		}
	}

	public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
		String name = oname.toString();
		Object result = this.setPossibleProperty(context, target, name, value);
		if (result == OgnlRuntime.NotFound) {
			throw new NoSuchPropertyException(target, name);
		}
	}

	public Class getPropertyClass(OgnlContext context, Object target, Object index) {
		try {
			Method t = OgnlRuntime.getReadMethod(target.getClass(), index.toString());
			if (t == null) {
				if (String.class.isAssignableFrom(index.getClass()) && !target.getClass().isArray()) {
					String indexStr = (String) index;
					String key = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;

					try {
						Field e = target.getClass().getField(key);
						if (e != null) {
							return e.getType();
						}
					} catch (NoSuchFieldException arg7) {
						return null;
					}
				}

				return null;
			} else {
				return t.getReturnType();
			}
		} catch (Throwable arg8) {
			throw OgnlOps.castToRuntime(arg8);
		}
	}

	public String getSourceAccessor(OgnlContext context, Object target, Object index) {
		try {
			String t = index.toString();
			String methodName = t.indexOf(34) >= 0 ? t.replaceAll("\"", "") : t;
			Method m = OgnlRuntime.getReadMethod(target.getClass(), methodName);
			if (m == null && context.getCurrentObject() != null) {
				String f = context.getCurrentObject().toString();
				m = OgnlRuntime.getReadMethod(target.getClass(), f.indexOf(34) >= 0 ? f.replaceAll("\"", "") : f);
			}

			if (m == null) {
				try {
					if (String.class.isAssignableFrom(index.getClass()) && !target.getClass().isArray()) {
						Field f1 = target.getClass().getField(methodName);
						if (f1 != null) {
							context.setCurrentType(f1.getType());
							context.setCurrentAccessor(f1.getDeclaringClass());
							return "." + f1.getName();
						}
					}
				} catch (NoSuchFieldException arg7) {
					;
				}

				return "";
			} else {
				context.setCurrentType(m.getReturnType());
				context.setCurrentAccessor(
						OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
				return "." + m.getName() + "()";
			}
		} catch (Throwable arg8) {
			throw OgnlOps.castToRuntime(arg8);
		}
	}

	public String getSourceSetter(OgnlContext context, Object target, Object index) {
		try {
			String t = index.toString();
			String methodName = t.indexOf(34) >= 0 ? t.replaceAll("\"", "") : t;
			Method m = OgnlRuntime.getWriteMethod(target.getClass(), methodName);
			if (m == null && context.getCurrentObject() != null && context.getCurrentObject().toString() != null) {
				String parm = context.getCurrentObject().toString();
				m = OgnlRuntime.getWriteMethod(target.getClass(),
						parm.indexOf(34) >= 0 ? parm.replaceAll("\"", "") : parm);
			}

			if (m != null && m.getParameterTypes() != null && m.getParameterTypes().length > 0) {
				Class parm1 = m.getParameterTypes()[0];
				if (m.getParameterTypes().length > 1) {
					throw new UnsupportedCompilationException(
							"Object property accessors can only support single parameter setters.");
				} else {
					String conversion;
					if (parm1.isPrimitive()) {
						Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(parm1);
						conversion = OgnlRuntime.getCompiler().createLocalReference(context,
								"((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue($3," + wrapClass.getName()
										+ ".class, true))." + OgnlRuntime.getNumericValueGetter(wrapClass),
								parm1);
					} else if (parm1.isArray()) {
						conversion = OgnlRuntime.getCompiler()
								.createLocalReference(context, "(" + ExpressionCompiler.getCastString(parm1)
										+ ")ognl.OgnlOps#toArray($3," + parm1.getComponentType().getName() + ".class)",
										parm1);
					} else {
						conversion = OgnlRuntime.getCompiler().createLocalReference(context,
								"(" + parm1.getName() + ")ognl.OgnlOps#convertValue($3," + parm1.getName() + ".class)",
								parm1);
					}

					context.setCurrentType(m.getReturnType());
					context.setCurrentAccessor(
							OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
					return "." + m.getName() + "(" + conversion + ")";
				}
			} else {
				throw new UnsupportedCompilationException("Unable to determine setting expression on "
						+ context.getCurrentObject() + " with index of " + index);
			}
		} catch (Throwable arg9) {
			throw OgnlOps.castToRuntime(arg9);
		}
	}
}
