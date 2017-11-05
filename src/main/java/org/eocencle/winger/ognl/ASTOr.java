package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTOr extends BooleanExpression {
	public ASTOr(int id) {
		super(id);
	}

	public ASTOr(OgnlParser p, int id) {
		super(p, id);
	}

	public void jjtClose() {
		this.flattenTree();
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = null;
		int last = this._children.length - 1;

		for (int i = 0; i <= last; ++i) {
			result = this._children[i].getValue(context, source);
			if (i != last && OgnlOps.booleanValue(result)) {
				break;
			}
		}

		return result;
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		int last = this._children.length - 1;

		for (int i = 0; i < last; ++i) {
			Object v = this._children[i].getValue(context, target);
			if (OgnlOps.booleanValue(v)) {
				return;
			}
		}

		this._children[last].setValue(context, target, value);
	}

	public String getExpressionOperator(int index) {
		return "||";
	}

	public Class getGetterClass() {
		return null;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		if (this._children.length != 2) {
			throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
		} else {
			String result = "(";

			try {
				String t = OgnlRuntime.getChildSource(context, target, this._children[0]);
				if (!OgnlRuntime.isBoolean(t)) {
					t = OgnlRuntime.getCompiler().createLocalReference(context, t, context.getCurrentType());
				}

				Class firstType = context.getCurrentType();
				String second = OgnlRuntime.getChildSource(context, target, this._children[1]);
				if (!OgnlRuntime.isBoolean(second)) {
					second = OgnlRuntime.getCompiler().createLocalReference(context, second, context.getCurrentType());
				}

				Class secondType = context.getCurrentType();
				boolean mismatched = firstType.isPrimitive() && !secondType.isPrimitive()
						|| !firstType.isPrimitive() && secondType.isPrimitive();
				result = result + "org.eocencle.winger.ognl.OgnlOps.booleanValue(" + t + ")";
				result = result + " ? ";
				result = result + (mismatched ? " ($w) " : "") + t;
				result = result + " : ";
				result = result + (mismatched ? " ($w) " : "") + second;
				result = result + ")";
				context.setCurrentObject(target);
				context.setCurrentType(Boolean.TYPE);
				return result;
			} catch (Throwable arg8) {
				throw OgnlOps.castToRuntime(arg8);
			}
		}
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		if (this._children.length != 2) {
			throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
		} else {
			String pre = (String) context.get("_currentChain");
			if (pre == null) {
				pre = "";
			}

			String result = "";

			try {
				this._children[0].getValue(context, target);
				String t = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + pre
						+ this._children[0].toGetSourceString(context, target);
				if (!OgnlRuntime.isBoolean(t)) {
					t = OgnlRuntime.getCompiler().createLocalReference(context, t, Object.class);
				}

				this._children[1].getValue(context, target);
				String second = ExpressionCompiler.getRootExpression(this._children[1], context.getRoot(), context)
						+ pre + this._children[1].toSetSourceString(context, target);
				if (!OgnlRuntime.isBoolean(second)) {
					second = OgnlRuntime.getCompiler().createLocalReference(context, second, context.getCurrentType());
				}

				result = result + "org.eocencle.winger.ognl.OgnlOps.booleanValue(" + t + ")";
				result = result + " ? ";
				result = result + t;
				result = result + " : ";
				result = result + second;
				context.setCurrentObject(target);
				context.setCurrentType(Boolean.TYPE);
				return result;
			} catch (Throwable arg6) {
				throw OgnlOps.castToRuntime(arg6);
			}
		}
	}
}
