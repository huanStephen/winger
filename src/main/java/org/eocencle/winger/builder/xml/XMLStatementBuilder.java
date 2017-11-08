package org.eocencle.winger.builder.xml;

import java.util.List;
import java.util.Locale;

import org.eocencle.winger.builder.BaseBuilder;
import org.eocencle.winger.builder.MapperBuilderAssistant;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

public class XMLStatementBuilder extends BaseBuilder {
	private MapperBuilderAssistant builderAssistant;
	private XNode context;
	private String requiredDatabaseId;

	public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, XNode context) {
		this(configuration, builderAssistant, context, null);
	}

	public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, XNode context, String databaseId) {
		super(configuration);
		this.builderAssistant = builderAssistant;
		this.context = context;
		this.requiredDatabaseId = databaseId;
	}

	public void parseStatementNode() {
		String id = context.getStringAttribute("id");
		String databaseId = context.getStringAttribute("databaseId");

		if (!databaseIdMatchesCurrent(id, databaseId, this.requiredDatabaseId)) {
			return;
		}

		Integer fetchSize = context.getIntAttribute("fetchSize", null);
		Integer timeout = context.getIntAttribute("timeout", null);
		String parameterMap = context.getStringAttribute("parameterMap");
		String parameterType = context.getStringAttribute("parameterType");
		Class<?> parameterTypeClass = resolveClass(parameterType);
		String resultMap = context.getStringAttribute("resultMap");
		String resultType = context.getStringAttribute("resultType");
		String lang = context.getStringAttribute("lang");
		LanguageDriver langDriver = getLanguageDriver(lang);

		Class<?> resultTypeClass = resolveClass(resultType);
		String resultSetType = context.getStringAttribute("resultSetType");
		StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType", StatementType.PREPARED.toString()));
		ResultSetType resultSetTypeEnum = resolveResultSetType(resultSetType);

		String nodeName = context.getNode().getNodeName();
		SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
		boolean flushCache = context.getBooleanAttribute("flushCache", !isSelect);
		boolean useCache = context.getBooleanAttribute("useCache", isSelect);
		boolean resultOrdered = context.getBooleanAttribute("resultOrdered", false);

		// Include Fragments before parsing
		XMLIncludeTransformer includeParser = new XMLIncludeTransformer(configuration, builderAssistant);
		includeParser.applyIncludes(context.getNode());

		// Parse selectKey after includes,
		// in case if IncompleteElementException (issue #291)
		List<XNode> selectKeyNodes = context.evalNodes("selectKey");
		if (configuration.getDatabaseId() != null) {
			parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver, configuration.getDatabaseId());
		}
		parseSelectKeyNodes(id, selectKeyNodes, parameterTypeClass, langDriver, null);

		// Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
		SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);

		String keyProperty = context.getStringAttribute("keyProperty");
		String keyColumn = context.getStringAttribute("keyColumn");
		KeyGenerator keyGenerator;
		String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
		keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
		if (configuration.hasKeyGenerator(keyStatementId)) {
			keyGenerator = configuration.getKeyGenerator(keyStatementId);
		} else {
			keyGenerator = context.getBooleanAttribute("useGeneratedKeys", configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType))
				? new Jdbc3KeyGenerator() : new NoKeyGenerator();
		}

		builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
			fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
			resultSetTypeEnum, flushCache, useCache, resultOrdered, 
			keyGenerator, keyProperty, keyColumn, databaseId, langDriver);
	}
	
	public void parseSelectKeyNodes(String parentId, List<XNode> list, Class<?> parameterTypeClass, LanguageDriver langDriver, String skRequiredDatabaseId) {
		for (XNode nodeToHandle : list) {
			String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
			String databaseId = nodeToHandle.getStringAttribute("databaseId");
			if (databaseIdMatchesCurrent(id, databaseId, skRequiredDatabaseId)) {
				parseSelectKeyNode(id, nodeToHandle, parameterTypeClass, langDriver, databaseId);
			}
		}
	}

	public void parseSelectKeyNode(String id, XNode nodeToHandle, Class<?> parameterTypeClass, LanguageDriver langDriver, String databaseId) {
		String resultType = nodeToHandle.getStringAttribute("resultType");
		Class<?> resultTypeClass = resolveClass(resultType);
		StatementType statementType = StatementType.valueOf(nodeToHandle.getStringAttribute("statementType", StatementType.PREPARED.toString()));
		String keyProperty = nodeToHandle.getStringAttribute("keyProperty");
		boolean executeBefore = "BEFORE".equals(nodeToHandle.getStringAttribute("order", "AFTER"));

		//defaults
		boolean useCache = false;
		boolean resultOrdered = false;
		KeyGenerator keyGenerator = new NoKeyGenerator();
		Integer fetchSize = null;
		Integer timeout = null;
		boolean flushCache = false;
		String parameterMap = null;
		String resultMap = null;
		ResultSetType resultSetTypeEnum = null;

		SqlSource sqlSource = langDriver.createSqlSource(configuration, nodeToHandle, parameterTypeClass);
		SqlCommandType sqlCommandType = SqlCommandType.SELECT;

		builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
			fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
			resultSetTypeEnum, flushCache, useCache, resultOrdered,
			keyGenerator, keyProperty, null, databaseId, langDriver);

		id = builderAssistant.applyCurrentNamespace(id, false);

		MappedStatement keyStatement = configuration.getMappedStatement(id, false);
		configuration.addKeyGenerator(id, new SelectKeyGenerator(keyStatement, executeBefore));
		nodeToHandle.getParent().getNode().removeChild(nodeToHandle.getNode());
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
			// skip this statement if there is a previous one with a not null databaseId
			id = builderAssistant.applyCurrentNamespace(id, false);
			if (this.configuration.hasStatement(id, false)) {
				MappedStatement previous = this.configuration.getMappedStatement(id, false); // issue #2
				if (previous.getDatabaseId() != null) {
					return false;
				}
			}
		}
		return true;
	}

	private LanguageDriver getLanguageDriver(String lang) {
		Class<?> langClass;
		if (lang == null) {
			langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
		} else {
			langClass = resolveClass(lang);
			configuration.getLanguageRegistry().register(langClass);
		}
		if (langClass == null) {
			langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
		}
		return configuration.getLanguageRegistry().getDriver(langClass);
	}
}