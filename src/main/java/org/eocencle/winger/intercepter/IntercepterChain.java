package org.eocencle.winger.intercepter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器链
 * @author huan
 *
 */
public class IntercepterChain implements Intercepter {
	
	private List<Intercepter> intercepters = new ArrayList<Intercepter>();
	private int index = 0;
	
	public IntercepterChain addIntercepter(Intercepter i) {
		this.intercepters.add(i);
		return this;
	} 

	public void doIntercepter(HttpServletRequest request, HttpServletResponse response, IntercepterChain chain) {
		if (index == intercepters.size()) return;
		
		Intercepter i = intercepters.get(index);
		index ++;
		i.doIntercepter(request, response, chain);
	}
	
	public void open() {
		this.index = 0;
	}

}
