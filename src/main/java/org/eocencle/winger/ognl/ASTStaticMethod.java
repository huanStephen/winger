package org.eocencle.winger.ognl;

import java.lang.reflect.Method;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTStaticMethod extends SimpleNode implements NodeType {
	private String _className;
	private String _methodName;
	private Class _getterClass;

	public ASTStaticMethod(int id) {
		super(id);
	}

	public ASTStaticMethod(OgnlParser p, int id) {
		super(p, id);
	}

	void init(String className, String methodName) {
		this._className = className;
		this._methodName = methodName;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object[] args = OgnlRuntime.getObjectArrayPool().create(this.jjtGetNumChildren());
		Object root = context.getRoot();

		try {
			int i = 0;

			for (int icount = args.length; i < icount; ++i) {
				args[i] = this._children[i].getValue(context, root);
			}

			Object arg9 = OgnlRuntime.callStaticMethod(context, this._className, this._methodName, args);
			return arg9;
		} finally {
			OgnlRuntime.getObjectArrayPool().recycle(args);
		}
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return this._getterClass;
	}

	public String toString() {
		String result = "@" + this._className + "@" + this._methodName;
		result = result + "(";
		if (this._children != null && this._children.length > 0) {
			for (int i = 0; i < this._children.length; ++i) {
				if (i > 0) {
					result = result + ", ";
				}

				result = result + this._children[i];
			}
		}

		result = result + ")";
		return result;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = this._className + "#" + this._methodName + "(";

		try {
			Class t = OgnlRuntime.classForName(context, this._className);
			Method m = OgnlRuntime.getMethod(context, t, this._methodName, this._children, true);
			if (t != null && m != null) {
				if (!context.getMemberAccess().isAccessible(context, t, m, this._methodName)) {
					throw new UnsupportedCompilationException(
							"Method is not accessible, check your jvm runtime security settings. For static class method "
									+ this._className + " / " + this._methodName);
				} else {
					if (this._children != null && this._children.length > 0) {
						Class[] contextObj = m.getParameterTypes();

						for (int i = 0; i < this._children.length; ++i) {
							if (i > 0) {
								result = result + ", ";
							}

							Class prevType = context.getCurrentType();
							Object value = this._children[i].getValue(context, context.getRoot());
							String parmString = this._children[i].toGetSourceString(context, context.getRoot());
							if (parmString == null || parmString.trim().length() < 1) {
								parmString = "null";
							}

							if (ASTConst.class.isInstance(this._children[i])) {
								context.setCurrentType(prevType);
							}

							parmString = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(),
									context) + parmString;
							String cast = "";
							if (ExpressionCompiler.shouldCast(this._children[i])) {
								cast = (String) context.remove("_preCast");
							}

							if (cast == null) {
								cast = "";
							}

							if (!ASTConst.class.isInstance(this._children[i])) {
								parmString = cast + parmString;
							}

							Class valueClass = value != null ? value.getClass() : null;
							if (NodeType.class.isAssignableFrom(this._children[i].getClass())) {
								valueClass = ((NodeType) this._children[i]).getGetterClass();
							}

							if (valueClass != contextObj[i]) {
								if (contextObj[i].isArray()) {
									parmString = OgnlRuntime.getCompiler().createLocalReference(context,
											"(" + ExpressionCompiler.getCastString(contextObj[i])
													+ ")ognl.OgnlOps.toArray(" + parmString + ", "
													+ contextObj[i].getComponentType().getName() + ".class, true)",
											contextObj[i]);
								} else if (contextObj[i].isPrimitive()) {
									Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(contextObj[i]);
									parmString = OgnlRuntime.getCompiler().createLocalReference(context,
											"((" + wrapClass.getName() + ")ognl.OgnlOps.convertValue(" + parmString
													+ "," + wrapClass.getName() + ".class, true))."
													+ OgnlRuntime.getNumericValueGetter(wrapClass),
											contextObj[i]);
								} else if (contextObj[i] != Object.class) {
									parmString = OgnlRuntime.getCompiler().createLocalReference(
											context, "(" + contextObj[i].getName() + ")ognl.OgnlOps.convertValue("
													+ parmString + "," + contextObj[i].getName() + ".class)",
											contextObj[i]);
								} else if ((!NodeType.class.isInstance(this._children[i])
										|| ((NodeType) this._children[i]).getGetterClass() == null
										|| !Number.class
												.isAssignableFrom(((NodeType) this._children[i]).getGetterClass()))
										&& !valueClass.isPrimitive()) {
									if (valueClass.isPrimitive()) {
										parmString = "($w) " + parmString;
									}
								} else {
									parmString = " ($w) " + parmString;
								}
							}

							result = result + parmString;
						}
					}

					result = result + ")";

					try {
						Object arg15 = this.getValueBody(context, target);
						context.setCurrentObject(arg15);
					} catch (Throwable arg13) {
						;
					}

					if (m != null) {
						this._getterClass = m.getReturnType();
						context.setCurrentType(m.getReturnType());
						context.setCurrentAccessor(
								OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
					}

					return result;
				}
			} else {
				throw new UnsupportedCompilationException(
						"Unable to find class/method combo " + this._className + " / " + this._methodName);
			}
		} catch (Throwable arg14) {
			throw OgnlOps.castToRuntime(arg14);
		}
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		return this.toGetSourceString(context, target);
	}
}
