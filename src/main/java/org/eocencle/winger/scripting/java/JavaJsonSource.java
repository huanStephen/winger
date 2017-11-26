package org.eocencle.winger.scripting.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eocencle.winger.exceptions.ProcessException;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.session.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.google.gson.Gson;

public class JavaJsonSource extends JavaMethodReflectBase implements JsonSource {
	
	private Configuration configuration;
	
	private ParameterNameDiscoverer parameterUtil = new LocalVariableTableParameterNameDiscoverer();
	
	private Gson gson = new Gson();

	public JavaJsonSource(Object target, Method method, Configuration configuration) {
		super(target, method);
		this.configuration = configuration;
	}

	@Override
	public BoundJson getBoundJson(Map<String, Object> params) {
		Object[] args = this.buildParams(params);
		Object obj = null;
		try {
			obj = this.invoke(args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return new BoundJson(this.configuration, this.convertBeanToJson(obj));
	}
	
	private Object[] buildParams(Map<String, Object> params) {
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
	
	private Object convertJsonToBean(Object jsonObj, Class<?> paramType) {
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
	
	private String convertBeanToJson(Object obj) {
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
