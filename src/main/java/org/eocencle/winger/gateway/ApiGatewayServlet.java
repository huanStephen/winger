package org.eocencle.winger.gateway;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ApiGatewayServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private ApplicationContext context;
	
	private ApiGatewayHandler apiHandler;

	@Override
	public void init() throws ServletException {
		super.init();
		this.context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		this.apiHandler = this.context.getBean(ApiGatewayHandler.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.apiHandler.handle(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.apiHandler.handle(request, response);
	}

}
