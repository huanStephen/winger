package org.eocencle.winger.ognl;

public class ASTEq extends ComparisonExpression {
	public ASTEq(int id) {
		super(id);
	}

	public ASTEq(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.equal(v1, v2) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String getExpressionOperator(int index) {
		return "==";
	}

	public String getComparisonFunction() {
		return "org.eocencle.winger.ognl.OgnlOps.equal";
	}
}
