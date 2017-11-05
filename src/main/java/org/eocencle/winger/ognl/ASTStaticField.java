package org.eocencle.winger.ognl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ASTStaticField extends SimpleNode implements NodeType {
	private String className;
	private String fieldName;
	private Class _getterClass;

	public ASTStaticField(int id) {
		super(id);
	}

	public ASTStaticField(OgnlParser p, int id) {
		super(p, id);
	}

	void init(String className, String fieldName) {
		this.className = className;
		this.fieldName = fieldName;
	}

	protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
		return OgnlRuntime.getStaticField(context, this.className, this.fieldName);
	}

	public boolean isNodeConstant(OgnlContext context) throws OgnlException {
		boolean result = false;
		Object reason = null;

		try {
			Class e = OgnlRuntime.classForName(context, this.className);
			if (this.fieldName.equals("class")) {
				result = true;
			} else if (OgnlRuntime.isJdk15() && e.isEnum()) {
				result = true;
			} else {
				Field f = e.getField(this.fieldName);
				if (!Modifier.isStatic(f.getModifiers())) {
					throw new OgnlException(
							"Field " + this.fieldName + " of class " + this.className + " is not static");
				}

				result = Modifier.isFinal(f.getModifiers());
			}
		} catch (ClassNotFoundException arg5) {
			reason = arg5;
		} catch (NoSuchFieldException arg6) {
			reason = arg6;
		} catch (SecurityException arg7) {
			reason = arg7;
		}

		if (reason != null) {
			throw new OgnlException("Could not get static field " + this.fieldName + " from class " + this.className,
					(Throwable) reason);
		} else {
			return result;
		}
	}

	Class getFieldClass(OgnlContext context) throws OgnlException {
		Object reason = null;

		try {
			Class e = OgnlRuntime.classForName(context, this.className);
			if (this.fieldName.equals("class")) {
				return e;
			}

			if (OgnlRuntime.isJdk15() && e.isEnum()) {
				return e;
			}

			Field f = e.getField(this.fieldName);
			return f.getType();
		} catch (ClassNotFoundException arg4) {
			reason = arg4;
		} catch (NoSuchFieldException arg5) {
			reason = arg5;
		} catch (SecurityException arg6) {
			reason = arg6;
		}

		if (reason != null) {
			throw new OgnlException("Could not get static field " + this.fieldName + " from class " + this.className,
					(Throwable) reason);
		} else {
			return null;
		}
	}

	public Class getGetterClass() {
		return this._getterClass;
	}

	public Class getSetterClass() {
		return this._getterClass;
	}

	public String toString() {
		return "@" + this.className + "@" + this.fieldName;
	}

	public String toGetSourceString(OgnlContext context, Object target) {
		try {
			Object t = OgnlRuntime.getStaticField(context, this.className, this.fieldName);
			context.setCurrentObject(t);
			this._getterClass = this.getFieldClass(context);
			context.setCurrentType(this._getterClass);
		} catch (Throwable arg3) {
			throw OgnlOps.castToRuntime(arg3);
		}

		return this.className + "." + this.fieldName;
	}

	public String toSetSourceString(OgnlContext context, Object target) {
		try {
			Object t = OgnlRuntime.getStaticField(context, this.className, this.fieldName);
			context.setCurrentObject(t);
			this._getterClass = this.getFieldClass(context);
			context.setCurrentType(this._getterClass);
		} catch (Throwable arg3) {
			throw OgnlOps.castToRuntime(arg3);
		}

		return this.className + "." + this.fieldName;
	}
}
