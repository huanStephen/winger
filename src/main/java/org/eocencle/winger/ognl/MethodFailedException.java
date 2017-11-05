package org.eocencle.winger.ognl;

public class MethodFailedException extends OgnlException {
	public MethodFailedException(Object source, String name) {
		super("Method \"" + name + "\" failed for object " + source);
	}

	public MethodFailedException(Object source, String name, Throwable reason) {
		super("Method \"" + name + "\" failed for object " + source, reason);
	}
}
