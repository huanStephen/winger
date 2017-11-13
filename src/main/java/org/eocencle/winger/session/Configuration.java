package org.eocencle.winger.session;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eocencle.winger.binding.MapperRegistry;
import org.eocencle.winger.binding.ResponseRegistry;
import org.eocencle.winger.builder.CacheRefResolver;
import org.eocencle.winger.builder.MethodResolver;
import org.eocencle.winger.builder.ResultMapResolver;
import org.eocencle.winger.builder.xml.XMLBranchBuilder;
import org.eocencle.winger.builder.xml.XMLStatementBuilder;
import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.keygen.KeyGenerator;
import org.eocencle.winger.executor.loader.CglibProxyFactory;
import org.eocencle.winger.executor.loader.ProxyFactory;
import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.executor.resultset.FastResultSetHandler;
import org.eocencle.winger.executor.resultset.ResultSetHandler;
import org.eocencle.winger.io.ResolverUtil;
import org.eocencle.winger.javassist.bytecode.analysis.Executor;
import org.eocencle.winger.logging.Log;
import org.eocencle.winger.logging.LogFactory;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.BoundSql;
import org.eocencle.winger.mapping.Environment;
import org.eocencle.winger.mapping.MappedStatement;
import org.eocencle.winger.mapping.ParameterMap;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.mapping.ResultMap;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.plugin.Interceptor;
import org.eocencle.winger.plugin.InterceptorChain;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.reflection.factory.DefaultObjectFactory;
import org.eocencle.winger.reflection.factory.ObjectFactory;
import org.eocencle.winger.reflection.wrapper.DefaultObjectWrapperFactory;
import org.eocencle.winger.reflection.wrapper.ObjectWrapperFactory;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.scripting.LanguageDriverRegistry;
import org.eocencle.winger.scripting.defaults.RawLanguageDriver;
import org.eocencle.winger.scripting.xmltags.XMLLanguageDriver;
import org.eocencle.winger.transaction.Transaction;
import org.eocencle.winger.type.JdbcType;
import org.eocencle.winger.type.TypeAliasRegistry;
import org.eocencle.winger.type.TypeHandlerRegistry;

public class Configuration {
	protected Environment environment;

	protected boolean safeRowBoundsEnabled = false;
	protected boolean safeResultHandlerEnabled = true;
	protected boolean mapUnderscoreToCamelCase = false;
	protected boolean aggressiveLazyLoading = true;
	protected boolean multipleResultSetsEnabled = true;
	protected boolean useGeneratedKeys = false;
	protected boolean useColumnLabel = true;
	protected boolean cacheEnabled = true;
	protected boolean callSettersOnNulls = false;
	protected String logPrefix;
	protected Class <? extends Log> logImpl;
	protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;
	protected JdbcType jdbcTypeForNull = JdbcType.OTHER;
	protected Set<String> lazyLoadTriggerMethods = new HashSet<String>(Arrays.asList(new String[] { "equals", "clone", "hashCode", "toString" }));
	protected Integer defaultStatementTimeout;
	protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;
	protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;

	protected Properties variables = new Properties();
	protected ObjectFactory objectFactory = new DefaultObjectFactory();
	protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
	protected MapperRegistry mapperRegistry = new MapperRegistry(this);
	protected ResponseRegistry responseRegistry = new ResponseRegistry(this);
	
	protected boolean lazyLoadingEnabled = false;
	protected ProxyFactory proxyFactory;

	protected String databaseId;

	protected final InterceptorChain interceptorChain = new InterceptorChain();
	protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
	protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
	protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
	
	protected final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>("Mapped Statements collection");
	protected final Map<String, ResponseBranch> responseBranchs = new StrictMap<ResponseBranch>("Response Branchs collection");
	protected final Map<String, Cache> caches = new StrictMap<Cache>("Caches collection");
	protected final Map<String, ResultMap> resultMaps = new StrictMap<ResultMap>("Result Maps collection");
	protected final Map<String, ParameterMap> parameterMaps = new StrictMap<ParameterMap>("Parameter Maps collection");
	protected final Map<String, KeyGenerator> keyGenerators = new StrictMap<KeyGenerator>("Key Generators collection");

	protected final Set<String> loadedResources = new HashSet<String>();
	protected final Map<String, XNode> sqlFragments = new StrictMap<XNode>("XML fragments parsed from previous mappers");
	protected final Map<String, XNode> jsonFragments = new StrictMap<XNode>("XML fragments parsed from previous responses");

