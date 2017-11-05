package org.eocencle.winger.javassist.expr;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtBehavior;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.Javac;
import org.eocencle.winger.javassist.compiler.JvstCodeGen;
import org.eocencle.winger.javassist.compiler.JvstTypeChecker;
import org.eocencle.winger.javassist.compiler.ProceedHandler;
import org.eocencle.winger.javassist.compiler.ast.ASTList;

public class Cast extends Expr {
	protected Cast(int pos, CodeIterator i, CtClass declaring, MethodInfo m) {
		super(pos, i, declaring, m);
	}

	public CtBehavior where() {
		return super.where();
	}

	public int getLineNumber() {
		return super.getLineNumber();
	}

	public String getFileName() {
		return super.getFileName();
	}

	public CtClass getType() throws NotFoundException {
		ConstPool cp = this.getConstPool();
		int pos = this.currentPos;
		int index = this.iterator.u16bitAt(pos + 1);
		String name = cp.getClassInfo(index);
		return this.thisClass.getClassPool().getCtClass(name);
	}

	public CtClass[] mayThrow() {
		return super.mayThrow();
	}

	public void replace(String statement) throws CannotCompileException {
		this.thisClass.getClassFile();
		ConstPool constPool = this.getConstPool();
		int pos = this.currentPos;
		int index = this.iterator.u16bitAt(pos + 1);
		Javac jc = new Javac(this.thisClass);
		ClassPool cp = this.thisClass.getClassPool();
		CodeAttribute ca = this.iterator.get();

		try {
			CtClass[] e = new CtClass[] { cp.get("java.lang.Object") };
			CtClass retType = this.getType();
			int paramVar = ca.getMaxLocals();
			jc.recordParams("java.lang.Object", e, true, paramVar, this.withinStatic());
			int retVar = jc.recordReturnType(retType, true);
			jc.recordProceed(new Cast.ProceedForCast(index, retType));
			checkResultValue(retType, statement);
			Bytecode bytecode = jc.getBytecode();
			storeStack(e, true, paramVar, bytecode);
			jc.recordLocalVariables(ca, pos);
			bytecode.addConstZero(retType);
			bytecode.addStore(retVar, retType);
			jc.compileStmnt(statement);
			bytecode.addLoad(retVar, retType);
			this.replace0(pos, bytecode, 3);
		} catch (CompileError arg12) {
			throw new CannotCompileException(arg12);
		} catch (NotFoundException arg13) {
			throw new CannotCompileException(arg13);
		} catch (BadBytecode arg14) {
			throw new CannotCompileException("broken method");
		}
	}

	static class ProceedForCast implements ProceedHandler {
		int index;
		CtClass retType;

		ProceedForCast(int i, CtClass t) {
			this.index = i;
			this.retType = t;
		}

		public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
			if (gen.getMethodArgsLength(args) != 1) {
				throw new CompileError("$proceed() cannot take more than one parameter for cast");
			} else {
				gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
				bytecode.addOpcode(192);
				bytecode.addIndex(this.index);
				gen.setType(this.retType);
			}
		}

		public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
			c.atMethodArgs(args, new int[1], new int[1], new String[1]);
			c.setType(this.retType);
		}
	}
}
