package org.eocencle.winger.ognl;

public class InappropriateExpressionException extends OgnlException {
	public InappropriateExpressionException(Node tree) {
		super("Inappropriate OGNL expression: " + tree);
	}
}
