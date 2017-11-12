package org.eocencle.winger.scripting.defaults;

import java.util.HashMap;

import org.eocencle.winger.builder.SqlSourceBuilder;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.session.Configuration;

public class RawJsonSource implements JsonSource {
	private final Configuration configuration;
	private final String sql;

	public RawJsonSource(Configuration configuration, String sql) {
		this.configuration = configuration;
		this.sql = sql;
	}

	public BoundJson getBoundJson(Object parameterObject) {
		SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
		Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
		JsonSource sqlSource = sqlSourceParser.parse(sql, parameterType, new HashMap<String, Object>());
		BoundJson boundSql = sqlSource.getBoundJson(parameterObject);
		return boundSql;
	}
}
