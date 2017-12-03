package org.eocencle.winger.session;

import java.util.Map;

import org.eocencle.winger.mapping.AbstractResponseBranch;

public class DefaultJsonSession implements JsonSession {

	private Configuration configuration;
	
	public DefaultJsonSession(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String request(String name, Map<String, Object> params) {
		AbstractResponseBranch branch = this.configuration.getResponseBranch(name);
		return branch.getBoundJson(params).getJson();
	}

}
