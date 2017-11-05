package org.eocencle.winger.ognl;

public class ASTUnsignedShiftRight extends NumericExpression {
	public ASTUnsignedShiftRight(int id) {
		super(id);
	}

	public ASTUnsignedShiftRight(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.unsignedShiftRight(v1, v2);
	}

	public String getExpressionOperator(int index) {
		return ">>>";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = "";

		try {
			String t = OgnlRuntime.getChildSource(context, target, this._children[0]);
			t = this.coerceToNumeric(t, context, this._children[0]);
			String child2 = OgnlRuntime.getChildSource(context, target, this._children[1]);
			child2 = this.coerceToNumeric(child2, context, this._children[1]);
			Object v1 = this._children[0].getValue(context, target);
			int type = OgnlOps.getNumericType(v1);
			if (type <= 4) {
				t = "(int)" + t;
				child2 = "(int)" + child2;
			}

			result = t + " >>> " + child2;
			context.setCurrentType(Integer.TYPE);
			context.setCurrentObject(this.getValueBody(context, target));
			return result;
		} catch (Throwable arg7) {
			throw OgnlOps.castToRuntime(arg7);
		}
	}
}
