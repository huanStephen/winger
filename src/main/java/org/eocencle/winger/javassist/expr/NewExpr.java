package org.eocencle.winger.javassist.expr;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtBehavior;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtConstructor;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.Descriptor;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.Javac;
import org.eocencle.winger.javassist.compiler.JvstCodeGen;
import org.eocencle.winger.javassist.compiler.JvstTypeChecker;
import org.eocencle.winger.javassist.compiler.MemberResolver.Method;
import org.eocencle.winger.javassist.compiler.ProceedHandler;
import org.eocencle.winger.javassist.compiler.ast.ASTList;

public class NewExpr extends Expr {
	String newTypeName;
	int newPos;

	protected NewExpr(int pos, CodeIterator i, CtClass declaring, MethodInfo m, String type, int np) {
		super(pos, i, declaring, m);
		this.newTypeName = type;
		this.newPos = np;
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

	private CtClass getCtClass() throws NotFoundException {
		return this.thisClass.getClassPool().get(this.newTypeName);
	}

	public String getClassName() {
		return this.newTypeName;
	}

	public String getSignature() {
		ConstPool constPool = this.getConstPool();
		int methodIndex = this.iterator.u16bitAt(this.currentPos + 1);
		return constPool.getMethodrefType(methodIndex);
	}

	public CtConstructor getConstructor() throws NotFoundException {
		ConstPool cp = this.getConstPool();
		int index = this.iterator.u16bitAt(this.currentPos + 1);
		String desc = cp.getMethodrefType(index);
		return this.getCtClass().getConstructor(desc);
	}

	public CtClass[] mayThrow() {
		return super.mayThrow();
	}

	private int canReplace() throws CannotCompileException {
		int op = this.iterator.byteAt(this.newPos + 3);
		return op != 89 ? (op == 90 && this.iterator.byteAt(this.newPos + 4) == 95 ? 5 : 3)
				: (this.iterator.byteAt(this.newPos + 4) == 94 && this.iterator.byteAt(this.newPos + 5) == 88 ? 6 : 4);
	}

	public void replace(String statement) throws CannotCompileException {
		this.thisClass.getClassFile();
		boolean bytecodeSize = true;
		int pos = this.newPos;
		int newIndex = this.iterator.u16bitAt(pos + 1);
		int codeSize = this.canReplace();
		int end = pos + codeSize;

		for (int constPool = pos; constPool < end; ++constPool) {
			this.iterator.writeByte(0, constPool);
		}

		ConstPool arg20 = this.getConstPool();
		pos = this.currentPos;
		int methodIndex = this.iterator.u16bitAt(pos + 1);
		String signature = arg20.getMethodrefType(methodIndex);
		Javac jc = new Javac(this.thisClass);
		ClassPool cp = this.thisClass.getClassPool();
		CodeAttribute ca = this.iterator.get();

		try {
			CtClass[] e = Descriptor.getParameterTypes(signature, cp);
			CtClass newType = cp.get(this.newTypeName);
			int paramVar = ca.getMaxLocals();
			jc.recordParams(this.newTypeName, e, true, paramVar, this.withinStatic());
			int retVar = jc.recordReturnType(newType, true);
			jc.recordProceed(new NewExpr.ProceedForNew(newType, newIndex, methodIndex));
			checkResultValue(newType, statement);
			Bytecode bytecode = jc.getBytecode();
			storeStack(e, true, paramVar, bytecode);
			jc.recordLocalVariables(ca, pos);
			bytecode.addConstZero(newType);
			bytecode.addStore(retVar, newType);
			jc.compileStmnt(statement);
			if (codeSize > 3) {
				bytecode.addAload(retVar);
			}

			this.replace0(pos, bytecode, 3);
		} catch (CompileError arg17) {
			throw new CannotCompileException(arg17);
		} catch (NotFoundException arg18) {
			throw new CannotCompileException(arg18);
		} catch (BadBytecode arg19) {
			throw new CannotCompileException("broken method");
		}
	}

	static class ProceedForNew implements ProceedHandler {
		CtClass newType;
		int newIndex;
		int methodIndex;

		ProceedForNew(CtClass nt, int ni, int mi) {
			this.newType = nt;
			this.newIndex = ni;
			this.methodIndex = mi;
		}

		public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
			bytecode.addOpcode(187);
			bytecode.addIndex(this.newIndex);
			bytecode.addOpcode(89);
			gen.atMethodCallCore(this.newType, "<init>", args, false, true, -1, (Method) null);
			gen.setType(this.newType);
		}

		public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
			c.atMethodCallCore(this.newType, "<init>", args);
			c.setType(this.newType);
		}
	}
}
