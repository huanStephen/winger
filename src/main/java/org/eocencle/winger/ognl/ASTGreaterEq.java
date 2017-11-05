package org.eocencle.winger.ognl;

public class ASTGreaterEq extends ComparisonExpression {
	public ASTGreaterEq(int id) {
		super(id);
	}

	public ASTGreaterEq(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.less(v1, v2) ? Boolean.FALSE : Boolean.TRUE;
	}

	public String getExpressionOperator(int index) {
		return ">=";
	}

	public String getComparisonFunction() {
		return "!ognl.OgnlOps.less";
	}
}
