package org.eocencle.winger.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器接口
 * @author huan
 *
 */
public interface Intercepter {
	void doIntercepter(HttpServletRequest request, HttpServletResponse response, IntercepterChain chain);
}
