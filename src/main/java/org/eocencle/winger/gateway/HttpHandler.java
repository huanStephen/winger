package org.eocencle.winger.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.session.JsonSession;
import org.eocencle.winger.util.JsonUtil;

public final class HttpHandler {
	
	private List<Interceptor> interceptors;
	
	private JsonSession jsonSession;
	
	public HttpHandler(JsonSession jsonSession) {
		this.interceptors = new ArrayList<Interceptor>();
		this.jsonSession = jsonSession;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = this.paramsWrapper(request, response);
		for (int i = 0; i < this.interceptors.size(); i ++) {
			this.interceptors.get(i).before(params, request);
		}
		Object obj = this.run(request.getRequestURI(), params);
		for (int i = this.interceptors.size() - 1; i >= 0; i --) {
			this.interceptors.get(i).after(obj, response);
		}
		this.returnJson(obj, response);
	}
	
	private Object run(String name, Map<String, Object> params) {
		return this.jsonSession.request(name, params);
	}
	
	private Map<String, Object> paramsWrapper(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>();
		for (String key : request.getParameterMap().keySet()) {
			params = JsonUtil.toMap(key);
			break;
		}
		params.put("_request", request);
		params.put("_session", request.getSession());
		params.put("_response", response);
		return params;
	}
	
	private void returnJson(Object obj, HttpServletResponse response) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().write(JsonUtil.parseString(obj));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addInterceptor(Interceptor interceptor) {
		this.interceptors.add(interceptor);
	}
	
	public static interface Interceptor {
		void before(Map<String, Object> params, HttpServletRequest request);
		void after(Object obj, HttpServletResponse response);
	}
}
