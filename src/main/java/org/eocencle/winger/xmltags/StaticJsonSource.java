package org.eocencle.winger.xmltags;

import java.util.Map;

import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.session.Configuration;

public class StaticJsonSource implements JsonSource {
	private String json;
	private Configuration config;

	public StaticJsonSource(Configuration config, String json) {
		this.json = json;
		this.config = config;
	}

	public BoundJson getBoundJson(Map<String, Object> params) {
		return new BoundJson(this.config, this.json, params);
	}
}
