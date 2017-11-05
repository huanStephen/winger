package org.eocencle.winger.ognl;

import java.util.ArrayList;
import java.util.List;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTList extends SimpleNode implements NodeType {
	public ASTList(int id) {
		super(id);
	}

	public ASTList(OgnlParser p, int id) {
		super(p, id);
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		ArrayList answer = new ArrayList(this.jjtGetNumChildren());

		for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
			answer.add(this._children[i].getValue(context, source));
		}

		return answer;
	}

	public Class getGetterClass() {
		return null;
	}

	public Class getSetterClass() {
		return null;
	}

	public String toString() {
		String result = "{ ";

		for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
			if (i > 0) {
				result = result + ", ";
			}

			result = result + this._children[i].toString();
		}

		return result + " }";
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = "";
		boolean array = false;
		if (this._parent != null && ASTCtor.class.isInstance(this._parent) && ((ASTCtor) this._parent).isArray()) {
			array = true;
		}

		context.setCurrentType(List.class);
		context.setCurrentAccessor(List.class);
		if (!array) {
			if (this.jjtGetNumChildren() < 1) {
				return "java.util.Arrays.asList( new Object[0])";
			}

			result = result + "java.util.Arrays.asList( new Object[] ";
		}

		result = result + "{ ";

		try {
			for (int t = 0; t < this.jjtGetNumChildren(); ++t) {
				if (t > 0) {
					result = result + ", ";
				}

				Class prevType = context.getCurrentType();
				Object objValue = this._children[t].getValue(context, context.getRoot());
				String value = this._children[t].toGetSourceString(context, target);
				if (ASTConst.class.isInstance(this._children[t])) {
					context.setCurrentType(prevType);
				}

				value = ExpressionCompiler.getRootExpression(this._children[t], target, context) + value;
				String cast = "";
				if (ExpressionCompiler.shouldCast(this._children[t])) {
					cast = (String) context.remove("_preCast");
				}

				if (cast == null) {
					cast = "";
				}

				if (!ASTConst.class.isInstance(this._children[t])) {
					value = cast + value;
				}

				Class ctorClass = (Class) context.get("_ctorClass");
				if (array && ctorClass != null && !ctorClass.isPrimitive()) {
					Class valueClass = value != null ? value.getClass() : null;
					if (NodeType.class.isAssignableFrom(this._children[t].getClass())) {
						valueClass = ((NodeType) this._children[t]).getGetterClass();
					}

					if (valueClass != null && ctorClass.isArray()) {
						value = OgnlRuntime.getCompiler().createLocalReference(
								context, "(" + ExpressionCompiler.getCastString(ctorClass) + ")ognl.OgnlOps.toArray("
										+ value + ", " + ctorClass.getComponentType().getName() + ".class, true)",
								ctorClass);
					} else if (ctorClass.isPrimitive()) {
						Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(ctorClass);
						value = OgnlRuntime.getCompiler().createLocalReference(context,
								"((" + wrapClass.getName() + ")ognl.OgnlOps.convertValue(" + value + ","
										+ wrapClass.getName() + ".class, true))."
										+ OgnlRuntime.getNumericValueGetter(wrapClass),
								ctorClass);
					} else if (ctorClass != Object.class) {
						value = OgnlRuntime.getCompiler().createLocalReference(context, "(" + ctorClass.getName()
								+ ")ognl.OgnlOps.convertValue(" + value + "," + ctorClass.getName() + ".class)",
								ctorClass);
					} else if ((!NodeType.class.isInstance(this._children[t])
							|| ((NodeType) this._children[t]).getGetterClass() == null
							|| !Number.class.isAssignableFrom(((NodeType) this._children[t]).getGetterClass()))
							&& !valueClass.isPrimitive()) {
						if (valueClass.isPrimitive()) {
							value = "($w) (" + value + ")";
						}
					} else {
						value = " ($w) (" + value + ")";
					}
				} else if (ctorClass == null || !ctorClass.isPrimitive()) {
					value = " ($w) (" + value + ")";
				}

				if (objValue == null || value.length() <= 0) {
					value = "null";
				}

				result = result + value;
			}
		} catch (Throwable arg12) {
			throw OgnlOps.castToRuntime(arg12);
		}

		context.setCurrentType(List.class);
		context.setCurrentAccessor(List.class);
		result = result + "}";
		if (!array) {
			result = result + ")";
		}

		return result;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		throw new UnsupportedCompilationException("Can\'t generate setter for ASTList.");
	}
}
