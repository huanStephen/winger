package org.eocencle.winger.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eocencle.winger.exceptions.ProcessException;
import org.eocencle.winger.session.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.google.gson.Gson;

/**
 * 能反射方法的响应分支
 * @author huanStephen
 *
 */
public abstract class ReflectResponseBranch extends AbstractResponseBranch {
	// 目标对象
	private Object target;
	// 执行方法
	protected Method method;
	
	protected ParameterNameDiscoverer parameterUtil = new LocalVariableTableParameterNameDiscoverer();
	
	protected Gson gson = new Gson();
	
	public ReflectResponseBranch(String action, Object target, Method method, Configuration configuration) {
		super(action, configuration);
		this.target = target;
		this.method = method;
	}
	
	public ReflectResponseBranch(String action, RequestType type, Object target, Method method, Configuration configuration) {
		super(action, type, configuration);
		this.target = target;
		this.method = method;
	}
	
	public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.method.invoke(this.target, args);
	}
	
	protected Object[] buildParams(Map<String, Object> params) {
		List<String> paramsNames = Arrays.asList(this.parameterUtil.getParameterNames(this.method));
		Class<?>[] paramTypes = this.method.getParameterTypes();
		
		for (String param : paramsNames) {
			if (null == params.get(param)) {
				throw new ProcessException("调用失败：接口不存在'" + param + "'");
			}
		}
		
		Object[] args = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i ++) {
			if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
				args[i] = params.get("_request");
			} else if (paramTypes[i].isAssignableFrom(HttpSession.class)) {
				args[i] = params.get("_session");
			} else if (paramTypes[i].isAssignableFrom(HttpServletResponse.class)) {
				args[i] = params.get("_response");
			} else if (params.containsKey(paramsNames.get(i))) {
				args[i] = this.convertJsonToBean(params.get(paramsNames.get(i)), paramTypes[i]);
			}
		}
		
		return args;
	}
	
	protected Object convertJsonToBean(Object jsonObj, Class<?> paramType) {
		String json = jsonObj.toString();
		if (paramType.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(json.substring(0, (json.indexOf(".") == -1 ? json.length() : json.indexOf("."))));
		}
		if (paramType.isAssignableFrom(Float.class)) {
			return Float.valueOf(json);
		}
		if (paramType.isAssignableFrom(Double.class)) {
			return Double.valueOf(json);
		}
		if (paramType.isAssignableFrom(String.class)) {
			return json;
		}
		if (paramType.isAssignableFrom(Boolean.class)) {
			return Boolean.valueOf(json);
		}
		if (paramType.isAssignableFrom(Byte.class)) {
			return Byte.valueOf(json.substring(0, (json.indexOf(".") == -1 ? json.length() : json.indexOf("."))));
		}
		if (paramType.isAssignableFrom(Long.class)) {
			return Long.valueOf(json.substring(0, (json.indexOf(".") == -1 ? json.length() : json.indexOf("."))));
		}
		if (paramType.isAssignableFrom(Short.class)) {
			return Short.valueOf(json.substring(0, (json.indexOf(".") == -1 ? json.length() : json.indexOf("."))));
		}
		if (paramType.isAssignableFrom(Character.class)) {
			return Character.valueOf(json.charAt(0));
		}

		return this.gson.fromJson(jsonObj.toString(), paramType);
	}
	
	protected String convertBeanToJson(Object obj) {
		if (null == obj) {
			return null;
		}
		
		String json;
		if (obj instanceof String) {
			json = obj.toString();
		} else {
			json = this.gson.toJson(obj);
		}
		return json;
	}

}
