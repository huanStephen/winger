package org.eocencle.winger.mapping;

import java.util.Map;

import org.eocencle.winger.session.Configuration;
/**
 * XML解析的响应分支
 * @author huanStephen
 *
 */
public final class XmlResponseBranch extends AbstractResponseBranch {
	
	private JsonSource jsonSource;

	public XmlResponseBranch(String name, Configuration configuration, JsonSource jsonSource) {
		super(name, configuration);
		this.configuration = configuration;
		this.jsonSource = jsonSource;
	}
	
	public XmlResponseBranch(String name, RequestType type, Configuration configuration, JsonSource jsonSource) {
		super(name, type, configuration);
		this.configuration = configuration;
		this.jsonSource = jsonSource;
	}

	@Override
	public BoundJson getBoundJson(Map<String, Object> params) {
		return this.jsonSource.getBoundJson(params);
	}

}
