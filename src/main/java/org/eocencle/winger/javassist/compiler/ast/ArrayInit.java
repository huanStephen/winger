package org.eocencle.winger.javassist.compiler.ast;

import org.eocencle.winger.javassist.compiler.CompileError;

public class ArrayInit extends ASTList {
	public ArrayInit(ASTree firstElement) {
		super(firstElement);
	}

	public void accept(Visitor v) throws CompileError {
		v.atArrayInit(this);
	}

	public String getTag() {
		return "array";
	}
}
