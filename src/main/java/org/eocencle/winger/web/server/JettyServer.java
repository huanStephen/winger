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
import org.eocencle.winger.web.servlet.DispatchServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer extends Server {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);
	
	private String contextPath;
	
	private String resourceBase;
	
	private List<String> resourceSuffixes;

	public JettyServer(Integer port, String contextPath, String resourceBase, List<String> resourceSuffixes) {
		super(port);
		
		if (StringUtils.isNotBlank(contextPath)) {
			this.contextPath = contextPath;
		}
		if (StringUtils.isNotBlank(resourceBase)) {
			this.resourceBase = resourceBase;
		}
		if (null != resourceSuffixes) {
			this.resourceSuffixes = resourceSuffixes;
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
		webapp.setContextPath(this.contextPath);
		webapp.setResourceBase(this.resourceBase);
		
		ServletHolder servlet = new ServletHolder();
		servlet.setHeldClass(DispatchServlet.class);
		servlet.setInitParameter("config", "config.xml");
		servlet.setInitOrder(1);
		webapp.addServlet(servlet, "/");
		
		if (null != this.resourceSuffixes && 0 != this.resourceSuffixes.size()) {
			for (String resourceSuffix : this.resourceSuffixes) {
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

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getResourceBase() {
		return resourceBase;
	}

	public void setResourceBase(String resourceBase) {
		this.resourceBase = resourceBase;
	}
}
