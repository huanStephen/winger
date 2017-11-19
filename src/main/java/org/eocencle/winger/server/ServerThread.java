package org.eocencle.winger.server;

import org.eclipse.jetty.server.Server;

public class ServerThread extends Thread {
	
	private volatile Server server;
	
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
			server = new Server(8088);
			server.setHandler(new HelloWorld());
			server.start();
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
