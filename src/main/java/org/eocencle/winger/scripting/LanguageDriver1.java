package org.eocencle.winger.scripting;

import org.eocencle.winger.executor.parameter.ParameterHandler;
import org.eocencle.winger.mapping.BoundSql;
import org.eocencle.winger.mapping.MappedStatement;
import org.eocencle.winger.mapping.SqlSource;
import org.eocencle.winger.parsing.XNode;
import org.eocencle.winger.session.Configuration;

public interface LanguageDriver1 {
	/**
	 * Creates a {@link ParameterHandler} that will set the parameters of the
	 *
	 * @param mappedStatement The mapped statement that is being executed
	 * @param parameterObject The input parameter object (can be null) 
	 * @param boundSql The resulting SQL once the dynamic language has been executed.
	 * @return
	 */
	public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

	/**
	 * Creates an {@link SqlSource} that will hold the statement read from a mapper xml file
	 * 
	 * @param configuration The MyBatis configuration
	 * @param script XNode parsed from a XML file
	 * @param parameterType input parameter type
	 * @return
	 */
	public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);

	/**
	 * Creates an {@link SqlSource} that will hold the statement read from an annotation
	 * 
	 * @param configuration The MyBatis configuration
	 * @param script The content of the annotation
	 * @param parameterType input parameter type
	 * @return 
	 */
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);
}
