package org.eocencle.winger.ognl;

import java.io.PrintWriter;
import java.io.Serializable;

import org.eocencle.winger.ognl.enhance.ExpressionAccessor;

public abstract class SimpleNode implements Node, Serializable {
	protected Node _parent;
	protected Node[] _children;
	protected int _id;
	protected OgnlParser _parser;
	private boolean _constantValueCalculated;
	private volatile boolean _hasConstantValue;
	private Object _constantValue;
	private ExpressionAccessor _accessor;

	public SimpleNode(int i) {
		this._id = i;
	}

	public SimpleNode(OgnlParser p, int i) {
		this(i);
		this._parser = p;
	}

	public void jjtOpen() {
	}

	public void jjtClose() {
	}

	public void jjtSetParent(Node n) {
		this._parent = n;
	}

	public Node jjtGetParent() {
		return this._parent;
	}

	public void jjtAddChild(Node n, int i) {
		if (this._children == null) {
			this._children = new Node[i + 1];
		} else if (i >= this._children.length) {
			Node[] c = new Node[i + 1];
			System.arraycopy(this._children, 0, c, 0, this._children.length);
			this._children = c;
		}

		this._children[i] = n;
	}

	public Node jjtGetChild(int i) {
		return this._children[i];
	}

	public int jjtGetNumChildren() {
		return this._children == null ? 0 : this._children.length;
	}

	public String toString() {
		return OgnlParserTreeConstants.jjtNodeName[this._id];
	}

	public String toString(String prefix) {
		return prefix + OgnlParserTreeConstants.jjtNodeName[this._id] + " " + this.toString();
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		return this.toString();
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		return this.toString();
	}

	public void dump(PrintWriter writer, String prefix) {
		writer.println(this.toString(prefix));
		if (this._children != null) {
			for (int i = 0; i < this._children.length; ++i) {
				SimpleNode n = (SimpleNode) this._children[i];
				if (n != null) {
					n.dump(writer, prefix + "  ");
				}
			}
		}

	}

	public int getIndexInParent() {
		int result = -1;
		if (this._parent != null) {
			int icount = this._parent.jjtGetNumChildren();

			for (int i = 0; i < icount; ++i) {
				if (this._parent.jjtGetChild(i) == this) {
					result = i;
					break;
				}
			}
		}

		return result;
	}

	public Node getNextSibling() {
		Node result = null;
		int i = this.getIndexInParent();
		if (i >= 0) {
			int icount = this._parent.jjtGetNumChildren();
			if (i < icount) {
				result = this._parent.jjtGetChild(i + 1);
			}
		}

		return result;
	}

	protected Object evaluateGetValueBody(OgnlContext context, Object source) throws OgnlException {
		context.setCurrentObject(source);
		context.setCurrentNode(this);
		if (!this._constantValueCalculated) {
			this._constantValueCalculated = true;
			boolean constant = this.isConstant(context);
			if (constant) {
				this._constantValue = this.getValueBody(context, source);
			}

			this._hasConstantValue = constant;
		}

		return this._hasConstantValue ? this._constantValue : this.getValueBody(context, source);
	}

