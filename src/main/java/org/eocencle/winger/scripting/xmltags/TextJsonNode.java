package org.eocencle.winger.scripting.xmltags;

import org.eocencle.winger.parsing.GenericTokenParser;
import org.eocencle.winger.parsing.TokenHandler;
import org.eocencle.winger.type.SimpleTypeRegistry;

public class TextJsonNode implements JsonNode {
	private String text;

	public TextJsonNode(String text) {
		this.text = text;
	}

	public boolean apply(DynamicContext context) {
		GenericTokenParser parser = new GenericTokenParser("${", "}", new BindingTokenParser(context));
		context.appendJson(parser.parse(text));
		return true;
	}

	private static class BindingTokenParser implements TokenHandler {

		private DynamicContext context;

		public BindingTokenParser(DynamicContext context) {
			this.context = context;
		}

		public String handleToken(String content) {
			Object parameter = context.getBindings().get("_parameter");
			if (parameter == null) {
				context.getBindings().put("value", null);
			} else if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
				context.getBindings().put("value", parameter);
			}
			Object value = OgnlCache.getValue(content, context.getBindings());
			return (value == null ? "" : String.valueOf(value)); // issue #274 return "" instead of "null"
		}
	}
}
