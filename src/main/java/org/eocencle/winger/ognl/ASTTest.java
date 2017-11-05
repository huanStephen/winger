package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTTest extends ExpressionNode {
	public ASTTest(int id) {
		super(id);
	}

	public ASTTest(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object test = this._children[0].getValue(context, source);
		int branch = OgnlOps.booleanValue(test) ? 1 : 2;
		return this._children[branch].getValue(context, source);
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		Object test = this._children[0].getValue(context, target);
		int branch = OgnlOps.booleanValue(test) ? 1 : 2;
		this._children[branch].setValue(context, target, value);
	}

	public String getExpressionOperator(int index) {
		return index == 1 ? "?" : ":";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		if (target == null) {
			throw new UnsupportedCompilationException("evaluation resulted in null expression.");
		} else if (this._children.length != 3) {
			throw new UnsupportedCompilationException(
					"Can only compile test expressions with two children." + this._children.length);
		} else {
			String result = "";

			try {
				String t = OgnlRuntime.getChildSource(context, target, this._children[0]);
				if (!OgnlRuntime.isBoolean(t) && !context.getCurrentType().isPrimitive()) {
					t = OgnlRuntime.getCompiler().createLocalReference(context, t, context.getCurrentType());
				}

				if (ExpressionNode.class.isInstance(this._children[0])) {
					t = "(" + t + ")";
				}

				String second = OgnlRuntime.getChildSource(context, target, this._children[1]);
				Class secondType = context.getCurrentType();
				if (!OgnlRuntime.isBoolean(second) && !context.getCurrentType().isPrimitive()) {
					second = OgnlRuntime.getCompiler().createLocalReference(context, second, context.getCurrentType());
				}

				if (ExpressionNode.class.isInstance(this._children[1])) {
					second = "(" + second + ")";
				}

				String third = OgnlRuntime.getChildSource(context, target, this._children[2]);
				Class thirdType = context.getCurrentType();
				if (!OgnlRuntime.isBoolean(third) && !context.getCurrentType().isPrimitive()) {
					third = OgnlRuntime.getCompiler().createLocalReference(context, third, context.getCurrentType());
				}

				if (ExpressionNode.class.isInstance(this._children[2])) {
					third = "(" + third + ")";
				}

				boolean mismatched = secondType.isPrimitive() && !thirdType.isPrimitive()
						|| !secondType.isPrimitive() && thirdType.isPrimitive();
				result = result + "org.eocencle.winger.ognl.OgnlOps.booleanValue(" + t + ")";
				result = result + " ? ";
				result = result + (mismatched ? " ($w) " : "") + second;
				result = result + " : ";
				result = result + (mismatched ? " ($w) " : "") + third;
				context.setCurrentObject(target);
				context.setCurrentType(mismatched ? Object.class : secondType);
				return result;
			} catch (NullPointerException arg9) {
				throw new UnsupportedCompilationException("evaluation resulted in null expression.");
			} catch (Throwable arg10) {
				throw OgnlOps.castToRuntime(arg10);
			}
		}
	}
}
