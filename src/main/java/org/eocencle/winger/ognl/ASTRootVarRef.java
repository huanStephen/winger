package org.eocencle.winger.ognl;

public class ASTRootVarRef extends ASTVarRef {
	public ASTRootVarRef(int id) {
		super(id);
	}

	public ASTRootVarRef(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return context.getRoot();
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		context.setRoot(value);
	}

	public String toString() {
		return "#root";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		if (target != null) {
			this._getterClass = target.getClass();
		}

		if (this._getterClass != null) {
			context.setCurrentType(this._getterClass);
		}

		return this._parent != null && (this._getterClass == null || !this._getterClass.isArray())
				? ExpressionCompiler.getRootExpression(this, target, context) : "";
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		return this._parent != null && (this._getterClass == null || !this._getterClass.isArray()) ? "$3" : "";
	}
}
