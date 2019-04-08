package org.eocencle.winger.xmltags;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eocencle.winger.exceptions.BuilderException;
import org.eocencle.winger.exceptions.ParamFormatException;
import org.eocencle.winger.exceptions.ParamNotFoundException;
import org.eocencle.winger.exceptions.WingerException;

public class ExpressionEvaluator {
	public boolean evaluateBoolean(String expression, Object parameterObject) throws WingerException {
		Object value = null;
		try {
			value = OgnlCache.getValue(expression, parameterObject);
		} catch (NumberFormatException e) {
			String[] split = e.getMessage().split(" ");
			throw new ParamFormatException(split[split.length - 1].substring(1, split[split.length - 1].length() - 1) + " cannot be formatted");
		} catch (IllegalArgumentException e) {
			String[] split = e.getMessage().split(" ");
			throw new ParamNotFoundException("Param " + split[split.length - 1] + " is not found");
		}
		if (value instanceof Boolean) return (Boolean) value;
		if (value instanceof Number) return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
		return value != null;
	}

	public Iterable<?> evaluateIterable(String expression, Object parameterObject) throws WingerException {
		Object value = OgnlCache.getValue(expression, parameterObject);
		if (value == null) throw new BuilderException("The expression '" + expression + "' evaluated to a null value.");
		if (value instanceof Iterable) return (Iterable<?>) value;
		if (value.getClass().isArray()) {
			int size = Array.getLength(value);
			List<Object> answer = new ArrayList<Object>();
			for (int i = 0; i < size; i++) {
				Object o = Array.get(value, i);
				answer.add(o);
			}
			return answer;
		}
		if (value instanceof Map) {
			return ((Map) value).entrySet();
		}
		throw new BuilderException("Error evaluating expression '" + expression + "'.Return value (" + value + ") was not iterable.");
	}
}
