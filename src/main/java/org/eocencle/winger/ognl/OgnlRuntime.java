package org.eocencle.winger.ognl;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eocencle.winger.ognl.internal.ClassCache;
import org.eocencle.winger.ognl.internal.ClassCacheImpl;

public class OgnlRuntime {
	public static final Object NotFound = new Object();
	public static final List NotFoundList = new ArrayList();
	public static final Map NotFoundMap = new HashMap();
	public static final Object[] NoArguments = new Object[0];
	public static final Class[] NoArgumentTypes = new Class[0];
	public static final Object NoConversionPossible = "org.eocencle.winger.ognl.NoConversionPossible";
	public static int INDEXED_PROPERTY_NONE = 0;
	public static int INDEXED_PROPERTY_INT = 1;
	public static int INDEXED_PROPERTY_OBJECT = 2;
	public static final String NULL_STRING = "" + null;
	private static final String SET_PREFIX = "set";
	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";
	private static final Map HEX_PADDING = new HashMap();
	private static final int HEX_LENGTH = 8;
	private static final String NULL_OBJECT_STRING = "<null>";
	private static boolean _jdk15 = false;
	private static boolean _jdkChecked = false;
	static final ClassCache _methodAccessors = new ClassCacheImpl();
	static final ClassCache _propertyAccessors = new ClassCacheImpl();
	static final ClassCache _elementsAccessors = new ClassCacheImpl();
	static final ClassCache _nullHandlers = new ClassCacheImpl();
	static final ClassCache _propertyDescriptorCache = new ClassCacheImpl();
	static final ClassCache _constructorCache = new ClassCacheImpl();
	static final ClassCache _staticMethodCache = new ClassCacheImpl();
	static final ClassCache _instanceMethodCache = new ClassCacheImpl();
	static final ClassCache _invokePermissionCache = new ClassCacheImpl();
	static final ClassCache _fieldCache = new ClassCacheImpl();
	static final List _superclasses = new ArrayList();
	static final ClassCache[] _declaredMethods = new ClassCache[] { new ClassCacheImpl(), new ClassCacheImpl() };
	static final Map _primitiveTypes = new HashMap(101);
	static final ClassCache _primitiveDefaults = new ClassCacheImpl();
	static final Map _methodParameterTypesCache = new HashMap(101);
	static final Map _genericMethodParameterTypesCache = new HashMap(101);
	static final Map _ctorParameterTypesCache = new HashMap(101);
	static SecurityManager _securityManager = System.getSecurityManager();
	static final EvaluationPool _evaluationPool = new EvaluationPool();
	static final ObjectArrayPool _objectArrayPool = new ObjectArrayPool();
	static final Map<Method, Boolean> _methodAccessCache = new ConcurrentHashMap();
	static final Map<Method, Boolean> _methodPermCache = new ConcurrentHashMap();
	static final OgnlRuntime.ClassPropertyMethodCache cacheSetMethod = new OgnlRuntime.ClassPropertyMethodCache();
	static final OgnlRuntime.ClassPropertyMethodCache cacheGetMethod = new OgnlRuntime.ClassPropertyMethodCache();
	static ClassCacheInspector _cacheInspector;
	private static OgnlExpressionCompiler _compiler;
	private static final Class[] EMPTY_CLASS_ARRAY;
	private static IdentityHashMap PRIMITIVE_WRAPPER_CLASSES;
	private static final Map NUMERIC_CASTS;
	private static final Map NUMERIC_VALUES;
	private static final Map NUMERIC_LITERALS;
	private static final Map NUMERIC_DEFAULTS;
	public static final OgnlRuntime.ArgsCompatbilityReport NoArgsReport;

	public static void clearCache() {
		_methodParameterTypesCache.clear();
		_ctorParameterTypesCache.clear();
		_propertyDescriptorCache.clear();
		_constructorCache.clear();
		_staticMethodCache.clear();
		_instanceMethodCache.clear();
		_invokePermissionCache.clear();
		_fieldCache.clear();
		_superclasses.clear();
		_declaredMethods[0].clear();
		_declaredMethods[1].clear();
		_methodAccessCache.clear();
		_methodPermCache.clear();
	}

	public static boolean isJdk15() {
		if (_jdkChecked) {
			return _jdk15;
		} else {
			try {
				Class.forName("java.lang.annotation.Annotation");
				_jdk15 = true;
			} catch (Exception arg0) {
				;
			}

			_jdkChecked = true;
			return _jdk15;
		}
	}

	public static String getNumericValueGetter(Class type) {
		return (String) NUMERIC_VALUES.get(type);
	}

	public static Class getPrimitiveWrapperClass(Class primitiveClass) {
		return (Class) PRIMITIVE_WRAPPER_CLASSES.get(primitiveClass);
	}

	public static String getNumericCast(Class type) {
		return (String) NUMERIC_CASTS.get(type);
	}

	public static String getNumericLiteral(Class type) {
		return (String) NUMERIC_LITERALS.get(type);
	}

	public static void setCompiler(OgnlExpressionCompiler compiler) {
		_compiler = compiler;
	}

	public static OgnlExpressionCompiler getCompiler() {
		return _compiler;
	}

	public static void compileExpression(OgnlContext context, Node expression, Object root) throws Exception {
		_compiler.compileExpression(context, expression, root);
	}

	public static Class getTargetClass(Object o) {
		return o == null ? null : (o instanceof Class ? (Class) o : o.getClass());
	}

	public static String getBaseName(Object o) {
		return o == null ? null : getClassBaseName(o.getClass());
	}

	public static String getClassBaseName(Class c) {
		String s = c.getName();
		return s.substring(s.lastIndexOf(46) + 1);
	}

	public static String getClassName(Object o, boolean fullyQualified) {
		if (!(o instanceof Class)) {
			o = o.getClass();
		}

		return getClassName((Class) o, fullyQualified);
	}

	public static String getClassName(Class c, boolean fullyQualified) {
		return fullyQualified ? c.getName() : getClassBaseName(c);
	}

	public static String getPackageName(Object o) {
		return o == null ? null : getClassPackageName(o.getClass());
	}

	public static String getClassPackageName(Class c) {
		String s = c.getName();
		int i = s.lastIndexOf(46);
		return i < 0 ? null : s.substring(0, i);
	}

	public static String getPointerString(int num) {
		StringBuffer result = new StringBuffer();
		String hex = Integer.toHexString(num);
		Integer l = new Integer(hex.length());
		String pad;
		if ((pad = (String) HEX_PADDING.get(l)) == null) {
			StringBuffer pb = new StringBuffer();

			for (int i = hex.length(); i < 8; ++i) {
				pb.append('0');
			}

			pad = new String(pb);
			HEX_PADDING.put(l, pad);
		}

		result.append(pad);
		result.append(hex);
		return new String(result);
	}

	public static String getPointerString(Object o) {
		return getPointerString(o == null ? 0 : System.identityHashCode(o));
	}

	public static String getUniqueDescriptor(Object object, boolean fullyQualified) {
		StringBuffer result = new StringBuffer();
		if (object != null) {
			if (object instanceof Proxy) {
				Class interfaceClass = object.getClass().getInterfaces()[0];
				result.append(getClassName(interfaceClass, fullyQualified));
				result.append('^');
				object = Proxy.getInvocationHandler(object);
			}

			result.append(getClassName(object, fullyQualified));
			result.append('@');
			result.append(getPointerString(object));
		} else {
			result.append("<null>");
		}

		return new String(result);
	}

	public static String getUniqueDescriptor(Object object) {
		return getUniqueDescriptor(object, false);
	}

	public static Object[] toArray(List list) {
		int size = list.size();
		Object[] result;
		if (size == 0) {
			result = NoArguments;
		} else {
			result = getObjectArrayPool().create(list.size());

			for (int i = 0; i < size; ++i) {
				result[i] = list.get(i);
			}
		}

		return result;
	}

	public static Class[] getParameterTypes(Method m) {
		Map arg0 = _methodParameterTypesCache;
		synchronized (_methodParameterTypesCache) {
			Class[] result;
			if ((result = (Class[]) ((Class[]) _methodParameterTypesCache.get(m))) == null) {
				_methodParameterTypesCache.put(m, result = m.getParameterTypes());
			}

			return result;
		}
	}

	public static Class[] findParameterTypes(Class type, Method m) {
		Type[] genTypes = m.getGenericParameterTypes();
		Class[] types = new Class[genTypes.length];
		boolean noGenericParameter = true;

		for (int typeGenericSuperclass = 0; typeGenericSuperclass < genTypes.length; ++typeGenericSuperclass) {
			if (!Class.class.isInstance(genTypes[typeGenericSuperclass])) {
				noGenericParameter = false;
				break;
			}

			types[typeGenericSuperclass] = (Class) genTypes[typeGenericSuperclass];
		}

		if (noGenericParameter) {
			return types;
		} else if (type != null && isJdk15()) {
			Type arg12 = type.getGenericSuperclass();
			if (arg12 != null && ParameterizedType.class.isInstance(arg12)
					&& m.getDeclaringClass().getTypeParameters() != null) {
				ParameterizedType param;
				if ((types = (Class[]) ((Class[]) _genericMethodParameterTypesCache.get(m))) != null) {
					param = (ParameterizedType) arg12;
					if (Arrays.equals(types, param.getActualTypeArguments())) {
						return types;
					}
				}

				param = (ParameterizedType) arg12;
				TypeVariable[] declaredTypes = m.getDeclaringClass().getTypeParameters();
				types = new Class[genTypes.length];

				for (int i = 0; i < genTypes.length; ++i) {
					TypeVariable paramType = null;
					if (TypeVariable.class.isInstance(genTypes[i])) {
						paramType = (TypeVariable) genTypes[i];
					} else if (GenericArrayType.class.isInstance(genTypes[i])) {
						paramType = (TypeVariable) ((GenericArrayType) genTypes[i]).getGenericComponentType();
					} else {
						if (ParameterizedType.class.isInstance(genTypes[i])) {
							types[i] = (Class) ((ParameterizedType) genTypes[i]).getRawType();
							continue;
						}

						if (Class.class.isInstance(genTypes[i])) {
							types[i] = (Class) genTypes[i];
							continue;
						}
					}

					Class resolved = resolveType(param, paramType, declaredTypes);
					if (resolved != null) {
						if (GenericArrayType.class.isInstance(genTypes[i])) {
							resolved = Array.newInstance(resolved, 0).getClass();
						}

						types[i] = resolved;
					} else {
						types[i] = m.getParameterTypes()[i];
					}
				}

				Map arg13 = _genericMethodParameterTypesCache;
				synchronized (_genericMethodParameterTypesCache) {
					_genericMethodParameterTypesCache.put(m, types);
					return types;
				}
			} else {
				return getParameterTypes(m);
			}
		} else {
			return getParameterTypes(m);
		}
	}

