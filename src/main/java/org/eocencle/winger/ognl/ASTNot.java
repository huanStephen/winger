package org.eocencle.winger.ognl;

public class ASTNot extends BooleanExpression {
	public ASTNot(int id) {
		super(id);
	}

	public ASTNot(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return OgnlOps.booleanValue(this._children[0].getValue(context, source)) ? Boolean.FALSE : Boolean.TRUE;
	}

	public String getExpressionOperator(int index) {
		return "!";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		try {
			String t = super.toGetSourceString(context, target);
			if (t == null || t.trim().length() < 1) {
				t = "null";
			}

			context.setCurrentType(Boolean.TYPE);
			return "(! ognl.OgnlOps.booleanValue(" + t + ") )";
		} catch (Throwable arg3) {
			throw OgnlOps.castToRuntime(arg3);
		}
	}
}
