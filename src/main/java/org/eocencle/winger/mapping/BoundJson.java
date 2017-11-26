package org.eocencle.winger.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;

public class BoundJson {
	private String json;
	private List<ParameterMapping> parameterMappings;
	private Object parameterObject;
	private Map<String, Object> additionalParameters;
	private MetaObject metaParameters;
	
	public BoundJson(Configuration configuration, String json) {
		this(configuration, json, null);
	}

	public BoundJson(Configuration configuration, String json, Object parameterObject) {
		this.json = json;
		this.parameterObject = parameterObject;
		this.additionalParameters = new HashMap<String, Object>();
		this.metaParameters = configuration.newMetaObject(additionalParameters);
	}

	public String getJson() {
		return this.json;
	}

	public List<ParameterMapping> getParameterMappings() {
		return this.parameterMappings;
	}

	public Object getParameterObject() {
		return this.parameterObject;
	}

	public boolean hasAdditionalParameter(String name) {
		return this.metaParameters.hasGetter(name);
	}

	public void setAdditionalParameter(String name, Object value) {
		this.metaParameters.setValue(name, value);
	}

	public Object getAdditionalParameter(String name) {
		return this.metaParameters.getValue(name);
	}
}
