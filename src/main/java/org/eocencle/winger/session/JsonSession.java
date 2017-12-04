package org.eocencle.winger.session;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JsonSession {
	String request(String action, Map<String, Object> params);
	
	void request(HttpServletRequest request, HttpServletResponse response);
}
