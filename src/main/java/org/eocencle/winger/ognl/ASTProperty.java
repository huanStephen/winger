package org.eocencle.winger.ognl;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTProperty extends SimpleNode implements NodeType {
	private boolean _indexedAccess = false;
	private Class _getterClass;
	private Class _setterClass;

	public ASTProperty(int id) {
		super(id);
	}

	public void setIndexedAccess(boolean value) {
		this._indexedAccess = value;
	}

	public boolean isIndexedAccess() {
		return this._indexedAccess;
	}

	public int getIndexedPropertyType(OgnlContext context, Object source) throws OgnlException {
		Class type = context.getCurrentType();
		Class prevType = context.getPreviousType();

		int property1;
		try {
			if (!this.isIndexedAccess()) {
				Object property = this.getProperty(context, source);
				if (property instanceof String) {
					int arg5 = OgnlRuntime.getIndexedPropertyType(context,
							source == null ? null : OgnlRuntime.getCompiler().getInterfaceClass(source.getClass()),
							(String) property);
					return arg5;
				}
			}

			property1 = OgnlRuntime.INDEXED_PROPERTY_NONE;
		} finally {
			context.setCurrentObject(source);
			context.setCurrentType(type);
			context.setPreviousType(prevType);
		}

		return property1;
	}

	public Object getProperty(OgnlContext context, Object source) throws OgnlException {
		return this._children[0].getValue(context, context.getRoot());
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object property = this.getProperty(context, source);
		Object result = OgnlRuntime.getProperty(context, source, property);
		if (result == null) {
			result = OgnlRuntime.getNullHandler(OgnlRuntime.getTargetClass(source)).nullPropertyValue(context, source,
					property);
		}

		return result;
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		OgnlRuntime.setProperty(context, target, this.getProperty(context, target), value);
	}

	public boolean isNodeSimpleProperty(OgnlContext context) throws OgnlException {
		return this._children != null && this._children.length == 1
				&& ((SimpleNode) this._children[0]).isConstant(context);
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return this._setterClass;
	}

	public String toString() {
		String result;
		if (this.isIndexedAccess()) {
			result = "[" + this._children[0] + "]";
		} else {
			result = ((ASTConst) this._children[0]).getValue().toString();
		}

		return result;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		if (context.getCurrentObject() == null) {
			throw new UnsupportedCompilationException("Current target is null.");
		} else {
			String result = "";
			Method m = null;

			try {
				PropertyAccessor pa;
				Object currObj;
				Class currType;
				Class prevType;
				if (this.isIndexedAccess()) {
					Object t1 = this._children[0].getValue(context, context.getRoot());
					if (t1 != null && !DynamicSubscript.class.isAssignableFrom(t1.getClass())) {
						String pd1 = this._children[0].toGetSourceString(context, context.getRoot());
						pd1 = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + pd1;
						if (ASTChain.class.isInstance(this._children[0])) {
							String pa1 = (String) context.remove("_preCast");
							if (pa1 != null) {
								pd1 = pa1 + pd1;
							}
						}

						if (ASTConst.class.isInstance(this._children[0])
								&& String.class.isInstance(context.getCurrentObject())) {
							pd1 = "\"" + pd1 + "\"";
						}

						if (context.get("_indexedMethod") != null) {
							m = (Method) context.remove("_indexedMethod");
							this._getterClass = m.getReturnType();
							Object pa2 = OgnlRuntime.callMethod(context, target, m.getName(), new Object[] { t1 });
							context.setCurrentType(this._getterClass);
							context.setCurrentObject(pa2);
							context.setCurrentAccessor(
									OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
							return "." + m.getName() + "(" + pd1 + ")";
						}

						pa = OgnlRuntime.getPropertyAccessor(target.getClass());
						currObj = context.getCurrentObject();
						currType = context.getCurrentType();
						prevType = context.getPreviousType();
						Object srcString1 = pa.getProperty(context, target, t1);
						context.setCurrentObject(currObj);
						context.setCurrentType(currType);
						context.setPreviousType(prevType);
						if (ASTConst.class.isInstance(this._children[0])
								&& Number.class.isInstance(context.getCurrentObject())) {
							context.setCurrentType(
									OgnlRuntime.getPrimitiveWrapperClass(context.getCurrentObject().getClass()));
						}

						result = pa.getSourceAccessor(context, target, pd1);
						this._getterClass = context.getCurrentType();
						context.setCurrentObject(srcString1);
						return result;
					}

					throw new UnsupportedCompilationException(
							"Value passed as indexed property was null or not supported.");
				}

				String t = ((ASTConst) this._children[0]).getValue().toString();
				if (!Iterator.class.isAssignableFrom(context.getCurrentObject().getClass())
						|| Iterator.class.isAssignableFrom(context.getCurrentObject().getClass())
								&& t.indexOf("next") < 0) {
					try {
						target = this.getValue(context, context.getCurrentObject());
					} catch (NoSuchPropertyException arg17) {
						try {
							target = this.getValue(context, context.getRoot());
						} catch (NoSuchPropertyException arg16) {
							;
						}
					} finally {
						context.setCurrentObject(target);
					}
				}

				PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(context.getCurrentObject().getClass(), t);
				if (pd != null && pd.getReadMethod() != null && !context.getMemberAccess().isAccessible(context,
						context.getCurrentObject(), pd.getReadMethod(), t)) {
					throw new UnsupportedCompilationException("Member access forbidden for property " + t + " on class "
							+ context.getCurrentObject().getClass());
				}

				if (this.getIndexedPropertyType(context, context.getCurrentObject()) > 0 && pd != null) {
					if (pd instanceof IndexedPropertyDescriptor) {
						m = ((IndexedPropertyDescriptor) pd).getIndexedReadMethod();
					} else {
						if (!(pd instanceof ObjectIndexedPropertyDescriptor)) {
							throw new OgnlException("property \'" + t + "\' is not an indexed property");
						}

						m = ((ObjectIndexedPropertyDescriptor) pd).getIndexedReadMethod();
					}

					if (this._parent == null) {
						m = OgnlRuntime.getReadMethod(context.getCurrentObject().getClass(), t);
						result = m.getName() + "()";
						this._getterClass = m.getReturnType();
					} else {
						context.put("_indexedMethod", m);
					}
				} else {
					pa = OgnlRuntime.getPropertyAccessor(context.getCurrentObject().getClass());
					if (context.getCurrentObject().getClass().isArray()) {
						if (pd == null) {
							pd = OgnlRuntime.getProperty(context.getCurrentObject().getClass(), t);
							if (pd != null && pd.getReadMethod() != null) {
								m = pd.getReadMethod();
								result = pd.getName();
							} else {
								this._getterClass = Integer.TYPE;
								context.setCurrentAccessor(context.getCurrentObject().getClass());
								context.setCurrentType(Integer.TYPE);
								result = "." + t;
							}
						}
					} else if (pd != null && pd.getReadMethod() != null) {
						m = pd.getReadMethod();
						result = "." + m.getName() + "()";
					} else if (pa != null) {
						currObj = context.getCurrentObject();
						currType = context.getCurrentType();
						prevType = context.getPreviousType();
						String srcString = this._children[0].toGetSourceString(context, context.getRoot());
						if (ASTConst.class.isInstance(this._children[0])
								&& String.class.isInstance(context.getCurrentObject())) {
							srcString = "\"" + srcString + "\"";
						}

						context.setCurrentObject(currObj);
						context.setCurrentType(currType);
						context.setPreviousType(prevType);
						result = pa.getSourceAccessor(context, context.getCurrentObject(), srcString);
						this._getterClass = context.getCurrentType();
					}
				}
			} catch (Throwable arg19) {
				throw OgnlOps.castToRuntime(arg19);
			}

			if (m != null) {
				this._getterClass = m.getReturnType();
				context.setCurrentType(m.getReturnType());
				context.setCurrentAccessor(
						OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
			}

			context.setCurrentObject(target);
			return result;
		}
	}

	Method getIndexedWriteMethod(PropertyDescriptor pd) {
		return IndexedPropertyDescriptor.class.isInstance(pd) ? ((IndexedPropertyDescriptor) pd).getIndexedWriteMethod()
				: (ObjectIndexedPropertyDescriptor.class.isInstance(pd)
						? ((ObjectIndexedPropertyDescriptor) pd).getIndexedWriteMethod() : null);
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		String result = "";
		Method m = null;
		if (context.getCurrentObject() == null) {
			throw new UnsupportedCompilationException("Current target is null.");
		} else {
			try {
				Object currObj;
				PropertyAccessor pa1;
				if (this.isIndexedAccess()) {
					Object t1 = this._children[0].getValue(context, context.getRoot());
					if (t1 == null) {
						throw new UnsupportedCompilationException(
								"Value passed as indexed property is null, can\'t enhance statement to bytecode.");
					}

					String pd1 = this._children[0].toGetSourceString(context, context.getRoot());
					pd1 = ExpressionCompiler.getRootExpression(this._children[0], context.getRoot(), context) + pd1;
					if (ASTChain.class.isInstance(this._children[0])) {
						String pa5 = (String) context.remove("_preCast");
						if (pa5 != null) {
							pd1 = pa5 + pd1;
						}
					}

					if (ASTConst.class.isInstance(this._children[0])
							&& String.class.isInstance(context.getCurrentObject())) {
						pd1 = "\"" + pd1 + "\"";
					}

					if (context.get("_indexedMethod") != null) {
						m = (Method) context.remove("_indexedMethod");
						PropertyDescriptor pa6 = (PropertyDescriptor) context.remove("_indexedDescriptor");
						boolean currObj2 = this.lastChild(context);
						if (currObj2) {
							m = this.getIndexedWriteMethod(pa6);
							if (m == null) {
								throw new UnsupportedCompilationException(
										"Indexed property has no corresponding write method.");
							}
						}

						this._setterClass = m.getParameterTypes()[0];
						Object srcString2 = null;
						if (!currObj2) {
							srcString2 = OgnlRuntime.callMethod(context, target, m.getName(), new Object[] { t1 });
						}

						context.setCurrentType(this._setterClass);
						context.setCurrentAccessor(
								OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
						if (!currObj2) {
							context.setCurrentObject(srcString2);
							return "." + m.getName() + "(" + pd1 + ")";
						}

						return "." + m.getName() + "(" + pd1 + ", $3)";
					}

					pa1 = OgnlRuntime.getPropertyAccessor(target.getClass());
					currObj = context.getCurrentObject();
					Class srcString1 = context.getCurrentType();
					Class prevType = context.getPreviousType();
					Object indexVal = pa1.getProperty(context, target, t1);
					context.setCurrentObject(currObj);
					context.setCurrentType(srcString1);
					context.setPreviousType(prevType);
					if (ASTConst.class.isInstance(this._children[0])
							&& Number.class.isInstance(context.getCurrentObject())) {
						context.setCurrentType(
								OgnlRuntime.getPrimitiveWrapperClass(context.getCurrentObject().getClass()));
					}

					result = this.lastChild(context) ? pa1.getSourceSetter(context, target, pd1)
							: pa1.getSourceAccessor(context, target, pd1);
					this._getterClass = context.getCurrentType();
					context.setCurrentObject(indexVal);
					return result;
				}

				String t = ((ASTConst) this._children[0]).getValue().toString();
				if (!Iterator.class.isAssignableFrom(context.getCurrentObject().getClass())
						|| Iterator.class.isAssignableFrom(context.getCurrentObject().getClass())
								&& t.indexOf("next") < 0) {
					try {
						target = this.getValue(context, context.getCurrentObject());
					} catch (NoSuchPropertyException arg17) {
						try {
							target = this.getValue(context, context.getRoot());
						} catch (NoSuchPropertyException arg16) {
							;
						}
					} finally {
						context.setCurrentObject(target);
					}
				}

				PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(
						OgnlRuntime.getCompiler().getInterfaceClass(context.getCurrentObject().getClass()), t);
				if (pd != null) {
					Method pa = this.lastChild(context) ? pd.getWriteMethod() : pd.getReadMethod();
					if (pa != null
							&& !context.getMemberAccess().isAccessible(context, context.getCurrentObject(), pa, t)) {
						throw new UnsupportedCompilationException("Member access forbidden for property " + t
								+ " on class " + context.getCurrentObject().getClass());
					}
				}

				if (pd != null && this.getIndexedPropertyType(context, context.getCurrentObject()) > 0) {
					if (pd instanceof IndexedPropertyDescriptor) {
						IndexedPropertyDescriptor pa2 = (IndexedPropertyDescriptor) pd;
						m = this.lastChild(context) ? pa2.getIndexedWriteMethod() : pa2.getIndexedReadMethod();
					} else {
						if (!(pd instanceof ObjectIndexedPropertyDescriptor)) {
							throw new OgnlException("property \'" + t + "\' is not an indexed property");
						}

						ObjectIndexedPropertyDescriptor pa3 = (ObjectIndexedPropertyDescriptor) pd;
						m = this.lastChild(context) ? pa3.getIndexedWriteMethod() : pa3.getIndexedReadMethod();
					}

					if (this._parent == null) {
						m = OgnlRuntime.getWriteMethod(context.getCurrentObject().getClass(), t);
						Class pa4 = m.getParameterTypes()[0];
						String currObj1 = pa4.isArray() ? ExpressionCompiler.getCastString(pa4) : pa4.getName();
						result = m.getName() + "((" + currObj1 + ")$3)";
						this._setterClass = pa4;
					} else {
						context.put("_indexedMethod", m);
						context.put("_indexedDescriptor", pd);
					}
				} else {
					pa1 = OgnlRuntime.getPropertyAccessor(context.getCurrentObject().getClass());
					if (target != null) {
						this._setterClass = target.getClass();
					}

					if (this._parent != null && pd != null && pa1 == null) {
						m = pd.getReadMethod();
						result = m.getName() + "()";
					} else if (context.getCurrentObject().getClass().isArray()) {
						result = "";
					} else if (pa1 != null) {
						currObj = context.getCurrentObject();
						String srcString = this._children[0].toGetSourceString(context, context.getRoot());
						if (ASTConst.class.isInstance(this._children[0])
								&& String.class.isInstance(context.getCurrentObject())) {
							srcString = "\"" + srcString + "\"";
						}

						context.setCurrentObject(currObj);
						if (!this.lastChild(context)) {
							result = pa1.getSourceAccessor(context, context.getCurrentObject(), srcString);
						} else {
							result = pa1.getSourceSetter(context, context.getCurrentObject(), srcString);
						}

						this._getterClass = context.getCurrentType();
					}
				}
			} catch (Throwable arg19) {
				throw OgnlOps.castToRuntime(arg19);
			}

			context.setCurrentObject(target);
			if (m != null) {
				context.setCurrentType(m.getReturnType());
				context.setCurrentAccessor(
						OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, m.getDeclaringClass()));
			}

			return result;
		}
	}
}
