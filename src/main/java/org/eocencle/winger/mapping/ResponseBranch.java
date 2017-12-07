package org.eocencle.winger.mapping;

import java.util.Map;

import org.eocencle.winger.logging.Log;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

public final class ResponseBranch {
	private String resource;
	private Configuration configuration;
	private String action;
	private String method;
	private JsonSource jsonSource;
	private Log jsonLog;
	private LanguageDriver lang;

	private ResponseBranch() {
		// constructor disabled
	}

	public static class Builder {
		private ResponseBranch responseBranch = new ResponseBranch();

		public Builder(Configuration configuration, String action, String method, JsonSource jsonSource) {
			this.responseBranch.configuration = configuration;
			this.responseBranch.action = action;
			this.responseBranch.method = method;
			this.responseBranch.jsonSource = jsonSource;
			String logId = action;
			if (configuration.getLogPrefix() != null) logId = configuration.getLogPrefix() + action;
			//this.responseBranch.jsonLog = LogFactory.getLog(logId);
			this.responseBranch.lang = configuration.getDefaultScriptingLanuageInstance();
		}

		public Builder resource(String resource) {
			this.responseBranch.resource = resource;
			return this;
		}

		public String action() {
			return this.responseBranch.action;
		}
		
		public String method() {
			return this.responseBranch.method;
		}

		public Builder lang(LanguageDriver driver) {
			this.responseBranch.lang = driver;
			return this;
		}

		public ResponseBranch build() {
			assert this.responseBranch.configuration != null;
			assert this.responseBranch.action != null;
			assert this.responseBranch.jsonSource != null;
			assert this.responseBranch.lang != null;
			return this.responseBranch;
		}
	}

	public String getResource() {
		return resource;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getAction() {
		return this.action;
	}
	
	public String getMethod() {
		return this.method;
	}

	public Log getStatementLog() {
		return jsonLog;
	}

	public LanguageDriver getLang() {
		return lang;
	}

	public BoundJson getBoundJson(Map<String, Object> params) {
		BoundJson boundJson = this.jsonSource.getBoundJson(params);
		return new BoundJson(configuration, boundJson.getJson(), params);
	}
}
