package org.eocencle.winger.session;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eocencle.winger.binding.ResponseRegistry;
import org.eocencle.winger.builder.CacheRefResolver;
import org.eocencle.winger.builder.MethodResolver;
import org.eocencle.winger.builder.xml.XMLBranchBuilder;
import org.eocencle.winger.logging.Log;
import org.eocencle.winger.logging.LogFactory;
import org.eocencle.winger.mapping.AbstractResponseBranch;
import org.eocencle.winger.mapping.Environment;
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
import org.eocencle.winger.type.TypeAliasRegistry;
import org.eocencle.winger.type.TypeHandlerRegistry;

public class Configuration {
	protected Environment environment;

	protected String logPrefix;
	protected Class <? extends Log> logImpl;
	protected Set<String> lazyLoadTriggerMethods = new HashSet<String>(Arrays.asList(new String[] { "equals", "clone", "hashCode", "toString" }));

	protected Properties variables = new Properties();
	protected ObjectFactory objectFactory = new DefaultObjectFactory();
	protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
	protected ResponseRegistry responseRegistry = new ResponseRegistry(this);

	protected final InterceptorChain interceptorChain = new InterceptorChain();
	protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
	protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
	protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

	protected final Map<String, AbstractResponseBranch> responseBranchs = new StrictMap<AbstractResponseBranch>("Response Branchs collection");

	protected final Set<String> loadedResources = new HashSet<String>();
	protected final Map<String, XNode> jsonFragments = new StrictMap<XNode>("XML fragments parsed from previous responses");

	protected final Collection<XMLBranchBuilder> incompleteBranchs = new LinkedList<XMLBranchBuilder>();
	protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<CacheRefResolver>();
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

	public void addLoadedResource(String resource) {
		loadedResources.add(resource);
	}

	public boolean isResourceLoaded(String resource) {
		return loadedResources.contains(resource);
	}
	
	public void removeResourceLoaded(String resource) {
		this.loadedResources.remove(resource);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Set<String> getLazyLoadTriggerMethods() {
		return lazyLoadTriggerMethods;
	}

	public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
		this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
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
	
	public void addResponseBranch(AbstractResponseBranch arb) {
		this.responseBranchs.put(arb.getName(), arb);
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

	public void addIncompleteMethod(MethodResolver builder) {
		incompleteMethods.add(builder);
	}

	public Collection<MethodResolver> getIncompleteMethods() {
		return incompleteMethods;
	}
	
	public AbstractResponseBranch getResponseBranch(String action) {
		return this.responseBranchs.get(action);
	}
	
	public Map<String, XNode> getJsonFragments() {
		return this.jsonFragments;
	}

	public void addInterceptor(Interceptor interceptor) {
		interceptorChain.addInterceptor(interceptor);
	}

	public void addCacheRef(String namespace, String referencedNamespace) {
		cacheRefMap.put(namespace, referencedNamespace);
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
