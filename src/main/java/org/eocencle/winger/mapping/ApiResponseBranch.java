package org.eocencle.winger.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eocencle.winger.exceptions.IllegalParamException;
import org.eocencle.winger.exceptions.ParamFormatException;
import org.eocencle.winger.exceptions.ParamNotFoundException;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.util.StrictMap;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.google.gson.Gson;

/**
 * api响应分支
 * @author huan
 *
 */
public class ApiResponseBranch extends AbstractResponseBranch {
	
	// 执行对象
	private Object tarObj;
	
	// 执行方法
	private Method method;
	
	// json解析
	private static Gson gson = new Gson();
	
	// 获取方法参数
	private static ParameterNameDiscoverer parameterUtil = new LocalVariableTableParameterNameDiscoverer();
	
	public ApiResponseBranch(Configuration config, String namespace, String url, Object tarObj, Method method) {
		super(config, namespace, url);
		this.tarObj = tarObj;
		this.method = method;
	}
	
	@Override
	public String getCompleteJson(StrictMap<Object> params) throws WingerException {
		Object[] args = this.buildParams(params);
		Object obj = null;
		try {
			obj = this.method.invoke(this.tarObj, args);
		} catch (IllegalAccessException e) {
			throw new WingerException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new WingerException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new WingerException(e.getMessage());
		}
		return this.convertBeanToJson(obj);
	}
	
	/**
	 * 建构参数
	 * @param params
	 * @return
	 */
	private Object[] buildParams(Map<String, Object> params) throws IllegalParamException {
		List<String> paramsNames = Arrays.asList(parameterUtil.getParameterNames(this.method));
		Class<?>[] paramTypes = this.method.getParameterTypes();
		
		for (String param : paramsNames) {
			try {
				params.get(param);
			} catch (IllegalArgumentException e) {
				throw new ParamNotFoundException("Param " + param + " is not found");
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
	
	/**
	 * 由json转为bean
	 * @param jsonObj
	 * @param paramType
	 * @return
	 */
	private Object convertJsonToBean(Object jsonObj, Class<?> paramType) throws ParamFormatException {
		String json = jsonObj.toString();
		try {
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
		} catch (NumberFormatException e) {
			throw new ParamFormatException(json + " cannot be formatted to " + paramType.getName() + " type");
		}

		return this.gson.fromJson(jsonObj.toString(), paramType);
	}
	
	/**
	 * 由bean转为json
	 * @param obj
	 * @return
	 */
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
