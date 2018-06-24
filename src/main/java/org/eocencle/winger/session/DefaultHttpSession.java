package org.eocencle.winger.session;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.cache.ResponseCache;
import org.eocencle.winger.exceptions.IllegalParamException;
import org.eocencle.winger.exceptions.ParamFormatException;
import org.eocencle.winger.exceptions.ParamNotFoundException;
import org.eocencle.winger.exceptions.UriNotFoundException;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.intercepter.Intercepter;
import org.eocencle.winger.intercepter.IntercepterChain;
import org.eocencle.winger.util.JsonUtil;
import org.eocencle.winger.util.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认Http协议session
 * 功能是通过request和response，适配session数据接口
 * @author huan
 *
 */
public class DefaultHttpSession extends Session implements Intercepter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpSession.class);
	
	private IntercepterChain chain;
	
	public DefaultHttpSession(Configuration config) {
		super(config);
	}
	
	public void setChain(IntercepterChain chain) {
		this.chain = chain;
	}

	public boolean request(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.debug("---trace 2---");
		this.chain.open();
		this.chain.doIntercepter(request, response, this.chain);
		return false;
	}
	
	private StrictMap<Object> paramsWrapper(HttpServletRequest request, HttpServletResponse response) {
		StrictMap<Object> params = new StrictMap<Object>("Paratemers collection");
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
		LOGGER.debug("---trace 6---");
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().write(JsonUtil.parseString(obj));
			LOGGER.debug("---trace 7---");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doIntercepter(HttpServletRequest request, HttpServletResponse response, IntercepterChain chain) {
		LOGGER.debug("---trace 4---");
		StrictMap<Object> params = this.paramsWrapper(request, response);
		String uri = request.getRequestURI().substring(this.config.getContextPath().length());
		Object result = null; 
		ResponseCache cache = this.config.getResponseCache();
		
		try {
			if (cache.isOpen()) {
				try {
					result = cache.get(uri, params.hashCode());
					if (null == result) {
						result = this.request(uri, params);
					} else {
						LOGGER.info("Cache got " + uri + " result");
					}
				} catch (UriNotFoundException e) {
					result = this.request(uri, params);
				}
				
				if (null != result) {
					cache.push(uri, params.hashCode(), result.toString());
				}
			} else {
				result = this.request(uri, params);
			}
		} catch (UriNotFoundException e) {
			LOGGER.info(e.getMessage());
			result = new SystemResult(SystemResult.CODE_URI_NOT_FOUND, e.getMessage());
		} catch (ParamNotFoundException e) {
			LOGGER.info(e.getMessage());
			result = new SystemResult(SystemResult.CODE_PARAM_NOT_FOUND, e.getMessage());
		} catch (ParamFormatException e) {
			LOGGER.info(e.getMessage());
			result = new SystemResult(SystemResult.CODE_PARAM_FORMAT_ERROR, e.getMessage());
		} catch (IllegalParamException e) {
			LOGGER.debug(e.getMessage());
			result = new SystemResult(SystemResult.CODE_SYSTEM_ERROR, "System error");
		} catch (WingerException e) {
			LOGGER.debug(e.getMessage());
			result = new SystemResult(SystemResult.CODE_SYSTEM_ERROR, "System error");
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			result = new SystemResult(SystemResult.CODE_SYSTEM_ERROR, "System error");
		}
		
		/*try {
			cache = this.config.getResponseCache();
			if (cache.isOpen()) {
				result = cache.get(uri, params.hashCode());
				if (null == result) {
					throw new IllegalArgumentException();
				}
				LOGGER.info("Cache got " + uri + " result");
			} else {
				result = this.request(uri, params);
			}
			
		} catch (IllegalArgumentException e) {
			result = this.request(uri, params);
			if (cache.isOpen() && null != result) {
				cache.push(uri, params.hashCode(), result.toString());
			}
		}*/
		
		LOGGER.debug("---trace 5---");
		this.returnJson(result, response);
		
		chain.doIntercepter(request, response, chain);
	}
}
