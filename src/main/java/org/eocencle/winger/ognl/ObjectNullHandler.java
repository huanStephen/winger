package org.eocencle.winger.ognl;

import java.util.Map;

public class ObjectNullHandler implements NullHandler {
	public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
		return null;
	}

	public Object nullPropertyValue(Map context, Object target, Object property) {
		return null;
	}
}
