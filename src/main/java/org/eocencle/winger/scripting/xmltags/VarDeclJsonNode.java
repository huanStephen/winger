package org.eocencle.winger.scripting.xmltags;

public class VarDeclJsonNode implements JsonNode {
	private final String name;
	private final String expression;

	public VarDeclJsonNode(String var, String exp) {
		name = var;
		expression = exp;
	}

	public boolean apply(DynamicContext context) {
		final Object value = OgnlCache.getValue(expression, context.getBindings());
		context.bind(name, value);
		return true;
	}
}
