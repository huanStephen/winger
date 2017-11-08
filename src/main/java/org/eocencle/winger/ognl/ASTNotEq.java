package org.eocencle.winger.ognl;

public class ASTNotEq extends ComparisonExpression {
	public ASTNotEq(int id) {
		super(id);
	}

	public ASTNotEq(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.equal(v1, v2) ? Boolean.FALSE : Boolean.TRUE;
	}

	public String getExpressionOperator(int index) {
		return "!=";
	}

	public String getComparisonFunction() {
		return "!ognl.OgnlOps.equal";
	}
}