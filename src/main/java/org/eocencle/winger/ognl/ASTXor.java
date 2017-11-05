package org.eocencle.winger.ognl;

public class ASTXor extends NumericExpression {
	public ASTXor(int id) {
		super(id);
	}

	public ASTXor(OgnlParser p, int id) {
		super(p, id);
	}

	public void jjtClose() {
		this.flattenTree();
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = this._children[0].getValue(context, source);

		for (int i = 1; i < this._children.length; ++i) {
			result = OgnlOps.binaryXor(result, this._children[i].getValue(context, source));
		}

		return result;
	}

	public String getExpressionOperator(int index) {
		return "^";
	}
}
