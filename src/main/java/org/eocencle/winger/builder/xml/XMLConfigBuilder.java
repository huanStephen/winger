package org.eocencle.winger.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import javax.sql.DataSource;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.BuilderException;
import org.eocencle.winger.datasource.DataSourceFactory;
import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.executor.loader.ProxyFactory;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.mapping.DatabaseIdProvider;
import org.eocencle.winger.mapping.Environment;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.plugin.Interceptor;
import org.eocencle.winger.reflection.MetaClass;
import org.eocencle.winger.reflection.factory.ObjectFactory;
import org.eocencle.winger.reflection.wrapper.ObjectWrapperFactory;
import org.eocencle.winger.session.AutoMappingBehavior;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.session.ExecutorType;
import org.eocencle.winger.session.LocalCacheScope;
import org.eocencle.winger.transaction.TransactionFactory;
import org.eocencle.winger.type.JdbcType;

public class XMLConfigBuilder extends BaseBuilder {
	private boolean parsed;
	private XPathParser parser;
	private String environment;

	public XMLConfigBuilder(Reader reader) {
		this(reader, null, null);
	}

	public XMLConfigBuilder(Reader reader, String environment) {
		this(reader, environment, null);
	}

	public XMLConfigBuilder(Reader reader, String environment, Properties props) {
		this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
	}

	public XMLConfigBuilder(InputStream inputStream) {
		this(inputStream, null, null);
	}

	public XMLConfigBuilder(InputStream inputStream, String environment) {
		this(inputStream, environment, null);
	}

