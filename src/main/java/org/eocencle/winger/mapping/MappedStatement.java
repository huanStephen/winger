package org.eocencle.winger.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.keygen.Jdbc3KeyGenerator;
import org.eocencle.winger.executor.keygen.KeyGenerator;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

public final class MappedStatement {
	private String resource;
	private Configuration configuration;
	private String id;
	private Integer fetchSize;
	private Integer timeout;
	private StatementType statementType;
	private ResultSetType resultSetType;
	private SqlSource sqlSource;
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

	private MappedStatement() {
		// constructor disabled
	}

	public static class Builder {
		private MappedStatement mappedStatement = new MappedStatement();

		public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
			mappedStatement.configuration = configuration;
			mappedStatement.id = id;
			mappedStatement.sqlSource = sqlSource;
			mappedStatement.statementType = StatementType.PREPARED;
			mappedStatement.parameterMap = new ParameterMap.Builder(configuration, "defaultParameterMap", null, new ArrayList<ParameterMapping>()).build();
			mappedStatement.resultMaps = new ArrayList<ResultMap>();
			mappedStatement.timeout = configuration.getDefaultStatementTimeout();
			mappedStatement.sqlCommandType = sqlCommandType;
			mappedStatement.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
			String logId = id;
			if (configuration.getLogPrefix() != null) logId = configuration.getLogPrefix() + id;
			mappedStatement.statementLog = LogFactory.getLog(logId);
			mappedStatement.lang = configuration.getDefaultScriptingLanuageInstance();
		}

		public Builder resource(String resource) {
			mappedStatement.resource = resource;
			return this;
		}

		public String id() {
			return mappedStatement.id;
		}

		public Builder parameterMap(ParameterMap parameterMap) {
			mappedStatement.parameterMap = parameterMap;
			return this;
		}

		public Builder resultMaps(List<ResultMap> resultMaps) {
			mappedStatement.resultMaps = resultMaps;
			for (ResultMap resultMap : resultMaps) {
				mappedStatement.hasNestedResultMaps = mappedStatement.hasNestedResultMaps || resultMap.hasNestedResultMaps();
			}
			return this;
		}

		public Builder fetchSize(Integer fetchSize) {
			mappedStatement.fetchSize = fetchSize;
			return this;
		}

		public Builder timeout(Integer timeout) {
			mappedStatement.timeout = timeout;
			return this;
		}

		public Builder statementType(StatementType statementType) {
			mappedStatement.statementType = statementType;
			return this;
		}

		public Builder resultSetType(ResultSetType resultSetType) {
			mappedStatement.resultSetType = resultSetType;
			return this;
		}

		public Builder cache(Cache cache) {
			mappedStatement.cache = cache;
			return this;
		}

		public Builder flushCacheRequired(boolean flushCacheRequired) {
			mappedStatement.flushCacheRequired = flushCacheRequired;
			return this;
		}

		public Builder useCache(boolean useCache) {
			mappedStatement.useCache = useCache;
			return this;
		}

		public Builder resultOrdered(boolean resultOrdered) {
			mappedStatement.resultOrdered = resultOrdered;
			return this;
		}

		public Builder keyGenerator(KeyGenerator keyGenerator) {
			mappedStatement.keyGenerator = keyGenerator;
			return this;
		}

		public Builder keyProperty(String keyProperty) {
			mappedStatement.keyProperties = delimitedStringtoArray(keyProperty);
			return this;
		}

		public Builder keyColumn(String keyColumn) {
			mappedStatement.keyColumns = delimitedStringtoArray(keyColumn);
			return this;
		}

		public Builder databaseId(String databaseId) {
			mappedStatement.databaseId = databaseId;
			return this;
		}

		public Builder lang(LanguageDriver driver) {
			mappedStatement.lang = driver;
			return this;
		}

		public MappedStatement build() {
			assert mappedStatement.configuration != null;
			assert mappedStatement.id != null;
			assert mappedStatement.sqlSource != null;
			assert mappedStatement.lang != null;
			mappedStatement.resultMaps = Collections.unmodifiableList(mappedStatement.resultMaps);
			return mappedStatement;
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

	public String getId() {
		return id;
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

	public SqlSource getSqlSource() {
		return sqlSource;
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

	public BoundSql getBoundSql(Object parameterObject) {
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings == null || parameterMappings.size() <= 0) {
			boundSql = new BoundSql(configuration, boundSql.getSql(), parameterMap.getParameterMappings(), parameterObject);
		}

		// check for nested result maps in parameter mappings (issue #30)
		for (ParameterMapping pm : boundSql.getParameterMappings()) {
			String rmId = pm.getResultMapId();
			if (rmId != null) {
				ResultMap rm = configuration.getResultMap(rmId);
				if (rm != null) {
					hasNestedResultMaps |= rm.hasNestedResultMaps();
				}
			}
		}

		return boundSql;
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
