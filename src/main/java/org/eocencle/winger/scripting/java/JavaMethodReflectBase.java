package org.eocencle.winger.scripting.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class JavaMethodReflectBase {
	// 目标对象
	protected Object target;
	// 执行方法
	protected Method method;
	
	public JavaMethodReflectBase(Object target, Method method) {
		this.target = target;
		this.method = method;
	}
	
	public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.method.invoke(this.target, args);
	}
}
