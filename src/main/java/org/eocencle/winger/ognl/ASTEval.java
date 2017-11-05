package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTEval extends SimpleNode {
	public ASTEval(int id) {
		super(id);
	}

	public ASTEval(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object expr = this._children[0].getValue(context, source);
		Object previousRoot = context.getRoot();
		source = this._children[1].getValue(context, source);
		Node node = expr instanceof Node ? (Node) expr : (Node) Ognl.parseExpression(expr.toString());

		Object result;
		try {
			context.setRoot(source);
			result = node.getValue(context, source);
		} finally {
			context.setRoot(previousRoot);
		}

		return result;
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		Object expr = this._children[0].getValue(context, target);
		Object previousRoot = context.getRoot();
		target = this._children[1].getValue(context, target);
		Node node = expr instanceof Node ? (Node) expr : (Node) Ognl.parseExpression(expr.toString());

		try {
			context.setRoot(target);
			node.setValue(context, target, value);
		} finally {
			context.setRoot(previousRoot);
		}

	}

	public boolean isEvalChain(OgnlContext context) throws OgnlException {
		return true;
	}

	public String toString() {
		return "(" + this._children[0] + ")(" + this._children[1] + ")";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
	}
}
