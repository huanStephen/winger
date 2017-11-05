package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTNotIn extends SimpleNode implements NodeType {
	public ASTNotIn(int id) {
		super(id);
	}

	public ASTNotIn(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object v1 = this._children[0].getValue(context, source);
		Object v2 = this._children[1].getValue(context, source);
		return OgnlOps.in(v1, v2) ? Boolean.FALSE : Boolean.TRUE;
	}

	public String toString() {
		return this._children[0] + " not in " + this._children[1];
	}

	public Class getGetterClass() {
		return Boolean.TYPE;
	}

	public Class getSetterClass() {
		return null;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		try {
			String t = "(! ognl.OgnlOps.in( ($w) ";
			t = t + OgnlRuntime.getChildSource(context, target, this._children[0]) + ", ($w) "
					+ OgnlRuntime.getChildSource(context, target, this._children[1]);
			t = t + ") )";
			context.setCurrentType(Boolean.TYPE);
			return t;
		} catch (NullPointerException arg3) {
			arg3.printStackTrace();
			throw new UnsupportedCompilationException("evaluation resulted in null expression.");
		} catch (Throwable arg4) {
			throw OgnlOps.castToRuntime(arg4);
		}
	}
}
