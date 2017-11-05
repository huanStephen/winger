package org.eocencle.winger.ognl;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Enumeration;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public abstract class OgnlOps implements NumericTypes {
	public static int compareWithConversion(Object v1, Object v2) {
		int result;
		if (v1 == v2) {
			result = 0;
		} else {
			int t1 = getNumericType(v1);
			int t2 = getNumericType(v2);
			int type = getNumericType(t1, t2, true);
			switch (type) {
			case 6:
				result = bigIntValue(v1).compareTo(bigIntValue(v2));
				break;
			case 9:
				result = bigDecValue(v1).compareTo(bigDecValue(v2));
				break;
			case 10:
				if (t1 == 10 && t2 == 10) {
					if (v1 instanceof Comparable && v1.getClass().isAssignableFrom(v2.getClass())) {
						result = ((Comparable) v1).compareTo(v2);
						break;
					}

					throw new IllegalArgumentException(
							"invalid comparison: " + v1.getClass().getName() + " and " + v2.getClass().getName());
				}
			case 7:
			case 8:
				double dv1 = doubleValue(v1);
				double dv2 = doubleValue(v2);
				return dv1 == dv2 ? 0 : (dv1 < dv2 ? -1 : 1);
			default:
				long lv1 = longValue(v1);
				long lv2 = longValue(v2);
				return lv1 == lv2 ? 0 : (lv1 < lv2 ? -1 : 1);
			}
		}

		return result;
	}

	public static boolean isEqual(Object object1, Object object2) {
		boolean result = false;
		if (object1 == object2) {
			result = true;
		} else if (object1 != null && object1.getClass().isArray()) {
			if (object2 != null && object2.getClass().isArray() && object2.getClass() == object1.getClass()) {
				result = Array.getLength(object1) == Array.getLength(object2);
				if (result) {
					int i = 0;

					for (int icount = Array.getLength(object1); result && i < icount; ++i) {
						result = isEqual(Array.get(object1, i), Array.get(object2, i));
					}
				}
			}
		} else {
			result = object1 != null && object2 != null
					&& (object1.equals(object2) || compareWithConversion(object1, object2) == 0);
		}

		return result;
	}

	public static boolean booleanValue(boolean value) {
		return value;
	}

	public static boolean booleanValue(int value) {
		return value > 0;
	}

	public static boolean booleanValue(float value) {
		return value > 0.0F;
	}

	public static boolean booleanValue(long value) {
		return value > 0L;
	}

	public static boolean booleanValue(double value) {
		return value > 0.0D;
	}

	public static boolean booleanValue(Object value) {
		if (value == null) {
			return false;
		} else {
			Class c = value.getClass();
			return c == Boolean.class ? ((Boolean) value).booleanValue()
					: (c == String.class ? Boolean.parseBoolean(String.valueOf(value))
							: (c == Character.class ? ((Character) value).charValue() != 0
									: (value instanceof Number ? ((Number) value).doubleValue() != 0.0D : true)));
		}
	}

	public static long longValue(Object value) throws NumberFormatException {
		if (value == null) {
			return 0L;
		} else {
			Class c = value.getClass();
			return c.getSuperclass() == Number.class ? ((Number) value).longValue()
					: (c == Boolean.class ? (((Boolean) value).booleanValue() ? 1L : 0L)
							: (c == Character.class ? (long) ((Character) value).charValue()
									: Long.parseLong(stringValue(value, true))));
		}
	}

	public static double doubleValue(Object value) throws NumberFormatException {
		if (value == null) {
			return 0.0D;
		} else {
			Class c = value.getClass();
			if (c.getSuperclass() == Number.class) {
				return ((Number) value).doubleValue();
			} else if (c == Boolean.class) {
				return ((Boolean) value).booleanValue() ? 1.0D : 0.0D;
			} else if (c == Character.class) {
				return (double) ((Character) value).charValue();
			} else {
				String s = stringValue(value, true);
				return s.length() == 0 ? 0.0D : Double.parseDouble(s);
			}
		}
	}

	public static BigInteger bigIntValue(Object value) throws NumberFormatException {
		if (value == null) {
			return BigInteger.valueOf(0L);
		} else {
			Class c = value.getClass();
			return c == BigInteger.class ? (BigInteger) value
					: (c == BigDecimal.class ? ((BigDecimal) value).toBigInteger()
							: (c.getSuperclass() == Number.class ? BigInteger.valueOf(((Number) value).longValue())
									: (c == Boolean.class
											? BigInteger.valueOf(((Boolean) value).booleanValue() ? 1L : 0L)
											: (c == Character.class
													? BigInteger.valueOf((long) ((Character) value).charValue())
													: new BigInteger(stringValue(value, true))))));
		}
	}

	public static BigDecimal bigDecValue(Object value) throws NumberFormatException {
		if (value == null) {
			return BigDecimal.valueOf(0L);
		} else {
			Class c = value.getClass();
			return c == BigDecimal.class ? (BigDecimal) value
					: (c == BigInteger.class ? new BigDecimal((BigInteger) value)
							: (c == Boolean.class ? BigDecimal.valueOf(((Boolean) value).booleanValue() ? 1L : 0L)
									: (c == Character.class ? BigDecimal.valueOf((long) ((Character) value).charValue())
											: new BigDecimal(stringValue(value, true)))));
		}
	}

	public static String stringValue(Object value, boolean trim) {
		String result;
		if (value == null) {
			result = OgnlRuntime.NULL_STRING;
		} else {
			result = value.toString();
			if (trim) {
				result = result.trim();
			}
		}

		return result;
	}

	public static String stringValue(Object value) {
		return stringValue(value, false);
	}

	public static int getNumericType(Object value) {
		if (value != null) {
			Class c = value.getClass();
			if (c == Integer.class) {
				return 4;
			}

			if (c == Double.class) {
				return 8;
			}

			if (c == Boolean.class) {
				return 0;
			}

			if (c == Byte.class) {
				return 1;
			}

			if (c == Character.class) {
				return 2;
			}

			if (c == Short.class) {
				return 3;
			}

			if (c == Long.class) {
				return 5;
			}

			if (c == Float.class) {
				return 7;
			}

			if (c == BigInteger.class) {
				return 6;
			}

			if (c == BigDecimal.class) {
				return 9;
			}
		}

		return 10;
	}

	public static Object toArray(char value, Class toType) {
		return toArray(new Character(value), toType);
	}

	public static Object toArray(byte value, Class toType) {
		return toArray(new Byte(value), toType);
	}

	public static Object toArray(int value, Class toType) {
		return toArray(new Integer(value), toType);
	}

	public static Object toArray(long value, Class toType) {
		return toArray(new Long(value), toType);
	}

	public static Object toArray(float value, Class toType) {
		return toArray(new Float(value), toType);
	}

	public static Object toArray(double value, Class toType) {
		return toArray(new Double(value), toType);
	}

	public static Object toArray(boolean value, Class toType) {
		return toArray(new Boolean(value), toType);
	}

	public static Object convertValue(char value, Class toType) {
		return convertValue(new Character(value), toType);
	}

	public static Object convertValue(byte value, Class toType) {
		return convertValue(new Byte(value), toType);
	}

	public static Object convertValue(int value, Class toType) {
		return convertValue(new Integer(value), toType);
	}

	public static Object convertValue(long value, Class toType) {
		return convertValue(new Long(value), toType);
	}

	public static Object convertValue(float value, Class toType) {
		return convertValue(new Float(value), toType);
	}

	public static Object convertValue(double value, Class toType) {
		return convertValue(new Double(value), toType);
	}

	public static Object convertValue(boolean value, Class toType) {
		return convertValue(new Boolean(value), toType);
	}

	public static Object convertValue(char value, Class toType, boolean preventNull) {
		return convertValue(new Character(value), toType, preventNull);
	}

	public static Object convertValue(byte value, Class toType, boolean preventNull) {
		return convertValue(new Byte(value), toType, preventNull);
	}

	public static Object convertValue(int value, Class toType, boolean preventNull) {
		return convertValue(new Integer(value), toType, preventNull);
	}

	public static Object convertValue(long value, Class toType, boolean preventNull) {
		return convertValue(new Long(value), toType, preventNull);
	}

	public static Object convertValue(float value, Class toType, boolean preventNull) {
		return convertValue(new Float(value), toType, preventNull);
	}

	public static Object convertValue(double value, Class toType, boolean preventNull) {
		return convertValue(new Double(value), toType, preventNull);
	}

	public static Object convertValue(boolean value, Class toType, boolean preventNull) {
		return convertValue(new Boolean(value), toType, preventNull);
	}

	public static Object toArray(char value, Class toType, boolean preventNull) {
		return toArray(new Character(value), toType, preventNull);
	}

	public static Object toArray(byte value, Class toType, boolean preventNull) {
		return toArray(new Byte(value), toType, preventNull);
	}

	public static Object toArray(int value, Class toType, boolean preventNull) {
		return toArray(new Integer(value), toType, preventNull);
	}

	public static Object toArray(long value, Class toType, boolean preventNull) {
		return toArray(new Long(value), toType, preventNull);
	}

	public static Object toArray(float value, Class toType, boolean preventNull) {
		return toArray(new Float(value), toType, preventNull);
	}

	public static Object toArray(double value, Class toType, boolean preventNull) {
		return toArray(new Double(value), toType, preventNull);
	}

	public static Object toArray(boolean value, Class toType, boolean preventNull) {
		return toArray(new Boolean(value), toType, preventNull);
	}

	public static Object convertValue(Object value, Class toType) {
		return convertValue(value, toType, false);
	}

	public static Object toArray(Object value, Class toType) {
		return toArray(value, toType, false);
	}

	public static Object toArray(Object value, Class toType, boolean preventNulls) {
		if (value == null) {
			return null;
		} else {
			Object result = null;
			if (value.getClass().isArray() && toType.isAssignableFrom(value.getClass().getComponentType())) {
				return value;
			} else if (!value.getClass().isArray()) {
				if (toType == Character.TYPE) {
					return stringValue(value).toCharArray();
				} else if (value instanceof Collection) {
					return ((Collection) value).toArray((Object[]) ((Object[]) Array.newInstance(toType, 0)));
				} else {
					Object arg5 = Array.newInstance(toType, 1);
					Array.set(arg5, 0, convertValue(value, toType, preventNulls));
					return arg5;
				}
			} else {
				result = Array.newInstance(toType, Array.getLength(value));
				int i = 0;

				for (int icount = Array.getLength(value); i < icount; ++i) {
					Array.set(result, i, convertValue(Array.get(value, i), toType));
				}

				return result == null && preventNulls ? value : result;
			}
		}
	}

	public static Object convertValue(Object value, Class toType, boolean preventNulls) {
		Object result = null;
		if (value != null && toType.isAssignableFrom(value.getClass())) {
			return value;
		} else {
			if (value != null) {
				if (value.getClass().isArray() && toType.isArray()) {
					Class arg6 = toType.getComponentType();
					result = Array.newInstance(arg6, Array.getLength(value));
					int i = 0;

					for (int icount = Array.getLength(value); i < icount; ++i) {
						Array.set(result, i, convertValue(Array.get(value, i), arg6));
					}
				} else {
					if (value.getClass().isArray() && !toType.isArray()) {
						return convertValue(Array.get(value, 0), toType);
					}

					if (!value.getClass().isArray() && toType.isArray()) {
						if (toType.getComponentType() == Character.TYPE) {
							result = stringValue(value).toCharArray();
						} else if (toType.getComponentType() == Object.class) {
							if (value instanceof Collection) {
								Collection vc = (Collection) value;
								return vc.toArray(new Object[0]);
							}

							return new Object[] { value };
						}
					} else {
						if (toType == Integer.class || toType == Integer.TYPE) {
							result = new Integer((int) longValue(value));
						}

						if (toType == Double.class || toType == Double.TYPE) {
							result = new Double(doubleValue(value));
						}

						if (toType == Boolean.class || toType == Boolean.TYPE) {
							result = booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
						}

						if (toType == Byte.class || toType == Byte.TYPE) {
							result = new Byte((byte) ((int) longValue(value)));
						}

						if (toType == Character.class || toType == Character.TYPE) {
							result = new Character((char) ((int) longValue(value)));
						}

						if (toType == Short.class || toType == Short.TYPE) {
							result = new Short((short) ((int) longValue(value)));
						}

						if (toType == Long.class || toType == Long.TYPE) {
							result = new Long(longValue(value));
						}

						if (toType == Float.class || toType == Float.TYPE) {
							result = new Float(doubleValue(value));
						}

						if (toType == BigInteger.class) {
							result = bigIntValue(value);
						}

						if (toType == BigDecimal.class) {
							result = bigDecValue(value);
						}

						if (toType == String.class) {
							result = stringValue(value);
						}
					}
				}
			} else if (toType.isPrimitive()) {
				result = OgnlRuntime.getPrimitiveDefaultValue(toType);
			} else if (preventNulls && toType == Boolean.class) {
				result = Boolean.FALSE;
			} else if (preventNulls && Number.class.isAssignableFrom(toType)) {
				result = OgnlRuntime.getNumericDefaultValue(toType);
			}

			if (result == null && preventNulls) {
				return value;
			} else if (value != null && result == null) {
				throw new IllegalArgumentException("Unable to convert type " + value.getClass().getName() + " of "
						+ value + " to type of " + toType.getName());
			} else {
				return result;
			}
		}
	}

	public static int getIntValue(Object value) {
		try {
			if (value == null) {
				return -1;
			} else if (Number.class.isInstance(value)) {
				return ((Number) value).intValue();
			} else {
				String t = String.class.isInstance(value) ? (String) value : value.toString();
				return Integer.parseInt(t);
			}
		} catch (Throwable arg1) {
			throw new RuntimeException("Error converting " + value + " to integer:", arg1);
		}
	}

	public static int getNumericType(Object v1, Object v2) {
		return getNumericType(v1, v2, false);
	}

	public static int getNumericType(int t1, int t2, boolean canBeNonNumeric) {
		if (t1 == t2) {
			return t1;
		} else if (canBeNonNumeric && (t1 == 10 || t2 == 10 || t1 == 2 || t2 == 2)) {
			return 10;
		} else {
			if (t1 == 10) {
				t1 = 8;
			}

			if (t2 == 10) {
				t2 = 8;
			}

			return t1 >= 7 ? (t2 >= 7 ? Math.max(t1, t2) : (t2 < 4 ? t1 : (t2 == 6 ? 9 : Math.max(8, t1))))
					: (t2 >= 7 ? (t1 < 4 ? t2 : (t1 == 6 ? 9 : Math.max(8, t2))) : Math.max(t1, t2));
		}
	}

	public static int getNumericType(Object v1, Object v2, boolean canBeNonNumeric) {
		return getNumericType(getNumericType(v1), getNumericType(v2), canBeNonNumeric);
	}

	public static Number newInteger(int type, long value) {
		switch (type) {
		case 0:
		case 2:
		case 4:
			return new Integer((int) value);
		case 1:
			return new Byte((byte) ((int) value));
		case 3:
			return new Short((short) ((int) value));
		case 6:
		default:
			return BigInteger.valueOf(value);
		case 7:
			if ((long) ((float) value) == value) {
				return new Float((float) value);
			}
		case 8:
			if ((long) ((double) value) == value) {
				return new Double((double) value);
			}
		case 5:
			return new Long(value);
		}
	}

	public static Number newReal(int type, double value) {
		return (Number) (type == 7 ? new Float((float) value) : new Double(value));
	}

	public static Object binaryOr(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		return type != 6 && type != 9 ? newInteger(type, longValue(v1) | longValue(v2))
				: bigIntValue(v1).or(bigIntValue(v2));
	}

	public static Object binaryXor(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		return type != 6 && type != 9 ? newInteger(type, longValue(v1) ^ longValue(v2))
				: bigIntValue(v1).xor(bigIntValue(v2));
	}

	public static Object binaryAnd(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		return type != 6 && type != 9 ? newInteger(type, longValue(v1) & longValue(v2))
				: bigIntValue(v1).and(bigIntValue(v2));
	}

	public static boolean equal(Object v1, Object v2) {
		return v1 == null ? v2 == null
				: (v1 != v2 && !isEqual(v1, v2) ? (v1 instanceof Number && v2 instanceof Number
						? ((Number) v1).doubleValue() == ((Number) v2).doubleValue() : false) : true);
	}

	public static boolean less(Object v1, Object v2) {
		return compareWithConversion(v1, v2) < 0;
	}

	public static boolean greater(Object v1, Object v2) {
		return compareWithConversion(v1, v2) > 0;
	}

	public static boolean in(Object v1, Object v2) throws OgnlException {
		if (v2 == null) {
			return false;
		} else {
			ElementsAccessor elementsAccessor = OgnlRuntime.getElementsAccessor(OgnlRuntime.getTargetClass(v2));
			Enumeration e = elementsAccessor.getElements(v2);

			Object o;
			do {
				if (!e.hasMoreElements()) {
					return false;
				}

				o = e.nextElement();
			} while (!equal(v1, o));

			return true;
		}
	}

	public static Object shiftLeft(Object v1, Object v2) {
		int type = getNumericType(v1);
		return type != 6 && type != 9 ? newInteger(type, longValue(v1) << (int) longValue(v2))
				: bigIntValue(v1).shiftLeft((int) longValue(v2));
	}

	public static Object shiftRight(Object v1, Object v2) {
		int type = getNumericType(v1);
		return type != 6 && type != 9 ? newInteger(type, longValue(v1) >> (int) longValue(v2))
				: bigIntValue(v1).shiftRight((int) longValue(v2));
	}

	public static Object unsignedShiftRight(Object v1, Object v2) {
		int type = getNumericType(v1);
		return type != 6 && type != 9
				? (type <= 4 ? newInteger(4, (long) ((int) longValue(v1) >>> (int) longValue(v2)))
						: newInteger(type, longValue(v1) >>> (int) longValue(v2)))
				: bigIntValue(v1).shiftRight((int) longValue(v2));
	}

	public static Object add(Object v1, Object v2) {
		int type = getNumericType(v1, v2, true);
		switch (type) {
		case 6:
			return bigIntValue(v1).add(bigIntValue(v2));
		case 7:
		case 8:
			return newReal(type, doubleValue(v1) + doubleValue(v2));
		case 9:
			return bigDecValue(v1).add(bigDecValue(v2));
		case 10:
			int t1 = getNumericType(v1);
			int t2 = getNumericType(v2);
			if ((t1 == 10 || v2 != null) && (t2 == 10 || v1 != null)) {
				return stringValue(v1) + stringValue(v2);
			} else {
				throw new NullPointerException("Can\'t add values " + v1 + " , " + v2);
			}
		default:
			return newInteger(type, longValue(v1) + longValue(v2));
		}
	}

	public static Object subtract(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		switch (type) {
		case 6:
			return bigIntValue(v1).subtract(bigIntValue(v2));
		case 7:
		case 8:
			return newReal(type, doubleValue(v1) - doubleValue(v2));
		case 9:
			return bigDecValue(v1).subtract(bigDecValue(v2));
		default:
			return newInteger(type, longValue(v1) - longValue(v2));
		}
	}

	public static Object multiply(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		switch (type) {
		case 6:
			return bigIntValue(v1).multiply(bigIntValue(v2));
		case 7:
		case 8:
			return newReal(type, doubleValue(v1) * doubleValue(v2));
		case 9:
			return bigDecValue(v1).multiply(bigDecValue(v2));
		default:
			return newInteger(type, longValue(v1) * longValue(v2));
		}
	}

	public static Object divide(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		switch (type) {
		case 6:
			return bigIntValue(v1).divide(bigIntValue(v2));
		case 7:
		case 8:
			return newReal(type, doubleValue(v1) / doubleValue(v2));
		case 9:
			return bigDecValue(v1).divide(bigDecValue(v2), 6);
		default:
			return newInteger(type, longValue(v1) / longValue(v2));
		}
	}

	public static Object remainder(Object v1, Object v2) {
		int type = getNumericType(v1, v2);
		switch (type) {
		case 6:
		case 9:
			return bigIntValue(v1).remainder(bigIntValue(v2));
		default:
			return newInteger(type, longValue(v1) % longValue(v2));
		}
	}

	public static Object negate(Object value) {
		int type = getNumericType(value);
		switch (type) {
		case 6:
			return bigIntValue(value).negate();
		case 7:
		case 8:
			return newReal(type, -doubleValue(value));
		case 9:
			return bigDecValue(value).negate();
		default:
			return newInteger(type, -longValue(value));
		}
	}

	public static Object bitNegate(Object value) {
		int type = getNumericType(value);
		switch (type) {
		case 6:
		case 9:
			return bigIntValue(value).not();
		default:
			return newInteger(type, ~longValue(value));
		}
	}

	public static String getEscapeString(String value) {
		StringBuffer result = new StringBuffer();
		int i = 0;

		for (int icount = value.length(); i < icount; ++i) {
			result.append(getEscapedChar(value.charAt(i)));
		}

		return new String(result);
	}

	public static String getEscapedChar(char ch) {
		String result;
		switch (ch) {
		case '\b':
			result = "\b";
			break;
		case '\t':
			result = "\\t";
			break;
		case '\n':
			result = "\\n";
			break;
		case '\f':
			result = "\\f";
			break;
		case '\r':
			result = "\\r";
			break;
		case '\"':
			result = "\\\"";
			break;
		case '\'':
			result = "\\\'";
			break;
		case '\\':
			result = "\\\\";
			break;
		default:
			if (Character.isISOControl(ch)) {
				String hc = Integer.toString(ch, 16);
				int hcl = hc.length();
				result = "\\u";
				if (hcl < 4) {
					if (hcl == 3) {
						result = result + "0";
					} else if (hcl == 2) {
						result = result + "00";
					} else {
						result = result + "000";
					}
				}

				result = result + hc;
			} else {
				result = new String(ch + "");
			}
		}

		return result;
	}

	public static Object returnValue(Object ignore, Object returnValue) {
		return returnValue;
	}

	public static RuntimeException castToRuntime(Throwable t) {
		if (RuntimeException.class.isInstance(t)) {
			return (RuntimeException) t;
		} else if (OgnlException.class.isInstance(t)) {
			throw new UnsupportedCompilationException("Error evluating expression: " + t.getMessage(), t);
		} else {
			return new RuntimeException(t);
		}
	}
}
