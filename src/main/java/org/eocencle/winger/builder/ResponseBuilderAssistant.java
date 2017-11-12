package org.eocencle.winger.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.executor.keygen.KeyGenerator;
import org.eocencle.winger.mapping.CacheBuilder;
import org.eocencle.winger.mapping.Discriminator;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.MappedStatement;
import org.eocencle.winger.mapping.ParameterMap;
import org.eocencle.winger.mapping.ParameterMapping;
import org.eocencle.winger.mapping.ParameterMode;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.mapping.ResultFlag;
import org.eocencle.winger.mapping.ResultMap;
import org.eocencle.winger.mapping.ResultMapping;
import org.eocencle.winger.mapping.ResultSetType;
import org.eocencle.winger.mapping.SqlCommandType;
import org.eocencle.winger.mapping.SqlSource;
import org.eocencle.winger.mapping.StatementType;
import org.eocencle.winger.reflection.MetaClass;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.ContextPathType;
import org.eocencle.winger.type.JdbcType;
import org.eocencle.winger.type.TypeHandler;

public class ResponseBuilderAssistant extends BaseBuilder {
	private String currentContextPath;
	private String resource;
	private Cache currentCache;
	private boolean unresolvedCacheRef;

	public ResponseBuilderAssistant(Configuration configuration, String resource) {
		super(configuration);
		ErrorContext.instance().resource(resource);
		this.resource = resource;
	}

	public String getCurrentContextPath() {
		return currentContextPath;
	}

	public void setCurrentContextPath(String currentContextPath) {
		if (currentContextPath == null) {
			throw new BuilderException("The response element requires a contextpath attribute to be specified.");
		}

		if (this.currentContextPath != null && !this.currentContextPath.equals(currentContextPath)) {
			throw new BuilderException("Wrong namespace. Expected '" + this.currentContextPath + "' but found '" + currentContextPath + "'.");
		}

		this.currentContextPath = currentContextPath;
	}

	public String applyCurrentContextPath(String base, boolean isReference, ContextPathType type) {
		if (base == null || base.isEmpty()) return null;
		// branch不能被索引
		if (isReference && ContextPathType.BRANCH == type) {
			throw new BuilderException("Branches cannot be referenced");
		}
		if (ContextPathType.BRANCH == type) {
			// 第一个字符不是/则补全；仅有/的抛无效action异常
			if (0 != base.indexOf("/")) {
				base = "/" + base;
			} else {
				if (1 == base.length()) {
					throw new BuilderException("Invalid action from " + base);
				}
			}
		} else if (ContextPathType.JSON == type) {
			// 索引情况下必须包含/和#或者什么也不包含，否则抛无效reference异常
			if (isReference) {
				if (base.contains("/") && base.contains("#")) {
					return base;
				} else {
					if (base.contains("/") || base.contains("#")) {
						throw new BuilderException("Invalid reference from " + base);
					}
				}
			}
			// 非索引情况下不能包含/或#
			else {
				if (base.contains("/") || base.contains("#")) {
					throw new BuilderException("Slashes are not allowed in element id of json, please remove it from " + base);
				}
			}
			base = "#" + base;
		}
		return this.currentContextPath + base;
	}

	public Cache useCacheRef(String contextPath) {
		if (contextPath == null) {
			throw new BuilderException("cache-ref element requires a namespace attribute.");
		}
		try {
			unresolvedCacheRef = true;
			Cache cache = configuration.getCache(contextPath);
			if (cache == null) {
				throw new IncompleteElementException("No cache for contextpath '" + contextPath + "' could be found.");
			}
			currentCache = cache;
			unresolvedCacheRef = false;
			return cache;
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("No cache for contextpath '" + contextPath + "' could be found.", e);
		}
	}

