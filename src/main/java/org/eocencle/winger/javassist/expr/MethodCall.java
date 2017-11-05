package org.eocencle.winger.javassist.expr;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtBehavior;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtMethod;
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

public class MethodCall extends Expr {
	protected MethodCall(int pos, CodeIterator i, CtClass declaring, MethodInfo m) {
		super(pos, i, declaring, m);
	}

	private int getNameAndType(ConstPool cp) {
		int pos = this.currentPos;
		int c = this.iterator.byteAt(pos);
		int index = this.iterator.u16bitAt(pos + 1);
		return c == 185 ? cp.getInterfaceMethodrefNameAndType(index) : cp.getMethodrefNameAndType(index);
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

	protected CtClass getCtClass() throws NotFoundException {
		return this.thisClass.getClassPool().get(this.getClassName());
	}

	public String getClassName() {
		ConstPool cp = this.getConstPool();
		int pos = this.currentPos;
		int c = this.iterator.byteAt(pos);
		int index = this.iterator.u16bitAt(pos + 1);
		String cname;
		if (c == 185) {
			cname = cp.getInterfaceMethodrefClassName(index);
		} else {
			cname = cp.getMethodrefClassName(index);
		}

		if (cname.charAt(0) == 91) {
			cname = Descriptor.toClassName(cname);
		}

		return cname;
	}

	public String getMethodName() {
		ConstPool cp = this.getConstPool();
		int nt = this.getNameAndType(cp);
		return cp.getUtf8Info(cp.getNameAndTypeName(nt));
	}

	public CtMethod getMethod() throws NotFoundException {
		return this.getCtClass().getMethod(this.getMethodName(), this.getSignature());
	}

	public String getSignature() {
		ConstPool cp = this.getConstPool();
		int nt = this.getNameAndType(cp);
		return cp.getUtf8Info(cp.getNameAndTypeDescriptor(nt));
	}

	public CtClass[] mayThrow() {
		return super.mayThrow();
	}

	public boolean isSuper() {
		return this.iterator.byteAt(this.currentPos) == 183
				&& !this.where().getDeclaringClass().getName().equals(this.getClassName());
	}

	public void replace(String statement) throws CannotCompileException {
		this.thisClass.getClassFile();
		ConstPool constPool = this.getConstPool();
		int pos = this.currentPos;
		int index = this.iterator.u16bitAt(pos + 1);
		int c = this.iterator.byteAt(pos);
		byte opcodeSize;
		String classname;
		String methodname;
		String signature;
		if (c == 185) {
			opcodeSize = 5;
			classname = constPool.getInterfaceMethodrefClassName(index);
			methodname = constPool.getInterfaceMethodrefName(index);
			signature = constPool.getInterfaceMethodrefType(index);
		} else {
			if (c != 184 && c != 183 && c != 182) {
				throw new CannotCompileException("not method invocation");
			}

			opcodeSize = 3;
			classname = constPool.getMethodrefClassName(index);
			methodname = constPool.getMethodrefName(index);
			signature = constPool.getMethodrefType(index);
		}

		Javac jc = new Javac(this.thisClass);
		ClassPool cp = this.thisClass.getClassPool();
		CodeAttribute ca = this.iterator.get();

		try {
			CtClass[] e = Descriptor.getParameterTypes(signature, cp);
			CtClass retType = Descriptor.getReturnType(signature, cp);
			int paramVar = ca.getMaxLocals();
			jc.recordParams(classname, e, true, paramVar, this.withinStatic());
			int retVar = jc.recordReturnType(retType, true);
			if (c == 184) {
				jc.recordStaticProceed(classname, methodname);
			} else if (c == 183) {
				jc.recordSpecialProceed("$0", classname, methodname, signature);
			} else {
				jc.recordProceed("$0", methodname);
			}

			checkResultValue(retType, statement);
			Bytecode bytecode = jc.getBytecode();
			storeStack(e, c == 184, paramVar, bytecode);
			jc.recordLocalVariables(ca, pos);
			if (retType != CtClass.voidType) {
				bytecode.addConstZero(retType);
				bytecode.addStore(retVar, retType);
			}

			jc.compileStmnt(statement);
			if (retType != CtClass.voidType) {
				bytecode.addLoad(retVar, retType);
			}

			this.replace0(pos, bytecode, opcodeSize);
		} catch (CompileError arg17) {
			throw new CannotCompileException(arg17);
		} catch (NotFoundException arg18) {
			throw new CannotCompileException(arg18);
		} catch (BadBytecode arg19) {
			throw new CannotCompileException("broken method");
		}
	}
}
