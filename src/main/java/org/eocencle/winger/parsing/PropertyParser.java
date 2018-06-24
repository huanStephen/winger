package org.eocencle.winger.parsing;

import java.util.Properties;

import org.eocencle.winger.exceptions.WingerException;

public class PropertyParser {
	public static String parse(String string, Properties variables) throws WingerException {
		VariableTokenHandler handler = new VariableTokenHandler(variables);
		GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
		return parser.parse(string);
	}

	private static class VariableTokenHandler implements TokenHandler {
		private Properties variables;

		public VariableTokenHandler(Properties variables) {
		this.variables = variables;
		}

		public String handleToken(String content) {
		if (variables != null && variables.containsKey(content)) {
			return variables.getProperty(content);
		}
		return "${" + content + "}";
		}
	}
}
