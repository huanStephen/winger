package org.eocencle.winger.ognl;

public class NoSuchPropertyException extends OgnlException {
	private Object target;
	private Object name;

	public NoSuchPropertyException(Object target, Object name) {
		super(getReason(target, name));
	}

	public NoSuchPropertyException(Object target, Object name, Throwable reason) {
		super(getReason(target, name), reason);
		this.target = target;
		this.name = name;
	}

	static String getReason(Object target, Object name) {
		String ret = null;
		if (target == null) {
			ret = "null";
		} else if (target instanceof Class) {
			ret = ((Class) target).getName();
		} else {
			ret = target.getClass().getName();
		}

		ret = ret + "." + name;
		return ret;
	}

	public Object getTarget() {
		return this.target;
	}

	public Object getName() {
		return this.name;
	}
}
