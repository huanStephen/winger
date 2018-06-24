package org.eocencle.winger.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eocencle.winger.exceptions.IllegalParamException;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.mapping.AbstractResponseBranch;
import org.eocencle.winger.util.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用session
 * 提供传入名称和参数Map，而返回字符串的功能
 * @author huan
 *
 */
public abstract class Session {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
	
	protected Configuration config;
	
	public Session(Configuration config) {
		this.config = config;
	}
	
	public final String request(String name, StrictMap<Object> params) throws WingerException {
		AbstractResponseBranch branch = null;
		try {
			branch = this.config.getBranch(name);
		} catch (IllegalArgumentException e) {
			throw new IllegalParamException(e.getMessage());
		}
		return branch.getCompleteJson(params);
	}
	
	public boolean request(HttpServletRequest request, HttpServletResponse response) {
		// 如果子类未实现则抛出不支持异常
		LOGGER.debug("不支持HTTP协议请求！");
		throw new RuntimeException("不支持HTTP协议请求！");
	}
}
