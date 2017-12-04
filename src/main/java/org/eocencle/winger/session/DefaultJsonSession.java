package org.eocencle.winger.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.mapping.AbstractResponseBranch;
import org.eocencle.winger.util.JsonUtil;

public class DefaultJsonSession implements JsonSession {

	private Configuration configuration;
	
	public DefaultJsonSession(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String request(String name, Map<String, Object> params) {
		AbstractResponseBranch branch = this.configuration.getResponseBranch(name);
		return branch.getBoundJson(params).getJson();
	}
	
	public void request(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = this.paramsWrapper(request, response);
		Object obj = this.request(request.getRequestURI(), params);
		this.returnJson(obj, response);
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

}