	public Cache useNewCache(Class<? extends Cache> typeClass,
		Class<? extends Cache> evictionClass,
		Long flushInterval,
		Integer size,
		boolean readWrite,
		Properties props) {
		typeClass = valueOrDefault(typeClass, PerpetualCache.class);
		evictionClass = valueOrDefault(evictionClass, LruCache.class);
		Cache cache = new CacheBuilder(currentContextPath)
			.implementation(typeClass)
			.addDecorator(evictionClass)
			.clearInterval(flushInterval)
			.size(size)
			.readWrite(readWrite)
			.properties(props)
			.build();
		configuration.addCache(cache);
		currentCache = cache;
		return cache;
	}

	public ParameterMap addParameterMap(String id, Class<?> parameterClass, List<ParameterMapping> parameterMappings) {
		id = this.applyCurrentContextPath(id, false, ContextPathType.BRANCH);
		ParameterMap.Builder parameterMapBuilder = new ParameterMap.Builder(configuration, id, parameterClass, parameterMappings);
		ParameterMap parameterMap = parameterMapBuilder.build();
		configuration.addParameterMap(parameterMap);
		return parameterMap;
	}

	public ParameterMapping buildParameterMapping(
		Class<?> parameterType,
		String property,
		Class<?> javaType,
		JdbcType jdbcType,
		String resultMap,
		ParameterMode parameterMode,
		Class<? extends TypeHandler<?>> typeHandler,
		Integer numericScale) {
		resultMap = this.applyCurrentContextPath(resultMap, true, ContextPathType.BRANCH);

		// Class parameterType = parameterMapBuilder.type();
		Class<?> javaTypeClass = resolveParameterJavaType(parameterType, property, javaType, jdbcType);
		TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);

		ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, javaTypeClass);
		builder.jdbcType(jdbcType);
		builder.resultMapId(resultMap);
		builder.mode(parameterMode);
		builder.numericScale(numericScale);
		builder.typeHandler(typeHandlerInstance);
		return builder.build();
	}

	public ResultMap addResultMap(
		String id,
		Class<?> type,
		String extend,
		Discriminator discriminator,
		List<ResultMapping> resultMappings,
		Boolean autoMapping) {
		id = this.applyCurrentContextPath(id, false, ContextPathType.BRANCH);
		extend = this.applyCurrentContextPath(extend, true, ContextPathType.BRANCH);

		ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, id, type, resultMappings, autoMapping);
		if (extend != null) {
			if (!configuration.hasResultMap(extend)) {
				throw new IncompleteElementException("Could not find a parent resultmap with id '" + extend + "'");
			}
			ResultMap resultMap = configuration.getResultMap(extend);
			List<ResultMapping> extendedResultMappings = new ArrayList<ResultMapping>(resultMap.getResultMappings());
			extendedResultMappings.removeAll(resultMappings);
			// Remove parent constructor if this resultMap declares a constructor.
			boolean declaresConstructor = false;
			for (ResultMapping resultMapping : resultMappings) {
				if (resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR)) {
					declaresConstructor = true;
					break;
				}
			}
			if (declaresConstructor) {
				Iterator<ResultMapping> extendedResultMappingsIter = extendedResultMappings.iterator();
				while (extendedResultMappingsIter.hasNext()) {
					if (extendedResultMappingsIter.next().getFlags().contains(ResultFlag.CONSTRUCTOR)) {
						extendedResultMappingsIter.remove();
					}
				}
			}
			resultMappings.addAll(extendedResultMappings);
		}
		resultMapBuilder.discriminator(discriminator);
		ResultMap resultMap = resultMapBuilder.build();
		configuration.addResultMap(resultMap);
		return resultMap;
	}

	public ResultMapping buildResultMapping(
		Class<?> resultType,
		String property,
		String column,
		Class<?> javaType,
		JdbcType jdbcType,
		String nestedSelect,
		String nestedResultMap,
		String notNullColumn,
		String columnPrefix,
		Class<? extends TypeHandler<?>> typeHandler,
		List<ResultFlag> flags) {
		ResultMapping resultMapping = assembleResultMapping(
			resultType,
			property,
			column,
			javaType,
			jdbcType,
			nestedSelect,
			nestedResultMap,
			notNullColumn,
			columnPrefix,
			typeHandler,
			flags);
		return resultMapping;
	}

	public Discriminator buildDiscriminator(
		Class<?> resultType,
		String column,
		Class<?> javaType,
		JdbcType jdbcType,
		Class<? extends TypeHandler<?>> typeHandler,
		Map<String, String> discriminatorMap) {
		ResultMapping resultMapping = assembleResultMapping(
			resultType,
			null,
			column,
			javaType,
			jdbcType,
			null,
			null,
			null,
			null,
			typeHandler,
			new ArrayList<ResultFlag>());
		Map<String, String> namespaceDiscriminatorMap = new HashMap<String, String>();
		for (Map.Entry<String, String> e : discriminatorMap.entrySet()) {
			String resultMap = e.getValue();
			resultMap = this.applyCurrentContextPath(resultMap, true, ContextPathType.BRANCH);
			namespaceDiscriminatorMap.put(e.getKey(), resultMap);
		}
		Discriminator.Builder discriminatorBuilder = new Discriminator.Builder(configuration, resultMapping, namespaceDiscriminatorMap);
		return discriminatorBuilder.build();
	}

	public MappedStatement addMappedStatement(
			String id,
			SqlSource sqlSource,
			StatementType statementType,
			SqlCommandType sqlCommandType,
			Integer fetchSize,
			Integer timeout,
			String parameterMap,
			Class<?> parameterType,
			String resultMap,
			Class<?> resultType,
			ResultSetType resultSetType,
			boolean flushCache,
			boolean useCache,
			boolean resultOrdered,
			KeyGenerator keyGenerator,
			String keyProperty,
			String keyColumn,
			String databaseId,
			LanguageDriver lang) {
		
		if (unresolvedCacheRef) throw new IncompleteElementException("Cache-ref not yet resolved");
		
		id = this.applyCurrentContextPath(id, false, ContextPathType.BRANCH);
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType);
		statementBuilder.resource(resource);
		statementBuilder.fetchSize(fetchSize);
		statementBuilder.statementType(statementType);
		statementBuilder.keyGenerator(keyGenerator);
		statementBuilder.keyProperty(keyProperty);
		statementBuilder.keyColumn(keyColumn);
		statementBuilder.databaseId(databaseId);
		statementBuilder.lang(lang);
		statementBuilder.resultOrdered(resultOrdered);
		setStatementTimeout(timeout, statementBuilder);

		setStatementParameterMap(parameterMap, parameterType, statementBuilder);
		setStatementResultMap(resultMap, resultType, resultSetType, statementBuilder);
		setStatementCache(isSelect, flushCache, useCache, currentCache, statementBuilder);

		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
		return statement;
	}
	
	public ResponseBranch addResponseBranch(String action, String method, JsonSource jsonSource, StatementType statementType, 
		SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap, Class<?> parameterType, String resultMap, 
		Class<?> resultType, ResultSetType resultSetType, boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, 
		String keyProperty, String keyColumn, String databaseId, LanguageDriver lang) {
		
		if (unresolvedCacheRef) throw new IncompleteElementException("Cache-ref not yet resolved");
		
		action = this.applyCurrentContextPath(action, false, ContextPathType.BRANCH);
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

		ResponseBranch.Builder branchBuilder = new ResponseBranch.Builder(configuration, action, method, jsonSource, sqlCommandType);
		branchBuilder.resource(resource);
		branchBuilder.fetchSize(fetchSize);
		branchBuilder.statementType(statementType);
		branchBuilder.keyGenerator(keyGenerator);
		branchBuilder.keyProperty(keyProperty);
		branchBuilder.keyColumn(keyColumn);
		branchBuilder.databaseId(databaseId);
		branchBuilder.lang(lang);
		branchBuilder.resultOrdered(resultOrdered);
		
		
		this.setBranchTimeout(timeout, branchBuilder);

		this.setBranchParameterMap(parameterMap, parameterType, branchBuilder);
		this.setBranchResultMap(resultMap, resultType, resultSetType, branchBuilder);
		this.setBranchCache(isSelect, flushCache, useCache, this.currentCache, branchBuilder);

		ResponseBranch branch = branchBuilder.build();
		this.configuration.addResponseBranch(branch);
		return branch;
	}

	private <T> T valueOrDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	private void setStatementCache(
		boolean isSelect,
		boolean flushCache,
		boolean useCache,
		Cache cache,
		MappedStatement.Builder statementBuilder) {
		flushCache = valueOrDefault(flushCache, !isSelect);
		useCache = valueOrDefault(useCache, isSelect);
		statementBuilder.flushCacheRequired(flushCache);
		statementBuilder.useCache(useCache);
		statementBuilder.cache(cache);
	}
	
	private void setBranchCache(boolean isSelect, boolean flushCache, boolean useCache, Cache cache, ResponseBranch.Builder branchBuilder) {
		flushCache = valueOrDefault(flushCache, !isSelect);
		useCache = valueOrDefault(useCache, isSelect);
		branchBuilder.flushCacheRequired(flushCache);
		branchBuilder.useCache(useCache);
		branchBuilder.cache(cache);
	}

	private void setStatementParameterMap(
		String parameterMap,
		Class<?> parameterTypeClass,
		MappedStatement.Builder statementBuilder) {
		parameterMap = this.applyCurrentContextPath(parameterMap, true, ContextPathType.BRANCH);

		if (parameterMap != null) {
			try {
				statementBuilder.parameterMap(configuration.getParameterMap(parameterMap));
			} catch (IllegalArgumentException e) {
				throw new IncompleteElementException("Could not find parameter map " + parameterMap, e);
			}
		} else if (parameterTypeClass != null) {
			List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
			ParameterMap.Builder inlineParameterMapBuilder = new ParameterMap.Builder(
				configuration,
				statementBuilder.id() + "-Inline",
				parameterTypeClass,
				parameterMappings);
			statementBuilder.parameterMap(inlineParameterMapBuilder.build());
		}
	}
	
	private void setBranchParameterMap(String parameterMap, Class<?> parameterTypeClass, ResponseBranch.Builder branchBuilder) {
		parameterMap = this.applyCurrentContextPath(parameterMap, true, ContextPathType.BRANCH);

		if (parameterMap != null) {
			try {
				branchBuilder.parameterMap(configuration.getParameterMap(parameterMap));
			} catch (IllegalArgumentException e) {
				throw new IncompleteElementException("Could not find parameter map " + parameterMap, e);
			}
		} else if (parameterTypeClass != null) {
			List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
			ParameterMap.Builder inlineParameterMapBuilder = new ParameterMap.Builder(
				configuration,
				branchBuilder.action() + "-Inline",
				parameterTypeClass,
				parameterMappings);
			branchBuilder.parameterMap(inlineParameterMapBuilder.build());
		}
	}

	private void setStatementResultMap(
		String resultMap,
		Class<?> resultType,
		ResultSetType resultSetType,
		MappedStatement.Builder statementBuilder) {
		resultMap = this.applyCurrentContextPath(resultMap, true, ContextPathType.BRANCH);

		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		if (resultMap != null) {
			String[] resultMapNames = resultMap.split(",");
			for (String resultMapName : resultMapNames) {
				try {
					resultMaps.add(configuration.getResultMap(resultMapName.trim()));
				} catch (IllegalArgumentException e) {
					throw new IncompleteElementException("Could not find result map " + resultMapName, e);
				}
			}
		} else if (resultType != null) {
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
				configuration,
				statementBuilder.id() + "-Inline",
				resultType,
				new ArrayList<ResultMapping>(),
				null);
			resultMaps.add(inlineResultMapBuilder.build());
		}
		statementBuilder.resultMaps(resultMaps);

		statementBuilder.resultSetType(resultSetType);
	}
	
	private void setBranchResultMap(String resultMap, Class<?> resultType, ResultSetType resultSetType, ResponseBranch.Builder branchBuilder) {
		resultMap = this.applyCurrentContextPath(resultMap, true, ContextPathType.BRANCH);

		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		if (resultMap != null) {
			String[] resultMapNames = resultMap.split(",");
			for (String resultMapName : resultMapNames) {
				try {
					resultMaps.add(configuration.getResultMap(resultMapName.trim()));
				} catch (IllegalArgumentException e) {
					throw new IncompleteElementException("Could not find result map " + resultMapName, e);
				}
			}
		} else if (resultType != null) {
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
				configuration,
				branchBuilder.action() + "-Inline",
				resultType,
				new ArrayList<ResultMapping>(),
				null);
			resultMaps.add(inlineResultMapBuilder.build());
		}
		branchBuilder.resultMaps(resultMaps);

		branchBuilder.resultSetType(resultSetType);
	}

	private void setStatementTimeout(Integer timeout, MappedStatement.Builder statementBuilder) {
		if (timeout == null) {
			timeout = configuration.getDefaultStatementTimeout();
		}
		statementBuilder.timeout(timeout);
	}
	
	private void setBranchTimeout(Integer timeout, ResponseBranch.Builder responseBuilder) {
		if (timeout == null) {
			timeout = this.configuration.getDefaultStatementTimeout();
		}
		responseBuilder.timeout(timeout);
	}

	private ResultMapping assembleResultMapping(
		Class<?> resultType,
		String property,
		String column,
		Class<?> javaType,
		JdbcType jdbcType,
		String nestedSelect,
		String nestedResultMap,
		String notNullColumn,
		String columnPrefix,
		Class<? extends TypeHandler<?>> typeHandler,
		List<ResultFlag> flags) {
		Class<?> javaTypeClass = resolveResultJavaType(resultType, property, javaType);
		TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
		List<ResultMapping> composites = parseCompositeColumnName(column);
		if (composites.size() > 0) column = null;
		ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property, column, javaTypeClass);
		builder.jdbcType(jdbcType);
		builder.nestedQueryId(this.applyCurrentContextPath(nestedSelect, true, ContextPathType.BRANCH));
		builder.nestedResultMapId(this.applyCurrentContextPath(nestedResultMap, true, ContextPathType.BRANCH));
		builder.typeHandler(typeHandlerInstance);
		builder.flags(flags == null ? new ArrayList<ResultFlag>() : flags);
		builder.composites(composites);
		builder.notNullColumns(parseMultipleColumnNames(notNullColumn));
		builder.columnPrefix(columnPrefix);
		return builder.build();
	}

	private Set<String> parseMultipleColumnNames(String columnName) {
		Set<String> columns = new HashSet<String>();
		if (columnName != null) {
			if (columnName.indexOf(',') > -1) {
				StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
				while (parser.hasMoreTokens()) {
					String column = parser.nextToken();
					columns.add(column);
				}
			} else {
				columns.add(columnName);
			}
		}
		return columns;
	}

	private List<ResultMapping> parseCompositeColumnName(String columnName) {
		List<ResultMapping> composites = new ArrayList<ResultMapping>();
		if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
			StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
			while (parser.hasMoreTokens()) {
				String property = parser.nextToken();
				String column = parser.nextToken();
				ResultMapping.Builder complexBuilder = new ResultMapping.Builder(configuration, property, column, configuration.getTypeHandlerRegistry().getUnknownTypeHandler());
				composites.add(complexBuilder.build());
			}
		}
		return composites;
	}

	private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
		if (javaType == null && property != null) {
			try {
				MetaClass metaResultType = MetaClass.forClass(resultType);
				javaType = metaResultType.getSetterType(property);
			} catch (Exception e) {
				//ignore, following null check statement will deal with the situation
			}
		}
		if (javaType == null) {
			javaType = Object.class;
		}
		return javaType;
	}

	private Class<?> resolveParameterJavaType(Class<?> resultType, String property, Class<?> javaType, JdbcType jdbcType) {
		if (javaType == null) {
			if (JdbcType.CURSOR.equals(jdbcType)) {
				javaType = java.sql.ResultSet.class;
			} else if (Map.class.isAssignableFrom(resultType)) {
				javaType = Object.class;
			} else {
				MetaClass metaResultType = MetaClass.forClass(resultType);
				javaType = metaResultType.getGetterType(property);
			}
		}
		if (javaType == null) {
			javaType = Object.class;
		}
		return javaType;
	}
}
