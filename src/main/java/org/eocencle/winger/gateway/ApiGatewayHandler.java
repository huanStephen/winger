package org.eocencle.winger.gateway;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.gateway.ApiStore.ApiRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class ApiGatewayHandler {
	
	@Autowired
	private ApiStore apiStore;
	
	private ParameterNameDiscoverer parameterUtil;
	
	public ApiGatewayHandler() {
		this.parameterUtil = new LocalVariableTableParameterNameDiscoverer();
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = (String)enu.nextElement();
			params.put(paraName, request.getParameter(paraName));
		}
		
		Object result;
		ApiRunnable apiRun = null;
		
		try {
			apiRun = this.sysParamsVaildate(request);
			Object[] args = this.buildParams(apiRun, params, request, response);
			result = apiRun.run(args);
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = null;
			
			out = response.getWriter();
			out.write(new Gson().toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private ApiRunnable sysParamsVaildate(HttpServletRequest request) throws Exception {
		String uri = request.getRequestURI();
		uri = uri.substring(1);
		String apiName = uri.substring(uri.indexOf("/"));
		
		ApiRunnable api;
		if (null == apiName || "".equals(apiName.trim())) {
			throw new Exception("调用失败：参数'method'为空！");
		} else if (null == (api = this.apiStore.findApiRunnable(apiName))) {
			throw new Exception("调用失败：指定API不存在，API：" + apiName + "！");
		}
		return api;
	}
	
	private Object[] buildParams(ApiRunnable apiRunnable, Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Method method = apiRunnable.getTargetMethod();
		List<String> paramsNames = Arrays.asList(this.parameterUtil.getParameterNames(method));
		Class<?>[] paramTypes = method.getParameterTypes();
		
		for (Map.Entry<String, Object> m : map.entrySet()) {
			if (!paramsNames.contains(m.getKey())) {
				throw new Exception("调用失败：接口不存在'" + m.getKey() + "'");
			}
		}
		
		Object[] args = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i ++) {
			if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
				args[i] = request;
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

		return new Gson().fromJson(jsonObj.toString(), paramType);
	}
	
}
