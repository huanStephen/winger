package org.eocencle.winger.mapping;

import java.util.HashMap;
import java.util.Map;

import org.eocencle.winger.session.Configuration;

public class BoundJson {
	private String json;
	private Object parameterObject;
	private Map<String, Object> additionalParameters;
	private Map<String, Object> metaParameters;
	
	public BoundJson(Configuration config, String json) {
		this(config, json, null);
	}

	public BoundJson(Configuration configuration, String json, Object parameterObject) {
		this.json = json;
		this.parameterObject = parameterObject;
		this.additionalParameters = new HashMap<String, Object>();
		this.metaParameters = new HashMap<String, Object>();
	}

	public String getJson() {
		return this.json;
	}

	public Object getParameterObject() {
		return this.parameterObject;
	}

	public boolean hasAdditionalParameter(String name) {
		//return this.metaParameters.hasGetter(name);
		return false;
	}

	public void setAdditionalParameter(String name, Object value) {
		this.metaParameters.put(name, value);
	}

	public Object getAdditionalParameter(String name) {
		return this.metaParameters.get(name);
	}
}
