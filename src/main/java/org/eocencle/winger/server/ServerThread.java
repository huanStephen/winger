package org.eocencle.winger.server;

public class ServerThread extends Thread {
	
	private volatile JettyServer server;
	
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
		this.server = new JettyServer("/winger", "C:\\Users\\dell\\Desktop\\winger\\winger\\webRoot");
		this.server.startServer();
	}

}
