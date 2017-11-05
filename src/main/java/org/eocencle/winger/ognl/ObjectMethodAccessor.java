package org.eocencle.winger.ognl;

import java.util.List;
import java.util.Map;

public class ObjectMethodAccessor implements MethodAccessor {
	public Object callStaticMethod(Map context, Class targetClass, String methodName, Object[] args)
			throws MethodFailedException {
		List methods = OgnlRuntime.getMethods(targetClass, methodName, true);
		return OgnlRuntime.callAppropriateMethod((OgnlContext) context, targetClass, (Object) null, methodName,
				(String) null, methods, args);
	}

	public Object callMethod(Map context, Object target, String methodName, Object[] args)
			throws MethodFailedException {
		Class targetClass = target == null ? null : target.getClass();
		List methods = OgnlRuntime.getMethods(targetClass, methodName, false);
		if (methods == null || methods.size() == 0) {
			methods = OgnlRuntime.getMethods(targetClass, methodName, true);
		}

		return OgnlRuntime.callAppropriateMethod((OgnlContext) context, target, target, methodName, (String) null,
				methods, args);
	}
}
