package org.eocencle.winger.scripting.defaults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.eocencle.winger.executor.ErrorContext;
import org.eocencle.winger.executor.ExecutorException;
import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.mapping.BoundJson;
import org.eocencle.winger.mapping.ParameterMapping;
import org.eocencle.winger.mapping.ParameterMode;
import org.eocencle.winger.mapping.ResponseBranch;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.JdbcType;
import org.eocencle.winger.type.TypeHandler;
import org.eocencle.winger.type.TypeHandlerRegistry;

public class DefaultParameterHandler implements ParameterHandler {
	private final TypeHandlerRegistry typeHandlerRegistry;

	private final ResponseBranch responseBranch;
	private final Object parameterObject;
	private BoundJson boundJson;
	private Configuration configuration;

	public DefaultParameterHandler(ResponseBranch responseBranch, Object parameterObject, BoundJson boundJson) {
		this.responseBranch = responseBranch;
		this.configuration = responseBranch.getConfiguration();
		this.typeHandlerRegistry = responseBranch.getConfiguration().getTypeHandlerRegistry();
		this.parameterObject = parameterObject;
		this.boundJson = boundJson;
	}

	public Object getParameterObject() {
		return parameterObject;
	}

	public void setParameters(PreparedStatement ps) throws SQLException {
		//ErrorContext.instance().activity("setting parameters").object(responseBranch.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundJson.getParameterMappings();
		if (parameterMappings != null) {
		MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
		for (int i = 0; i < parameterMappings.size(); i++) {
			ParameterMapping parameterMapping = parameterMappings.get(i);
			if (parameterMapping.getMode() != ParameterMode.OUT) {
			Object value;
			String propertyName = parameterMapping.getProperty();
			if (boundJson.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
				value = boundJson.getAdditionalParameter(propertyName);
			} else if (parameterObject == null) {
				value = null;
			} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				value = parameterObject;
			} else {
				value = metaObject == null ? null : metaObject.getValue(propertyName);
			}
			TypeHandler typeHandler = parameterMapping.getTypeHandler();
			if (typeHandler == null) {
				//throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + responseBranch.getId());
			}
			JdbcType jdbcType = parameterMapping.getJdbcType();
			if (value == null && jdbcType == null) jdbcType = configuration.getJdbcTypeForNull();
			typeHandler.setParameter(ps, i + 1, value, jdbcType);
			}
		}
		}
	}
}
