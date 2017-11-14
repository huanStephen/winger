package org.eocencle.winger.executor.keygen;

import java.sql.Statement;
import java.util.List;

import org.eocencle.winger.executor.ExecutorException;
import org.eocencle.winger.javassist.bytecode.analysis.Executor;
import org.eocencle.winger.mapping.MappedStatement;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.session.ExecutorType;
import org.eocencle.winger.session.RowBounds;

public class SelectKeyGenerator implements KeyGenerator {
	public static final String SELECT_KEY_SUFFIX = "!selectKey";
	private boolean executeBefore;
	private MappedStatement keyStatement;

	public SelectKeyGenerator(MappedStatement keyStatement, boolean executeBefore) {
		this.executeBefore = executeBefore;
		this.keyStatement = keyStatement;
	}

	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		if (executeBefore) {
		processGeneratedKeys(executor, ms, parameter);
		}
	}

	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		if (!executeBefore) {
		processGeneratedKeys(executor, ms, parameter);
		}
	}

	private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
		try {
		final Configuration configuration = ms.getConfiguration();
		if (parameter != null) {
			String keyStatementName = ms.getId() + SELECT_KEY_SUFFIX;
			if (configuration.hasStatement(keyStatementName)) {

			if (keyStatement != null && keyStatement.getKeyProperties() != null) {
				String keyProperty = keyStatement.getKeyProperties()[0]; //just one key property is supported
				final MetaObject metaParam = configuration.newMetaObject(parameter);
				if (keyProperty != null && metaParam.hasSetter(keyProperty)) {
				// Do not close keyExecutor.
				// The transaction will be closed by parent executor.
				Executor keyExecutor = configuration.newExecutor(executor.getTransaction(), ExecutorType.SIMPLE);
				List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
				if (values.size() > 1) {
					throw new ExecutorException("Select statement for SelectKeyGenerator returned more than one value.");
				}
				metaParam.setValue(keyProperty, values.get(0));
				}
			}
			}
		}
		} catch (Exception e) {
		throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
		}
	}
}
