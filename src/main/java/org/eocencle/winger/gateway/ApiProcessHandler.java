package org.eocencle.winger.gateway;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.exceptions.ProcessException;
import org.eocencle.winger.gateway.ApiStore.ApiRunnable;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.google.gson.Gson;

public class ApiProcessHandler implements ProcessHandler {
	
	private ApiStore apiStore;
	
	private ParameterNameDiscoverer parameterUtil;
	
	private Gson gson;
	
	public ApiProcessHandler(ApiStore apiStore) {
		this.apiStore = apiStore;
		this.parameterUtil = new LocalVariableTableParameterNameDiscoverer();
		this.gson = new Gson();
	}

	@Override
	public Object handle(String apiName, Map<String, Object> params) {
		if (StringUtils.isBlank(apiName)) {
			throw new ProcessException("调用失败：参数'method'为空！");
		}
		ApiRunnable api = null;
		if (null == (api = this.apiStore.findApiRunnable(apiName))) {
			throw new ProcessException("调用失败：指定API不存在，API：" + apiName + "！");
		}
		try {
			return api.run(this.buildParams(api, params));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Object[] buildParams(ApiRunnable apiRunnable, Map<String, Object> map) throws Exception {
		
		Method method = apiRunnable.getTargetMethod();
		List<String> paramsNames = Arrays.asList(this.parameterUtil.getParameterNames(method));
		Class<?>[] paramTypes = method.getParameterTypes();
		
		for (String param : paramsNames) {
			if (null == map.get(param)) {
				throw new ProcessException("调用失败：接口不存在'" + param + "'");
			}
		}
		
		Object[] args = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i ++) {
			if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
				args[i] = map.get("_request");
			} else if (paramTypes[i].isAssignableFrom(HttpSession.class)) {
				args[i] = map.get("_session");
			} else if (paramTypes[i].isAssignableFrom(HttpServletResponse.class)) {
				args[i] = map.get("_response");
			} else if (map.containsKey(paramsNames.get(i))) {
				args[i] = this.convertJsonToBean(map.get(paramsNames.get(i)), paramTypes[i]);
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
}
