package org.eocencle.winger.web.server;

import org.eocencle.winger.exceptions.IllegalParamException;
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
		try {
			LOGGER.info("Server port: " + this.config.getPort());
			LOGGER.info("Server context path: " + this.config.getContextPath());
			LOGGER.info("Server resource base: " + this.config.getResourceBase());
			this.server = new JettyServer(this.config);
			this.server.startServer();
		} catch (IllegalParamException e) {
			e.printStackTrace();
		}
	}

}
