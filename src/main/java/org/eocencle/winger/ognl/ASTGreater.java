package org.eocencle.winger.ognl;

public class ASTGreater extends ComparisonExpression {
	public ASTGreater(int id) {
		super(id);
	}

	public ASTGreater(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.greater(v1, v2) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String getExpressionOperator(int index) {
		return ">";
	}

	public String getComparisonFunction() {
		return "org.eocencle.winger.ognl.OgnlOps.greater";
	}
}
