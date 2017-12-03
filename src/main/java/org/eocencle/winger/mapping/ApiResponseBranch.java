package org.eocencle.winger.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.eocencle.winger.session.Configuration;
/**
 * API解析的响应分支
 * @author huanStephen
 *
 */
public class ApiResponseBranch extends ReflectResponseBranch {
	
	private JsonSource jsonSource;

	public ApiResponseBranch(String name, Object target, Method method, Configuration configuration, JsonSource jsonSource) {
		super(name, target, method, configuration);
		this.jsonSource = jsonSource;
	}
	
	public ApiResponseBranch(String action, RequestType type, Object target, Method method, Configuration configuration, JsonSource jsonSource) {
		super(action, type, target, method, configuration);
		this.jsonSource = jsonSource;
	}

	@Override
	public BoundJson getBoundJson(Map<String, Object> params) {
		return this.jsonSource.getBoundJson(params);
	}
}
