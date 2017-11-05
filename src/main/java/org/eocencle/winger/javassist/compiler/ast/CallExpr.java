package org.eocencle.winger.javassist.compiler.ast;

import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.MemberResolver.Method;

public class CallExpr extends Expr {
	private Method method = null;

	private CallExpr(ASTree _head, ASTList _tail) {
		super(67, _head, _tail);
	}

	public void setMethod(Method m) {
		this.method = m;
	}

	public Method getMethod() {
		return this.method;
	}

	public static CallExpr makeCall(ASTree target, ASTree args) {
		return new CallExpr(target, new ASTList(args));
	}

	public void accept(Visitor v) throws CompileError {
		v.atCallExpr(this);
	}
}
