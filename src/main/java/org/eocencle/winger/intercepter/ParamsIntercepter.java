package org.eocencle.winger.intercepter;

import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.util.JsonUtil;
import org.eocencle.winger.util.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 显示参数拦截器
 * @author huan
 *
 */
public class ParamsIntercepter implements Intercepter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParamsIntercepter.class);
	
	// 显示cookies
	private Boolean showCookies;
	
	// 头部信息列表
	private String[] headers;
	
	// 显示参数
	private Boolean showParams;
	
	public void doIntercepter(HttpServletRequest request, HttpServletResponse response, IntercepterChain chain) {
		LOGGER.debug("---trace 3---");
		LOGGER.info("Request " + request.getRequestURI());
		
		LOGGER.debug("---trace 3.1---");
		if (null != this.showCookies && this.showCookies) {
			LOGGER.info(this.cookiesWrapper(request));
		}
		
		LOGGER.debug("---trace 3.2---");
		if (null != this.headers && 0 != this.headers.length) {
			LOGGER.info(this.headerWrapper(request).toString());
		}
		
		LOGGER.debug("---trace 3.3---");
		if (null != this.showParams && this.showParams) {
			LOGGER.info(this.paramsWrapper(request).toString());
		}
		
		LOGGER.debug("---trace 3.4---");
		chain.doIntercepter(request, response, chain);
	}
	
	private String cookiesWrapper(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer("cookies: \n");
		Cookie[] cookies = request.getCookies();
		if (null != cookies && 0 != cookies.length) {
			for (Cookie cookie : cookies) {
				sb.append("  " + cookie.getName() + " : " + cookie.getValue() + "\n");
			}
		}
		return sb.toString();
	}
	
	private String headerWrapper(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer("header: \n");
		for (String header : this.headers) {
			sb.append("  " + header + " : " + request.getHeader(header) + "\n");
		}
		return sb.toString();
	}
	
	private String paramsWrapper(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer("params: \n");
		for (String key : request.getParameterMap().keySet()) {
			sb.append(this.paramLine("  ", key, request.getParameter(key)));
		}
		return sb.toString();
	}
	
	private String paramLine(String tab, String key, Object value) {
		StringBuffer sb = new StringBuffer();
		StrictMap<Object> json = null;
		if (0 == key.indexOf("{")) {
			json = JsonUtil.toMap(key);
			for (Entry<String, Object> obj : json.entrySet()) {
				sb.append(this.paramLine(tab + "  ", obj.getKey(), obj.getValue()));
			}
		} else if (0 == value.toString().indexOf("{")) {
			json = JsonUtil.toMap(value.toString());
			sb.append(tab + key + " : \n");
			for (Entry<String, Object> obj : json.entrySet()) {
				sb.append(this.paramLine(tab + "  ", obj.getKey(), obj.getValue()));
			}
		} else {
			sb.append(tab + key + " : " + value + "\n");
		}
		return sb.toString();
	}

	public Boolean isShowCookies() {
		return showCookies;
	}

	public void setShowCookies(Boolean showCookies) {
		this.showCookies = showCookies;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public Boolean isShowParams() {
		return showParams;
	}

	public void setShowParams(Boolean showParams) {
		this.showParams = showParams;
	}

}
