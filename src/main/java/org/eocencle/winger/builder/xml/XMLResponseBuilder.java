package org.eocencle.winger.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.CacheRefResolver;
import org.eocencle.winger.builder.IncompleteElementException;
import org.eocencle.winger.builder.ResponseBuilderAssistant;
import org.eocencle.winger.builder.ResultMapResolver;
import org.eocencle.winger.cache.Cache;
import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.mapping.Discriminator;
import org.eocencle.winger.mapping.ParameterMapping;
import org.eocencle.winger.mapping.ParameterMode;
import org.eocencle.winger.mapping.ResultFlag;
import org.eocencle.winger.mapping.ResultMap;
import org.eocencle.winger.mapping.ResultMapping;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.ContextPathType;
import org.eocencle.winger.type.JdbcType;
import org.eocencle.winger.type.TypeHandler;

public class XMLResponseBuilder extends BaseBuilder {
	private XPathParser parser;
	private ResponseBuilderAssistant builderAssistant;
	private Map<String, XNode> sqlFragments;
	private Map<String, XNode> jsonFragments;
	private String resource;

	@Deprecated
	public XMLResponseBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
		this(reader, configuration, resource, sqlFragments);
		this.builderAssistant.setCurrentContextPath(namespace);
	}

	@Deprecated
	public XMLResponseBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
		this(new XPathParser(reader, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration, resource, sqlFragments);
	}

	public XMLResponseBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
		this(inputStream, configuration, resource, sqlFragments);
		this.builderAssistant.setCurrentContextPath(namespace);
	}

	public XMLResponseBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> jsonFragments) {
		this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()),
			configuration, resource, jsonFragments);
	}

	private XMLResponseBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> jsonFragments) {
		super(configuration);
		this.builderAssistant = new ResponseBuilderAssistant(configuration, resource);
		this.parser = parser;
		this.jsonFragments = jsonFragments;
		this.resource = resource;
	}

	public void parse() {
		if (!configuration.isResourceLoaded(resource)) {
			this.configurationElement(parser.evalNode("/response"));
			configuration.addLoadedResource(resource);
			bindMapperForNamespace();
		}

		parsePendingResultMaps();
		parsePendingChacheRefs();
		parsePendingStatements();
	}

	public XNode getSqlFragment(String refid) {
		return sqlFragments.get(refid);
	}

	private void configurationElement(XNode context) {
		try {
			String contextpath = context.getStringAttribute("contextpath");
			if (0 != contextpath.indexOf("/")) {
				contextpath = "/" + contextpath;
			}
			if (contextpath.lastIndexOf("/") == contextpath.length() - 1) {
				contextpath = contextpath.substring(0, contextpath.length() - 1);
			}
			this.builderAssistant.setCurrentContextPath(contextpath);
			this.jsonElement(context.evalNodes("json"));
			this.buildBranchFormContext(context.evalNodes("branch"));
		} catch (Exception e) {
			throw new RuntimeException("Error parsing Response XML. Cause: " + e, e);
		}
	}

	private void buildStatementFromContext(List<XNode> list) {
		if (configuration.getDatabaseId() != null) {
			buildStatementFromContext(list, configuration.getDatabaseId());
		}
		buildStatementFromContext(list, null);
	}

	private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
		for (XNode context : list) {
			final XMLStatementBuilder statementParser = null; //new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
			try {
				statementParser.parseStatementNode();
			} catch (IncompleteElementException e) {
				configuration.addIncompleteStatement(statementParser);
			}
		}
	}
	
	private void buildBranchFormContext(List<XNode> list) {
		for (XNode context : list) {
			final XMLBranchBuilder branchParser = new XMLBranchBuilder(configuration, builderAssistant, context);
			try {
				branchParser.parseBranchNode();
			} catch (IncompleteElementException e) {
				this.configuration.addIncompleteBranch(branchParser);
			}
		}
	}

	private void parsePendingResultMaps() {
		Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
		synchronized (incompleteResultMaps) {
			Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().resolve();
					iter.remove();
				} catch (IncompleteElementException e) {
					// ResultMap is still missing a resource...
				}
			}
		}
	}

	private void parsePendingChacheRefs() {
		Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
		synchronized (incompleteCacheRefs) {
			Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().resolveCacheRef();
					iter.remove();
				} catch (IncompleteElementException e) {
					// Cache ref is still missing a resource...
				}
			}
		}
	}

	private void parsePendingStatements() {
		Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
		synchronized (incompleteStatements) {
			Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().parseStatementNode();
					iter.remove();
				} catch (IncompleteElementException e) {
					// Statement is still missing a resource...
				}
			}
		}
	}

	private void cacheRefElement(XNode context) {
		if (context != null) {
			configuration.addCacheRef(builderAssistant.getCurrentContextPath(), context.getStringAttribute("namespace"));
			CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, context.getStringAttribute("namespace"));
			try {
				cacheRefResolver.resolveCacheRef();
			} catch (IncompleteElementException e) {
				configuration.addIncompleteCacheRef(cacheRefResolver);
			}
		}
	}

	private void cacheElement(XNode context) throws Exception {
		if (context != null) {
			String type = context.getStringAttribute("type", "PERPETUAL");
			Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
			String eviction = context.getStringAttribute("eviction", "LRU");
			Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
			Long flushInterval = context.getLongAttribute("flushInterval");
			Integer size = context.getIntAttribute("size");
			boolean readWrite = !context.getBooleanAttribute("readOnly", false);
			Properties props = context.getChildrenAsProperties();
			builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, props);
		}
	}

	private void parameterMapElement(List<XNode> list) throws Exception {
		for (XNode parameterMapNode : list) {
			String id = parameterMapNode.getStringAttribute("id");
			String type = parameterMapNode.getStringAttribute("type");
			Class<?> parameterClass = resolveClass(type);
			List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
			List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
			for (XNode parameterNode : parameterNodes) {
				String property = parameterNode.getStringAttribute("property");
				String javaType = parameterNode.getStringAttribute("javaType");
				String jdbcType = parameterNode.getStringAttribute("jdbcType");
				String resultMap = parameterNode.getStringAttribute("resultMap");
				String mode = parameterNode.getStringAttribute("mode");
				String typeHandler = parameterNode.getStringAttribute("typeHandler");
				Integer numericScale = parameterNode.getIntAttribute("numericScale", null);
				ParameterMode modeEnum = resolveParameterMode(mode);
				Class<?> javaTypeClass = resolveClass(javaType);
				JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
				@SuppressWarnings("unchecked")
				Class<? extends TypeHandler<?>> typeHandlerClass = (Class<? extends TypeHandler<?>>) resolveClass(typeHandler);
				ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property, javaTypeClass, jdbcTypeEnum, resultMap, modeEnum, typeHandlerClass, numericScale);
				parameterMappings.add(parameterMapping);
			}
			builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
		}
	}

	private void resultMapElements(List<XNode> list) throws Exception {
		for (XNode resultMapNode : list) {
			try {
				resultMapElement(resultMapNode);
			} catch (IncompleteElementException e) {
				// ignore, it will be retried
			}
		}
	}

	private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
		return resultMapElement(resultMapNode, Collections.<ResultMapping> emptyList());
	}

	private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings) throws Exception {
		ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
		String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
		String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType", resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
		String extend = resultMapNode.getStringAttribute("extends");
		Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping", null);
		Class<?> typeClass = resolveClass(type);
		Discriminator discriminator = null;
		List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
		resultMappings.addAll(additionalResultMappings);
		List<XNode> resultChildren = resultMapNode.getChildren();
		for (XNode resultChild : resultChildren) {
			if ("constructor".equals(resultChild.getName())) {
				processConstructorElement(resultChild, typeClass, resultMappings);
			} else if ("discriminator".equals(resultChild.getName())) {
				discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
			} else {
				ArrayList<ResultFlag> flags = new ArrayList<ResultFlag>();
				if ("id".equals(resultChild.getName())) {
					flags.add(ResultFlag.ID);
				}
				resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
			}
		}
		ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
		try {
			return resultMapResolver.resolve();
		} catch (IncompleteElementException e) {
			configuration.addIncompleteResultMap(resultMapResolver);
			throw e;
		}
	}

	private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings) throws Exception {
		List<XNode> argChildren = resultChild.getChildren();
		for (XNode argChild : argChildren) {
			ArrayList<ResultFlag> flags = new ArrayList<ResultFlag>();
			flags.add(ResultFlag.CONSTRUCTOR);
			if ("idArg".equals(argChild.getName())) {
				flags.add(ResultFlag.ID);
			}
			resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
		}
	}

	private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType, List<ResultMapping> resultMappings) throws Exception {
		String column = context.getStringAttribute("column");
		String javaType = context.getStringAttribute("javaType");
		String jdbcType = context.getStringAttribute("jdbcType");
		String typeHandler = context.getStringAttribute("typeHandler");
		Class<?> javaTypeClass = resolveClass(javaType);
		@SuppressWarnings("unchecked")
		Class<? extends TypeHandler<?>> typeHandlerClass = (Class<? extends TypeHandler<?>>) resolveClass(typeHandler);
		JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
		Map<String, String> discriminatorMap = new HashMap<String, String>();
		for (XNode caseChild : context.getChildren()) {
			String value = caseChild.getStringAttribute("value");
			String resultMap = caseChild.getStringAttribute("resultMap", processNestedResultMappings(caseChild, resultMappings));
			discriminatorMap.put(value, resultMap);
		}
		return builderAssistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
	}

	private void sqlElement(List<XNode> list) throws Exception {
		if (configuration.getDatabaseId() != null) {
			sqlElement(list, configuration.getDatabaseId());
		}
		sqlElement(list, null);
	}

	private void sqlElement(List<XNode> list, String requiredDatabaseId) throws Exception {
		for (XNode context : list) {
			String databaseId = context.getStringAttribute("databaseId");
			String id = context.getStringAttribute("id");
			id = builderAssistant.applyCurrentContextPath(id, false, ContextPathType.BRANCH);
			if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) sqlFragments.put(id, context);
		}
	}
	
	private void jsonElement(List<XNode> list) throws Exception {
		for (XNode context : list) {
			String id = context.getStringAttribute("id");
			id = this.builderAssistant.applyCurrentContextPath(id, false, ContextPathType.JSON);
			
			this.jsonFragments.put(id, context);
		}
	}
	
	private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
		if (requiredDatabaseId != null) {
			if (!requiredDatabaseId.equals(databaseId)) {
				return false;
			}
		} else {
			if (databaseId != null) {
				return false;
			}
			// skip this fragment if there is a previous one with a not null databaseId
			if (this.sqlFragments.containsKey(id)) {
				XNode context = this.sqlFragments.get(id);
				if (context.getStringAttribute("databaseId") != null) {
					return false;
				}
			}
		}
		return true;
	}

	private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, ArrayList<ResultFlag> flags) throws Exception {
		String property = context.getStringAttribute("property");
		String column = context.getStringAttribute("column");
		String javaType = context.getStringAttribute("javaType");
		String jdbcType = context.getStringAttribute("jdbcType");
		String nestedSelect = context.getStringAttribute("select");
		String nestedResultMap = context.getStringAttribute("resultMap", processNestedResultMappings(context, Collections.<ResultMapping> emptyList()));
		String notNullColumn = context.getStringAttribute("notNullColumn");
		String columnPrefix = context.getStringAttribute("columnPrefix");
		String typeHandler = context.getStringAttribute("typeHandler");
		Class<?> javaTypeClass = resolveClass(javaType);
		@SuppressWarnings("unchecked")
		Class<? extends TypeHandler<?>> typeHandlerClass = (Class<? extends TypeHandler<?>>) resolveClass(typeHandler);
		JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
		return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags);
	}

	private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings) throws Exception {
		if ("association".equals(context.getName()) || "collection".equals(context.getName()) || "case".equals(context.getName())) {
			if (context.getStringAttribute("select") == null) {
				ResultMap resultMap = resultMapElement(context, resultMappings);
				return resultMap.getId();
			}
		}
		return null;
	}

	private void bindMapperForNamespace() {
		String namespace = builderAssistant.getCurrentContextPath();
		if (namespace != null) {
			Class<?> boundType = null;
			try {
				boundType = Resources.classForName(namespace);
			} catch (ClassNotFoundException e) {
				//ignore, bound type is not required
			}
			if (boundType != null) {
				if (!configuration.hasMapper(boundType)) {
					// Spring may not know the real resource name so we set a flag
					// to prevent loading again this resource from the mapper interface
					// look at MapperAnnotationBuilder#loadXmlResource
					configuration.addLoadedResource("namespace:" + namespace);
					configuration.addMapper(boundType);
				}
			}
		}
	}
	
	private void bindResponseForContextPath() {
		String contextPath = this.builderAssistant.getCurrentContextPath();
		if (contextPath != null) {
			Class<?> boundType = null;
			try {
				boundType = Resources.classForName(contextPath);
			} catch (ClassNotFoundException e) {
				//ignore, bound type is not required
			}
			if (boundType != null) {
				if (!this.configuration.hasMapper(boundType)) {
					// Spring may not know the real resource name so we set a flag
					// to prevent loading again this resource from the mapper interface
					// look at MapperAnnotationBuilder#loadXmlResource
					this.configuration.addLoadedResource("contextpath:" + contextPath);
					this.configuration.addMapper(boundType);
				}
			}
		}
	}
}
