package org.eocencle.winger.builder;

import java.util.List;

import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.ParameterMapping;
import org.eocencle.winger.session.Configuration;

public class StaticJsonSource implements JsonSource {
	private String json;
	private List<ParameterMapping> parameterMappings;
	private Configuration configuration;

	public StaticJsonSource(Configuration configuration, String json) {
		this(configuration, json, null);
	}

	public StaticJsonSource(Configuration configuration, String json, List<ParameterMapping> parameterMappings) {
		this.json = json;
		this.parameterMappings = parameterMappings;
		this.configuration = configuration;
	}

	public BoundJson getBoundJson(Object parameterObject) {
		return new BoundJson(this.configuration, this.json, this.parameterMappings, parameterObject);
	}
}
