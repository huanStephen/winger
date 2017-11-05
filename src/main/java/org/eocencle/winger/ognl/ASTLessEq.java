package org.eocencle.winger.ognl;

public class ASTLessEq extends ComparisonExpression {
	public ASTLessEq(int id) {
		super(id);
	}

	public ASTLessEq(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.greater(v1, v2) ? Boolean.FALSE : Boolean.TRUE;
	}

	public String getExpressionOperator(int index) {
		return "<=";
	}

	public String getComparisonFunction() {
		return "!ognl.OgnlOps.greater";
	}
}
