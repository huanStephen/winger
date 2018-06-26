package org.eocencle.winger.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.session.Session;
import org.eocencle.winger.session.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;

public class DispatcherFilter implements Filter {

	private WebApplicationContext wac;
	
	private Session session;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.wac = (WebApplicationContext) filterConfig.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		String config = filterConfig.getInitParameter("config");
		if (StringUtils.isBlank(config)) {
			throw new RuntimeException("请填写配置文件！");
		}
		try {
			this.session = new SessionFactory().build(config, this.wac);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!this.session.request((HttpServletRequest)request, (HttpServletResponse)response)) {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}

}
