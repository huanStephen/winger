package org.eocencle.winger.ognl.enhance;

import org.eocencle.winger.ognl.OgnlContext;

public class ContextClassLoader extends ClassLoader {
	private OgnlContext context;

	public ContextClassLoader(ClassLoader parentClassLoader, OgnlContext context) {
		super(parentClassLoader);
		this.context = context;
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		return this.context != null && this.context.getClassResolver() != null
				? this.context.getClassResolver().classForName(name, this.context) : super.findClass(name);
	}
}
