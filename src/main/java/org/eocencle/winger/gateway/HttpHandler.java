package org.eocencle.winger.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public final class HttpHandler {
	
	private Gson gson = new Gson();
	
	private List<Interceptor> interceptors = new ArrayList<Interceptor>();
	
	private ProcessHandler handler;
	
	public HttpHandler(ProcessHandler handler) {
		this.handler = handler;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = this.paramsWrapper(request, response);
		for (int i = 0; i < this.interceptors.size(); i ++) {
			this.interceptors.get(i).before(params, request);
		}
		Object obj = this.handler.handle(this.getMethodName(request), params);
		for (int i = this.interceptors.size() - 1; i >= 0; i --) {
			this.interceptors.get(i).after(obj, response);
		}
		this.returnJson(obj, response);
	}
	
	private String getMethodName(HttpServletRequest request) {
		String uri = request.getRequestURI();
		uri = uri.substring(1);
		return uri.substring(uri.indexOf("/"));
	}
	
	private Map<String, Object> paramsWrapper(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = (String)enu.nextElement();
			params.put(paraName, request.getParameter(paraName));
		}
		params.put("_request", request);
		params.put("_session", request.getSession());
		params.put("_response", response);
		return params;
	}
	
	private void returnJson(Object obj, HttpServletResponse response) {
		String json;
		if (obj instanceof String) {
			json = obj.toString();
		} else {
			json = this.gson.toJson(obj);
		}
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().write(json);
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
