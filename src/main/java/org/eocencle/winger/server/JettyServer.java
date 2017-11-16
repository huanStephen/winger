package org.eocencle.winger.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

public class JettyServer extends Server {
	
	public final static Logger LOGGER = Logger.getLogger(JettyServer.class);
	
	private String classPath;
	
	private String xmlConfigPath;
	private String defaultsDescriptor;
	
	private String contextPath;
	private String resourceBase;
	private String webXmlPath;
	
	private String warPath;
	
	public JettyServer() {
		LOGGER.info("JettyServer init");
		
		this.classPath = JettyServer.class.getResource("/").toString().replaceAll("file:/", "") + "/etc";
		
		this.xmlConfigPath = this.classPath + "/jetty.xml";
		this.defaultsDescriptor = this.classPath + "/webdefault.xml";
	}
	
	public void init() {
		if (StringUtils.isNotBlank(this.xmlConfigPath)) {
			//readXmlConfig();
		}
		if (StringUtils.isNotBlank(this.warPath)) {
			if (StringUtils.isNotBlank(this.contextPath)) {
				applyHandle(true);
			}
		} else {
			if (StringUtils.isNotBlank(this.contextPath)) {
				applyHandle(false);
			}
		}
	}

	public JettyServer(String xmlConfigPath, String contextPath, String resourceBase, String webXmlPath, 
		String warPath) {
		super();
		if (StringUtils.isNotBlank(xmlConfigPath)) {
			this.xmlConfigPath = xmlConfigPath;
			readXmlConfig();
		}

		if (StringUtils.isNotBlank(warPath)) {
			this.warPath = warPath;
			if (StringUtils.isNotBlank(contextPath)) {
				this.contextPath = contextPath;
				applyHandle(true);
			}
		} else {
			if (StringUtils.isNotBlank(resourceBase))
				this.resourceBase = resourceBase;
			if (StringUtils.isNotBlank(webXmlPath))
				this.webXmlPath = webXmlPath;
			if (StringUtils.isNotBlank(contextPath)) {
				this.contextPath = contextPath;
				applyHandle(false);
			}
		}
	}

	private void readXmlConfig() {
		LOGGER.info("Reading xml");
		
		try {
			XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(this.xmlConfigPath));
			configuration.configure(this);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void applyHandle(Boolean warDeployFlag) {
		
		LOGGER.info("Load handle");
		
		ContextHandlerCollection handler = new ContextHandlerCollection();
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(contextPath);
		webapp.setDefaultsDescriptor(this.defaultsDescriptor);
		if (!warDeployFlag) {
			webapp.setResourceBase(resourceBase);
			webapp.setDescriptor(webXmlPath);
		} else {
			webapp.setWar(warPath);
		}
		handler.addHandler(webapp);
		super.setHandler(handler);
	}

	public void startServer() {
		try {
			super.start();
			LOGGER.info("Current thread:" + this.getThreadPool().getIdleThreads() + 
					" | idle thread:" + this.getThreadPool().getIdleThreads());
			super.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getXmlConfigPath() {
		return xmlConfigPath;
	}

	public void setXmlConfigPath(String xmlConfigPath) {
		this.xmlConfigPath = xmlConfigPath;
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

	public String getWarPath() {
		return warPath;
	}

	public void setWarPath(String warPath) {
		this.warPath = warPath;
	}
}
