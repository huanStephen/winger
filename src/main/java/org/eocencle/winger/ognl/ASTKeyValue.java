package org.eocencle.winger.ognl;

public class ASTKeyValue extends SimpleNode {
	public ASTKeyValue(int id) {
		super(id);
	}

	public ASTKeyValue(OgnlParser p, int id) {
		super(p, id);
	}

	protected Node getKey() {
		return this._children[0];
	}

	protected Node getValue() {
		return this.jjtGetNumChildren() > 1 ? this._children[1] : null;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return null;
	}

	public String toString() {
		return this.getKey() + " -> " + this.getValue();
	}
}
