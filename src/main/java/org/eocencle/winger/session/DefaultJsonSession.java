package org.eocencle.winger.session;

import java.util.Map;

import org.eocencle.winger.mapping.ResponseBranch;

public class DefaultJsonSession implements JsonSession {

	private Configuration configuration;
	
	public DefaultJsonSession(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String request(String action, Map<String, Object> params) {
		ResponseBranch branch = this.configuration.getResponseBranch(action);
		return branch.getBoundJson(params).getJson();
	}

}
