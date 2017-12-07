package org.eocencle.winger.builder;

import java.util.Map;

import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.parsing.GenericTokenParser;
import org.eocencle.winger.parsing.TokenHandler;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;

public class JsonSourceBuilder extends BaseBuilder {
	private static final String parameterProperties = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";
	
	public JsonSourceBuilder(Configuration configuration) {
		super(configuration);
	}

	public JsonSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
		ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
		GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
		String json = parser.parse(originalSql);
		return new StaticJsonSource(configuration, json);
	}

	private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

		private Class<?> parameterType;
		private MetaObject metaParameters;

		public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
		super(configuration);
		this.parameterType = parameterType;
		this.metaParameters = configuration.newMetaObject(additionalParameters);
		}

		public String handleToken(String content) {
			return "?";
		}

		private Map<String, String> parseParameterMapping(String content) {
			try {
				return ParameterExpressionParser.parse(content);
			} catch (BuilderException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new BuilderException("Parsing error was found in mapping #{" + content + "}.Check syntax #{property|(expression), var1=value1, var2=value2, ...} ", ex);
			}
		}
	}
}
