package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public abstract class BooleanExpression extends ExpressionNode implements NodeType {
	protected Class _getterClass;

	public BooleanExpression(int id) {
		super(id);
	}

	public BooleanExpression(OgnlParser p, int id) {
		super(p, id);
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return null;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		try {
			Object t = this.getValueBody(context, target);
			if (t != null && Boolean.class.isAssignableFrom(t.getClass())) {
				this._getterClass = Boolean.TYPE;
			} else if (t != null) {
				this._getterClass = t.getClass();
			} else {
				this._getterClass = Boolean.TYPE;
			}

			String ret = super.toGetSourceString(context, target);
			return "(false)".equals(ret) ? "false" : ("(true)".equals(ret) ? "true" : ret);
		} catch (NullPointerException arg4) {
			arg4.printStackTrace();
			throw new UnsupportedCompilationException("evaluation resulted in null expression.");
		} catch (Throwable arg5) {
			throw OgnlOps.castToRuntime(arg5);
		}
	}
}