	static Class resolveType(ParameterizedType param, TypeVariable var, TypeVariable[] declaredTypes) {
		if (param.getActualTypeArguments().length < 1) {
			return null;
		} else {
			for (int i = 0; i < declaredTypes.length; ++i) {
				if (!TypeVariable.class.isInstance(param.getActualTypeArguments()[i])
						&& declaredTypes[i].getName().equals(var.getName())) {
					return (Class) param.getActualTypeArguments()[i];
				}
			}

			return null;
		}
	}

	static Class findType(Type[] types, Class type) {
		for (int i = 0; i < types.length; ++i) {
			if (Class.class.isInstance(types[i]) && type.isAssignableFrom((Class) types[i])) {
				return (Class) types[i];
			}
		}

		return null;
	}

	public static Class[] getParameterTypes(Constructor c) {
		Class[] result;
		if ((result = (Class[]) ((Class[]) _ctorParameterTypesCache.get(c))) == null) {
			Map arg1 = _ctorParameterTypesCache;
			synchronized (_ctorParameterTypesCache) {
				if ((result = (Class[]) ((Class[]) _ctorParameterTypesCache.get(c))) == null) {
					_ctorParameterTypesCache.put(c, result = c.getParameterTypes());
				}
			}
		}

		return result;
	}

	public static SecurityManager getSecurityManager() {
		return _securityManager;
	}

	public static void setSecurityManager(SecurityManager value) {
		_securityManager = value;
	}

	public static Permission getPermission(Method method) {
		Class mc = method.getDeclaringClass();
		ClassCache arg1 = _invokePermissionCache;
		synchronized (_invokePermissionCache) {
			Object permissions = (Map) _invokePermissionCache.get(mc);
			if (permissions == null) {
				_invokePermissionCache.put(mc, permissions = new HashMap(101));
			}

			Object result;
			if ((result = (Permission) ((Map) permissions).get(method.getName())) == null) {
				result = new OgnlInvokePermission("invoke." + mc.getName() + "." + method.getName());
				((Map) permissions).put(method.getName(), result);
			}

			return (Permission) result;
		}
	}

	public static Object invokeMethod(Object target, Method method, Object[] argsArray)
			throws InvocationTargetException, IllegalAccessException {
		boolean syncInvoke = false;
		boolean checkPermission = false;
		synchronized (method) {
			if (_methodAccessCache.get(method) == null || _methodAccessCache.get(method) == Boolean.TRUE) {
				syncInvoke = true;
			}

			if (_securityManager != null && _methodPermCache.get(method) == null
					|| _methodPermCache.get(method) == Boolean.FALSE) {
				checkPermission = true;
			}
		}

		boolean wasAccessible = true;
		Object result;
		if (syncInvoke) {
			synchronized (method) {
				if (checkPermission) {
					try {
						_securityManager.checkPermission(getPermission(method));
						_methodPermCache.put(method, Boolean.TRUE);
					} catch (SecurityException arg10) {
						_methodPermCache.put(method, Boolean.FALSE);
						throw new IllegalAccessException("Method [" + method + "] cannot be accessed.");
					}
				}

				if (Modifier.isPublic(method.getModifiers())
						&& Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
					_methodAccessCache.put(method, Boolean.FALSE);
				} else if (!(wasAccessible = method.isAccessible())) {
					method.setAccessible(true);
					_methodAccessCache.put(method, Boolean.TRUE);
				} else {
					_methodAccessCache.put(method, Boolean.FALSE);
				}

				result = method.invoke(target, argsArray);
				if (!wasAccessible) {
					method.setAccessible(false);
				}
			}
		} else {
			if (checkPermission) {
				try {
					_securityManager.checkPermission(getPermission(method));
					_methodPermCache.put(method, Boolean.TRUE);
				} catch (SecurityException arg9) {
					_methodPermCache.put(method, Boolean.FALSE);
					throw new IllegalAccessException("Method [" + method + "] cannot be accessed.");
				}
			}

			result = method.invoke(target, argsArray);
		}

		return result;
	}

	public static final Class getArgClass(Object arg) {
		if (arg == null) {
			return null;
		} else {
			Class c = arg.getClass();
			if (c == Boolean.class) {
				return Boolean.TYPE;
			} else {
				if (c.getSuperclass() == Number.class) {
					if (c == Integer.class) {
						return Integer.TYPE;
					}

					if (c == Double.class) {
						return Double.TYPE;
					}

					if (c == Byte.class) {
						return Byte.TYPE;
					}

					if (c == Long.class) {
						return Long.TYPE;
					}

					if (c == Float.class) {
						return Float.TYPE;
					}

					if (c == Short.class) {
						return Short.TYPE;
					}
				} else if (c == Character.class) {
					return Character.TYPE;
				}

				return c;
			}
		}
	}

	public static Class[] getArgClasses(Object[] args) {
		if (args == null) {
			return null;
		} else {
			Class[] argClasses = new Class[args.length];

			for (int i = 0; i < args.length; ++i) {
				argClasses[i] = getArgClass(args[i]);
			}

			return argClasses;
		}
	}

	public static final boolean isTypeCompatible(Object object, Class c) {
		if (object == null) {
			return true;
		} else {
			OgnlRuntime.ArgsCompatbilityReport report = new OgnlRuntime.ArgsCompatbilityReport(0, new boolean[1]);
			return !isTypeCompatible(getArgClass(object), c, 0, report) ? false : !report.conversionNeeded[0];
		}
	}

	public static final boolean isTypeCompatible(Class parameterClass, Class methodArgumentClass, int index,
			OgnlRuntime.ArgsCompatbilityReport report) {
		if (parameterClass == null) {
			report.score += 500;
			return true;
		} else if (parameterClass == methodArgumentClass) {
			return true;
		} else {
			Class ptc;
			if (methodArgumentClass.isArray()) {
				if (parameterClass.isArray()) {
					ptc = parameterClass.getComponentType();
					Class mct = methodArgumentClass.getComponentType();
					if (mct.isAssignableFrom(ptc)) {
						report.score += 25;
						return true;
					}
				}

				if (Collection.class.isAssignableFrom(parameterClass)) {
					ptc = methodArgumentClass.getComponentType();
					if (ptc == Object.class) {
						report.conversionNeeded[index] = true;
						report.score += 30;
						return true;
					}

					return false;
				}
			} else if (Collection.class.isAssignableFrom(methodArgumentClass)) {
				if (parameterClass.isArray()) {
					report.conversionNeeded[index] = true;
					report.score += 50;
					return true;
				}

				if (Collection.class.isAssignableFrom(parameterClass)) {
					if (methodArgumentClass.isAssignableFrom(parameterClass)) {
						report.score += 2;
						return true;
					}

					report.conversionNeeded[index] = true;
					report.score += 50;
					return true;
				}
			}

			if (methodArgumentClass.isAssignableFrom(parameterClass)) {
				report.score += 40;
				return true;
			} else {
				if (parameterClass.isPrimitive()) {
					ptc = (Class) PRIMITIVE_WRAPPER_CLASSES.get(parameterClass);
					if (methodArgumentClass == ptc) {
						report.score += 2;
						return true;
					}

					if (methodArgumentClass.isAssignableFrom(ptc)) {
						report.score += 10;
						return true;
					}
				}

				return false;
			}
		}
	}

