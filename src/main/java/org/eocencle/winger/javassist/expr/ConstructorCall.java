package org.eocencle.winger.javassist.expr;

import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtConstructor;
import org.eocencle.winger.javassist.CtMethod;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.MethodInfo;

public class ConstructorCall extends MethodCall {
	protected ConstructorCall(int pos, CodeIterator i, CtClass decl, MethodInfo m) {
		super(pos, i, decl, m);
	}

	public String getMethodName() {
		return this.isSuper() ? "super" : "this";
	}

	public CtMethod getMethod() throws NotFoundException {
		throw new NotFoundException("this is a constructor call.  Call getConstructor().");
	}

	public CtConstructor getConstructor() throws NotFoundException {
		return this.getCtClass().getConstructor(this.getSignature());
	}

	public boolean isSuper() {
		return super.isSuper();
	}
}