	protected void evaluateSetValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		context.setCurrentObject(target);
		context.setCurrentNode(this);
		this.setValueBody(context, target, value);
	}

	public final Object getValue(OgnlContext context, Object source) throws OgnlException {
		Object result = null;
		if (context.getTraceEvaluations()) {
			EvaluationPool pool = OgnlRuntime.getEvaluationPool();
			Object evalException = null;
			Evaluation evaluation = pool.create(this, source);
			context.pushEvaluation(evaluation);
			boolean arg12 = false;

			try {
				arg12 = true;
				result = this.evaluateGetValueBody(context, source);
				arg12 = false;
			} catch (OgnlException arg13) {
				evalException = arg13;
				throw arg13;
			} catch (RuntimeException arg14) {
				evalException = arg14;
				throw arg14;
			} finally {
				if (arg12) {
					Evaluation eval = context.popEvaluation();
					eval.setResult(result);
					if (evalException != null) {
						eval.setException((Throwable) evalException);
					}

					if (evalException == null && context.getRootEvaluation() == null
							&& !context.getKeepLastEvaluation()) {
						pool.recycleAll(eval);
					}

				}
			}

			Evaluation ex = context.popEvaluation();
			ex.setResult(result);
			if (evalException != null) {
				ex.setException((Throwable) evalException);
			}

			if (evalException == null && context.getRootEvaluation() == null && !context.getKeepLastEvaluation()) {
				pool.recycleAll(ex);
			}
		} else {
			result = this.evaluateGetValueBody(context, source);
		}

		return result;
	}

	protected abstract Object getValueBody(OgnlContext arg0, Object arg1) throws OgnlException;

	public final void setValue(OgnlContext context, Object target, Object value) throws OgnlException {
		if (context.getTraceEvaluations()) {
			EvaluationPool pool = OgnlRuntime.getEvaluationPool();
			Object evalException = null;
			Evaluation evaluation = pool.create(this, target, true);
			context.pushEvaluation(evaluation);
			boolean arg12 = false;

			try {
				arg12 = true;
				this.evaluateSetValueBody(context, target, value);
				arg12 = false;
			} catch (OgnlException arg13) {
				evalException = arg13;
				arg13.setEvaluation(evaluation);
				throw arg13;
			} catch (RuntimeException arg14) {
				evalException = arg14;
				throw arg14;
			} finally {
				if (arg12) {
					Evaluation eval = context.popEvaluation();
					if (evalException != null) {
						eval.setException((Throwable) evalException);
					}

					if (evalException == null && context.getRootEvaluation() == null
							&& !context.getKeepLastEvaluation()) {
						pool.recycleAll(eval);
					}

				}
			}

			Evaluation ex = context.popEvaluation();
			if (evalException != null) {
				ex.setException((Throwable) evalException);
			}

			if (evalException == null && context.getRootEvaluation() == null && !context.getKeepLastEvaluation()) {
				pool.recycleAll(ex);
			}
		} else {
			this.evaluateSetValueBody(context, target, value);
		}

	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		throw new InappropriateExpressionException(this);
	}

	public boolean isNodeConstant(OgnlContext context) throws OgnlException {
		return false;
	}

	public boolean isConstant(OgnlContext context) throws OgnlException {
		return this.isNodeConstant(context);
	}

	public boolean isNodeSimpleProperty(OgnlContext context) throws OgnlException {
		return false;
	}

	public boolean isSimpleProperty(OgnlContext context) throws OgnlException {
		return this.isNodeSimpleProperty(context);
	}

	public boolean isSimpleNavigationChain(OgnlContext context) throws OgnlException {
		return this.isSimpleProperty(context);
	}

	public boolean isEvalChain(OgnlContext context) throws OgnlException {
		if (this._children == null) {
			return false;
		} else {
			Node[] arr$ = this._children;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Node child = arr$[i$];
				if (child instanceof SimpleNode && ((SimpleNode) child).isEvalChain(context)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isSequence(OgnlContext context) throws OgnlException {
		if (this._children == null) {
			return false;
		} else {
			Node[] arr$ = this._children;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Node child = arr$[i$];
				if (child instanceof SimpleNode && ((SimpleNode) child).isSequence(context)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isOperation(OgnlContext context) throws OgnlException {
		if (this._children == null) {
			return false;
		} else {
			Node[] arr$ = this._children;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Node child = arr$[i$];
				if (child instanceof SimpleNode && ((SimpleNode) child).isOperation(context)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isChain(OgnlContext context) throws OgnlException {
		if (this._children == null) {
			return false;
		} else {
			Node[] arr$ = this._children;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Node child = arr$[i$];
				if (child instanceof SimpleNode && ((SimpleNode) child).isChain(context)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isSimpleMethod(OgnlContext context) throws OgnlException {
		return false;
	}

	protected boolean lastChild(OgnlContext context) {
		return this._parent == null || context.get("_lastChild") != null;
	}

	protected void flattenTree() {
		boolean shouldFlatten = false;
		int newSize = 0;

		for (int newChildren = 0; newChildren < this._children.length; ++newChildren) {
			if (this._children[newChildren].getClass() == this.getClass()) {
				shouldFlatten = true;
				newSize += this._children[newChildren].jjtGetNumChildren();
			} else {
				++newSize;
			}
		}

		if (shouldFlatten) {
			Node[] arg7 = new Node[newSize];
			int j = 0;

			for (int i = 0; i < this._children.length; ++i) {
				Node c = this._children[i];
				if (c.getClass() == this.getClass()) {
					for (int k = 0; k < c.jjtGetNumChildren(); ++k) {
						arg7[j++] = c.jjtGetChild(k);
					}
				} else {
					arg7[j++] = c;
				}
			}

			if (j != newSize) {
				throw new Error("Assertion error: " + j + " != " + newSize);
			}

			this._children = arg7;
		}

	}

	public ExpressionAccessor getAccessor() {
		return this._accessor;
	}

	public void setAccessor(ExpressionAccessor accessor) {
		this._accessor = accessor;
	}
}
