package org.eocencle.winger.session;

import java.util.Map;

import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.ResponseBranch;

public class DefaultJsonSession implements JsonSession {

	private Configuration configuration;
	
	@Override
	public String request(String action, Map<String, Object> params) {
		ResponseBranch branch = this.configuration.getResponseBranch(action);
		BoundJson json = branch.getBoundJson(params);
		return null;
	}

}
