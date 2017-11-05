package org.eocencle.winger.ognl;

import java.lang.reflect.Array;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTChain extends SimpleNode implements NodeType, OrderedReturn {
	private Class _getterClass;
	private Class _setterClass;
	private String _lastExpression;
	private String _coreExpression;

	public ASTChain(int id) {
		super(id);
	}

	public ASTChain(OgnlParser p, int id) {
		super(p, id);
	}

	public String getLastExpression() {
		return this._lastExpression;
	}

	public String getCoreExpression() {
		return this._coreExpression;
	}

	public void jjtClose() {
		this.flattenTree();
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		Object result = source;
		int i = 0;

		for (int ilast = this._children.length - 1; i <= ilast; ++i) {
			boolean handled = false;
			if (i < ilast && this._children[i] instanceof ASTProperty) {
				ASTProperty propertyNode = (ASTProperty) this._children[i];
				int indexType = propertyNode.getIndexedPropertyType(context, result);
				if (indexType != OgnlRuntime.INDEXED_PROPERTY_NONE && this._children[i + 1] instanceof ASTProperty) {
					ASTProperty indexNode = (ASTProperty) this._children[i + 1];
					if (indexNode.isIndexedAccess()) {
						Object index = indexNode.getProperty(context, result);
						if (index instanceof DynamicSubscript) {
							if (indexType == OgnlRuntime.INDEXED_PROPERTY_INT) {
								Object array = propertyNode.getValue(context, result);
								int len = Array.getLength(array);
								switch (((DynamicSubscript) index).getFlag()) {
								case 0:
									index = new Integer(len > 0 ? 0 : -1);
									break;
								case 1:
									index = new Integer(len > 0 ? len / 2 : -1);
									break;
								case 2:
									index = new Integer(len > 0 ? len - 1 : -1);
									break;
								case 3:
									result = Array.newInstance(array.getClass().getComponentType(), len);
									System.arraycopy(array, 0, result, 0, len);
									handled = true;
									++i;
								}
							} else if (indexType == OgnlRuntime.INDEXED_PROPERTY_OBJECT) {
								throw new OgnlException("DynamicSubscript \'" + indexNode
										+ "\' not allowed for object indexed property \'" + propertyNode + "\'");
							}
						}

						if (!handled) {
							result = OgnlRuntime.getIndexedProperty(context, result,
									propertyNode.getProperty(context, result).toString(), index);
							handled = true;
							++i;
						}
					}
				}
			}

			if (!handled) {
				result = this._children[i].getValue(context, result);
			}
		}

		return result;
	}

	protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
		boolean handled = false;
		int i = 0;

		for (int ilast = this._children.length - 2; i <= ilast; ++i) {
			if (i <= ilast && this._children[i] instanceof ASTProperty) {
				ASTProperty propertyNode = (ASTProperty) this._children[i];
				int indexType = propertyNode.getIndexedPropertyType(context, target);
				if (indexType != OgnlRuntime.INDEXED_PROPERTY_NONE && this._children[i + 1] instanceof ASTProperty) {
					ASTProperty indexNode = (ASTProperty) this._children[i + 1];
					if (indexNode.isIndexedAccess()) {
						Object index = indexNode.getProperty(context, target);
						if (index instanceof DynamicSubscript) {
							if (indexType == OgnlRuntime.INDEXED_PROPERTY_INT) {
								Object array = propertyNode.getValue(context, target);
								int len = Array.getLength(array);
								switch (((DynamicSubscript) index).getFlag()) {
								case 0:
									index = new Integer(len > 0 ? 0 : -1);
									break;
								case 1:
									index = new Integer(len > 0 ? len / 2 : -1);
									break;
								case 2:
									index = new Integer(len > 0 ? len - 1 : -1);
									break;
								case 3:
									System.arraycopy(target, 0, value, 0, len);
									handled = true;
									++i;
								}
							} else if (indexType == OgnlRuntime.INDEXED_PROPERTY_OBJECT) {
								throw new OgnlException("DynamicSubscript \'" + indexNode
										+ "\' not allowed for object indexed property \'" + propertyNode + "\'");
							}
						}

						if (!handled && i == ilast) {
							OgnlRuntime.setIndexedProperty(context, target,
									propertyNode.getProperty(context, target).toString(), index, value);
							handled = true;
							++i;
						} else if (!handled) {
							target = OgnlRuntime.getIndexedProperty(context, target,
									propertyNode.getProperty(context, target).toString(), index);
							++i;
							continue;
						}
					}
				}
			}

			if (!handled) {
				target = this._children[i].getValue(context, target);
			}
		}

		if (!handled) {
			this._children[this._children.length - 1].setValue(context, target, value);
		}

	}

	public boolean isSimpleNavigationChain(OgnlContext context) throws OgnlException {
		boolean result = false;
		if (this._children != null && this._children.length > 0) {
			result = true;

			for (int i = 0; result && i < this._children.length; ++i) {
				if (this._children[i] instanceof SimpleNode) {
					result = ((SimpleNode) this._children[i]).isSimpleProperty(context);
				} else {
					result = false;
				}
			}
		}

		return result;
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return this._setterClass;
	}

	public String toString() {
		String result = "";
		if (this._children != null && this._children.length > 0) {
			for (int i = 0; i < this._children.length; ++i) {
				if (i > 0 && (!(this._children[i] instanceof ASTProperty)
						|| !((ASTProperty) this._children[i]).isIndexedAccess())) {
					result = result + ".";
				}

				result = result + this._children[i].toString();
			}
		}

		return result;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		String prevChain = (String) context.get("_currentChain");
		if (target != null) {
			context.setCurrentObject(target);
			context.setCurrentType(target.getClass());
		}

		String result = "";
		NodeType _lastType = null;
		boolean ordered = false;
		boolean constructor = false;

		try {
			if (this._children != null && this._children.length > 0) {
				for (int t = 0; t < this._children.length; ++t) {
					String value = this._children[t].toGetSourceString(context, context.getCurrentObject());
					if (ASTCtor.class.isInstance(this._children[t])) {
						constructor = true;
					}

					if (NodeType.class.isInstance(this._children[t])
							&& ((NodeType) this._children[t]).getGetterClass() != null) {
						_lastType = (NodeType) this._children[t];
					}

					if (!ASTVarRef.class.isInstance(this._children[t]) && !constructor
							&& (!OrderedReturn.class.isInstance(this._children[t])
									|| ((OrderedReturn) this._children[t]).getLastExpression() == null)
							&& (this._parent == null || !ASTSequence.class.isInstance(this._parent))) {
						value = OgnlRuntime.getCompiler().castExpression(context, this._children[t], value);
					}

					if (OrderedReturn.class.isInstance(this._children[t])
							&& ((OrderedReturn) this._children[t]).getLastExpression() != null) {
						ordered = true;
						OrderedReturn or = (OrderedReturn) this._children[t];
						if (or.getCoreExpression() != null && or.getCoreExpression().trim().length() > 0) {
							result = result + or.getCoreExpression();
						} else {
							result = "";
						}

						this._lastExpression = or.getLastExpression();
						if (context.get("_preCast") != null) {
							this._lastExpression = context.remove("_preCast") + this._lastExpression;
						}
					} else if (!ASTOr.class.isInstance(this._children[t]) && !ASTAnd.class.isInstance(this._children[t])
							&& !ASTCtor.class.isInstance(this._children[t])
							&& (!ASTStaticField.class.isInstance(this._children[t]) || this._parent != null)) {
						result = result + value;
					} else {
						context.put("_noRoot", "true");
						result = value;
					}

					context.put("_currentChain", result);
				}
			}
		} catch (Throwable arg10) {
			throw OgnlOps.castToRuntime(arg10);
		}

		if (_lastType != null) {
			this._getterClass = _lastType.getGetterClass();
			this._setterClass = _lastType.getSetterClass();
		}

		if (ordered) {
			this._coreExpression = result;
		}

		context.put("_currentChain", prevChain);
		return result;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		String prevChain = (String) context.get("_currentChain");
		String prevChild = (String) context.get("_lastChild");
		if (prevChain != null) {
			throw new UnsupportedCompilationException("Can\'t compile nested chain expressions.");
		} else {
			if (target != null) {
				context.setCurrentObject(target);
				context.setCurrentType(target.getClass());
			}

			String result = "";
			NodeType _lastType = null;
			boolean constructor = false;

			try {
				if (this._children != null && this._children.length > 0) {
					if (ASTConst.class.isInstance(this._children[0])) {
						throw new UnsupportedCompilationException("Can\'t modify constant values.");
					}

					for (int t = 0; t < this._children.length; ++t) {
						if (t == this._children.length - 1) {
							context.put("_lastChild", "true");
						}

						String value = this._children[t].toSetSourceString(context, context.getCurrentObject());
						if (ASTCtor.class.isInstance(this._children[t])) {
							constructor = true;
						}

						if (NodeType.class.isInstance(this._children[t])
								&& ((NodeType) this._children[t]).getGetterClass() != null) {
							_lastType = (NodeType) this._children[t];
						}

						if (!ASTVarRef.class.isInstance(this._children[t]) && !constructor
								&& (!OrderedReturn.class.isInstance(this._children[t])
										|| ((OrderedReturn) this._children[t]).getLastExpression() == null)
								&& (this._parent == null || !ASTSequence.class.isInstance(this._parent))) {
							value = OgnlRuntime.getCompiler().castExpression(context, this._children[t], value);
						}

						if (!ASTOr.class.isInstance(this._children[t]) && !ASTAnd.class.isInstance(this._children[t])
								&& !ASTCtor.class.isInstance(this._children[t])
								&& !ASTStaticField.class.isInstance(this._children[t])) {
							result = result + value;
						} else {
							context.put("_noRoot", "true");
							result = value;
						}

						context.put("_currentChain", result);
					}
				}
			} catch (Throwable arg9) {
				throw OgnlOps.castToRuntime(arg9);
			}

			context.put("_lastChild", prevChild);
			context.put("_currentChain", prevChain);
			if (_lastType != null) {
				this._setterClass = _lastType.getSetterClass();
			}

			return result;
		}
	}

	public boolean isChain(OgnlContext context) {
		return true;
	}
}
