package org.eocencle.winger.scripting.xmltags;

public class IfJsonNode implements JsonNode {
	private ExpressionEvaluator evaluator;
	private String test;
	private JsonNode contents;

	public IfJsonNode(JsonNode contents, String test) {
		this.test = test;
		this.contents = contents;
		this.evaluator = new ExpressionEvaluator();
	}

	public boolean apply(DynamicContext context) {
		if (this.evaluator.evaluateBoolean(this.test, context.getBindings())) {
			this.contents.apply(context);
			return true;
		} else {
			return false;
		}
	}
}
