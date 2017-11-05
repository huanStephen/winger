package org.eocencle.winger.javassist.compiler;

import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.compiler.ast.ASTList;

public interface ProceedHandler {
	void doit(JvstCodeGen arg0, Bytecode arg1, ASTList arg2) throws CompileError;

	void setReturnType(JvstTypeChecker arg0, ASTList arg1) throws CompileError;
}
