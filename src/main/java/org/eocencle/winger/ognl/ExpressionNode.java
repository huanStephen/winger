package org.eocencle.winger.ognl;

public abstract class ExpressionNode extends SimpleNode {
	public ExpressionNode(int i) {
		super(i);
	}

	public ExpressionNode(OgnlParser p, int i) {
		super(p, i);
	}

	public boolean isNodeConstant(OgnlContext context) throws OgnlException {
		return false;
	}

	public boolean isConstant(OgnlContext context) throws OgnlException {
		boolean result = this.isNodeConstant(context);
		if (this._children != null && this._children.length > 0) {
			result = true;

			for (int i = 0; result && i < this._children.length; ++i) {
				if (this._children[i] instanceof SimpleNode) {
					result = ((SimpleNode) this._children[i]).isConstant(context);
				} else {
					result = false;
				}
			}
		}

		return result;
	}

	public String getExpressionOperator(int index) {
		throw new RuntimeException("unknown operator for " + OgnlParserTreeConstants.jjtNodeName[this._id]);
	}

	public String toString() {
		String result = this._parent == null ? "" : "(";
		if (this._children != null && this._children.length > 0) {
			for (int i = 0; i < this._children.length; ++i) {
				if (i > 0) {
					result = result + " " + this.getExpressionOperator(i) + " ";
				}

				result = result + this._children[i].toString();
			}
		}

		if (this._parent != null) {
			result = result + ")";
		}

		return result;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = this._parent != null && !NumericExpression.class.isAssignableFrom(this._parent.getClass()) ? "("
				: "";
		if (this._children != null && this._children.length > 0) {
			for (int i = 0; i < this._children.length; ++i) {
				if (i > 0) {
					result = result + " " + this.getExpressionOperator(i) + " ";
				}

				String value = this._children[i].toGetSourceString(context, target);
				if ((ASTProperty.class.isInstance(this._children[i]) || ASTMethod.class.isInstance(this._children[i])
						|| ASTSequence.class.isInstance(this._children[i])
						|| ASTChain.class.isInstance(this._children[i])) && value != null
						&& value.trim().length() > 0) {
					String pre = null;
					if (ASTMethod.class.isInstance(this._children[i])) {
						pre = (String) context.get("_currentChain");
					}

					if (pre == null) {
						pre = "";
					}

					String cast = (String) context.remove("_preCast");
					if (cast == null) {
						cast = "";
					}

					value = cast + ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context)
							+ pre + value;
				}

				result = result + value;
			}
		}

		if (this._parent != null && !NumericExpression.class.isAssignableFrom(this._parent.getClass())) {
			result = result + ")";
		}

		return result;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		String result = this._parent == null ? "" : "(";
		if (this._children != null && this._children.length > 0) {
			for (int i = 0; i < this._children.length; ++i) {
				if (i > 0) {
					result = result + " " + this.getExpressionOperator(i) + " ";
				}

				result = result + this._children[i].toSetSourceString(context, target);
			}
		}

		if (this._parent != null) {
			result = result + ")";
		}

		return result;
	}

	public boolean isOperation(OgnlContext context) {
		return true;
	}
}
