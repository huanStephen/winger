package org.eocencle.winger.ognl;

public class ASTSequence extends SimpleNode implements NodeType, OrderedReturn {
	private Class _getterClass;
	private String _lastExpression;
	private String _coreExpression;

	public ASTSequence(int id) {
		super(id);
	}

	public ASTSequence(OgnlParser p, int id) {
		super(p, id);
	}

	public void jjtClose() {
		this.flattenTree();
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = null;

		for (int i = 0; i < this._children.length; ++i) {
			result = this._children[i].getValue(context, source);
		}

		return result;
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		int last = this._children.length - 1;

		for (int i = 0; i < last; ++i) {
			this._children[i].getValue(context, target);
		}

		this._children[last].setValue(context, target, value);
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return null;
	}

	public String getLastExpression() {
		return this._lastExpression;
	}

	public String getCoreExpression() {
		return this._coreExpression;
	}

	public String toString() {
		String result = "";

		for (int i = 0; i < this._children.length; ++i) {
			if (i > 0) {
				result = result + ", ";
			}

			result = result + this._children[i];
		}

		return result;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		return "";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = "";
		NodeType _lastType = null;

		for (int i = 0; i < this._children.length; ++i) {
			String seqValue = this._children[i].toGetSourceString(context, target);
			if (i + 1 < this._children.length && ASTOr.class.isInstance(this._children[i])) {
				seqValue = "(" + seqValue + ")";
			}

			if (i > 0 && ASTProperty.class.isInstance(this._children[i]) && seqValue != null
					&& seqValue.trim().length() > 0) {
				String pre = (String) context.get("_currentChain");
				if (pre == null) {
					pre = "";
				}

				seqValue = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context) + pre
						+ seqValue;
				context.setCurrentAccessor(context.getRoot().getClass());
			}

			if (i + 1 >= this._children.length) {
				this._coreExpression = result;
				this._lastExpression = seqValue;
			}

			if (seqValue != null && seqValue.trim().length() > 0 && i + 1 < this._children.length) {
				result = result + seqValue + ";";
			} else if (seqValue != null && seqValue.trim().length() > 0) {
				result = result + seqValue;
			}

			if (NodeType.class.isInstance(this._children[i])
					&& ((NodeType) this._children[i]).getGetterClass() != null) {
				_lastType = (NodeType) this._children[i];
			}
		}

		if (_lastType != null) {
			this._getterClass = _lastType.getGetterClass();
		}

		return result;
	}

	public boolean isSequence(OgnlContext context) {
		return true;
	}
}
