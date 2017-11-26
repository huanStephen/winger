package org.eocencle.winger.scripting;

import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;

public interface LanguageDriver {

	ParameterHandler createParameterHandler(ResponseBranch responseBranch, Object parameterObject, BoundJson boundJson);

	JsonSource createJsonSource(Configuration configuration, XNode script);

	JsonSource createJsonSource(Configuration configuration, String script);
}
