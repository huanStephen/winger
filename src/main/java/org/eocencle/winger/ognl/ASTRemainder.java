package org.eocencle.winger.ognl;

public class ASTRemainder extends NumericExpression {
	public ASTRemainder(int id) {
		super(id);
	}

	public ASTRemainder(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.remainder(v1, v2);
	}

	public String getExpressionOperator(int index) {
		return "%";
	}
}