	protected final Collection<XMLStatementBuilder> incompleteStatements = new LinkedList<XMLStatementBuilder>();
	protected final Collection<XMLBranchBuilder> incompleteBranchs = new LinkedList<XMLBranchBuilder>();
	protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<CacheRefResolver>();
	protected final Collection<ResultMapResolver> incompleteResultMaps = new LinkedList<ResultMapResolver>();
	protected final Collection<MethodResolver> incompleteMethods = new LinkedList<MethodResolver>();

	/*
	 * A map holds cache-ref relationship. The key is the namespace that
	 * references a cache bound to another namespace and the value is the
	 * namespace which the actual cache is bound to.
	 */
	protected final Map<String, String> cacheRefMap = new HashMap<String, String>();

	public Configuration(Environment environment) {
		this();
		this.environment = environment;
	}

	public Configuration() {
		/*typeAliasRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);
		typeAliasRegistry.registerAlias("JNDI", JndiDataSourceFactory.class);
		typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
		typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);

		typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
		typeAliasRegistry.registerAlias("FIFO", FifoCache.class);
		typeAliasRegistry.registerAlias("LRU", LruCache.class);
		typeAliasRegistry.registerAlias("SOFT", SoftCache.class);
		typeAliasRegistry.registerAlias("WEAK", WeakCache.class);

		typeAliasRegistry.registerAlias("VENDOR", VendorDatabaseIdProvider.class);

		typeAliasRegistry.registerAlias("XML", XMLLanguageDriver.class);
		typeAliasRegistry.registerAlias("RAW", RawLanguageDriver.class);

		typeAliasRegistry.registerAlias("SLF4J", Slf4jImpl.class);
		typeAliasRegistry.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
		typeAliasRegistry.registerAlias("LOG4J", Log4jImpl.class);
		typeAliasRegistry.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
		typeAliasRegistry.registerAlias("STDOUT_LOGGING", StdOutImpl.class);
		typeAliasRegistry.registerAlias("NO_LOGGING", NoLoggingImpl.class);
		
		typeAliasRegistry.registerAlias("CGLIB", CglibProxyFactory.class);
		typeAliasRegistry.registerAlias("JAVASSIST", JavassistProxyFactory.class);*/
		
		this.languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
		this.languageRegistry.register(RawLanguageDriver.class);
	}

	public String getLogPrefix() {
		return logPrefix;
	}

	public void setLogPrefix(String logPrefix) {
		this.logPrefix = logPrefix;
	}

	public Class<? extends Log> getLogImpl() {
		return logImpl;
	}

	@SuppressWarnings("unchecked")
	public void setLogImpl(Class<?> logImpl) {
		if (logImpl != null) {
		this.logImpl = (Class<? extends Log>) logImpl;
		LogFactory.useCustomLogging(this.logImpl);
		}
	}

	public boolean isCallSettersOnNulls() {
		return callSettersOnNulls;
	}

	public void setCallSettersOnNulls(boolean callSettersOnNulls) {
		this.callSettersOnNulls = callSettersOnNulls;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	@Deprecated
	public boolean isSafeResultHandlerEnabled() {
		return safeResultHandlerEnabled;
	}

	@Deprecated // "Not needed as of the fix for issue #542"
	public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
		this.safeResultHandlerEnabled = safeResultHandlerEnabled;
	}

	public boolean isSafeRowBoundsEnabled() {
		return safeRowBoundsEnabled;
	}

