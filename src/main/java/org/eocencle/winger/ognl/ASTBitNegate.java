package org.eocencle.winger.ognl;

public class ASTBitNegate extends NumericExpression {
	public ASTBitNegate(int id) {
		super(id);
	}

	public ASTBitNegate(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return OgnlOps.bitNegate(this._children[0].getValue(context, source));
	}

	public String toString() {
		return "~" + this._children[0];
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String source = this._children[0].toGetSourceString(context, target);
		return !ASTBitNegate.class.isInstance(this._children[0])
				? "~(" + super.coerceToNumeric(source, context, this._children[0]) + ")" : "~(" + source + ")";
	}
}