	public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
		this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
	}

	private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
		super(new Configuration());
		ErrorContext.instance().resource("SQL Mapper Configuration");
		this.configuration.setVariables(props);
		this.parsed = false;
		this.environment = environment;
		this.parser = parser;
	}

	public Configuration parse() {
		if (parsed) {
			throw new BuilderException("Each MapperConfigParser can only be used once.");
		}
		parsed = true;
		this.parseConfiguration(parser.evalNode("/configuration"));
		return configuration;
	}

	private void parseConfiguration(XNode root) {
		try {
			this.responseElement(root.evalNode("response"));
		} catch (Exception e) {
			throw new BuilderException("Error parsing Response Configuration. Cause: " + e, e);
		}
	}

	private void typeAliasesElement(XNode parent) {
		if (parent != null) {
		for (XNode child : parent.getChildren()) {
			if ("package".equals(child.getName())) {
				String typeAliasPackage = child.getStringAttribute("name");
				configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
			} else {
				String alias = child.getStringAttribute("alias");
				String type = child.getStringAttribute("type");
				try {
					Class<?> clazz = Resources.classForName(type);
					if (alias == null) {
						typeAliasRegistry.registerAlias(clazz);
					} else {
						typeAliasRegistry.registerAlias(alias, clazz);
					}
				} catch (ClassNotFoundException e) {
					throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
				}
			}
		}
		}
	}

	private void pluginElement(XNode parent) throws Exception {
		if (parent != null) {
			for (XNode child : parent.getChildren()) {
				String interceptor = child.getStringAttribute("interceptor");
				Properties properties = child.getChildrenAsProperties();
				Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
				interceptorInstance.setProperties(properties);
				configuration.addInterceptor(interceptorInstance);
			}
		}
	}

	private void objectFactoryElement(XNode context) throws Exception {
		if (context != null) {
			String type = context.getStringAttribute("type");
			Properties properties = context.getChildrenAsProperties();
			ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
			factory.setProperties(properties);
			configuration.setObjectFactory(factory);
		}
	}

	private void objectWrapperFactoryElement(XNode context) throws Exception {
		if (context != null) {
			String type = context.getStringAttribute("type");
			ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).newInstance();
			configuration.setObjectWrapperFactory(factory);
		}
	}

	private void propertiesElement(XNode context) throws Exception {
		if (context != null) {
			Properties defaults = context.getChildrenAsProperties();
			String resource = context.getStringAttribute("resource");
			String url = context.getStringAttribute("url");
			if (resource != null && url != null) {
				throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.Please specify one or the other.");
			}
			if (resource != null) {
				defaults.putAll(Resources.getResourceAsProperties(resource));
			} else if (url != null) {
				defaults.putAll(Resources.getUrlAsProperties(url));
			}
			Properties vars = configuration.getVariables();
			if (vars != null) {
				defaults.putAll(vars);
			}
			parser.setVariables(defaults);
			configuration.setVariables(defaults);
		}
	}

	private void settingsElement(XNode context) throws Exception {
		if (context != null) {
			Properties props = context.getChildrenAsProperties();
			// Check that all settings are known to the configuration class
			MetaClass metaConfig = MetaClass.forClass(Configuration.class);
			for (Object key : props.keySet()) {
				if (!metaConfig.hasSetter(String.valueOf(key))) {
					throw new BuilderException("The setting " + key + " is not known.Make sure you spelled it correctly (case sensitive).");
				}
			}
			configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
			configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
			configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));	
			configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
			configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), true));
			configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
			configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
			configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
			configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
			configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
			configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
			configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
			configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
			configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
			configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
			configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
			configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
			configuration.setLogPrefix(props.getProperty("logPrefix"));
			configuration.setLogImpl(resolveClass(props.getProperty("logImpl")));
		}
	}

	private void environmentsElement(XNode context) throws Exception {
		if (context != null) {
			if (environment == null) {
				environment = context.getStringAttribute("default");
			}
			for (XNode child : context.getChildren()) {
				String id = child.getStringAttribute("id");
				if (isSpecifiedEnvironment(id)) {
					TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
					DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
					DataSource dataSource = dsFactory.getDataSource();
					Environment.Builder environmentBuilder = new Environment.Builder(id)
						.transactionFactory(txFactory)
						.dataSource(dataSource);
					configuration.setEnvironment(environmentBuilder.build());
				}
			}
		}
	}

	private void databaseIdProviderElement(XNode context) throws Exception {
		DatabaseIdProvider databaseIdProvider = null;
		if (context != null) {
			String type = context.getStringAttribute("type");
			Properties properties = context.getChildrenAsProperties();
			databaseIdProvider = (DatabaseIdProvider) resolveClass(type).newInstance();
			databaseIdProvider.setProperties(properties);
		}
		Environment environment = configuration.getEnvironment();
		if (environment != null && databaseIdProvider != null) {
			String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
			configuration.setDatabaseId(databaseId);
		}
	}

	private TransactionFactory transactionManagerElement(XNode context) throws Exception {
		if (context != null) {
			String type = context.getStringAttribute("type");
			Properties props = context.getChildrenAsProperties();
			TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
			factory.setProperties(props);
			return factory;
		}
		throw new BuilderException("Environment declaration requires a TransactionFactory.");
	}

	private DataSourceFactory dataSourceElement(XNode context) throws Exception {
		if (context != null) {
			String type = context.getStringAttribute("type");
			Properties props = context.getChildrenAsProperties();
			DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
			factory.setProperties(props);
			return factory;
		}
		throw new BuilderException("Environment declaration requires a DataSourceFactory.");
	}

	private void typeHandlerElement(XNode parent) throws Exception {
		if (parent != null) {
			for (XNode child : parent.getChildren()) {
				if ("package".equals(child.getName())) {
					String typeHandlerPackage = child.getStringAttribute("name");
					typeHandlerRegistry.register(typeHandlerPackage);
				} else {
					String javaTypeName = child.getStringAttribute("javaType");
					String jdbcTypeName = child.getStringAttribute("jdbcType");
					String handlerTypeName = child.getStringAttribute("handler");
					Class<?> javaTypeClass = resolveClass(javaTypeName);
					JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
					Class<?> typeHandlerClass = resolveClass(handlerTypeName);
					if (javaTypeClass != null) {
						if (jdbcType == null) {
							typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
						} else {
							typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
						}
					} else {
						typeHandlerRegistry.register(typeHandlerClass);
					}
				}
			}
		}
	}

	private void mapperElement(XNode parent) throws Exception {
		if (parent != null) {
			for (XNode child : parent.getChildren()) {
				if ("package".equals(child.getName())) {
					String mapperPackage = child.getStringAttribute("name");
					configuration.addMappers(mapperPackage);
				} else {
					String resource = child.getStringAttribute("resource");
					String url = child.getStringAttribute("url");
					String mapperClass = child.getStringAttribute("class");
					if (resource != null && url == null && mapperClass == null) {
						ErrorContext.instance().resource(resource);
						InputStream inputStream = Resources.getResourceAsStream(resource);
						XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
						mapperParser.parse();
					} else if (resource == null && url != null && mapperClass == null) {
						ErrorContext.instance().resource(url);
						InputStream inputStream = Resources.getUrlAsStream(url);
						XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
						mapperParser.parse();
					} else if (resource == null && url == null && mapperClass != null) {
						Class<?> mapperInterface = Resources.classForName(mapperClass);
						configuration.addMapper(mapperInterface);
					} else {
						throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
					}
				}
			}
		}
	}
	
	private void responseElement(XNode parent) throws Exception {
		if (parent != null) {
			for (XNode child : parent.getChildren()) {
				String resource = child.getStringAttribute("resource");
				if (resource != null) {
					ErrorContext.instance().resource(resource);
					InputStream inputStream = Resources.getResourceAsStream(resource);
					XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
					mapperParser.parse();
				} else {
					throw new BuilderException("A response element may only specify a resource.");
				}
			}
		}
	}

	private boolean isSpecifiedEnvironment(String id) {
		if (environment == null) {
			throw new BuilderException("No environment specified.");
		} else if (id == null) {
			throw new BuilderException("Environment requires an id attribute.");
		} else if (environment.equals(id)) {
			return true;
		}
		return false;
	}
}
