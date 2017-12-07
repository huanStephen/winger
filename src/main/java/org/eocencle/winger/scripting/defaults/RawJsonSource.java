package org.eocencle.winger.scripting.defaults;

import java.util.HashMap;
import java.util.Map;

import org.eocencle.winger.builder.JsonSourceBuilder;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.session.Configuration;

public class RawJsonSource implements JsonSource {
	private final Configuration configuration;
	private final String json;

	public RawJsonSource(Configuration configuration, String json) {
		this.configuration = configuration;
		this.json = json;
	}

	public BoundJson getBoundJson(Map<String, Object> params) {
		JsonSourceBuilder jsonSourceParser = new JsonSourceBuilder(configuration);
		Class<?> parameterType = params == null ? Object.class : params.getClass();
		JsonSource jsonSource = jsonSourceParser.parse(this.json, parameterType, new HashMap<String, Object>());
		BoundJson boundJson = jsonSource.getBoundJson(params);
		return boundJson;
	}
}
