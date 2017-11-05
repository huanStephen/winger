package org.eocencle.winger.ognl;

public class ASTInstanceof extends SimpleNode implements NodeType {
	private String targetType;

	public ASTInstanceof(int id) {
		super(id);
	}

	public ASTInstanceof(OgnlParser p, int id) {
		super(p, id);
	}

	void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object value = this._children[0].getValue(context, source);
		return OgnlRuntime.isInstance(context, value, this.targetType) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String toString() {
		return this._children[0] + " instanceof " + this.targetType;
	}

	public Class getGetterClass() {
		return Boolean.TYPE;
	}

	public Class getSetterClass() {
		return null;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		try {
			String t = "";
			if (ASTConst.class.isInstance(this._children[0])) {
				t = ((Boolean) this.getValueBody(context, target)).toString();
			} else {
				t = this._children[0].toGetSourceString(context, target) + " instanceof " + this.targetType;
			}

			context.setCurrentType(Boolean.TYPE);
			return t;
		} catch (Throwable arg3) {
			throw OgnlOps.castToRuntime(arg3);
		}
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		return this.toGetSourceString(context, target);
	}
}
