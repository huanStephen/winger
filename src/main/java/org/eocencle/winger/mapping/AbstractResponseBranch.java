package org.eocencle.winger.mapping;

import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.util.StrictMap;

/**
 * 抽象响应分支
 * @author huan
 *
 */
public abstract class AbstractResponseBranch {

	// 配置数据
	protected Configuration config;
	// 命名空间
	protected String namespace;
	// 完整url
	protected String url;
	// 请求类型
	protected String type = "GET";
	
	public static String TYPE_GET = "GET";
	public static String TYPE_POST = "POST";
	public static String TYPE_PUT = "PUT";
	public static String TYPE_DELETE = "DELETE";
	
	public AbstractResponseBranch(Configuration config, String namespace, String url) {
		this.config = config;
		this.namespace = namespace;
		this.url = url;
	}

	/**
	 * 获取完整json
	 * @param params	请求参数
	 * @return	json字符串
	 * @throws Exception	参数异常
	 */
	public abstract String getCompleteJson(StrictMap<Object> params) throws WingerException;

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
