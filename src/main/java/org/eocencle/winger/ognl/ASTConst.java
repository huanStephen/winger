package org.eocencle.winger.ognl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ASTConst extends SimpleNode implements NodeType {
	private Object value;
	private Class _getterClass;

	public ASTConst(int id) {
		super(id);
	}

	public ASTConst(OgnlParser p, int id) {
		super(p, id);
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return this.value;
	}

	public boolean isNodeConstant(OgnlContext context) throws OgnlException {
		return true;
	}

	public Class getGetterClass() {
		return this._getterClass == null ? null : this._getterClass;
	}

	public Class getSetterClass() {
		return null;
	}

	public String toString() {
		String result;
		if (this.value == null) {
			result = "null";
		} else if (this.value instanceof String) {
			result = '\"' + OgnlOps.getEscapeString(this.value.toString()) + '\"';
		} else if (this.value instanceof Character) {
			result = '\'' + OgnlOps.getEscapedChar(((Character) this.value).charValue()) + '\'';
		} else {
			result = this.value.toString();
			if (this.value instanceof Long) {
				result = result + "L";
			} else if (this.value instanceof BigDecimal) {
				result = result + "B";
			} else if (this.value instanceof BigInteger) {
				result = result + "H";
			} else if (this.value instanceof Node) {
				result = ":[ " + result + " ]";
			}
		}

		return result;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		if (this.value == null && this._parent != null && ExpressionNode.class.isInstance(this._parent)) {
			context.setCurrentType((Class) null);
			return "null";
		} else if (this.value == null) {
			context.setCurrentType((Class) null);
			return "";
		} else {
			this._getterClass = this.value.getClass();
			Object retval = this.value;
			if (this._parent != null && ASTProperty.class.isInstance(this._parent)) {
				context.setCurrentObject(this.value);
				return this.value.toString();
			} else if (this.value != null && Number.class.isAssignableFrom(this.value.getClass())) {
				context.setCurrentType(OgnlRuntime.getPrimitiveWrapperClass(this.value.getClass()));
				context.setCurrentObject(this.value);
				return this.value.toString();
			} else {
				String retval1;
				if ((this._parent == null || this.value == null
						|| !NumericExpression.class.isAssignableFrom(this._parent.getClass()))
						&& String.class.isAssignableFrom(this.value.getClass())) {
					context.setCurrentType(String.class);
					retval1 = '\"' + OgnlOps.getEscapeString(this.value.toString()) + '\"';
					context.setCurrentObject(retval1.toString());
					return retval1.toString();
				} else if (Character.class.isInstance(this.value)) {
					Character val = (Character) this.value;
					context.setCurrentType(Character.class);
					if (Character.isLetterOrDigit(val.charValue())) {
						retval1 = "\'" + ((Character) this.value).charValue() + "\'";
					} else {
						retval1 = "\'" + OgnlOps.getEscapedChar(((Character) this.value).charValue()) + "\'";
					}

					context.setCurrentObject(retval1);
					return retval1.toString();
				} else if (Boolean.class.isAssignableFrom(this.value.getClass())) {
					this._getterClass = Boolean.TYPE;
					context.setCurrentType(Boolean.TYPE);
					context.setCurrentObject(this.value);
					return this.value.toString();
				} else {
					return this.value.toString();
				}
			}
		}
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		if (this._parent == null) {
			throw new UnsupportedCompilationException("Can\'t modify constant values.");
		} else {
			return this.toGetSourceString(context, target);
		}
	}
}
