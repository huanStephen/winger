package org.eocencle.winger.javassist.expr;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.CtBehavior;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.ExceptionTable;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.Javac;

public class Handler extends Expr {
	private static String EXCEPTION_NAME = "$1";
	private ExceptionTable etable;
	private int index;

	protected Handler(ExceptionTable et, int nth, CodeIterator it, CtClass declaring, MethodInfo m) {
		super(et.handlerPc(nth), it, declaring, m);
		this.etable = et;
		this.index = nth;
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

	public CtClass[] mayThrow() {
		return super.mayThrow();
	}

	public CtClass getType() throws NotFoundException {
		int type = this.etable.catchType(this.index);
		if (type == 0) {
			return null;
		} else {
			ConstPool cp = this.getConstPool();
			String name = cp.getClassInfo(type);
			return this.thisClass.getClassPool().getCtClass(name);
		}
	}

	public boolean isFinally() {
		return this.etable.catchType(this.index) == 0;
	}

	public void replace(String statement) throws CannotCompileException {
		throw new RuntimeException("not implemented yet");
	}

	public void insertBefore(String src) throws CannotCompileException {
		this.edited = true;
		ConstPool cp = this.getConstPool();
		CodeAttribute ca = this.iterator.get();
		Javac jv = new Javac(this.thisClass);
		Bytecode b = jv.getBytecode();
		b.setStackDepth(1);
		b.setMaxLocals(ca.getMaxLocals());

		try {
			CtClass e = this.getType();
			int var = jv.recordVariable(e, EXCEPTION_NAME);
			jv.recordReturnType(e, false);
			b.addAstore(var);
			jv.compileStmnt(src);
			b.addAload(var);
			int oldHandler = this.etable.handlerPc(this.index);
			b.addOpcode(167);
			b.addIndex(oldHandler - this.iterator.getCodeLength() - b.currentPc() + 1);
			this.maxStack = b.getMaxStack();
			this.maxLocals = b.getMaxLocals();
			int pos = this.iterator.append(b.get());
			this.iterator.append(b.getExceptionTable(), pos);
			this.etable.setHandlerPc(this.index, pos);
		} catch (NotFoundException arg9) {
			throw new CannotCompileException(arg9);
		} catch (CompileError arg10) {
			throw new CannotCompileException(arg10);
		}
	}
}
