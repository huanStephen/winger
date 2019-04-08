package org.eocencle.winger.web.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eocencle.winger.exceptions.IllegalParamException;
import org.eocencle.winger.exceptions.ParamNotFoundException;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.web.servlet.DispatchServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer extends Server {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);
	
	private Configuration config;

	public JettyServer(Configuration config) throws IllegalParamException {
		super(config.getPort());
		
		this.config = config;
		
		if (StringUtils.isBlank(config.getContextPath())) {
			LOGGER.debug("ConfigPath not found!");
			throw new ParamNotFoundException("ContextPath not found!");
		}
		if (StringUtils.isBlank(config.getResourceBase())) {
			LOGGER.debug("ResourceBase not found!");
			throw new ParamNotFoundException("ResourceBase not found!");
		}
		if (null == config.getResourceSuffixes()) {
			LOGGER.debug("ResourceSuffixes is null!");
			throw new ParamNotFoundException("ResourceSuffixes is null!");
		}
		this.configServer();
		this.applyHandle();
	}
	
	private void configServer() {
		LOGGER.info("JettyServer configuring thread pool");
		
		QueuedThreadPool thread = (QueuedThreadPool) this.getThreadPool();
		thread.setMinThreads(30);
		thread.setMaxThreads(200);
		thread.setDetailedDump(false);
	}

	private void applyHandle() {
		LOGGER.info("JettyServer loading handler");
		
		ContextHandlerCollection handler = new ContextHandlerCollection();
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(this.config.getContextPath());
		webapp.setResourceBase(this.config.getResourceBase());
		
		ServletHolder servlet = new ServletHolder();
		servlet.setHeldClass(DispatchServlet.class);
		servlet.setInitParameter("config", this.config.getConfigPath());
		servlet.setInitOrder(1);
		webapp.addServlet(servlet, "/");
		
		List<String> resourceSuffixes = this.config.getResourceSuffixes();
		if (null != resourceSuffixes && 0 != resourceSuffixes.size()) {
			for (String resourceSuffix : resourceSuffixes) {
				webapp.addServlet(DefaultServlet.class, resourceSuffix);
			}
		}
		
		handler.setHandlers(new Handler[] {webapp, new DefaultHandler()});
		super.setHandler(handler);
	}

	public void startServer() {
		try {
			super.start();
			LOGGER.info("JettyServer current thread:" + this.getThreadPool().getIdleThreads() + 
				" | idle thread:" + this.getThreadPool().getIdleThreads());
			super.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
