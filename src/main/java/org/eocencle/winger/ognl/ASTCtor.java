package org.eocencle.winger.ognl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.List;

public class ASTCtor extends SimpleNode {
	private String className;
	private boolean isArray;

	public ASTCtor(int id) {
		super(id);
	}

	public ASTCtor(OgnlParser p, int id) {
		super(p, id);
	}

	void setClassName(String className) {
		this.className = className;
	}

	Class getCreatedClass(OgnlContext context) throws ClassNotFoundException {
		return OgnlRuntime.classForName(context, this.className);
	}

	void setArray(boolean value) {
		this.isArray = value;
	}

	public boolean isArray() {
		return this.isArray;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object root = context.getRoot();
		int count = this.jjtGetNumChildren();
		Object[] args = OgnlRuntime.getObjectArrayPool().create(count);

		Object arg19;
		try {
			for (int ex = 0; ex < count; ++ex) {
				args[ex] = this._children[ex].getValue(context, root);
			}

			Object result;
			if (!this.isArray) {
				result = OgnlRuntime.callConstructor(context, this.className, args);
			} else {
				label126: {
					if (args.length == 1) {
						try {
							Class arg18 = OgnlRuntime.classForName(context, this.className);
							List sourceList = null;
							int size;
							if (args[0] instanceof List) {
								sourceList = (List) args[0];
								size = sourceList.size();
							} else {
								size = (int) OgnlOps.longValue(args[0]);
							}

							result = Array.newInstance(arg18, size);
							if (sourceList == null) {
								break label126;
							}

							TypeConverter converter = context.getTypeConverter();
							int i = 0;
							int icount = sourceList.size();

							while (true) {
								if (i >= icount) {
									break label126;
								}

								Object o = sourceList.get(i);
								if (o != null && !arg18.isInstance(o)) {
									Array.set(result, i, converter.convertValue(context, (Object) null, (Member) null,
											(String) null, o, arg18));
								} else {
									Array.set(result, i, o);
								}

								++i;
							}
						} catch (ClassNotFoundException arg16) {
							throw new OgnlException("array component class \'" + this.className + "\' not found",
									arg16);
						}
					}

					throw new OgnlException("only expect array size or fixed initializer list");
				}
			}

			arg19 = result;
		} finally {
			OgnlRuntime.getObjectArrayPool().recycle(args);
		}

		return arg19;
	}

	public String toString() {
		String result = "new " + this.className;
		if (this.isArray) {
			if (this._children[0] instanceof ASTConst) {
				result = result + "[" + this._children[0] + "]";
			} else {
				result = result + "[] " + this._children[0];
			}
		} else {
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
		}

		return result;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String result = "new " + this.className;
		Class clazz = null;
		Object ctorValue = null;

		try {
			clazz = OgnlRuntime.classForName(context, this.className);
			ctorValue = this.getValueBody(context, target);
			context.setCurrentObject(ctorValue);
			if (clazz != null && ctorValue != null) {
				context.setCurrentType(ctorValue.getClass());
				context.setCurrentAccessor(ctorValue.getClass());
			}

			if (this.isArray) {
				context.put("_ctorClass", clazz);
			}
		} catch (Throwable arg15) {
			throw OgnlOps.castToRuntime(arg15);
		}

		try {
			if (this.isArray) {
				if (this._children[0] instanceof ASTConst) {
					result = result + "[" + this._children[0].toGetSourceString(context, target) + "]";
				} else if (ASTProperty.class.isInstance(this._children[0])) {
					result = result + "[" + ExpressionCompiler.getRootExpression(this._children[0], target, context)
							+ this._children[0].toGetSourceString(context, target) + "]";
				} else if (ASTChain.class.isInstance(this._children[0])) {
					result = result + "[" + this._children[0].toGetSourceString(context, target) + "]";
				} else {
					result = result + "[] " + this._children[0].toGetSourceString(context, target);
				}
			} else {
				result = result + "(";
				if (this._children != null && this._children.length > 0) {
					Object[] t = new Object[this._children.length];
					String[] expressions = new String[this._children.length];
					Class[] types = new Class[this._children.length];

					for (int cons = 0; cons < this._children.length; ++cons) {
						Object ctor = this._children[cons].getValue(context, context.getRoot());
						String ctorParamTypes = this._children[cons].toGetSourceString(context, target);
						if (!ASTRootVarRef.class.isInstance(this._children[cons])) {
							ctorParamTypes = ExpressionCompiler.getRootExpression(this._children[cons], target, context)
									+ ctorParamTypes;
						}

						String i = "";
						if (ExpressionCompiler.shouldCast(this._children[cons])) {
							i = (String) context.remove("_preCast");
						}

						if (i == null) {
							i = "";
						}

						if (!ASTConst.class.isInstance(this._children[cons])) {
							ctorParamTypes = i + ctorParamTypes;
						}

						t[cons] = ctor;
						expressions[cons] = ctorParamTypes;
						types[cons] = context.getCurrentType();
					}

					Constructor[] arg16 = clazz.getConstructors();
					Constructor arg17 = null;
					Class[] arg18 = null;

					int arg20;
					for (arg20 = 0; arg20 < arg16.length; ++arg20) {
						Class[] value = arg16[arg20].getParameterTypes();
						if (OgnlRuntime.areArgsCompatible(t, value)
								&& (arg17 == null || OgnlRuntime.isMoreSpecific(value, arg18))) {
							arg17 = arg16[arg20];
							arg18 = value;
						}
					}

					if (arg17 == null) {
						arg17 = OgnlRuntime.getConvertedConstructorAndArgs(context, clazz,
								OgnlRuntime.getConstructors(clazz), t, new Object[t.length]);
					}

					if (arg17 == null) {
						throw new NoSuchMethodException(
								"Unable to find constructor appropriate for arguments in class: " + clazz);
					}

					arg18 = arg17.getParameterTypes();

					for (arg20 = 0; arg20 < this._children.length; ++arg20) {
						if (arg20 > 0) {
							result = result + ", ";
						}

						String arg19 = expressions[arg20];
						if (types[arg20].isPrimitive()) {
							String literal = OgnlRuntime.getNumericLiteral(types[arg20]);
							if (literal != null) {
								arg19 = arg19 + literal;
							}
						}

						if (arg18[arg20] != types[arg20]) {
							if (t[arg20] != null && !types[arg20].isPrimitive() && !t[arg20].getClass().isArray()
									&& !ASTConst.class.isInstance(this._children[arg20])) {
								arg19 = "(" + OgnlRuntime.getCompiler().getInterfaceClass(t[arg20].getClass()).getName()
										+ ")" + arg19;
							} else if (!ASTConst.class.isInstance(this._children[arg20])
									|| ASTConst.class.isInstance(this._children[arg20])
											&& !types[arg20].isPrimitive()) {
								if (!types[arg20].isArray() && types[arg20].isPrimitive()
										&& !arg18[arg20].isPrimitive()) {
									arg19 = "new "
											+ ExpressionCompiler
													.getCastString(OgnlRuntime.getPrimitiveWrapperClass(types[arg20]))
											+ "(" + arg19 + ")";
								} else {
									arg19 = " ($w) " + arg19;
								}
							}
						}

						result = result + arg19;
					}
				}

				result = result + ")";
			}

			context.setCurrentType(ctorValue != null ? ctorValue.getClass() : clazz);
			context.setCurrentAccessor(clazz);
			context.setCurrentObject(ctorValue);
		} catch (Throwable arg14) {
			throw OgnlOps.castToRuntime(arg14);
		}

		context.remove("_ctorClass");
		return result;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		return "";
	}
}
