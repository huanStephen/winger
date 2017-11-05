package org.eocencle.winger.ognl;

public class ExpressionSyntaxException extends OgnlException {
	public ExpressionSyntaxException(String expression, Throwable reason) {
		super("Malformed OGNL expression: " + expression, reason);
	}
}
