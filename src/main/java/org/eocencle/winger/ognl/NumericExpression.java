package org.eocencle.winger.ognl;

public abstract class NumericExpression extends ExpressionNode implements NodeType {
	protected Class _getterClass;

	public NumericExpression(int id) {
		super(id);
	}

	public NumericExpression(OgnlParser p, int id) {
		super(p, id);
	}

	public Class getGetterClass() {
		return this._getterClass != null ? this._getterClass : Double.TYPE;
	}

	public Class getSetterClass() {
		return null;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		Object value = null;
		String result = "";

		try {
			value = this.getValueBody(context, target);
			if (value != null) {
				this._getterClass = value.getClass();
			}

			for (int t = 0; t < this._children.length; ++t) {
				if (t > 0) {
					result = result + " " + this.getExpressionOperator(t) + " ";
				}

				String str = OgnlRuntime.getChildSource(context, target, this._children[t]);
				result = result + this.coerceToNumeric(str, context, this._children[t]);
			}

			return result;
		} catch (Throwable arg6) {
			throw OgnlOps.castToRuntime(arg6);
		}
	}

	public String coerceToNumeric(String source, OgnlContext context, Node child) {
		String ret = source;
		Object value = context.getCurrentObject();
		if (ASTConst.class.isInstance(child) && value != null) {
			return value.toString();
		} else {
			if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive()
					&& context.getCurrentObject() != null && Number.class.isInstance(context.getCurrentObject())) {
				ret = "((" + ExpressionCompiler.getCastString(context.getCurrentObject().getClass()) + ")" + source
						+ ")";
				ret = ret + "." + OgnlRuntime.getNumericValueGetter(context.getCurrentObject().getClass());
			} else if (context.getCurrentType() != null && context.getCurrentType().isPrimitive()
					&& (ASTConst.class.isInstance(child) || NumericExpression.class.isInstance(child))) {
				ret = source + OgnlRuntime.getNumericLiteral(context.getCurrentType());
			} else if (context.getCurrentType() != null && String.class.isAssignableFrom(context.getCurrentType())) {
				ret = "Double.parseDouble(" + source + ")";
				context.setCurrentType(Double.TYPE);
			}

			if (NumericExpression.class.isInstance(child)) {
				ret = "(" + ret + ")";
			}

			return ret;
		}
	}
}
