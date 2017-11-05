package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTAssign extends SimpleNode {
	public ASTAssign(int id) {
		super(id);
	}

	public ASTAssign(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = this._children[1].getValue(context, source);
		this._children[0].setValue(context, source, result);
		return result;
	}

	public String toString() {
		return this._children[0] + " = " + this._children[1];
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = "";
		String first = this._children[0].toGetSourceString(context, target);
		String second = "";
		if (ASTProperty.class.isInstance(this._children[1])) {
			second = second + "((" + OgnlRuntime.getCompiler().getClassName(target.getClass()) + ")$2).";
		}

		second = second + this._children[1].toGetSourceString(context, target);
		if (ASTSequence.class.isAssignableFrom(this._children[1].getClass())) {
			ASTSequence seq = (ASTSequence) this._children[1];
			context.setCurrentType(Object.class);
			String core = seq.getCoreExpression();
			if (core.endsWith(";")) {
				core = core.substring(0, core.lastIndexOf(";"));
			}

			second = OgnlRuntime.getCompiler().createLocalReference(context,
					"org.eocencle.winger.ognl.OgnlOps.returnValue(($w)" + core + ", ($w) " + seq.getLastExpression()
							+ ")",
					Object.class);
		}

		if (NodeType.class.isInstance(this._children[1]) && !ASTProperty.class.isInstance(this._children[1])
				&& ((NodeType) this._children[1]).getGetterClass() != null
				&& !OrderedReturn.class.isInstance(this._children[1])) {
			second = "new " + ((NodeType) this._children[1]).getGetterClass().getName() + "(" + second + ")";
		}

		if (OrderedReturn.class.isAssignableFrom(this._children[0].getClass())
				&& ((OrderedReturn) this._children[0]).getCoreExpression() != null) {
			context.setCurrentType(Object.class);
			result = first + second + ")";
			result = OgnlRuntime.getCompiler().createLocalReference(context,
					"org.eocencle.winger.ognl.OgnlOps.returnValue(($w)" + result + ", ($w)"
							+ ((OrderedReturn) this._children[0]).getLastExpression() + ")",
					Object.class);
		}

		return result;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		String result = "";
		result = result + this._children[0].toSetSourceString(context, target);
		if (ASTProperty.class.isInstance(this._children[1])) {
			result = result + "((" + OgnlRuntime.getCompiler().getClassName(target.getClass()) + ")$2).";
		}

		String value = this._children[1].toSetSourceString(context, target);
		if (value == null) {
			throw new UnsupportedCompilationException(
					"Value for assignment is null, can\'t enhance statement to bytecode.");
		} else {
			if (ASTSequence.class.isAssignableFrom(this._children[1].getClass())) {
				ASTSequence seq = (ASTSequence) this._children[1];
				result = seq.getCoreExpression() + result;
				value = seq.getLastExpression();
			}

			if (NodeType.class.isInstance(this._children[1]) && !ASTProperty.class.isInstance(this._children[1])
					&& ((NodeType) this._children[1]).getGetterClass() != null) {
				value = "new " + ((NodeType) this._children[1]).getGetterClass().getName() + "(" + value + ")";
			}

			return result + value + ")";
		}
	}

	public boolean isOperation(OgnlContext context) {
		return true;
	}
}
