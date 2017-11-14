package org.eocencle.winger.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.keygen.Jdbc3KeyGenerator;
import org.eocencle.winger.executor.keygen.KeyGenerator;
import org.eocencle.winger.executor.keygen.NoKeyGenerator;
import org.eocencle.winger.logging.Log;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

public final class ResponseBranch {
	private String resource;
	private Configuration configuration;
	private String action;
	private String method;
	private Integer fetchSize;
	private Integer timeout;
	private StatementType statementType;
	private ResultSetType resultSetType;
	private JsonSource jsonSource;
	private Cache cache;
	private ParameterMap parameterMap;
	private List<ResultMap> resultMaps;
	private boolean flushCacheRequired;
	private boolean useCache;
	private boolean resultOrdered;
	private SqlCommandType sqlCommandType;
	private KeyGenerator keyGenerator;
	private String[] keyProperties;
	private String[] keyColumns;
	private boolean hasNestedResultMaps;
	private String databaseId;
	private Log statementLog;
	private LanguageDriver lang;

	private ResponseBranch() {
		// constructor disabled
	}

	public static class Builder {
		private ResponseBranch responseBranch = new ResponseBranch();

		public Builder(Configuration configuration, String action, String method, JsonSource jsonSource, SqlCommandType sqlCommandType) {
			this.responseBranch.configuration = configuration;
			this.responseBranch.action = action;
			this.responseBranch.method = method;
			this.responseBranch.jsonSource = jsonSource;
			this.responseBranch.statementType = StatementType.PREPARED;
			this.responseBranch.parameterMap = new ParameterMap.Builder(configuration, "defaultParameterMap", null, new ArrayList<ParameterMapping>()).build();
			this.responseBranch.resultMaps = new ArrayList<ResultMap>();
			this.responseBranch.timeout = configuration.getDefaultStatementTimeout();
			this.responseBranch.sqlCommandType = sqlCommandType;
			this.responseBranch.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
			String logId = action;
			if (configuration.getLogPrefix() != null) logId = configuration.getLogPrefix() + action;
			//this.responseBranch.statementLog = LogFactory.getLog(logId);
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

		public Builder parameterMap(ParameterMap parameterMap) {
			this.responseBranch.parameterMap = parameterMap;
			return this;
		}

		public Builder resultMaps(List<ResultMap> resultMaps) {
			this.responseBranch.resultMaps = resultMaps;
			for (ResultMap resultMap : resultMaps) {
				this.responseBranch.hasNestedResultMaps = this.responseBranch.hasNestedResultMaps || resultMap.hasNestedResultMaps();
			}
			return this;
		}

		public Builder fetchSize(Integer fetchSize) {
			this.responseBranch.fetchSize = fetchSize;
			return this;
		}

		public Builder timeout(Integer timeout) {
			this.responseBranch.timeout = timeout;
			return this;
		}

		public Builder statementType(StatementType statementType) {
			this.responseBranch.statementType = statementType;
			return this;
		}

		public Builder resultSetType(ResultSetType resultSetType) {
			this.responseBranch.resultSetType = resultSetType;
			return this;
		}

		public Builder cache(Cache cache) {
			this.responseBranch.cache = cache;
			return this;
		}

		public Builder flushCacheRequired(boolean flushCacheRequired) {
			this.responseBranch.flushCacheRequired = flushCacheRequired;
			return this;
		}

		public Builder useCache(boolean useCache) {
			this.responseBranch.useCache = useCache;
			return this;
		}

		public Builder resultOrdered(boolean resultOrdered) {
			this.responseBranch.resultOrdered = resultOrdered;
			return this;
		}

		public Builder keyGenerator(KeyGenerator keyGenerator) {
			this.responseBranch.keyGenerator = keyGenerator;
			return this;
		}

		public Builder keyProperty(String keyProperty) {
			this.responseBranch.keyProperties = delimitedStringtoArray(keyProperty);
			return this;
		}

		public Builder keyColumn(String keyColumn) {
			this.responseBranch.keyColumns = delimitedStringtoArray(keyColumn);
			return this;
		}

		public Builder databaseId(String databaseId) {
			this.responseBranch.databaseId = databaseId;
			return this;
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
			this.responseBranch.resultMaps = Collections.unmodifiableList(this.responseBranch.resultMaps);
			return this.responseBranch;
		}
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
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

	public boolean hasNestedResultMaps() {
		return hasNestedResultMaps;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public StatementType getStatementType() {
		return statementType;
	}

	public ResultSetType getResultSetType() {
		return resultSetType;
	}

	public JsonSource getJsonSource() {
		return this.jsonSource;
	}

	public ParameterMap getParameterMap() {
		return parameterMap;
	}

	public List<ResultMap> getResultMaps() {
		return resultMaps;
	}

	public Cache getCache() {
		return cache;
	}

	public boolean isFlushCacheRequired() {
		return flushCacheRequired;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public boolean isResultOrdered() {
		return resultOrdered;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public String[] getKeyProperties() {
		return keyProperties;
	}

	public String[] getKeyColumns() {
		return keyColumns;
	}

	public Log getStatementLog() {
		return statementLog;
	}

	public LanguageDriver getLang() {
		return lang;
	}

	public BoundJson getBoundJson(Map<String, Object> params) {
		BoundJson boundJson = this.jsonSource.getBoundJson(params);
		List<ParameterMapping> parameterMappings = boundJson.getParameterMappings();
		if (parameterMappings == null || parameterMappings.size() <= 0) {
			boundJson = new BoundJson(configuration, boundJson.getJson(), parameterMap.getParameterMappings(), params);
		}

		for (ParameterMapping pm : boundJson.getParameterMappings()) {
			String rmId = pm.getResultMapId();
			if (rmId != null) {
				ResultMap rm = configuration.getResultMap(rmId);
				if (rm != null) {
					hasNestedResultMaps |= rm.hasNestedResultMaps();
				}
			}
		}

		return boundJson;
	}

	private static String[] delimitedStringtoArray(String in) {
		if (in == null || in.trim().length() == 0) {
			return null;
		} else {
			String[] answer = in.split(",");
			return answer;
		}
	}
}
