package org.eocencle.winger.ognl;

public class ASTNegate extends NumericExpression {
	public ASTNegate(int id) {
		super(id);
	}

	public ASTNegate(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return OgnlOps.negate(this._children[0].getValue(context, source));
	}

	public String toString() {
		return "-" + this._children[0];
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String source = this._children[0].toGetSourceString(context, target);
		return !ASTNegate.class.isInstance(this._children[0]) ? "-" + source : "-(" + source + ")";
	}
}
