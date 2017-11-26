package org.eocencle.winger.scripting.defaults;

import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.scripting.LanguageDriver;
import org.eocencle.winger.session.Configuration;

public class RawLanguageDriver implements LanguageDriver {
	public ParameterHandler createParameterHandler(ResponseBranch responseBranch, Object parameterObject, BoundJson boundJson) {
		return new DefaultParameterHandler(responseBranch, parameterObject, boundJson);
	}

	public JsonSource createJsonSource(Configuration configuration, XNode script) {
		return new RawJsonSource(configuration, script.getStringBody(""));
	}

	public JsonSource createJsonSource(Configuration configuration, String script) {
		return new RawJsonSource(configuration, script);
	}
}
