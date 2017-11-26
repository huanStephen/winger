package org.eocencle.winger.mapping;

import java.util.Map;

import org.eocencle.winger.session.Configuration;
/**
 * 抽象响应分支
 * @author huanStephen
 *
 */
public abstract class AbstractResponseBranch {
	// 请求名称
	protected String name;
	// 请求方式
	protected RequestType type;
	// 配置
	protected Configuration configuration;
	
	public AbstractResponseBranch(String action, Configuration configuration) {
		this(action, RequestType.GET, configuration);
	}
	
	public AbstractResponseBranch(String name, RequestType type, Configuration configuration) {
		this.name = name;
		this.type = type;
		this.configuration = configuration;
	}
	
	public abstract BoundJson getBoundJson(Map<String, Object> params);
	
	public static enum RequestType {
		GET, POST, PUT, DELETE
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
}
