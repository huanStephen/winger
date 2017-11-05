package org.eocencle.winger.ognl;

public class ASTBitAnd extends NumericExpression {
	public ASTBitAnd(int id) {
		super(id);
	}

	public ASTBitAnd(OgnlParser p, int id) {
		super(p, id);
	}

	public void jjtClose() {
		this.flattenTree();
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = this._children[0].getValue(context, source);

		for (int i = 1; i < this._children.length; ++i) {
			result = OgnlOps.binaryAnd(result, this._children[i].getValue(context, source));
		}

		return result;
	}

	public String getExpressionOperator(int index) {
		return "&";
	}

	public String coerceToNumeric(String source, OgnlContext context, Node child) {
		return "(long)" + super.coerceToNumeric(source, context, child);
	}
}
