package org.eocencle.winger.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.gateway.HttpHandler;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.session.JsonSession;
import org.eocencle.winger.session.JsonSessionFactory;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -3132559656666240695L;
	
	private JsonSession session;
	
	private HttpHandler httpHandler;

	@Override
	public void init() throws ServletException {
		InputStream is = null;
		try {
			is = Resources.getResourceAsStream("config.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		session = new JsonSessionFactory().build(is);
		this.httpHandler = new HttpHandler(this.session);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.httpHandler.handle(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.httpHandler.handle(request, response);
	}

}
