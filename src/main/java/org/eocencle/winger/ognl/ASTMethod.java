package org.eocencle.winger.ognl;

import java.lang.reflect.Method;
import java.util.List;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTMethod extends SimpleNode implements OrderedReturn, NodeType {
	private String _methodName;
	private String _lastExpression;
	private String _coreExpression;
	private Class _getterClass;

	public ASTMethod(int id) {
		super(id);
	}

	public ASTMethod(OgnlParser p, int id) {
		super(p, id);
	}

	public void setMethodName(String methodName) {
		this._methodName = methodName;
	}

	public String getMethodName() {
		return this._methodName;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object[] args = OgnlRuntime.getObjectArrayPool().create(this.jjtGetNumChildren());

		try {
			Object root = context.getRoot();
			int nh = 0;

			for (int icount = args.length; nh < icount; ++nh) {
				args[nh] = this._children[nh].getValue(context, root);
			}

			Object result = OgnlRuntime.callMethod(context, source, this._methodName, args);
			if (result == null) {
				NullHandler arg10 = OgnlRuntime.getNullHandler(OgnlRuntime.getTargetClass(source));
				result = arg10.nullMethodResult(context, source, this._methodName, args);
			}

			Object arg11 = result;
			return arg11;
		} finally {
			OgnlRuntime.getObjectArrayPool().recycle(args);
		}
	}

	public String getLastExpression() {
		return this._lastExpression;
	}

	public String getCoreExpression() {
		return this._coreExpression;
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return this._getterClass;
	}

	public String toString() {
		String result = this._methodName;
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
		if (target == null) {
			throw new UnsupportedCompilationException("Target object is null.");
		} else {
			String post = "";
			String result = null;
			Method m = null;

			try {
				m = OgnlRuntime.getMethod(context,
						context.getCurrentType() != null ? context.getCurrentType() : target.getClass(),
						this._methodName, this._children, false);
				Class[] t = getChildrenClasses(context, this._children);
				if (m == null) {
					m = OgnlRuntime.getReadMethod(target.getClass(), this._methodName, t);
				}

				if (m == null) {
					m = OgnlRuntime.getWriteMethod(target.getClass(), this._methodName, t);
					if (m != null) {
						context.setCurrentType(m.getReturnType());
						context.setCurrentAccessor(
								OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
						this._coreExpression = this.toSetSourceString(context, target);
						if (this._coreExpression != null && this._coreExpression.length() >= 1) {
							this._coreExpression = this._coreExpression + ";";
							this._lastExpression = "null";
							return this._coreExpression;
						}

						throw new UnsupportedCompilationException("can\'t find suitable getter method");
					}

					return "";
				}

				this._getterClass = m.getReturnType();
				boolean varArgs = OgnlRuntime.isJdk15() && m.isVarArgs();
				if (varArgs) {
					throw new UnsupportedCompilationException(
							"Javassist does not currently support varargs method calls");
				}

				result = "." + m.getName() + "(";
				if (this._children != null && this._children.length > 0) {
					Class[] parms = m.getParameterTypes();
					String prevCast = (String) context.remove("_preCast");

					for (int i = 0; i < this._children.length; ++i) {
						if (i > 0) {
							result = result + ", ";
						}

						Class prevType = context.getCurrentType();
						context.setCurrentObject(context.getRoot());
						context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
						context.setCurrentAccessor((Class) null);
						context.setPreviousType((Class) null);
						Object value = this._children[i].getValue(context, context.getRoot());
						String parmString = this._children[i].toGetSourceString(context, context.getRoot());
						if (parmString == null || parmString.trim().length() < 1) {
							parmString = "null";
						}

						if (ASTConst.class.isInstance(this._children[i])) {
							context.setCurrentType(prevType);
						}

						parmString = ExpressionCompiler.getRootExpression(this._children[i], context.getRoot(), context)
								+ parmString;
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

						if ((!varArgs || varArgs && i + 1 < parms.length) && valueClass != parms[i]) {
							if (parms[i].isArray()) {
								parmString = OgnlRuntime.getCompiler().createLocalReference(context,
										"(" + ExpressionCompiler.getCastString(parms[i]) + ")ognl.OgnlOps#toArray("
												+ parmString + ", " + parms[i].getComponentType().getName()
												+ ".class, true)",
										parms[i]);
							} else if (parms[i].isPrimitive()) {
								Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(parms[i]);
								parmString = OgnlRuntime.getCompiler().createLocalReference(context,
										"((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue(" + parmString + ","
												+ wrapClass.getName() + ".class, true))."
												+ OgnlRuntime.getNumericValueGetter(wrapClass),
										parms[i]);
							} else if (parms[i] != Object.class) {
								parmString = OgnlRuntime.getCompiler().createLocalReference(context,
										"(" + parms[i].getName() + ")ognl.OgnlOps#convertValue(" + parmString + ","
												+ parms[i].getName() + ".class)",
										parms[i]);
							} else if ((!NodeType.class.isInstance(this._children[i])
									|| ((NodeType) this._children[i]).getGetterClass() == null
									|| !Number.class.isAssignableFrom(((NodeType) this._children[i]).getGetterClass()))
									&& (valueClass == null || !valueClass.isPrimitive())) {
								if (valueClass != null && valueClass.isPrimitive()) {
									parmString = "($w) " + parmString;
								}
							} else {
								parmString = " ($w) " + parmString;
							}
						}

						result = result + parmString;
					}

					if (prevCast != null) {
						context.put("_preCast", prevCast);
					}
				}
			} catch (Throwable arg17) {
				throw OgnlOps.castToRuntime(arg17);
			}

			try {
				Object arg18 = this.getValueBody(context, target);
				context.setCurrentObject(arg18);
			} catch (Throwable arg16) {
				throw OgnlOps.castToRuntime(arg16);
			}

			result = result + ")" + post;
			if (m.getReturnType() == Void.TYPE) {
				this._coreExpression = result + ";";
				this._lastExpression = "null";
			}

			context.setCurrentType(m.getReturnType());
			context.setCurrentAccessor(OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
			return result;
		}
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		Method m = OgnlRuntime.getWriteMethod(
				context.getCurrentType() != null ? context.getCurrentType() : target.getClass(), this._methodName,
				getChildrenClasses(context, this._children));
		if (m == null) {
			throw new UnsupportedCompilationException(
					"Unable to determine setter method generation for " + this._methodName);
		} else {
			String post = "";
			String result = "." + m.getName() + "(";
			if (m.getReturnType() != Void.TYPE && m.getReturnType().isPrimitive()
					&& (this._parent == null || !ASTTest.class.isInstance(this._parent))) {
				Class varArgs = OgnlRuntime.getPrimitiveWrapperClass(m.getReturnType());
				ExpressionCompiler.addCastString(context, "new " + varArgs.getName() + "(");
				post = ")";
				this._getterClass = varArgs;
			}

			boolean arg17 = OgnlRuntime.isJdk15() && m.isVarArgs();
			if (arg17) {
				throw new UnsupportedCompilationException("Javassist does not currently support varargs method calls");
			} else {
				try {
					if (this._children != null && this._children.length > 0) {
						Class[] contextObj = m.getParameterTypes();
						String prevCast = (String) context.remove("_preCast");
						int i = 0;

						while (true) {
							if (i >= this._children.length) {
								if (prevCast != null) {
									context.put("_preCast", prevCast);
								}
								break;
							}

							if (i > 0) {
								result = result + ", ";
							}

							Class prevType = context.getCurrentType();
							context.setCurrentObject(context.getRoot());
							context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
							context.setCurrentAccessor((Class) null);
							context.setPreviousType((Class) null);
							Object value = this._children[i].getValue(context, context.getRoot());
							String parmString = this._children[i].toSetSourceString(context, context.getRoot());
							if (context.getCurrentType() == Void.TYPE || context.getCurrentType() == Void.TYPE) {
								throw new UnsupportedCompilationException("Method argument can\'t be a void type.");
							}

							if (parmString == null || parmString.trim().length() < 1) {
								if (ASTProperty.class.isInstance(this._children[i])
										|| ASTMethod.class.isInstance(this._children[i])
										|| ASTStaticMethod.class.isInstance(this._children[i])
										|| ASTChain.class.isInstance(this._children[i])) {
									throw new UnsupportedCompilationException(
											"ASTMethod setter child returned null from a sub property expression.");
								}

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

							parmString = cast + parmString;
							Class valueClass = value != null ? value.getClass() : null;
							if (NodeType.class.isAssignableFrom(this._children[i].getClass())) {
								valueClass = ((NodeType) this._children[i]).getGetterClass();
							}

							if (valueClass != contextObj[i]) {
								if (contextObj[i].isArray()) {
									parmString = OgnlRuntime.getCompiler().createLocalReference(context,
											"(" + ExpressionCompiler.getCastString(contextObj[i])
													+ ")ognl.OgnlOps#toArray(" + parmString + ", "
													+ contextObj[i].getComponentType().getName() + ".class)",
											contextObj[i]);
								} else if (contextObj[i].isPrimitive()) {
									Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(contextObj[i]);
									parmString = OgnlRuntime.getCompiler().createLocalReference(context,
											"((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue(" + parmString
													+ "," + wrapClass.getName() + ".class, true))."
													+ OgnlRuntime.getNumericValueGetter(wrapClass),
											contextObj[i]);
								} else if (contextObj[i] != Object.class) {
									parmString = OgnlRuntime.getCompiler().createLocalReference(
											context, "(" + contextObj[i].getName() + ")ognl.OgnlOps#convertValue("
													+ parmString + "," + contextObj[i].getName() + ".class)",
											contextObj[i]);
								} else if (NodeType.class.isInstance(this._children[i])
										&& ((NodeType) this._children[i]).getGetterClass() != null
										&& Number.class
												.isAssignableFrom(((NodeType) this._children[i]).getGetterClass())
										|| valueClass != null && valueClass.isPrimitive()) {
									parmString = " ($w) " + parmString;
								} else if (valueClass != null && valueClass.isPrimitive()) {
									parmString = "($w) " + parmString;
								}
							}

							result = result + parmString;
							++i;
						}
					}
				} catch (Throwable arg16) {
					throw OgnlOps.castToRuntime(arg16);
				}

				try {
					Object arg18 = this.getValueBody(context, target);
					context.setCurrentObject(arg18);
				} catch (Throwable arg15) {
					;
				}

				context.setCurrentType(m.getReturnType());
				context.setCurrentAccessor(
						OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
				return result + ")" + post;
			}
		}
	}

	private static Class getClassMatchingAllChildren(OgnlContext context, Node[] _children) {
		Class[] cc = getChildrenClasses(context, _children);
		Class componentType = null;

		for (int j = 0; j < cc.length; ++j) {
			Class ic = cc[j];
			if (ic == null) {
				componentType = Object.class;
				break;
			}

			if (componentType == null) {
				componentType = ic;
			} else if (!componentType.isAssignableFrom(ic)) {
				if (ic.isAssignableFrom(componentType)) {
					componentType = ic;
				} else {
					Class pc;
					while ((pc = componentType.getSuperclass()) != null) {
						if (pc.isAssignableFrom(ic)) {
							componentType = pc;
							break;
						}
					}

					if (!componentType.isAssignableFrom(ic)) {
						componentType = Object.class;
						break;
					}
				}
			}
		}

		if (componentType == null) {
			componentType = Object.class;
		}

		return componentType;
	}

	private static Class[] getChildrenClasses(OgnlContext context, Node[] _children) {
		if (_children == null) {
			return null;
		} else {
			Class[] argumentClasses = new Class[_children.length];

			for (int i = 0; i < _children.length; ++i) {
				Node child = _children[i];
				if (child instanceof ASTList) {
					argumentClasses[i] = List.class;
				} else if (child instanceof NodeType) {
					argumentClasses[i] = ((NodeType) child).getGetterClass();
				} else if (child instanceof ASTCtor) {
					try {
						argumentClasses[i] = ((ASTCtor) child).getCreatedClass(context);
					} catch (ClassNotFoundException arg5) {
						throw OgnlOps.castToRuntime(arg5);
					}
				} else {
					if (!(child instanceof ASTTest)) {
						throw new UnsupportedOperationException("Don\'t know how to handle child: " + child);
					}

					argumentClasses[i] = getClassMatchingAllChildren(context, ((ASTTest) child)._children);
				}
			}

			return argumentClasses;
		}
	}

	public boolean isSimpleMethod(OgnlContext context) {
		return true;
	}
}
