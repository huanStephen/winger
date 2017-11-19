package org.eocencle.winger.server;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eocencle.winger.io.Resources;

public class JettyServer extends Server {
	
	public final static Logger LOG = Logger.getLogger(JettyServer.class);
	
	private String webXmlPath;
	
	private String contextPath;
	private String resourceBase;

	public JettyServer(String contextPath, String resourceBase) {
		super(8088);
		try {
			this.webXmlPath = Resources.getResourceURL("/").toString().replaceAll("file:/", "") + "etc/web.xml";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (StringUtils.isNotBlank(contextPath)) {
			this.contextPath = contextPath;
		}
		if (StringUtils.isNotBlank(resourceBase)) {
			this.resourceBase = resourceBase;
		}
		this.configServer();
		this.applyHandle();
	}
	
	private void configServer() {
		LOG.info("Config thread pool");
		
		QueuedThreadPool thread = (QueuedThreadPool) this.getThreadPool();
		thread.setMinThreads(30);
		thread.setMaxThreads(200);
		thread.setDetailedDump(false);
	}

	private void applyHandle() {
		LOG.info("Load handle");
		
		ContextHandlerCollection handler = new ContextHandlerCollection();
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(this.contextPath);
		webapp.setResourceBase(this.resourceBase);
		webapp.setDescriptor(this.webXmlPath);
		handler.addHandler(webapp);
		super.setHandler(handler);
	}

	public void startServer() {
		try {
			super.start();
			LOG.info("Current thread:" + this.getThreadPool().getIdleThreads() + 
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

	public String getWebXmlPath() {
		return webXmlPath;
	}

	public void setWebXmlPath(String webXmlPath) {
		this.webXmlPath = webXmlPath;
	}
}
