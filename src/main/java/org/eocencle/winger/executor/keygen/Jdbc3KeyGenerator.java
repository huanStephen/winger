package org.eocencle.winger.executor.keygen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eocencle.winger.executor.ExecutorException;
import org.eocencle.winger.javassist.bytecode.analysis.Executor;
import org.eocencle.winger.mapping.MappedStatement;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.type.TypeHandler;
import org.eocencle.winger.type.TypeHandlerRegistry;

public class Jdbc3KeyGenerator implements KeyGenerator {
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		// do nothing
	}

	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(parameter);
		processBatch(ms, stmt, parameters);
	}

	public void processBatch(MappedStatement ms, Statement stmt, List<Object> parameters) {
		ResultSet rs = null;
		try {
			rs = stmt.getGeneratedKeys();
			final Configuration configuration = ms.getConfiguration();
			final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			final String[] keyProperties = ms.getKeyProperties();
			final ResultSetMetaData rsmd = rs.getMetaData();
			TypeHandler<?>[] typeHandlers = null;
			if (keyProperties != null && rsmd.getColumnCount() >= keyProperties.length) {
				for (Object parameter : parameters) {
					if (!rs.next()) break; // there should be one row for each statement (also one for each parameter)
					final MetaObject metaParam = configuration.newMetaObject(parameter);
					if (typeHandlers == null) typeHandlers = getTypeHandlers(typeHandlerRegistry, metaParam, keyProperties);
					populateKeys(rs, metaParam, keyProperties, typeHandlers);
				}
			}
		} catch (Exception e) {
			throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	private TypeHandler<?>[] getTypeHandlers(TypeHandlerRegistry typeHandlerRegistry, MetaObject metaParam, String[] keyProperties) {
		TypeHandler<?>[] typeHandlers = new TypeHandler<?>[keyProperties.length];
		for (int i = 0; i < keyProperties.length; i++) {
			if (metaParam.hasSetter(keyProperties[i])) {
				Class<?> keyPropertyType = metaParam.getSetterType(keyProperties[i]);
				TypeHandler<?> th = typeHandlerRegistry.getTypeHandler(keyPropertyType);
				typeHandlers[i] = th;
			}
		}
		return typeHandlers;
	}

	private void populateKeys(ResultSet rs, MetaObject metaParam, String[] keyProperties, TypeHandler<?>[] typeHandlers) throws SQLException {
		for (int i = 0; i < keyProperties.length; i++) {
			TypeHandler<?> th = typeHandlers[i];
			if (th != null) {
				Object value = th.getResult(rs, i + 1);
				metaParam.setValue(keyProperties[i], value);
			}
		}
	}
}