	public static boolean areArgsCompatible(Object[] args, Class[] classes) {
		OgnlRuntime.ArgsCompatbilityReport report = areArgsCompatible(getArgClasses(args), classes, (Method) null);
		if (report == null) {
			return false;
		} else {
			boolean[] arr$ = report.conversionNeeded;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				boolean conversionNeeded = arr$[i$];
				if (conversionNeeded) {
					return false;
				}
			}

			return true;
		}
	}

	public static OgnlRuntime.ArgsCompatbilityReport areArgsCompatible(Class[] args, Class[] classes, Method m) {
		boolean varArgs = m != null && isJdk15() && m.isVarArgs();
		if (args != null && args.length != 0) {
			if (args.length != classes.length && !varArgs) {
				return null;
			} else {
				OgnlRuntime.ArgsCompatbilityReport report;
				int index;
				int count;
				if (varArgs) {
					report = new OgnlRuntime.ArgsCompatbilityReport(1000, new boolean[args.length]);
					if (classes.length - 1 > args.length) {
						return null;
					} else {
						index = 0;

						for (count = classes.length - 1; index < count; ++index) {
							if (!isTypeCompatible(args[index], classes[index], index, report)) {
								return null;
							}
						}

						Class arg7 = classes[classes.length - 1].getComponentType();
						count = classes.length - 1;

						for (int count1 = args.length; count < count1; ++count) {
							if (!isTypeCompatible(args[count], arg7, count, report)) {
								return null;
							}
						}

						return report;
					}
				} else {
					report = new OgnlRuntime.ArgsCompatbilityReport(0, new boolean[args.length]);
					index = 0;

					for (count = args.length; index < count; ++index) {
						if (!isTypeCompatible(args[index], classes[index], index, report)) {
							return null;
						}
					}

					return report;
				}
			}
		} else {
			return classes != null && classes.length != 0 ? null : NoArgsReport;
		}
	}

	public static final boolean isMoreSpecific(Class[] classes1, Class[] classes2) {
		int index = 0;

		for (int count = classes1.length; index < count; ++index) {
			Class c1 = classes1[index];
			Class c2 = classes2[index];
			if (c1 != c2) {
				if (c1.isPrimitive()) {
					return true;
				}

				if (c1.isAssignableFrom(c2)) {
					return false;
				}

				if (c2.isAssignableFrom(c1)) {
					return true;
				}
			}
		}

		return false;
	}

	public static String getModifierString(int modifiers) {
		String result;
		if (Modifier.isPublic(modifiers)) {
			result = "public";
		} else if (Modifier.isProtected(modifiers)) {
			result = "protected";
		} else if (Modifier.isPrivate(modifiers)) {
			result = "private";
		} else {
			result = "";
		}

		if (Modifier.isStatic(modifiers)) {
			result = "static " + result;
		}

		if (Modifier.isFinal(modifiers)) {
			result = "final " + result;
		}

		if (Modifier.isNative(modifiers)) {
			result = "native " + result;
		}

		if (Modifier.isSynchronized(modifiers)) {
			result = "synchronized " + result;
		}

		if (Modifier.isTransient(modifiers)) {
			result = "transient " + result;
		}

		return result;
	}

	public static Class classForName(OgnlContext context, String className) throws ClassNotFoundException {
		Class result = (Class) _primitiveTypes.get(className);
		if (result == null) {
			ClassResolver resolver;
			if (context == null || (resolver = context.getClassResolver()) == null) {
				resolver = OgnlContext.DEFAULT_CLASS_RESOLVER;
			}

			result = resolver.classForName(className, context);
		}

		if (result == null) {
			throw new ClassNotFoundException("Unable to resolve class: " + className);
		} else {
			return result;
		}
	}

	public static boolean isInstance(OgnlContext context, Object value, String className) throws OgnlException {
		try {
			Class e = classForName(context, className);
			return e.isInstance(value);
		} catch (ClassNotFoundException arg3) {
			throw new OgnlException("No such class: " + className, arg3);
		}
	}

	public static Object getPrimitiveDefaultValue(Class forClass) {
		return _primitiveDefaults.get(forClass);
	}

	public static Object getNumericDefaultValue(Class forClass) {
		return NUMERIC_DEFAULTS.get(forClass);
	}

	public static Object getConvertedType(OgnlContext context, Object target, Member member, String propertyName,
			Object value, Class type) {
		return context.getTypeConverter().convertValue(context, target, member, propertyName, value, type);
	}

	public static boolean getConvertedTypes(OgnlContext context, Object target, Member member, String propertyName,
			Class[] parameterTypes, Object[] args, Object[] newArgs) {
		boolean result = false;
		if (parameterTypes.length == args.length) {
			result = true;
			int i = 0;

			for (int ilast = parameterTypes.length - 1; result && i <= ilast; ++i) {
				Object arg = args[i];
				Class type = parameterTypes[i];
				if (isTypeCompatible(arg, type)) {
					newArgs[i] = arg;
				} else {
					Object v = getConvertedType(context, target, member, propertyName, arg, type);
					if (v == NoConversionPossible) {
						result = false;
					} else {
						newArgs[i] = v;
					}
				}
			}
		}

		return result;
	}

	public static Constructor getConvertedConstructorAndArgs(OgnlContext context, Object target, List constructors,
			Object[] args, Object[] newArgs) {
		Constructor result = null;
		TypeConverter converter = context.getTypeConverter();
		if (converter != null && constructors != null) {
			int i = 0;

			for (int icount = constructors.size(); result == null && i < icount; ++i) {
				Constructor ctor = (Constructor) constructors.get(i);
				Class[] parameterTypes = getParameterTypes(ctor);
				if (getConvertedTypes(context, target, ctor, (String) null, parameterTypes, args, newArgs)) {
					result = ctor;
				}
			}
		}

		return result;
	}

	public static Method getAppropriateMethod(OgnlContext context, Object source, Object target, String propertyName,
			String methodName, List methods, Object[] args, Object[] actualArgs) {
		Method result = null;
		if (methods != null) {
			Class typeClass = target != null ? target.getClass() : null;
			if (typeClass == null && source != null && Class.class.isInstance(source)) {
				typeClass = (Class) source;
			}

			Class[] argClasses = getArgClasses(args);
			OgnlRuntime.MatchingMethod mm = findBestMethod(methods, typeClass, methodName, argClasses);
			if (mm != null) {
				result = mm.mMethod;
				Class[] mParameterTypes = mm.mParameterTypes;
				System.arraycopy(args, 0, actualArgs, 0, args.length);

				for (int j = 0; j < mParameterTypes.length; ++j) {
					Class type = mParameterTypes[j];
					if (mm.report.conversionNeeded[j] || type.isPrimitive() && actualArgs[j] == null) {
						actualArgs[j] = getConvertedType(context, source, result, propertyName, args[j], type);
					}
				}
			}
		}

		if (result == null) {
			result = getConvertedMethodAndArgs(context, target, propertyName, methods, args, actualArgs);
		}

		return result;
	}

	public static Method getConvertedMethodAndArgs(OgnlContext context, Object target, String propertyName,
			List methods, Object[] args, Object[] newArgs) {
		Method result = null;
		TypeConverter converter = context.getTypeConverter();
		if (converter != null && methods != null) {
			int i = 0;

			for (int icount = methods.size(); result == null && i < icount; ++i) {
				Method m = (Method) methods.get(i);
				Class[] parameterTypes = findParameterTypes(target != null ? target.getClass() : null, m);
				if (getConvertedTypes(context, target, m, propertyName, parameterTypes, args, newArgs)) {
					result = m;
				}
			}
		}

		return result;
	}

	private static OgnlRuntime.MatchingMethod findBestMethod(List methods, Class typeClass, String name,
			Class[] argClasses) {
		OgnlRuntime.MatchingMethod mm = null;
		IllegalArgumentException failure = null;
		int i = 0;

		for (int icount = methods.size(); i < icount; ++i) {
			Method m = (Method) methods.get(i);
			Class[] mParameterTypes = findParameterTypes(typeClass, m);
			OgnlRuntime.ArgsCompatbilityReport report = areArgsCompatible(argClasses, mParameterTypes, m);
			if (report != null) {
				String methodName = m.getName();
				int score = report.score;
				if (!name.equals(methodName)) {
					if (name.equalsIgnoreCase(methodName)) {
						score += 200;
					} else if (methodName.toLowerCase().endsWith(name.toLowerCase())) {
						score += 500;
					} else {
						score += 5000;
					}
				}

				if (mm != null && mm.score <= score) {
					if (mm.score == score) {
						if (Arrays.equals(mm.mMethod.getParameterTypes(), m.getParameterTypes())
								&& mm.mMethod.getName().equals(m.getName())) {
							boolean arg18 = mm.mMethod.getReturnType().equals(m.getReturnType());
							if (mm.mMethod.getDeclaringClass().isAssignableFrom(m.getDeclaringClass())) {
								if (!arg18 && !mm.mMethod.getReturnType().isAssignableFrom(m.getReturnType())) {
									System.err.println(
											"Two methods with same method signature but return types conflict? \""
													+ mm.mMethod + "\" and \"" + m + "\" please report!");
								}

								mm = new OgnlRuntime.MatchingMethod(m, score, report, mParameterTypes);
								failure = null;
							} else if (!m.getDeclaringClass().isAssignableFrom(mm.mMethod.getDeclaringClass())) {
								System.err.println(
										"Two methods with same method signature but not providing classes assignable? \""
												+ mm.mMethod + "\" and \"" + m + "\" please report!");
							} else if (!arg18 && !m.getReturnType().isAssignableFrom(mm.mMethod.getReturnType())) {
								System.err
										.println("Two methods with same method signature but return types conflict? \""
												+ mm.mMethod + "\" and \"" + m + "\" please report!");
							}
						} else if (isJdk15() && (m.isVarArgs() || mm.mMethod.isVarArgs())) {
							if (!m.isVarArgs() || mm.mMethod.isVarArgs()) {
								if (!m.isVarArgs() && mm.mMethod.isVarArgs()) {
									mm = new OgnlRuntime.MatchingMethod(m, score, report, mParameterTypes);
									failure = null;
								} else {
									System.err.println("Two vararg methods with same score(" + score + "): \""
											+ mm.mMethod + "\" and \"" + m + "\" please report!");
								}
							}
						} else {
							int scoreCurr = 0;
							int scoreOther = 0;

							for (int j = 0; j < argClasses.length; ++j) {
								Class argClass = argClasses[j];
								Class mcClass = mm.mParameterTypes[j];
								Class moClass = mParameterTypes[j];
								if (argClass == null) {
									if (mcClass != moClass) {
										if (mcClass.isAssignableFrom(moClass)) {
											scoreOther += 1000;
										} else if (moClass.isAssignableFrom(moClass)) {
											scoreCurr += 1000;
										} else {
											failure = new IllegalArgumentException(
													"Can\'t decide wich method to use: \"" + mm.mMethod + "\" or \"" + m
															+ "\"");
										}
									}
								} else if (mcClass != moClass) {
									if (mcClass == argClass) {
										scoreOther += 100;
									} else if (moClass == argClass) {
										scoreCurr += 100;
									} else {
										failure = new IllegalArgumentException("Can\'t decide wich method to use: \""
												+ mm.mMethod + "\" or \"" + m + "\"");
									}
								}
							}

							if (scoreCurr == scoreOther) {
								if (failure == null) {
									System.err.println("Two methods with same score(" + score + "): \"" + mm.mMethod
											+ "\" and \"" + m + "\" please report!");
								}
							} else if (scoreCurr > scoreOther) {
								mm = new OgnlRuntime.MatchingMethod(m, score, report, mParameterTypes);
								failure = null;
							}
						}
					}
				} else {
					mm = new OgnlRuntime.MatchingMethod(m, score, report, mParameterTypes);
					failure = null;
				}
			}
		}

		if (failure != null) {
			throw failure;
		} else {
			return mm;
		}
	}

	public static Object callAppropriateMethod(OgnlContext context, Object source, Object target, String methodName,
			String propertyName, List methods, Object[] args) throws MethodFailedException {
		Object reason = null;
		Object[] actualArgs = _objectArrayPool.create(args.length);

		try {
			Method e = getAppropriateMethod(context, source, target, propertyName, methodName, methods, args,
					actualArgs);
			int i;
			if (e == null || !isMethodAccessible(context, source, e, propertyName)) {
				StringBuffer arg24 = new StringBuffer();
				String arg26 = "";
				if (target != null) {
					arg26 = target.getClass().getName() + ".";
				}

				i = 0;

				for (int arg27 = args.length - 1; i <= arg27; ++i) {
					Object arg28 = args[i];
					arg24.append(arg28 == null ? NULL_STRING : arg28.getClass().getName());
					if (i < arg27) {
						arg24.append(", ");
					}
				}

				throw new NoSuchMethodException(arg26 + methodName + "(" + arg24 + ")");
			}

			Object[] convertedArgs = actualArgs;
			if (isJdk15() && e.isVarArgs()) {
				Class[] parmTypes = e.getParameterTypes();

				for (i = 0; i < parmTypes.length; ++i) {
					if (parmTypes[i].isArray()) {
						convertedArgs = new Object[i + 1];
						System.arraycopy(actualArgs, 0, convertedArgs, 0, convertedArgs.length);
						Object[] varArgs;
						if (actualArgs.length <= i) {
							varArgs = new Object[0];
						} else {
							ArrayList varArgsList = new ArrayList();

							for (int j = i; j < actualArgs.length; ++j) {
								if (actualArgs[j] != null) {
									varArgsList.add(actualArgs[j]);
								}
							}

							varArgs = varArgsList.toArray();
						}

						convertedArgs[i] = varArgs;
						break;
					}
				}
			}

			Object arg25 = invokeMethod(target, e, convertedArgs);
			return arg25;
		} catch (NoSuchMethodException arg20) {
			reason = arg20;
		} catch (IllegalAccessException arg21) {
			reason = arg21;
		} catch (InvocationTargetException arg22) {
			reason = arg22.getTargetException();
		} finally {
			_objectArrayPool.recycle(actualArgs);
		}

		throw new MethodFailedException(source, methodName, (Throwable) reason);
	}

	public static Object callStaticMethod(OgnlContext context, String className, String methodName, Object[] args)
			throws OgnlException {
		try {
			Class ex = classForName(context, className);
			if (ex == null) {
				throw new ClassNotFoundException("Unable to resolve class with name " + className);
			} else {
				MethodAccessor ma = getMethodAccessor(ex);
				return ma.callStaticMethod(context, ex, methodName, args);
			}
		} catch (ClassNotFoundException arg5) {
			throw new MethodFailedException(className, methodName, arg5);
		}
	}

	public static Object callMethod(OgnlContext context, Object target, String methodName, String propertyName,
			Object[] args) throws OgnlException {
		return callMethod(context, target, methodName == null ? propertyName : methodName, args);
	}

	public static Object callMethod(OgnlContext context, Object target, String methodName, Object[] args)
			throws OgnlException {
		if (target == null) {
			throw new NullPointerException("target is null for method " + methodName);
		} else {
			return getMethodAccessor(target.getClass()).callMethod(context, target, methodName, args);
		}
	}

	public static Object callConstructor(OgnlContext context, String className, Object[] args) throws OgnlException {
		Object reason = null;
		Object[] actualArgs = args;

		Object arg25;
		try {
			Constructor e = null;
			Class[] ctorParameterTypes = null;
			Class target = classForName(context, className);
			List constructors = getConstructors(target);
			int i = 0;

			for (int icount = constructors.size(); i < icount; ++i) {
				Constructor c = (Constructor) constructors.get(i);
				Class[] cParameterTypes = getParameterTypes(c);
				if (areArgsCompatible(args, cParameterTypes)
						&& (e == null || isMoreSpecific(cParameterTypes, ctorParameterTypes))) {
					e = c;
					ctorParameterTypes = cParameterTypes;
				}
			}

			if (e == null) {
				actualArgs = _objectArrayPool.create(args.length);
				if ((e = getConvertedConstructorAndArgs(context, target, constructors, args, actualArgs)) == null) {
					throw new NoSuchMethodException();
				}
			}

			if (!context.getMemberAccess().isAccessible(context, target, e, (String) null)) {
				throw new IllegalAccessException("access denied to " + target.getName() + "()");
			}

			arg25 = e.newInstance(actualArgs);
		} catch (ClassNotFoundException arg19) {
			reason = arg19;
			throw new MethodFailedException(className, "new", (Throwable) reason);
		} catch (NoSuchMethodException arg20) {
			reason = arg20;
			throw new MethodFailedException(className, "new", (Throwable) reason);
		} catch (IllegalAccessException arg21) {
			reason = arg21;
			throw new MethodFailedException(className, "new", (Throwable) reason);
		} catch (InvocationTargetException arg22) {
			reason = arg22.getTargetException();
			throw new MethodFailedException(className, "new", (Throwable) reason);
		} catch (InstantiationException arg23) {
			reason = arg23;
			throw new MethodFailedException(className, "new", (Throwable) reason);
		} finally {
			if (actualArgs != args) {
				_objectArrayPool.recycle(actualArgs);
			}

		}

		return arg25;
	}

	@Deprecated
	public static final Object getMethodValue(OgnlContext context, Object target, String propertyName)
			throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
		return getMethodValue(context, target, propertyName, false);
	}

	public static final Object getMethodValue(OgnlContext context, Object target, String propertyName,
			boolean checkAccessAndExistence)
			throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
		Object result = null;
		Method m = getGetMethod(context, target == null ? null : target.getClass(), propertyName);
		if (m == null) {
			m = getReadMethod(target == null ? null : target.getClass(), propertyName, (Class[]) null);
		}

		if (checkAccessAndExistence
				&& (m == null || !context.getMemberAccess().isAccessible(context, target, m, propertyName))) {
			result = NotFound;
		}

		if (result == null) {
			if (m == null) {
				throw new NoSuchMethodException(propertyName);
			}

			try {
				result = invokeMethod(target, m, NoArguments);
			} catch (InvocationTargetException arg6) {
				throw new OgnlException(propertyName, arg6.getTargetException());
			}
		}

		return result;
	}

	@Deprecated
	public static boolean setMethodValue(OgnlContext context, Object target, String propertyName, Object value)
			throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
		return setMethodValue(context, target, propertyName, value, false);
	}

	public static boolean setMethodValue(OgnlContext context, Object target, String propertyName, Object value,
			boolean checkAccessAndExistence)
			throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
		boolean result = true;
		Method m = getSetMethod(context, target == null ? null : target.getClass(), propertyName);
		if (checkAccessAndExistence
				&& (m == null || !context.getMemberAccess().isAccessible(context, target, m, propertyName))) {
			result = false;
		}

		if (result) {
			if (m != null) {
				Object[] args = _objectArrayPool.create(value);

				try {
					callAppropriateMethod(context, target, target, m.getName(), propertyName, Collections.nCopies(1, m),
							args);
				} finally {
					_objectArrayPool.recycle(args);
				}
			} else {
				result = false;
			}
		}

		return result;
	}

	public static List getConstructors(Class targetClass) {
		List result;
		if ((result = (List) _constructorCache.get(targetClass)) == null) {
			ClassCache arg1 = _constructorCache;
			synchronized (_constructorCache) {
				if ((result = (List) _constructorCache.get(targetClass)) == null) {
					_constructorCache.put(targetClass, result = Arrays.asList(targetClass.getConstructors()));
				}
			}
		}

		return result;
	}

	public static Map getMethods(Class targetClass, boolean staticMethods) {
		ClassCache cache = staticMethods ? _staticMethodCache : _instanceMethodCache;
		Object result;
		if ((result = (Map) cache.get(targetClass)) == null) {
			synchronized (cache) {
				if ((result = (Map) cache.get(targetClass)) == null) {
					result = new HashMap(23);
					LinkedList toExamined = new LinkedList();

					for (Class i$ = targetClass; i$ != null; i$ = i$.getSuperclass()) {
						toExamined.add(i$);
					}

					toExamined.addAll(Arrays.asList(targetClass.getInterfaces()));
					Iterator arg13 = toExamined.iterator();

					while (true) {
						if (!arg13.hasNext()) {
							cache.put(targetClass, result);
							break;
						}

						Class c = (Class) arg13.next();
						Method[] ma = c.getDeclaredMethods();
						int i = 0;

						for (int icount = ma.length; i < icount; ++i) {
							if (isMethodCallable(ma[i]) && Modifier.isStatic(ma[i].getModifiers()) == staticMethods) {
								Object ml = (List) ((Map) result).get(ma[i].getName());
								if (ml == null) {
									((Map) result).put(ma[i].getName(), ml = new ArrayList());
								}

								((List) ml).add(ma[i]);
							}
						}
					}
				}
			}
		}

		return (Map) result;
	}

	public static Map getAllMethods(Class targetClass, boolean staticMethods) {
		ClassCache cache = staticMethods ? _staticMethodCache : _instanceMethodCache;
		Object result;
		if ((result = (Map) cache.get(targetClass)) == null) {
			synchronized (cache) {
				if ((result = (Map) cache.get(targetClass)) == null) {
					result = new HashMap(23);
					Class c = targetClass;

					while (true) {
						if (c == null) {
							cache.put(targetClass, result);
							break;
						}

						Method[] ma = c.getMethods();
						int i = 0;

						for (int icount = ma.length; i < icount; ++i) {
							if (isMethodCallable(ma[i]) && Modifier.isStatic(ma[i].getModifiers()) == staticMethods) {
								Object ml = (List) ((Map) result).get(ma[i].getName());
								if (ml == null) {
									((Map) result).put(ma[i].getName(), ml = new ArrayList());
								}

								((List) ml).add(ma[i]);
							}
						}

						c = c.getSuperclass();
					}
				}
			}
		}

		return (Map) result;
	}

	public static List getMethods(Class targetClass, String name, boolean staticMethods) {
		return (List) getMethods(targetClass, staticMethods).get(name);
	}

	public static List getAllMethods(Class targetClass, String name, boolean staticMethods) {
		return (List) getAllMethods(targetClass, staticMethods).get(name);
	}

	public static Map getFields(Class targetClass) {
		Object result;
		if ((result = (Map) _fieldCache.get(targetClass)) == null) {
			ClassCache arg1 = _fieldCache;
			synchronized (_fieldCache) {
				if ((result = (Map) _fieldCache.get(targetClass)) == null) {
					result = new HashMap(23);
					Field[] fa = targetClass.getDeclaredFields();

					for (int i = 0; i < fa.length; ++i) {
						((Map) result).put(fa[i].getName(), fa[i]);
					}

					_fieldCache.put(targetClass, result);
				}
			}
		}

		return (Map) result;
	}

	public static Field getField(Class inClass, String name) {
		Field result = null;
		Object o = getFields(inClass).get(name);
		if (o == null) {
			ClassCache arg3 = _fieldCache;
			synchronized (_fieldCache) {
				o = getFields(inClass).get(name);
				if (o != null) {
					if (o instanceof Field) {
						result = (Field) o;
					} else if (result == NotFound) {
						result = null;
					}
				} else {
					_superclasses.clear();

					for (Class i = inClass; i != null
							&& (o = getFields(i).get(name)) != NotFound; i = i.getSuperclass()) {
						_superclasses.add(i);
						if ((result = (Field) o) != null) {
							break;
						}
					}

					int arg8 = 0;

					for (int icount = _superclasses.size(); arg8 < icount; ++arg8) {
						getFields((Class) _superclasses.get(arg8)).put(name, result == null ? NotFound : result);
					}
				}
			}
		} else if (o instanceof Field) {
			result = (Field) o;
		} else if (result == NotFound) {
			result = null;
		}

		return result;
	}

	@Deprecated
	public static Object getFieldValue(OgnlContext context, Object target, String propertyName)
			throws NoSuchFieldException {
		return getFieldValue(context, target, propertyName, false);
	}

	public static Object getFieldValue(OgnlContext context, Object target, String propertyName,
			boolean checkAccessAndExistence) throws NoSuchFieldException {
		Object result = null;
		Field f = getField(target == null ? null : target.getClass(), propertyName);
		if (checkAccessAndExistence
				&& (f == null || !context.getMemberAccess().isAccessible(context, target, f, propertyName))) {
			result = NotFound;
		}

		if (result == null) {
			if (f == null) {
				throw new NoSuchFieldException(propertyName);
			}

			try {
				Object ex = null;
				if (Modifier.isStatic(f.getModifiers())) {
					throw new NoSuchFieldException(propertyName);
				}

				ex = context.getMemberAccess().setup(context, target, f, propertyName);
				result = f.get(target);
				context.getMemberAccess().restore(context, target, f, propertyName, ex);
			} catch (IllegalAccessException arg6) {
				throw new NoSuchFieldException(propertyName);
			}
		}

		return result;
	}

	public static boolean setFieldValue(OgnlContext context, Object target, String propertyName, Object value)
			throws OgnlException {
		boolean result = false;

		try {
			Field ex = getField(target == null ? null : target.getClass(), propertyName);
			if (ex != null && !Modifier.isStatic(ex.getModifiers())) {
				Object state = context.getMemberAccess().setup(context, target, ex, propertyName);

				try {
					if (isTypeCompatible(value, ex.getType()) || (value = getConvertedType(context, target, ex,
							propertyName, value, ex.getType())) != null) {
						ex.set(target, value);
						result = true;
					}
				} finally {
					context.getMemberAccess().restore(context, target, ex, propertyName, state);
				}
			}

			return result;
		} catch (IllegalAccessException arg10) {
			throw new NoSuchPropertyException(target, propertyName, arg10);
		}
	}

	public static boolean isFieldAccessible(OgnlContext context, Object target, Class inClass, String propertyName) {
		return isFieldAccessible(context, target, getField(inClass, propertyName), propertyName);
	}

	public static boolean isFieldAccessible(OgnlContext context, Object target, Field field, String propertyName) {
		return context.getMemberAccess().isAccessible(context, target, field, propertyName);
	}

	public static boolean hasField(OgnlContext context, Object target, Class inClass, String propertyName) {
		Field f = getField(inClass, propertyName);
		return f != null && isFieldAccessible(context, target, f, propertyName);
	}

	public static Object getStaticField(OgnlContext context, String className, String fieldName) throws OgnlException {
		Object reason = null;

		try {
			Class e = classForName(context, className);
			if (e == null) {
				throw new OgnlException(
						"Unable to find class " + className + " when resolving field name of " + fieldName);
			}

			if (fieldName.equals("class")) {
				return e;
			}

			if (isJdk15() && e.isEnum()) {
				try {
					return Enum.valueOf(e, fieldName);
				} catch (IllegalArgumentException arg5) {
					;
				}
			}

			Field f = e.getField(fieldName);
			if (!Modifier.isStatic(f.getModifiers())) {
				throw new OgnlException("Field " + fieldName + " of class " + className + " is not static");
			}

			return f.get((Object) null);
		} catch (ClassNotFoundException arg6) {
			reason = arg6;
		} catch (NoSuchFieldException arg7) {
			reason = arg7;
		} catch (SecurityException arg8) {
			reason = arg8;
		} catch (IllegalAccessException arg9) {
			reason = arg9;
		}

		throw new OgnlException("Could not get static field " + fieldName + " from class " + className,
				(Throwable) reason);
	}

	private static String capitalizeBeanPropertyName(String propertyName) {
		if (propertyName.length() == 1) {
			return propertyName.toUpperCase();
		} else if (propertyName.startsWith("get") && propertyName.endsWith("()")
				&& Character.isUpperCase(propertyName.substring(3, 4).charAt(0))) {
			return propertyName;
		} else if (propertyName.startsWith("set") && propertyName.endsWith(")")
				&& Character.isUpperCase(propertyName.substring(3, 4).charAt(0))) {
			return propertyName;
		} else if (propertyName.startsWith("is") && propertyName.endsWith("()")
				&& Character.isUpperCase(propertyName.substring(2, 3).charAt(0))) {
			return propertyName;
		} else {
			char first = propertyName.charAt(0);
			char second = propertyName.charAt(1);
			if (Character.isLowerCase(first) && Character.isUpperCase(second)) {
				return propertyName;
			} else {
				char[] chars = propertyName.toCharArray();
				chars[0] = Character.toUpperCase(chars[0]);
				return new String(chars);
			}
		}
	}

	public static List getDeclaredMethods(Class targetClass, String propertyName, boolean findSets) {
		Object result = null;
		ClassCache cache = _declaredMethods[findSets ? 0 : 1];
		Map propertyCache = (Map) cache.get(targetClass);
		if (propertyCache == null || (result = (List) propertyCache.get(propertyName)) == null) {
			synchronized (cache) {
				Object arg16 = (Map) cache.get(targetClass);
				if (arg16 == null || (result = (List) ((Map) arg16).get(propertyName)) == null) {
					String baseName = capitalizeBeanPropertyName(propertyName);
					Class c = targetClass;

					while (true) {
						if (c == null) {
							if (arg16 == null) {
								cache.put(targetClass, arg16 = new HashMap(101));
							}

							((Map) arg16).put(propertyName, result == null ? NotFoundList : result);
							break;
						}

						Method[] methods = c.getDeclaredMethods();

						for (int i = 0; i < methods.length; ++i) {
							if (isMethodCallable(methods[i])) {
								String ms = methods[i].getName();
								if (ms.endsWith(baseName)) {
									boolean isSet = false;
									boolean isIs = false;
									if ((isSet = ms.startsWith("set")) || ms.startsWith("get")
											|| (isIs = ms.startsWith("is"))) {
										int prefixLength = isIs ? 2 : 3;
										if (isSet == findSets && baseName.length() == ms.length() - prefixLength) {
											if (result == null) {
												result = new ArrayList();
											}

											((List) result).add(methods[i]);
										}
									}
								}
							}
						}

						c = c.getSuperclass();
					}
				}
			}
		}

		return (List) (result == NotFoundList ? null : result);
	}

	static boolean isMethodCallable(Method m) {
		return (!isJdk15() || !m.isSynthetic()) && !Modifier.isVolatile(m.getModifiers());
	}

	public static Method getGetMethod(OgnlContext context, Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		Method method = cacheGetMethod.get(targetClass, propertyName);
		if (method != null) {
			return method;
		} else if (cacheGetMethod.containsKey(targetClass, propertyName)) {
			return null;
		} else {
			method = _getGetMethod(context, targetClass, propertyName);
			cacheGetMethod.put(targetClass, propertyName, method);
			return method;
		}
	}

	private static Method _getGetMethod(OgnlContext context, Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		Method result = null;
		List methods = getDeclaredMethods(targetClass, propertyName, false);
		if (methods != null) {
			int i = 0;

			for (int icount = methods.size(); i < icount; ++i) {
				Method m = (Method) methods.get(i);
				Class[] mParameterTypes = findParameterTypes(targetClass, m);
				if (mParameterTypes.length == 0) {
					result = m;
					break;
				}
			}
		}

		return result;
	}

	public static boolean isMethodAccessible(OgnlContext context, Object target, Method method, String propertyName) {
		return method != null && context.getMemberAccess().isAccessible(context, target, method, propertyName);
	}

	public static boolean hasGetMethod(OgnlContext context, Object target, Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		return isMethodAccessible(context, target, getGetMethod(context, targetClass, propertyName), propertyName);
	}

	public static Method getSetMethod(OgnlContext context, Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		Method method = cacheSetMethod.get(targetClass, propertyName);
		if (method != null) {
			return method;
		} else if (cacheSetMethod.containsKey(targetClass, propertyName)) {
			return null;
		} else {
			method = _getSetMethod(context, targetClass, propertyName);
			cacheSetMethod.put(targetClass, propertyName, method);
			return method;
		}
	}

	private static Method _getSetMethod(OgnlContext context, Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		Method result = null;
		List methods = getDeclaredMethods(targetClass, propertyName, true);
		if (methods != null) {
			int i = 0;

			for (int icount = methods.size(); i < icount; ++i) {
				Method m = (Method) methods.get(i);
				Class[] mParameterTypes = findParameterTypes(targetClass, m);
				if (mParameterTypes.length == 1) {
					result = m;
					break;
				}
			}
		}

		return result;
	}

	public static final boolean hasSetMethod(OgnlContext context, Object target, Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		return isMethodAccessible(context, target, getSetMethod(context, targetClass, propertyName), propertyName);
	}

	public static final boolean hasGetProperty(OgnlContext context, Object target, Object oname)
			throws IntrospectionException, OgnlException {
		Class targetClass = target == null ? null : target.getClass();
		String name = oname.toString();
		return hasGetMethod(context, target, targetClass, name) || hasField(context, target, targetClass, name);
	}

	public static final boolean hasSetProperty(OgnlContext context, Object target, Object oname)
			throws IntrospectionException, OgnlException {
		Class targetClass = target == null ? null : target.getClass();
		String name = oname.toString();
		return hasSetMethod(context, target, targetClass, name) || hasField(context, target, targetClass, name);
	}

	private static final boolean indexMethodCheck(List methods) {
		boolean result = false;
		if (methods.size() > 0) {
			Method fm = (Method) methods.get(0);
			Class[] fmpt = getParameterTypes(fm);
			int fmpc = fmpt.length;
			Class lastMethodClass = fm.getDeclaringClass();
			result = true;

			for (int i = 1; result && i < methods.size(); ++i) {
				Method m = (Method) methods.get(i);
				Class c = m.getDeclaringClass();
				if (lastMethodClass == c) {
					result = false;
				} else {
					Class[] mpt = getParameterTypes(fm);
					int mpc = fmpt.length;
					if (fmpc != mpc) {
						result = false;
					}

					for (int j = 0; j < fmpc; ++j) {
						if (fmpt[j] != mpt[j]) {
							result = false;
							break;
						}
					}
				}

				lastMethodClass = c;
			}
		}

		return result;
	}

	static void findObjectIndexedPropertyDescriptors(Class targetClass, Map intoMap) throws OgnlException {
		Map allMethods = getMethods(targetClass, false);
		HashMap pairs = new HashMap(101);
		Iterator it = allMethods.keySet().iterator();

		while (true) {
			String propertyName;
			boolean method1;
			boolean method2;
			Method setMethod;
			do {
				List methods;
				do {
					if (!it.hasNext()) {
						it = pairs.keySet().iterator();

						while (it.hasNext()) {
							propertyName = (String) it.next();
							methods = (List) pairs.get(propertyName);
							if (methods.size() == 2) {
								Method method11 = (Method) methods.get(0);
								Method method21 = (Method) methods.get(1);
								setMethod = method11.getParameterTypes().length == 2 ? method11 : method21;
								Method getMethod1 = setMethod == method11 ? method21 : method11;
								Class keyType1 = getMethod1.getParameterTypes()[0];
								Class propertyType1 = getMethod1.getReturnType();
								if (keyType1 == setMethod.getParameterTypes()[0]
										&& propertyType1 == setMethod.getParameterTypes()[1]) {
									ObjectIndexedPropertyDescriptor propertyDescriptor1;
									try {
										propertyDescriptor1 = new ObjectIndexedPropertyDescriptor(propertyName,
												propertyType1, getMethod1, setMethod);
									} catch (Exception arg14) {
										throw new OgnlException("creating object indexed property descriptor for \'"
												+ propertyName + "\' in " + targetClass, arg14);
									}

									intoMap.put(propertyName, propertyDescriptor1);
								}
							}
						}

						return;
					}

					propertyName = (String) it.next();
					methods = (List) allMethods.get(propertyName);
				} while (!indexMethodCheck(methods));

				method1 = false;
				method2 = false;
				setMethod = (Method) methods.get(0);
			} while (!(method2 = propertyName.startsWith("set")) && !(method1 = propertyName.startsWith("get")));

			if (propertyName.length() > 3) {
				String getMethod = Introspector.decapitalize(propertyName.substring(3));
				Class[] keyType = getParameterTypes(setMethod);
				int propertyType = keyType.length;
				Object propertyDescriptor;
				if (method1 && propertyType == 1 && setMethod.getReturnType() != Void.TYPE) {
					propertyDescriptor = (List) pairs.get(getMethod);
					if (propertyDescriptor == null) {
						pairs.put(getMethod, propertyDescriptor = new ArrayList());
					}

					((List) propertyDescriptor).add(setMethod);
				}

				if (method2 && propertyType == 2 && setMethod.getReturnType() == Void.TYPE) {
					propertyDescriptor = (List) pairs.get(getMethod);
					if (propertyDescriptor == null) {
						pairs.put(getMethod, propertyDescriptor = new ArrayList());
					}

					((List) propertyDescriptor).add(setMethod);
				}
			}
		}
	}

	public static Map getPropertyDescriptors(Class targetClass) throws IntrospectionException, OgnlException {
		Object result;
		if ((result = (Map) _propertyDescriptorCache.get(targetClass)) == null) {
			ClassCache arg1 = _propertyDescriptorCache;
			synchronized (_propertyDescriptorCache) {
				if ((result = (Map) _propertyDescriptorCache.get(targetClass)) == null) {
					PropertyDescriptor[] pda = Introspector.getBeanInfo(targetClass).getPropertyDescriptors();
					result = new HashMap(101);
					int i = 0;

					for (int icount = pda.length; i < icount; ++i) {
						if (pda[i].getReadMethod() != null && !isMethodCallable(pda[i].getReadMethod())) {
							pda[i].setReadMethod(findClosestMatchingMethod(targetClass, pda[i].getReadMethod(),
									pda[i].getName(), pda[i].getPropertyType(), true));
						}

						if (pda[i].getWriteMethod() != null && !isMethodCallable(pda[i].getWriteMethod())) {
							pda[i].setWriteMethod(findClosestMatchingMethod(targetClass, pda[i].getWriteMethod(),
									pda[i].getName(), pda[i].getPropertyType(), false));
						}

						((Map) result).put(pda[i].getName(), pda[i]);
					}

					findObjectIndexedPropertyDescriptors(targetClass, (Map) result);
					_propertyDescriptorCache.put(targetClass, result);
				}
			}
		}

		return (Map) result;
	}

	public static PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName)
			throws IntrospectionException, OgnlException {
		return targetClass == null ? null : (PropertyDescriptor) getPropertyDescriptors(targetClass).get(propertyName);
	}

	static Method findClosestMatchingMethod(Class targetClass, Method m, String propertyName, Class propertyType,
			boolean isReadMethod) {
		List methods = getDeclaredMethods(targetClass, propertyName, !isReadMethod);
		if (methods != null) {
			Iterator i$ = methods.iterator();

			while (i$.hasNext()) {
				Object method1 = i$.next();
				Method method = (Method) method1;
				if (method.getName().equals(m.getName()) && m.getReturnType().isAssignableFrom(m.getReturnType())
						&& method.getReturnType() == propertyType
						&& method.getParameterTypes().length == m.getParameterTypes().length) {
					return method;
				}
			}
		}

		return m;
	}

	public static PropertyDescriptor[] getPropertyDescriptorsArray(Class targetClass) throws IntrospectionException {
		PropertyDescriptor[] result = null;
		if (targetClass != null && (result = (PropertyDescriptor[]) ((PropertyDescriptor[]) _propertyDescriptorCache
				.get(targetClass))) == null) {
			ClassCache arg1 = _propertyDescriptorCache;
			synchronized (_propertyDescriptorCache) {
				if ((result = (PropertyDescriptor[]) ((PropertyDescriptor[]) _propertyDescriptorCache
						.get(targetClass))) == null) {
					_propertyDescriptorCache.put(targetClass,
							result = Introspector.getBeanInfo(targetClass).getPropertyDescriptors());
				}
			}
		}

		return result;
	}

	public static PropertyDescriptor getPropertyDescriptorFromArray(Class targetClass, String name)
			throws IntrospectionException {
		PropertyDescriptor result = null;
		PropertyDescriptor[] pda = getPropertyDescriptorsArray(targetClass);
		int i = 0;

		for (int icount = pda.length; result == null && i < icount; ++i) {
			if (pda[i].getName().compareTo(name) == 0) {
				result = pda[i];
			}
		}

		return result;
	}

	public static void setMethodAccessor(Class cls, MethodAccessor accessor) {
		ClassCache arg1 = _methodAccessors;
		synchronized (_methodAccessors) {
			_methodAccessors.put(cls, accessor);
		}
	}

	public static MethodAccessor getMethodAccessor(Class cls) throws OgnlException {
		MethodAccessor answer = (MethodAccessor) getHandler(cls, _methodAccessors);
		if (answer != null) {
			return answer;
		} else {
			throw new OgnlException("No method accessor for " + cls);
		}
	}

	public static void setPropertyAccessor(Class cls, PropertyAccessor accessor) {
		ClassCache arg1 = _propertyAccessors;
		synchronized (_propertyAccessors) {
			_propertyAccessors.put(cls, accessor);
		}
	}

	public static PropertyAccessor getPropertyAccessor(Class cls) throws OgnlException {
		PropertyAccessor answer = (PropertyAccessor) getHandler(cls, _propertyAccessors);
		if (answer != null) {
			return answer;
		} else {
			throw new OgnlException("No property accessor for class " + cls);
		}
	}

	public static ElementsAccessor getElementsAccessor(Class cls) throws OgnlException {
		ElementsAccessor answer = (ElementsAccessor) getHandler(cls, _elementsAccessors);
		if (answer != null) {
			return answer;
		} else {
			throw new OgnlException("No elements accessor for class " + cls);
		}
	}

	public static void setElementsAccessor(Class cls, ElementsAccessor accessor) {
		ClassCache arg1 = _elementsAccessors;
		synchronized (_elementsAccessors) {
			_elementsAccessors.put(cls, accessor);
		}
	}

	public static NullHandler getNullHandler(Class cls) throws OgnlException {
		NullHandler answer = (NullHandler) getHandler(cls, _nullHandlers);
		if (answer != null) {
			return answer;
		} else {
			throw new OgnlException("No null handler for class " + cls);
		}
	}

	public static void setNullHandler(Class cls, NullHandler handler) {
		ClassCache arg1 = _nullHandlers;
		synchronized (_nullHandlers) {
			_nullHandlers.put(cls, handler);
		}
	}

	private static Object getHandler(Class forClass, ClassCache handlers) {
		Object answer = null;
		if ((answer = handlers.get(forClass)) == null) {
			synchronized (handlers) {
				if ((answer = handlers.get(forClass)) == null) {
					Class keyFound;
					if (forClass.isArray()) {
						answer = handlers.get(Object[].class);
						keyFound = null;
					} else {
						keyFound = forClass;

						label51: for (Class c = forClass; c != null; c = c.getSuperclass()) {
							answer = handlers.get(c);
							if (answer != null) {
								keyFound = c;
								break;
							}

							Class[] interfaces = c.getInterfaces();
							int index = 0;

							for (int count = interfaces.length; index < count; ++index) {
								Class iface = interfaces[index];
								answer = handlers.get(iface);
								if (answer == null) {
									answer = getHandler(iface, handlers);
								}

								if (answer != null) {
									keyFound = iface;
									break label51;
								}
							}
						}
					}

					if (answer != null && keyFound != forClass) {
						handlers.put(forClass, answer);
					}
				}
			}
		}

		return answer;
	}

	public static Object getProperty(OgnlContext context, Object source, Object name) throws OgnlException {
		if (source == null) {
			throw new OgnlException("source is null for getProperty(null, \"" + name + "\")");
		} else {
			PropertyAccessor accessor;
			if ((accessor = getPropertyAccessor(getTargetClass(source))) == null) {
				throw new OgnlException("No property accessor for " + getTargetClass(source).getName());
			} else {
				return accessor.getProperty(context, source, name);
			}
		}
	}

	public static void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
		if (target == null) {
			throw new OgnlException("target is null for setProperty(null, \"" + name + "\", " + value + ")");
		} else {
			PropertyAccessor accessor;
			if ((accessor = getPropertyAccessor(getTargetClass(target))) == null) {
				throw new OgnlException("No property accessor for " + getTargetClass(target).getName());
			} else {
				accessor.setProperty(context, target, name, value);
			}
		}
	}

	public static int getIndexedPropertyType(OgnlContext context, Class sourceClass, String name) throws OgnlException {
		int result = INDEXED_PROPERTY_NONE;

		try {
			PropertyDescriptor ex = getPropertyDescriptor(sourceClass, name);
			if (ex != null) {
				if (ex instanceof IndexedPropertyDescriptor) {
					result = INDEXED_PROPERTY_INT;
				} else if (ex instanceof ObjectIndexedPropertyDescriptor) {
					result = INDEXED_PROPERTY_OBJECT;
				}
			}

			return result;
		} catch (Exception arg4) {
			throw new OgnlException("problem determining if \'" + name + "\' is an indexed property", arg4);
		}
	}

	public static Object getIndexedProperty(OgnlContext context, Object source, String name, Object index)
			throws OgnlException {
		Object[] args = _objectArrayPool.create(index);

		Object arg6;
		try {
			PropertyDescriptor ex = getPropertyDescriptor(source == null ? null : source.getClass(), name);
			Method m;
			if (ex instanceof IndexedPropertyDescriptor) {
				m = ((IndexedPropertyDescriptor) ex).getIndexedReadMethod();
			} else {
				if (!(ex instanceof ObjectIndexedPropertyDescriptor)) {
					throw new OgnlException("property \'" + name + "\' is not an indexed property");
				}

				m = ((ObjectIndexedPropertyDescriptor) ex).getIndexedReadMethod();
			}

			arg6 = callMethod(context, source, m.getName(), args);
		} catch (OgnlException arg11) {
			throw arg11;
		} catch (Exception arg12) {
			throw new OgnlException("getting indexed property descriptor for \'" + name + "\'", arg12);
		} finally {
			_objectArrayPool.recycle(args);
		}

		return arg6;
	}

	public static void setIndexedProperty(OgnlContext context, Object source, String name, Object index, Object value)
			throws OgnlException {
		Object[] args = _objectArrayPool.create(index, value);

		try {
			PropertyDescriptor ex = getPropertyDescriptor(source == null ? null : source.getClass(), name);
			Method m;
			if (ex instanceof IndexedPropertyDescriptor) {
				m = ((IndexedPropertyDescriptor) ex).getIndexedWriteMethod();
			} else {
				if (!(ex instanceof ObjectIndexedPropertyDescriptor)) {
					throw new OgnlException("property \'" + name + "\' is not an indexed property");
				}

				m = ((ObjectIndexedPropertyDescriptor) ex).getIndexedWriteMethod();
			}

			callMethod(context, source, m.getName(), args);
		} catch (OgnlException arg11) {
			throw arg11;
		} catch (Exception arg12) {
			throw new OgnlException("getting indexed property descriptor for \'" + name + "\'", arg12);
		} finally {
			_objectArrayPool.recycle(args);
		}

	}

	public static EvaluationPool getEvaluationPool() {
		return _evaluationPool;
	}

	public static ObjectArrayPool getObjectArrayPool() {
		return _objectArrayPool;
	}

	public static void setClassCacheInspector(ClassCacheInspector inspector) {
		_cacheInspector = inspector;
		_propertyDescriptorCache.setClassInspector(_cacheInspector);
		_constructorCache.setClassInspector(_cacheInspector);
		_staticMethodCache.setClassInspector(_cacheInspector);
		_instanceMethodCache.setClassInspector(_cacheInspector);
		_invokePermissionCache.setClassInspector(_cacheInspector);
		_fieldCache.setClassInspector(_cacheInspector);
		_declaredMethods[0].setClassInspector(_cacheInspector);
		_declaredMethods[1].setClassInspector(_cacheInspector);
	}

	public static Method getMethod(OgnlContext context, Class target, String name, Node[] children,
			boolean includeStatic) throws Exception {
		Class[] parms;
		if (children != null && children.length > 0) {
			parms = new Class[children.length];
			Class methods = context.getCurrentType();
			Class i = context.getCurrentAccessor();
			Object m = context.get("_preCast");
			context.setCurrentObject(context.getRoot());
			context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
			context.setCurrentAccessor((Class) null);
			context.setPreviousType((Class) null);

			for (int varArgs = 0; varArgs < children.length; ++varArgs) {
				children[varArgs].toGetSourceString(context, context.getRoot());
				parms[varArgs] = context.getCurrentType();
			}

			context.put("_preCast", m);
			context.setCurrentType(methods);
			context.setCurrentAccessor(i);
			context.setCurrentObject(target);
		} else {
			parms = EMPTY_CLASS_ARRAY;
		}

		List arg12 = getMethods(target, name, includeStatic);
		if (arg12 == null) {
			return null;
		} else {
			for (int arg13 = 0; arg13 < arg12.size(); ++arg13) {
				Method arg14 = (Method) arg12.get(arg13);
				boolean arg15 = isJdk15() && arg14.isVarArgs();
				if (parms.length == arg14.getParameterTypes().length || arg15) {
					Class[] mparms = arg14.getParameterTypes();
					boolean matched = true;

					for (int p = 0; p < mparms.length; ++p) {
						if (!arg15 || !mparms[p].isArray()) {
							if (parms[p] == null) {
								matched = false;
								break;
							}

							if (parms[p] != mparms[p] && (!mparms[p].isPrimitive() || Character.TYPE == mparms[p]
									|| Byte.TYPE == mparms[p] || !Number.class.isAssignableFrom(parms[p])
									|| getPrimitiveWrapperClass(parms[p]) != mparms[p])) {
								matched = false;
								break;
							}
						}
					}

					if (matched) {
						return arg14;
					}
				}
			}

			return null;
		}
	}

	public static Method getReadMethod(Class target, String name) {
		return getReadMethod(target, name, (Class[]) null);
	}

	public static Method getReadMethod(Class target, String name, Class[] argClasses) {
		try {
			if (name.indexOf(34) >= 0) {
				name = name.replaceAll("\"", "");
			}

			name = name.toLowerCase();
			BeanInfo t = Introspector.getBeanInfo(target);
			MethodDescriptor[] methods = t.getMethodDescriptors();
			ArrayList candidates = new ArrayList();

			int reqArgCount;
			for (reqArgCount = 0; reqArgCount < methods.length; ++reqArgCount) {
				if (isMethodCallable(methods[reqArgCount].getMethod())
						&& (methods[reqArgCount].getName().equalsIgnoreCase(name)
								|| methods[reqArgCount].getName().toLowerCase().equals(name)
								|| methods[reqArgCount].getName().toLowerCase().equals("get" + name)
								|| methods[reqArgCount].getName().toLowerCase().equals("has" + name)
								|| methods[reqArgCount].getName().toLowerCase().equals("is" + name))
						&& !methods[reqArgCount].getName().startsWith("set")) {
					candidates.add(methods[reqArgCount].getMethod());
				}
			}

			OgnlRuntime.MatchingMethod arg9;
			if (!candidates.isEmpty()) {
				arg9 = findBestMethod(candidates, target, name, argClasses);
				if (arg9 != null) {
					return arg9.mMethod;
				}
			}

			for (reqArgCount = 0; reqArgCount < methods.length; ++reqArgCount) {
				if (isMethodCallable(methods[reqArgCount].getMethod())
						&& methods[reqArgCount].getName().toLowerCase().endsWith(name)
						&& !methods[reqArgCount].getName().startsWith("set")
						&& methods[reqArgCount].getMethod().getReturnType() != Void.TYPE) {
					Method i$ = methods[reqArgCount].getMethod();
					if (!candidates.contains(i$)) {
						candidates.add(i$);
					}
				}
			}

			if (!candidates.isEmpty()) {
				arg9 = findBestMethod(candidates, target, name, argClasses);
				if (arg9 != null) {
					return arg9.mMethod;
				}
			}

			if (!name.startsWith("get")) {
				Method arg11 = getReadMethod(target, "get" + name, argClasses);
				if (arg11 != null) {
					return arg11;
				}
			}

			if (!candidates.isEmpty()) {
				reqArgCount = argClasses == null ? 0 : argClasses.length;
				Iterator arg10 = candidates.iterator();

				while (arg10.hasNext()) {
					Method m = (Method) arg10.next();
					if (m.getParameterTypes().length == reqArgCount) {
						return m;
					}
				}
			}

			return null;
		} catch (Throwable arg8) {
			throw OgnlOps.castToRuntime(arg8);
		}
	}

	public static Method getWriteMethod(Class target, String name) {
		return getWriteMethod(target, name, (Class[]) null);
	}

	public static Method getWriteMethod(Class target, String name, Class[] argClasses) {
		try {
			if (name.indexOf(34) >= 0) {
				name = name.replaceAll("\"", "");
			}

			BeanInfo t = Introspector.getBeanInfo(target);
			MethodDescriptor[] methods = t.getMethodDescriptors();
			ArrayList candidates = new ArrayList();

			for (int cmethods = 0; cmethods < methods.length; ++cmethods) {
				if (isMethodCallable(methods[cmethods].getMethod())
						&& (methods[cmethods].getName().equalsIgnoreCase(name)
								|| methods[cmethods].getName().toLowerCase().equals(name.toLowerCase())
								|| methods[cmethods].getName().toLowerCase().equals("set" + name.toLowerCase()))
						&& !methods[cmethods].getName().startsWith("get")) {
					candidates.add(methods[cmethods].getMethod());
				}
			}

			if (!candidates.isEmpty()) {
				OgnlRuntime.MatchingMethod arg10 = findBestMethod(candidates, target, name, argClasses);
				if (arg10 != null) {
					return arg10.mMethod;
				}
			}

			Method[] arg11 = target.getClass().getMethods();

			int reqArgCount;
			for (reqArgCount = 0; reqArgCount < arg11.length; ++reqArgCount) {
				if (isMethodCallable(arg11[reqArgCount])
						&& (arg11[reqArgCount].getName().equalsIgnoreCase(name)
								|| arg11[reqArgCount].getName().toLowerCase().equals(name.toLowerCase())
								|| arg11[reqArgCount].getName().toLowerCase().equals("set" + name.toLowerCase()))
						&& !arg11[reqArgCount].getName().startsWith("get")) {
					Method i$ = methods[reqArgCount].getMethod();
					if (!candidates.contains(i$)) {
						candidates.add(i$);
					}
				}
			}

			if (!candidates.isEmpty()) {
				OgnlRuntime.MatchingMethod arg12 = findBestMethod(candidates, target, name, argClasses);
				if (arg12 != null) {
					return arg12.mMethod;
				}
			}

			if (!name.startsWith("set")) {
				Method arg13 = getReadMethod(target, "set" + name, argClasses);
				if (arg13 != null) {
					return arg13;
				}
			}

			if (!candidates.isEmpty()) {
				reqArgCount = argClasses == null ? 0 : argClasses.length;
				Iterator arg14 = candidates.iterator();

				while (arg14.hasNext()) {
					Method m = (Method) arg14.next();
					if (m.getParameterTypes().length == reqArgCount) {
						return m;
					}
				}

				if (argClasses == null && candidates.size() == 1) {
					return (Method) candidates.get(0);
				}
			}

			return null;
		} catch (Throwable arg9) {
			throw OgnlOps.castToRuntime(arg9);
		}
	}

	public static PropertyDescriptor getProperty(Class target, String name) {
		try {
			BeanInfo t = Introspector.getBeanInfo(target);
			PropertyDescriptor[] pds = t.getPropertyDescriptors();

			for (int i = 0; i < pds.length; ++i) {
				if (pds[i].getName().equalsIgnoreCase(name) || pds[i].getName().toLowerCase().equals(name.toLowerCase())
						|| pds[i].getName().toLowerCase().endsWith(name.toLowerCase())) {
					return pds[i];
				}
			}

			return null;
		} catch (Throwable arg4) {
			throw OgnlOps.castToRuntime(arg4);
		}
	}

	public static boolean isBoolean(String expression) {
		return expression == null ? false
				: "true".equals(expression) || "false".equals(expression) || "!true".equals(expression)
						|| "!false".equals(expression) || "(true)".equals(expression) || "!(true)".equals(expression)
						|| "(false)".equals(expression) || "!(false)".equals(expression)
						|| expression.startsWith("org.eocencle.winger.ognl.OgnlOps");
	}

	public static boolean shouldConvertNumericTypes(OgnlContext context) {
		return context.getCurrentType() != null && context.getPreviousType() != null
				? (context.getCurrentType() == context.getPreviousType() && context.getCurrentType().isPrimitive()
						&& context.getPreviousType().isPrimitive() ? false
								: context.getCurrentType() != null && !context.getCurrentType().isArray()
										&& context.getPreviousType() != null && !context.getPreviousType().isArray())
				: true;
	}

	public static String getChildSource(OgnlContext context, Object target, Node child) throws OgnlException {
		return getChildSource(context, target, child, false);
	}

	public static String getChildSource(OgnlContext context, Object target, Node child, boolean forceConversion)
			throws OgnlException {
		String pre = (String) context.get("_currentChain");
		if (pre == null) {
			pre = "";
		}

		try {
			child.getValue(context, target);
		} catch (NullPointerException arg7) {
			;
		} catch (ArithmeticException arg8) {
			context.setCurrentType(Integer.TYPE);
			return "0";
		} catch (Throwable arg9) {
			throw OgnlOps.castToRuntime(arg9);
		}

		String source = null;

		try {
			source = child.toGetSourceString(context, target);
		} catch (Throwable arg6) {
			throw OgnlOps.castToRuntime(arg6);
		}

		if (!ASTConst.class.isInstance(child) && (target == null || context.getRoot() != target)) {
			source = pre + source;
		}

		if (context.getRoot() != null) {
			source = ExpressionCompiler.getRootExpression(child, context.getRoot(), context) + source;
			context.setCurrentAccessor(context.getRoot().getClass());
		}

		if (ASTChain.class.isInstance(child)) {
			String cast = (String) context.remove("_preCast");
			if (cast == null) {
				cast = "";
			}

			source = cast + source;
		}

		if (source == null || source.trim().length() < 1) {
			source = "null";
		}

		return source;
	}

	static {
		try {
			Class.forName("org.eocencle.winger.javassist.ClassPool");
			_compiler = new ExpressionCompiler();
		} catch (ClassNotFoundException arg3) {
			throw new IllegalArgumentException(
					"Javassist library is missing in classpath! Please add missed dependency!", arg3);
		}

		EMPTY_CLASS_ARRAY = new Class[0];
		PRIMITIVE_WRAPPER_CLASSES = new IdentityHashMap();
		PRIMITIVE_WRAPPER_CLASSES.put(Boolean.TYPE, Boolean.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Boolean.class, Boolean.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Byte.TYPE, Byte.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Byte.class, Byte.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Character.TYPE, Character.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Character.class, Character.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Short.TYPE, Short.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Short.class, Short.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Integer.TYPE, Integer.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Integer.class, Integer.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Long.TYPE, Long.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Long.class, Long.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Float.TYPE, Float.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Float.class, Float.TYPE);
		PRIMITIVE_WRAPPER_CLASSES.put(Double.TYPE, Double.class);
		PRIMITIVE_WRAPPER_CLASSES.put(Double.class, Double.TYPE);
		NUMERIC_CASTS = new HashMap();
		NUMERIC_CASTS.put(Double.class, "(double)");
		NUMERIC_CASTS.put(Float.class, "(float)");
		NUMERIC_CASTS.put(Integer.class, "(int)");
		NUMERIC_CASTS.put(Long.class, "(long)");
		NUMERIC_CASTS.put(BigDecimal.class, "(double)");
		NUMERIC_CASTS.put(BigInteger.class, "");
		NUMERIC_VALUES = new HashMap();
		NUMERIC_VALUES.put(Double.class, "doubleValue()");
		NUMERIC_VALUES.put(Float.class, "floatValue()");
		NUMERIC_VALUES.put(Integer.class, "intValue()");
		NUMERIC_VALUES.put(Long.class, "longValue()");
		NUMERIC_VALUES.put(Short.class, "shortValue()");
		NUMERIC_VALUES.put(Byte.class, "byteValue()");
		NUMERIC_VALUES.put(BigDecimal.class, "doubleValue()");
		NUMERIC_VALUES.put(BigInteger.class, "doubleValue()");
		NUMERIC_VALUES.put(Boolean.class, "booleanValue()");
		NUMERIC_LITERALS = new HashMap();
		NUMERIC_LITERALS.put(Integer.class, "");
		NUMERIC_LITERALS.put(Integer.TYPE, "");
		NUMERIC_LITERALS.put(Long.class, "l");
		NUMERIC_LITERALS.put(Long.TYPE, "l");
		NUMERIC_LITERALS.put(BigInteger.class, "d");
		NUMERIC_LITERALS.put(Float.class, "f");
		NUMERIC_LITERALS.put(Float.TYPE, "f");
		NUMERIC_LITERALS.put(Double.class, "d");
		NUMERIC_LITERALS.put(Double.TYPE, "d");
		NUMERIC_LITERALS.put(BigInteger.class, "d");
		NUMERIC_LITERALS.put(BigDecimal.class, "d");
		NUMERIC_DEFAULTS = new HashMap();
		NUMERIC_DEFAULTS.put(Boolean.class, Boolean.FALSE);
		NUMERIC_DEFAULTS.put(Byte.class, new Byte((byte) 0));
		NUMERIC_DEFAULTS.put(Short.class, new Short((short) 0));
		NUMERIC_DEFAULTS.put(Character.class, new Character(' '));
		NUMERIC_DEFAULTS.put(Integer.class, new Integer(0));
		NUMERIC_DEFAULTS.put(Long.class, new Long(0L));
		NUMERIC_DEFAULTS.put(Float.class, new Float(0.0F));
		NUMERIC_DEFAULTS.put(Double.class, new Double(0.0D));
		NUMERIC_DEFAULTS.put(BigInteger.class, new BigInteger("0"));
		NUMERIC_DEFAULTS.put(BigDecimal.class, new BigDecimal(0.0D));
		ArrayPropertyAccessor p = new ArrayPropertyAccessor();
		setPropertyAccessor(Object.class, new ObjectPropertyAccessor());
		setPropertyAccessor(byte[].class, p);
		setPropertyAccessor(short[].class, p);
		setPropertyAccessor(char[].class, p);
		setPropertyAccessor(int[].class, p);
		setPropertyAccessor(long[].class, p);
		setPropertyAccessor(float[].class, p);
		setPropertyAccessor(double[].class, p);
		setPropertyAccessor(Object[].class, p);
		setPropertyAccessor(List.class, new ListPropertyAccessor());
		setPropertyAccessor(Map.class, new MapPropertyAccessor());
		setPropertyAccessor(Set.class, new SetPropertyAccessor());
		setPropertyAccessor(Iterator.class, new IteratorPropertyAccessor());
		setPropertyAccessor(Enumeration.class, new EnumerationPropertyAccessor());
		ArrayElementsAccessor e = new ArrayElementsAccessor();
		setElementsAccessor(Object.class, new ObjectElementsAccessor());
		setElementsAccessor(byte[].class, e);
		setElementsAccessor(short[].class, e);
		setElementsAccessor(char[].class, e);
		setElementsAccessor(int[].class, e);
		setElementsAccessor(long[].class, e);
		setElementsAccessor(float[].class, e);
		setElementsAccessor(double[].class, e);
		setElementsAccessor(Object[].class, e);
		setElementsAccessor(Collection.class, new CollectionElementsAccessor());
		setElementsAccessor(Map.class, new MapElementsAccessor());
		setElementsAccessor(Iterator.class, new IteratorElementsAccessor());
		setElementsAccessor(Enumeration.class, new EnumerationElementsAccessor());
		setElementsAccessor(Number.class, new NumberElementsAccessor());
		ObjectNullHandler nh = new ObjectNullHandler();
		setNullHandler(Object.class, nh);
		setNullHandler(byte[].class, nh);
		setNullHandler(short[].class, nh);
		setNullHandler(char[].class, nh);
		setNullHandler(int[].class, nh);
		setNullHandler(long[].class, nh);
		setNullHandler(float[].class, nh);
		setNullHandler(double[].class, nh);
		setNullHandler(Object[].class, nh);
		ObjectMethodAccessor ma = new ObjectMethodAccessor();
		setMethodAccessor(Object.class, ma);
		setMethodAccessor(byte[].class, ma);
		setMethodAccessor(short[].class, ma);
		setMethodAccessor(char[].class, ma);
		setMethodAccessor(int[].class, ma);
		setMethodAccessor(long[].class, ma);
		setMethodAccessor(float[].class, ma);
		setMethodAccessor(double[].class, ma);
		setMethodAccessor(Object[].class, ma);
		_primitiveTypes.put("boolean", Boolean.TYPE);
		_primitiveTypes.put("byte", Byte.TYPE);
		_primitiveTypes.put("short", Short.TYPE);
		_primitiveTypes.put("char", Character.TYPE);
		_primitiveTypes.put("int", Integer.TYPE);
		_primitiveTypes.put("long", Long.TYPE);
		_primitiveTypes.put("float", Float.TYPE);
		_primitiveTypes.put("double", Double.TYPE);
		_primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
		_primitiveDefaults.put(Boolean.class, Boolean.FALSE);
		_primitiveDefaults.put(Byte.TYPE, new Byte((byte) 0));
		_primitiveDefaults.put(Byte.class, new Byte((byte) 0));
		_primitiveDefaults.put(Short.TYPE, new Short((short) 0));
		_primitiveDefaults.put(Short.class, new Short((short) 0));
		_primitiveDefaults.put(Character.TYPE, new Character(' '));
		_primitiveDefaults.put(Integer.TYPE, new Integer(0));
		_primitiveDefaults.put(Long.TYPE, new Long(0L));
		_primitiveDefaults.put(Float.TYPE, new Float(0.0F));
		_primitiveDefaults.put(Double.TYPE, new Double(0.0D));
		_primitiveDefaults.put(BigInteger.class, new BigInteger("0"));
		_primitiveDefaults.put(BigDecimal.class, new BigDecimal(0.0D));
		NoArgsReport = new OgnlRuntime.ArgsCompatbilityReport(0, new boolean[0]);
	}
	
	private static final class ClassPropertyMethodCache {
		private static final Method NULL_REPLACEMENT;
		private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> cache = new ConcurrentHashMap();

		Method get(Class clazz, String propertyName) {
			ConcurrentHashMap methodsByPropertyName = (ConcurrentHashMap) this.cache.get(clazz);
			if (methodsByPropertyName == null) {
				methodsByPropertyName = new ConcurrentHashMap();
				this.cache.put(clazz, methodsByPropertyName);
			}

			Method method = (Method) methodsByPropertyName.get(propertyName);
			return method == NULL_REPLACEMENT ? null : method;
		}

		void put(Class clazz, String propertyName, Method method) {
			ConcurrentHashMap methodsByPropertyName = (ConcurrentHashMap) this.cache.get(clazz);
			if (methodsByPropertyName == null) {
				methodsByPropertyName = new ConcurrentHashMap();
				this.cache.put(clazz, methodsByPropertyName);
			}

			methodsByPropertyName.put(propertyName, method == null ? NULL_REPLACEMENT : method);
		}

		boolean containsKey(Class clazz, String propertyName) {
			ConcurrentHashMap methodsByPropertyName = (ConcurrentHashMap) this.cache.get(clazz);
			return methodsByPropertyName == null ? false : methodsByPropertyName.containsKey(propertyName);
		}

		static {
			try {
				NULL_REPLACEMENT = OgnlRuntime.ClassPropertyMethodCache.class.getDeclaredMethod("get",
						new Class[] { Class.class, String.class });
			} catch (NoSuchMethodException arg0) {
				throw new RuntimeException(arg0);
			}
		}
	}

	private static class MatchingMethod {
		Method mMethod;
		int score;
		OgnlRuntime.ArgsCompatbilityReport report;
		Class[] mParameterTypes;

		private MatchingMethod(Method method, int score, OgnlRuntime.ArgsCompatbilityReport report,
				Class[] mParameterTypes) {
			this.mMethod = method;
			this.score = score;
			this.report = report;
			this.mParameterTypes = mParameterTypes;
		}
	}

	public static class ArgsCompatbilityReport {
		int score;
		boolean[] conversionNeeded;

		public ArgsCompatbilityReport(int score, boolean[] conversionNeeded) {
			this.score = score;
			this.conversionNeeded = conversionNeeded;
		}
	}
}
