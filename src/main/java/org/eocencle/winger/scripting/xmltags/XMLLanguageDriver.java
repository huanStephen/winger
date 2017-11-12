package org.eocencle.winger.scripting.xmltags;

import java.util.ArrayList;
import java.util.List;

import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.MappedStatement;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.scripting.defaults.DefaultParameterHandler;
import org.eocencle.winger.session.Configuration;

public class XMLLanguageDriver implements LanguageDriver {
	public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundJson boundJson) {
		return new DefaultParameterHandler(mappedStatement, parameterObject, boundJson);
	}

	public JsonSource createJsonSource(Configuration configuration, XNode script, Class<?> parameterType) {
		XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script);
		return builder.parseScriptNode();
	}

	public JsonSource createJsonSource(Configuration configuration, String script, Class<?> parameterType) {
		if (script.startsWith("<script>")) { // issue #3
			XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script);
			return builder.parseScriptNode();
		} else {
			List<JsonNode> contents = new ArrayList<JsonNode>();
			contents.add(new TextJsonNode(script.toString()));
			MixedJsonNode rootSqlNode = new MixedJsonNode(contents);
			return new DynamicJsonSource(configuration, rootSqlNode);
		}
	}
}
