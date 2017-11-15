package org.eocencle.winger.builder;

import java.util.Map;

import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.session.Configuration;

public class StaticJsonSource implements JsonSource {
	private String json;
	private Configuration configuration;

	public StaticJsonSource(Configuration configuration, String json) {
		this.json = json;
		this.configuration = configuration;
	}

	public BoundJson getBoundJson(Map<String, Object> params) {
		return new BoundJson(this.configuration, this.json, params);
	}
}
