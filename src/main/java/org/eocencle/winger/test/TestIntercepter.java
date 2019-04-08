package org.eocencle.winger.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.intercepter.Intercepter;
import org.eocencle.winger.intercepter.IntercepterChain;

public class TestIntercepter implements Intercepter {

	public void doIntercepter(HttpServletRequest request, HttpServletResponse response, IntercepterChain chain) {
		
	}

}
