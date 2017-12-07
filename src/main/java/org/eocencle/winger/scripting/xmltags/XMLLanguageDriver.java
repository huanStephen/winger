package org.eocencle.winger.scripting.xmltags;

import java.util.ArrayList;
import java.util.List;

import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

public class XMLLanguageDriver implements LanguageDriver {
	public ParameterHandler createParameterHandler(ResponseBranch responseBranch, Object parameterObject, BoundJson boundJson) {
		//return new DefaultParameterHandler(responseBranch, parameterObject, boundJson);
		return null;
	}

	public JsonSource createJsonSource(Configuration configuration, XNode script) {
		XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script);
		return builder.parseScriptNode();
	}

	public JsonSource createJsonSource(Configuration configuration, String script) {
		if (script.startsWith("<script>")) {
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