	public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
		this.safeRowBoundsEnabled = safeRowBoundsEnabled;
	}

	public boolean isMapUnderscoreToCamelCase() {
		return mapUnderscoreToCamelCase;
	}

	public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
		this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
	}

	public void addLoadedResource(String resource) {
		loadedResources.add(resource);
	}

	public boolean isResourceLoaded(String resource) {
		return loadedResources.contains(resource);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public AutoMappingBehavior getAutoMappingBehavior() {
		return autoMappingBehavior;
	}

	public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
		this.autoMappingBehavior = autoMappingBehavior;
	}

	public boolean isLazyLoadingEnabled() {
		return lazyLoadingEnabled;
	}

	public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
		if (lazyLoadingEnabled && this.proxyFactory == null) {
			this.proxyFactory = new CglibProxyFactory();
		}
		this.lazyLoadingEnabled = lazyLoadingEnabled;
	}

	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	public void setProxyFactory(ProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	public boolean isAggressiveLazyLoading() {
		return aggressiveLazyLoading;
	}

	public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
		this.aggressiveLazyLoading = aggressiveLazyLoading;
	}

	public boolean isMultipleResultSetsEnabled() {
		return multipleResultSetsEnabled;
	}

	public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
		this.multipleResultSetsEnabled = multipleResultSetsEnabled;
	}

	public Set<String> getLazyLoadTriggerMethods() {
		return lazyLoadTriggerMethods;
	}

	public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
		this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
	}

	public boolean isUseGeneratedKeys() {
		return useGeneratedKeys;
	}

	public void setUseGeneratedKeys(boolean useGeneratedKeys) {
		this.useGeneratedKeys = useGeneratedKeys;
	}

	public ExecutorType getDefaultExecutorType() {
		return defaultExecutorType;
	}

	public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
		this.defaultExecutorType = defaultExecutorType;
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public Integer getDefaultStatementTimeout() {
		return defaultStatementTimeout;
	}

	public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
		this.defaultStatementTimeout = defaultStatementTimeout;
	}

	public boolean isUseColumnLabel() {
		return useColumnLabel;
	}

	public void setUseColumnLabel(boolean useColumnLabel) {
		this.useColumnLabel = useColumnLabel;
	}

	public LocalCacheScope getLocalCacheScope() {
		return localCacheScope;
	}

	public void setLocalCacheScope(LocalCacheScope localCacheScope) {
		this.localCacheScope = localCacheScope;
	}

	public JdbcType getJdbcTypeForNull() {
		return jdbcTypeForNull;
	}

	public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
		this.jdbcTypeForNull = jdbcTypeForNull;
	}

	public Properties getVariables() {
		return variables;
	}

	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return typeHandlerRegistry;
	}

	public TypeAliasRegistry getTypeAliasRegistry() {
		return typeAliasRegistry;
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public ObjectWrapperFactory getObjectWrapperFactory() {
		return objectWrapperFactory;
	}

	public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
		this.objectWrapperFactory = objectWrapperFactory;
	}

	public LanguageDriverRegistry getLanguageRegistry() {
		return languageRegistry;
	}

	public void setDefaultScriptingLanguage(Class<?> driver) {
		if (driver == null) {
			driver = XMLLanguageDriver.class;
		}
		getLanguageRegistry().setDefaultDriverClass(driver);
	}

	public LanguageDriver getDefaultScriptingLanuageInstance() {
		return languageRegistry.getDefaultDriver();
	}

	public MetaObject newMetaObject(Object object) {
		return MetaObject.forObject(object, objectFactory, objectWrapperFactory);
	}

	public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundJson boundJson) {
		ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundJson);
		parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
		return parameterHandler;
	}

	/*public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler,
		ResultHandler resultHandler, BoundSql boundSql) {
		ResultSetHandler resultSetHandler = mappedStatement.hasNestedResultMaps() ? new NestedResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql,
			rowBounds) : new FastResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
		resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
		return resultSetHandler;
	}

	public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
		statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
		return statementHandler;
	}

	public Executor newExecutor(Transaction transaction) {
		return newExecutor(transaction, defaultExecutorType);
	}

	public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
		return newExecutor(transaction, executorType, false);
	}

	public Executor newExecutor(Transaction transaction, ExecutorType executorType, boolean autoCommit) {
		executorType = executorType == null ? defaultExecutorType : executorType;
		executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
		Executor executor;
		if (ExecutorType.BATCH == executorType) {
		executor = new BatchExecutor(this, transaction);
		} else if (ExecutorType.REUSE == executorType) {
		executor = new ReuseExecutor(this, transaction);
		} else {
		executor = new SimpleExecutor(this, transaction);
		}
		if (cacheEnabled) {
		executor = new CachingExecutor(executor, autoCommit);
		}
		executor = (Executor) interceptorChain.pluginAll(executor);
		return executor;
	}*/
	
	public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
		keyGenerators.put(id, keyGenerator);
	}

	public Collection<String> getKeyGeneratorNames() {
		return keyGenerators.keySet();
	}

	public Collection<KeyGenerator> getKeyGenerators() {
		return keyGenerators.values();
	}

	public KeyGenerator getKeyGenerator(String id) {
		return keyGenerators.get(id);
	}

	public boolean hasKeyGenerator(String id) {
		return keyGenerators.containsKey(id);
	}

	public void addCache(Cache cache) {
		caches.put(cache.getId(), cache);
	}

	public Collection<String> getCacheNames() {
		return caches.keySet();
	}

	public Collection<Cache> getCaches() {
		return caches.values();
	}

	public Cache getCache(String id) {
		return caches.get(id);
	}

	public boolean hasCache(String id) {
		return caches.containsKey(id);
	}

	public void addResultMap(ResultMap rm) {
		resultMaps.put(rm.getId(), rm);
		checkLocallyForDiscriminatedNestedResultMaps(rm);
		checkGloballyForDiscriminatedNestedResultMaps(rm);
	}

	public Collection<String> getResultMapNames() {
		return resultMaps.keySet();
	}

	public Collection<ResultMap> getResultMaps() {
		return resultMaps.values();
	}

	public ResultMap getResultMap(String id) {
		return resultMaps.get(id);
	}

	public boolean hasResultMap(String id) {
		return resultMaps.containsKey(id);
	}

	public void addParameterMap(ParameterMap pm) {
		parameterMaps.put(pm.getId(), pm);
	}

	public Collection<String> getParameterMapNames() {
		return parameterMaps.keySet();
	}

	public Collection<ParameterMap> getParameterMaps() {
		return parameterMaps.values();
	}

	public ParameterMap getParameterMap(String id) {
		return parameterMaps.get(id);
	}

	public boolean hasParameterMap(String id) {
		return parameterMaps.containsKey(id);
	}

	public void addMappedStatement(MappedStatement ms) {
		mappedStatements.put(ms.getId(), ms);
	}
	
	public void addResponseBranch(ResponseBranch rb) {
		this.responseBranchs.put(rb.getAction(), rb);
	}

	public Collection<String> getMappedStatementNames() {
		buildAllStatements();
		return mappedStatements.keySet();
	}

	public Collection<MappedStatement> getMappedStatements() {
		buildAllStatements();
		return mappedStatements.values();
	}

	public Collection<XMLStatementBuilder> getIncompleteStatements() {
		return incompleteStatements;
	}

	public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
		incompleteStatements.add(incompleteStatement);
	}
	
	public void addIncompleteBranch(XMLBranchBuilder incompleteBranch) {
		incompleteBranchs.add(incompleteBranch);
	}

	public Collection<CacheRefResolver> getIncompleteCacheRefs() {
		return incompleteCacheRefs;
	}

	public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
		incompleteCacheRefs.add(incompleteCacheRef);
	}

	public Collection<ResultMapResolver> getIncompleteResultMaps() {
		return incompleteResultMaps;
	}

	public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
		incompleteResultMaps.add(resultMapResolver);
	}

	public void addIncompleteMethod(MethodResolver builder) {
		incompleteMethods.add(builder);
	}

	public Collection<MethodResolver> getIncompleteMethods() {
		return incompleteMethods;
	}

	public MappedStatement getMappedStatement(String id) {
		return this.getMappedStatement(id, true);
	}
	
	public ResponseBranch getResponseBranch(String action) {
		return this.responseBranchs.get(action);
	}

	public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
		if (validateIncompleteStatements) {
			buildAllStatements();
		}
		return mappedStatements.get(id);
	}

	public Map<String, XNode> getSqlFragments() {
		return sqlFragments;
	}
	
	public Map<String, XNode> getJsonFragments() {
		return this.jsonFragments;
	}

	public void addInterceptor(Interceptor interceptor) {
		interceptorChain.addInterceptor(interceptor);
	}

	public void addMappers(String packageName, Class<?> superType) {
		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
		resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
		Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
		for (Class<?> mapperClass : mapperSet) {
		addMapper(mapperClass);
		}
	}

	public void addMappers(String packageName) {
		addMappers(packageName, Object.class);
	}

	public <T> void addMapper(Class<T> type) {
		mapperRegistry.addMapper(type);
	}

	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return mapperRegistry.getMapper(type, sqlSession);
	}

	public boolean hasMapper(Class<?> type) {
		return mapperRegistry.hasMapper(type);
	}

	public boolean hasStatement(String statementName) {
		return hasStatement(statementName, true);
	}

	public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
		if (validateIncompleteStatements) {
		buildAllStatements();
		}
		return mappedStatements.containsKey(statementName);
	}

	public void addCacheRef(String namespace, String referencedNamespace) {
		cacheRefMap.put(namespace, referencedNamespace);
	}

	/*
	 * Parses all the unprocessed statement nodes in the cache. It is recommended
	 * to call this method once all the mappers are added as it provides fail-fast
	 * statement validation.
	 */
	protected void buildAllStatements() {
		if (!incompleteResultMaps.isEmpty()) {
		synchronized (incompleteResultMaps) {
			// This always throws a BuilderException.
			incompleteResultMaps.iterator().next().resolve();
		}
		}
		if (!incompleteCacheRefs.isEmpty()) {
		synchronized (incompleteCacheRefs) {
			// This always throws a BuilderException.
			incompleteCacheRefs.iterator().next().resolveCacheRef();
		}
		}
		if (!incompleteStatements.isEmpty()) {
		synchronized (incompleteStatements) {
			// This always throws a BuilderException.
			incompleteStatements.iterator().next().parseStatementNode();
		}
		}
	}

	/*
	 * Extracts namespace from fully qualified statement id.
	 *
	 * @param statementId
	 * @return namespace or null when id does not contain period.
	 */
	protected String extractNamespace(String statementId) {
		int lastPeriod = statementId.lastIndexOf('.');
		return lastPeriod > 0 ? statementId.substring(0, lastPeriod) : null;
	}

	// Slow but a one time cost. A better solution is welcome.
	protected void checkGloballyForDiscriminatedNestedResultMaps(ResultMap rm) {
		if (rm.hasNestedResultMaps()) {
		for (Map.Entry<String, ResultMap> entry : resultMaps.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof ResultMap) {
			ResultMap entryResultMap = (ResultMap) value;
			if (!entryResultMap.hasNestedResultMaps() && entryResultMap.getDiscriminator() != null) {
				Collection<String> discriminatedResultMapNames = entryResultMap.getDiscriminator().getDiscriminatorMap().values();
				if (discriminatedResultMapNames.contains(rm.getId())) {
				entryResultMap.forceNestedResultMaps();
				}
			}
			}
		}
		}
	}

	// Slow but a one time cost. A better solution is welcome.
	protected void checkLocallyForDiscriminatedNestedResultMaps(ResultMap rm) {
		if (!rm.hasNestedResultMaps() && rm.getDiscriminator() != null) {
			for (Map.Entry<String, String> entry : rm.getDiscriminator().getDiscriminatorMap().entrySet()) {
				String discriminatedResultMapName = entry.getValue();
				if (hasResultMap(discriminatedResultMapName)) {
					ResultMap discriminatedResultMap = resultMaps.get(discriminatedResultMapName);
					if (discriminatedResultMap.hasNestedResultMaps()) {
						rm.forceNestedResultMaps();
						break;
					}
				}
			}
		}
	}

	protected static class StrictMap<V> extends HashMap<String, V> {

		private static final long serialVersionUID = -4950446264854982944L;
		private String name;

		public StrictMap(String name, int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
			this.name = name;
		}

		public StrictMap(String name, int initialCapacity) {
			super(initialCapacity);
			this.name = name;
		}

		public StrictMap(String name) {
			super();
			this.name = name;
		}

		public StrictMap(String name, Map<String, ? extends V> m) {
			super(m);
			this.name = name;
		}

		@SuppressWarnings("unchecked")
		public V put(String key, V value) {
			if (containsKey(key))
				throw new IllegalArgumentException(name + " already contains value for " + key);
			if (key.contains(".")) {
				final String shortKey = getShortName(key);
				if (super.get(shortKey) == null) {
				super.put(shortKey, value);
				} else {
				super.put(shortKey, (V) new Ambiguity(shortKey));
				}
			}
			return super.put(key, value);
		}

		public V get(Object key) {
			V value = super.get(key);
			if (value == null) {
				throw new IllegalArgumentException(name + " does not contain value for " + key);
			}
			if (value instanceof Ambiguity) {
				throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name
					+ " (try using the full name including the namespace, or rename one of the entries)");
			}
			return value;
		}

		private String getShortName(String key) {
			final String[] keyparts = key.split("\\.");
			final String shortKey = keyparts[keyparts.length - 1];
			return shortKey;
		}

		protected static class Ambiguity {
			private String subject;
	
			public Ambiguity(String subject) {
				this.subject = subject;
			}
	
			public String getSubject() {
				return subject;
			}
		}
	}
}
