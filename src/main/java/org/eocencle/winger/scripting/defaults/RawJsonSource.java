package org.eocencle.winger.scripting.defaults;

import java.util.HashMap;
import java.util.Map;

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

	public BoundJson getBoundJson(Map<String, Object> params) {
		SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
		Class<?> parameterType = params == null ? Object.class : params.getClass();
		JsonSource sqlSource = sqlSourceParser.parse(sql, parameterType, new HashMap<String, Object>());
		BoundJson boundSql = sqlSource.getBoundJson(params);
		return boundSql;
	}
}
