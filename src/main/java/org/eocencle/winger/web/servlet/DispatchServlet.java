package org.eocencle.winger.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.session.Session;
import org.eocencle.winger.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;

public class DispatchServlet extends HttpServlet {
	private static final long serialVersionUID = 2232979877413501656L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatchServlet.class);

	private WebApplicationContext wac;
	
	private Session session;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.wac = (WebApplicationContext) config.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		String c = config.getInitParameter("config");
		if (StringUtils.isBlank(c)) {
			throw new RuntimeException("请填写配置文件！");
		}
		
		try {
			this.session = new SessionFactory().build(c, this.wac);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.debug("---trace 1---");
		this.session.request(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.debug("---trace 1---");
		this.session.request(request, response);
	}

}
