package org.eocencle.winger.web.server;

import org.eocencle.winger.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
	
	private volatile JettyServer server;
	
	private Configuration config;
	
	public ServerThread(Configuration config) {
		this.config = config;
	}
	
	public void stopServer() {
		try {
			if (null != server) {
				server.stop();
				server.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOGGER.info("Server port: " + this.config.getPort());
		LOGGER.info("Server context path: " + this.config.getContextPath());
		LOGGER.info("Server resource base: " + this.config.getResourceBase());
		this.server = new JettyServer(this.config.getPort(), this.config.getContextPath(), this.config.getResourceBase(), this.config.getResourceSuffixes());
		this.server.startServer();
	}

}
