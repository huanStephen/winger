package org.eocencle.winger.xmltags;

import java.util.Map;

import org.eocencle.winger.builder.JsonSourceBuilder;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.session.Configuration;

public class DynamicJsonSource implements JsonSource {
	private Configuration configuration;
	private JsonNode rootJsonNode;

	public DynamicJsonSource(Configuration configuration, JsonNode rootJsonNode) {
		this.configuration = configuration;
		this.rootJsonNode = rootJsonNode;
	}

	public BoundJson getBoundJson(Map<String, Object> params) throws WingerException {
		DynamicContext context = new DynamicContext(configuration, params);
		this.rootJsonNode.apply(context);
		JsonSourceBuilder jsonSourceParser = new JsonSourceBuilder(configuration);
		Class<?> parameterType = params == null ? Object.class : params.getClass();
		JsonSource jsonSource = jsonSourceParser.parse(context.getJson(), parameterType, context.getBindings());
		BoundJson boundJson = jsonSource.getBoundJson(params);
		for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
			boundJson.setAdditionalParameter(entry.getKey(), entry.getValue());
		}
		return boundJson;
	}
}
