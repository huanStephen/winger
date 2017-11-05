package org.eocencle.winger.scripting.xmltags;

import java.util.Map;

import org.eocencle.winger.builder.SqlSourceBuilder;
import org.eocencle.winger.mapping.BoundSql;
import org.eocencle.winger.mapping.SqlSource;
import org.eocencle.winger.session.Configuration;

public class DynamicSqlSource implements SqlSource {
	private Configuration configuration;
	private JsonNode rootSqlNode;

	public DynamicSqlSource(Configuration configuration, JsonNode rootSqlNode) {
		this.configuration = configuration;
		this.rootSqlNode = rootSqlNode;
	}

	public BoundSql getBoundSql(Object parameterObject) {
		DynamicContext context = new DynamicContext(configuration, parameterObject);
		rootSqlNode.apply(context);
		SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
		Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
		SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
			boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
		}
		return boundSql;
	}
}
