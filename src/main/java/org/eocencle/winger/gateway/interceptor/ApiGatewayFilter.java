package org.eocencle.winger.gateway.interceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.gateway.HttpHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

public class ApiGatewayFilter implements Filter {

private ApplicationContext applicationContext;
	
	private HttpHandler httpHandler;
	
	private Boolean vague = false;
	
	private Set<String> suffix = new HashSet<String>();
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		String uri = request.getRequestURI();
		if (uri.contains(".")) {
			if (this.vague || this.suffix.contains(uri.substring(uri.lastIndexOf(".") + 1))) {
				chain.doFilter(request, response);
				return ;
			}
		}
		
		this.httpHandler.handle(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.applicationContext = (ApplicationContext) filterConfig.getServletContext()
			.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		this.httpHandler = this.applicationContext.getBean(HttpHandler.class);
		
		String ignore = filterConfig.getInitParameter("suffix");
		if (StringUtils.isNotBlank(ignore)) {
			String[] res = ignore.split(",");
			for (String r : res) {
				if ("*".equals(r)) {
					this.vague = true;
					break;
				}
				this.suffix.add(r);
			}
		}
	}

}
