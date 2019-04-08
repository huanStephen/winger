package org.eocencle.winger.builder;

import java.util.Map;

import org.eocencle.winger.exceptions.BuilderException;
import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.mapping.JsonSource;
import org.eocencle.winger.parsing.GenericTokenParser;
import org.eocencle.winger.parsing.TokenHandler;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.xmltags.StaticJsonSource;

public class JsonSourceBuilder extends AbstractBuilder {
	private static final String parameterProperties = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";
	
	public JsonSourceBuilder(Configuration config) {
		super(config);
	}

	public JsonSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) throws WingerException {
		ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(this.config, parameterType, additionalParameters);
		GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
		String json = parser.parse(originalSql);
		return new StaticJsonSource(this.config, json);
	}

	private static class ParameterMappingTokenHandler extends AbstractBuilder implements TokenHandler {

		private Class<?> parameterType;

		public ParameterMappingTokenHandler(Configuration config, Class<?> parameterType, Map<String, Object> additionalParameters) {
			super(config);
			this.parameterType = parameterType;
			//this.metaParameters = config.newMetaObject(additionalParameters);
		}

		public String handleToken(String content) {
			return "?";
		}

		@Override
		public Configuration parse() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public Configuration parse() {
		// TODO Auto-generated method stub
		return null;
	}
}
