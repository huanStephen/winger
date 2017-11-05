package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTThisVarRef extends ASTVarRef {
	public ASTThisVarRef(int id) {
		super(id);
	}

	public ASTThisVarRef(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return context.getCurrentObject();
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		context.setCurrentObject(value);
	}

	public String toString() {
		return "#this";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Unable to compile this references.");
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Unable to compile this references.");
	}
}
