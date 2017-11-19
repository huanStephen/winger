package org.eocencle.winger.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -3132559656666240695L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("OK");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		String jsonStr = "{\"name\":\"fly\",\"type\":\"虫子\"}";
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

}
