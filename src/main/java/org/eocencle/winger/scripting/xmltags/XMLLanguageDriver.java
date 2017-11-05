package org.eocencle.winger.scripting.xmltags;

import java.util.ArrayList;
import java.util.List;

import org.eocencle.winger.mapping.BoundSql;
import org.eocencle.winger.mapping.SqlSource;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;

public class XMLLanguageDriver implements LanguageDriver {
	public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
	}

	public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
		XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script);
		return builder.parseScriptNode();
	}

	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		if (script.startsWith("<script>")) { // issue #3
			XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script);
			return builder.parseScriptNode();
		} else {
			List<JsonNode> contents = new ArrayList<JsonNode>();
			contents.add(new TextJsonNode(script.toString()));
			MixedJsonNode rootSqlNode = new MixedJsonNode(contents);
			return new DynamicSqlSource(configuration, rootSqlNode);
		}
	}
}
