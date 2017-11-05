package org.eocencle.winger.ognl;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ASTAdd extends NumericExpression {
	public ASTAdd(int id) {
		super(id);
	}

	public ASTAdd(OgnlParser p, int id) {
		super(p, id);
	}

	public void jjtClose() {
		this.flattenTree();
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = this._children[0].getValue(context, source);

		for (int i = 1; i < this._children.length; ++i) {
			result = OgnlOps.add(result, this._children[i].getValue(context, source));
		}

		return result;
	}

	public String getExpressionOperator(int index) {
		return "+";
	}

	boolean isWider(NodeType type, NodeType lastType) {
		return lastType == null ? true
				: (String.class.isAssignableFrom(lastType.getGetterClass()) ? false
						: (String.class.isAssignableFrom(type.getGetterClass()) ? true
								: (this._parent != null && String.class.isAssignableFrom(type.getGetterClass()) ? true
										: (String.class.isAssignableFrom(lastType.getGetterClass())
												&& Object.class == type.getGetterClass()
														? false
														: (this._parent != null && String.class
																.isAssignableFrom(lastType.getGetterClass())
																		? false
																		: (this._parent == null
																				&& String.class.isAssignableFrom(
																						lastType.getGetterClass())
																								? true
																								: (this._parent == null
																										&& String.class
																												.isAssignableFrom(
																														type.getGetterClass())
																																? false
																																: (!BigDecimal.class
																																		.isAssignableFrom(
																																				type.getGetterClass())
																																		&& !BigInteger.class
																																				.isAssignableFrom(
																																						type.getGetterClass())
																																								? (!BigDecimal.class
																																										.isAssignableFrom(
																																												lastType.getGetterClass())
																																										&& !BigInteger.class
																																												.isAssignableFrom(
																																														lastType.getGetterClass())
																																																? (Double.class
																																																		.isAssignableFrom(
																																																				type.getGetterClass())
																																																						? true
																																																						: (Integer.class
																																																								.isAssignableFrom(
																																																										type.getGetterClass())
																																																								&& Double.class
																																																										.isAssignableFrom(
																																																												lastType.getGetterClass())
																																																														? false
																																																														: (Float.class
																																																																.isAssignableFrom(
																																																																		type.getGetterClass())
																																																																&& Integer.class
																																																																		.isAssignableFrom(
																																																																				lastType.getGetterClass())
																																																																						? true
																																																																						: true)))
																																																: false)
																																								: true))))))));
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		try {
			String t = "";
			NodeType lastType = null;
			if (this._children != null && this._children.length > 0) {
				Class t1 = context.getCurrentType();
				Class expr = context.getCurrentAccessor();
				Object rootExpr = context.get("_preCast");

				for (int cast = 0; cast < this._children.length; ++cast) {
					this._children[cast].toGetSourceString(context, target);
					if (NodeType.class.isInstance(this._children[cast])
							&& ((NodeType) this._children[cast]).getGetterClass() != null
							&& this.isWider((NodeType) this._children[cast], lastType)) {
						lastType = (NodeType) this._children[cast];
					}
				}

				context.put("_preCast", rootExpr);
				context.setCurrentType(t1);
				context.setCurrentAccessor(expr);
			}

			context.setCurrentObject(target);
			if (this._children != null && this._children.length > 0) {
				for (int arg10 = 0; arg10 < this._children.length; ++arg10) {
					if (arg10 > 0) {
						t = t + " " + this.getExpressionOperator(arg10) + " ";
					}

					String arg12 = this._children[arg10].toGetSourceString(context, target);
					if (arg12 != null && "null".equals(arg12) || !ASTConst.class.isInstance(this._children[arg10])
							&& (arg12 == null || arg12.trim().length() <= 0)) {
						arg12 = "null";
					}

					if (ASTProperty.class.isInstance(this._children[arg10])) {
						arg12 = ExpressionCompiler.getRootExpression(this._children[arg10], context.getRoot(), context)
								+ arg12;
						context.setCurrentAccessor(context.getRoot().getClass());
					} else {
						String arg13;
						String arg14;
						if (ASTMethod.class.isInstance(this._children[arg10])) {
							arg13 = (String) context.get("_currentChain");
							arg14 = ExpressionCompiler.getRootExpression(this._children[arg10], context.getRoot(),
									context);
							if (arg14.endsWith(".") && arg13 != null && arg13.startsWith(").")) {
								arg13 = arg13.substring(1, arg13.length());
							}

							arg12 = arg14 + (arg13 != null ? arg13 + "." : "") + arg12;
							context.setCurrentAccessor(context.getRoot().getClass());
						} else if (ExpressionNode.class.isInstance(this._children[arg10])) {
							arg12 = "(" + arg12 + ")";
						} else if ((this._parent == null || !ASTChain.class.isInstance(this._parent))
								&& ASTChain.class.isInstance(this._children[arg10])) {
							arg13 = ExpressionCompiler.getRootExpression(this._children[arg10], context.getRoot(),
									context);
							if (!ASTProperty.class.isInstance(this._children[arg10].jjtGetChild(0))
									&& arg13.endsWith(")") && arg12.startsWith(")")) {
								arg12 = arg12.substring(1, arg12.length());
							}

							arg12 = arg13 + arg12;
							context.setCurrentAccessor(context.getRoot().getClass());
							arg14 = (String) context.remove("_preCast");
							if (arg14 == null) {
								arg14 = "";
							}

							arg12 = arg14 + arg12;
						}
					}

					if (context.getCurrentType() != null && context.getCurrentType() == Character.class
							&& ASTConst.class.isInstance(this._children[arg10])) {
						if (arg12.indexOf(39) >= 0) {
							arg12 = arg12.replaceAll("\'", "\"");
						}

						context.setCurrentType(String.class);
					} else if (!ASTVarRef.class.isAssignableFrom(this._children[arg10].getClass())
							&& !ASTProperty.class.isInstance(this._children[arg10])
							&& !ASTMethod.class.isInstance(this._children[arg10])
							&& !ASTSequence.class.isInstance(this._children[arg10])
							&& !ASTChain.class.isInstance(this._children[arg10])
							&& !NumericExpression.class.isAssignableFrom(this._children[arg10].getClass())
							&& !ASTStaticField.class.isInstance(this._children[arg10])
							&& !ASTStaticMethod.class.isInstance(this._children[arg10])
							&& !ASTTest.class.isInstance(this._children[arg10]) && lastType != null
							&& String.class.isAssignableFrom(lastType.getGetterClass())) {
						if (arg12.indexOf("&quot;") >= 0) {
							arg12 = arg12.replaceAll("&quot;", "\"");
						}

						if (arg12.indexOf(34) >= 0) {
							arg12 = arg12.replaceAll("\"", "\'");
						}

						arg12 = "\"" + arg12 + "\"";
					}

					t = t + arg12;
					if ((lastType == null || !String.class.isAssignableFrom(lastType.getGetterClass()))
							&& !ASTConst.class.isAssignableFrom(this._children[arg10].getClass())
							&& !NumericExpression.class.isAssignableFrom(this._children[arg10].getClass())
							&& context.getCurrentType() != null
							&& Number.class.isAssignableFrom(context.getCurrentType())
							&& !ASTMethod.class.isInstance(this._children[arg10])) {
						if (ASTVarRef.class.isInstance(this._children[arg10])
								|| ASTProperty.class.isInstance(this._children[arg10])
								|| ASTChain.class.isInstance(this._children[arg10])) {
							t = t + ".";
						}

						t = t + OgnlRuntime.getNumericValueGetter(context.getCurrentType());
						context.setCurrentType(OgnlRuntime.getPrimitiveWrapperClass(context.getCurrentType()));
					}

					if (lastType != null) {
						context.setCurrentAccessor(lastType.getGetterClass());
					}
				}
			}

			if (this._parent != null && !ASTSequence.class.isAssignableFrom(this._parent.getClass())) {
				context.setCurrentType(this._getterClass);
			} else if (this._getterClass != null && String.class.isAssignableFrom(this._getterClass)) {
				this._getterClass = Object.class;
			}

			try {
				Object arg11 = this.getValueBody(context, target);
				context.setCurrentObject(arg11);
			} catch (Throwable arg8) {
				throw OgnlOps.castToRuntime(arg8);
			}

			return t;
		} catch (Throwable arg9) {
			throw OgnlOps.castToRuntime(arg9);
		}
	}
}
