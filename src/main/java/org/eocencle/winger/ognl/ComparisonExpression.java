package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public abstract class ComparisonExpression extends BooleanExpression {
	public ComparisonExpression(int id) {
		super(id);
	}

	public ComparisonExpression(OgnlParser p, int id) {
		super(p, id);
	}

	public abstract String getComparisonFunction();

	public String toGetSourceString(OgnlContext context, Object target) {
		if (target == null) {
			throw new UnsupportedCompilationException("Current target is null, can\'t compile.");
		} else {
			try {
				Object t = this.getValueBody(context, target);
				if (t != null && Boolean.class.isAssignableFrom(t.getClass())) {
					this._getterClass = Boolean.TYPE;
				} else if (t != null) {
					this._getterClass = t.getClass();
				} else {
					this._getterClass = Boolean.TYPE;
				}

				OgnlRuntime.getChildSource(context, target, this._children[0]);
				OgnlRuntime.getChildSource(context, target, this._children[1]);
				boolean conversion = OgnlRuntime.shouldConvertNumericTypes(context);
				String result = conversion ? "(" + this.getComparisonFunction() + "( ($w) (" : "(";
				result = result + OgnlRuntime.getChildSource(context, target, this._children[0], conversion) + " "
						+ (conversion ? "), ($w) " : this.getExpressionOperator(0)) + " "
						+ OgnlRuntime.getChildSource(context, target, this._children[1], conversion);
				result = result + (conversion ? ")" : "");
				context.setCurrentType(Boolean.TYPE);
				result = result + ")";
				return result;
			} catch (NullPointerException arg5) {
				throw new UnsupportedCompilationException("evaluation resulted in null expression.");
			} catch (Throwable arg6) {
				throw OgnlOps.castToRuntime(arg6);
			}
		}
	}
}
