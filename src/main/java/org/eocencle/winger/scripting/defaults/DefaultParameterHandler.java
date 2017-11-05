package org.eocencle.winger.scripting.defaults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.eocencle.winger.mapping.BoundSql;
import org.eocencle.winger.mapping.ParameterMapping;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;

public class DefaultParameterHandler implements ParameterHandler {
	private final TypeHandlerRegistry typeHandlerRegistry;

	private final MappedStatement mappedStatement;
	private final Object parameterObject;
	private BoundSql boundSql;
	private Configuration configuration;

	public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		this.mappedStatement = mappedStatement;
		this.configuration = mappedStatement.getConfiguration();
		this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
		this.parameterObject = parameterObject;
		this.boundSql = boundSql;
	}

	public Object getParameterObject() {
		return parameterObject;
	}

	public void setParameters(PreparedStatement ps) throws SQLException {
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
		MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
		for (int i = 0; i < parameterMappings.size(); i++) {
			ParameterMapping parameterMapping = parameterMappings.get(i);
			if (parameterMapping.getMode() != ParameterMode.OUT) {
			Object value;
			String propertyName = parameterMapping.getProperty();
			if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
				value = boundSql.getAdditionalParameter(propertyName);
			} else if (parameterObject == null) {
				value = null;
			} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				value = parameterObject;
			} else {
				value = metaObject == null ? null : metaObject.getValue(propertyName);
			}
			TypeHandler typeHandler = parameterMapping.getTypeHandler();
			if (typeHandler == null) {
				throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
			}
			JdbcType jdbcType = parameterMapping.getJdbcType();
			if (value == null && jdbcType == null) jdbcType = configuration.getJdbcTypeForNull();
			typeHandler.setParameter(ps, i + 1, value, jdbcType);
			}
		}
		}
	}
}
