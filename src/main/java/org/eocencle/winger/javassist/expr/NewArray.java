package org.eocencle.winger.javassist.expr;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.CtBehavior;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtPrimitiveType;
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
import org.eocencle.winger.javassist.compiler.ProceedHandler;
import org.eocencle.winger.javassist.compiler.ast.ASTList;

public class NewArray extends Expr {
	int opcode;

	protected NewArray(int pos, CodeIterator i, CtClass declaring, MethodInfo m, int op) {
		super(pos, i, declaring, m);
		this.opcode = op;
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

	public CtClass getComponentType() throws NotFoundException {
		int index;
		if (this.opcode == 188) {
			index = this.iterator.byteAt(this.currentPos + 1);
			return this.getPrimitiveType(index);
		} else if (this.opcode != 189 && this.opcode != 197) {
			throw new RuntimeException("bad opcode: " + this.opcode);
		} else {
			index = this.iterator.u16bitAt(this.currentPos + 1);
			String desc = this.getConstPool().getClassInfo(index);
			int dim = Descriptor.arrayDimension(desc);
			desc = Descriptor.toArrayComponent(desc, dim);
			return Descriptor.toCtClass(desc, this.thisClass.getClassPool());
		}
	}

	CtClass getPrimitiveType(int atype) {
		switch (atype) {
		case 4:
			return CtClass.booleanType;
		case 5:
			return CtClass.charType;
		case 6:
			return CtClass.floatType;
		case 7:
			return CtClass.doubleType;
		case 8:
			return CtClass.byteType;
		case 9:
			return CtClass.shortType;
		case 10:
			return CtClass.intType;
		case 11:
			return CtClass.longType;
		default:
			throw new RuntimeException("bad atype: " + atype);
		}
	}

	public int getDimension() {
		if (this.opcode == 188) {
			return 1;
		} else if (this.opcode != 189 && this.opcode != 197) {
			throw new RuntimeException("bad opcode: " + this.opcode);
		} else {
			int index = this.iterator.u16bitAt(this.currentPos + 1);
			String desc = this.getConstPool().getClassInfo(index);
			return Descriptor.arrayDimension(desc) + (this.opcode == 189 ? 1 : 0);
		}
	}

	public int getCreatedDimensions() {
		return this.opcode == 197 ? this.iterator.byteAt(this.currentPos + 3) : 1;
	}

	public void replace(String statement) throws CannotCompileException {
		try {
			this.replace2(statement);
		} catch (CompileError arg2) {
			throw new CannotCompileException(arg2);
		} catch (NotFoundException arg3) {
			throw new CannotCompileException(arg3);
		} catch (BadBytecode arg4) {
			throw new CannotCompileException("broken method");
		}
	}

	private void replace2(String statement)
			throws CompileError, NotFoundException, BadBytecode, CannotCompileException {
		this.thisClass.getClassFile();
		ConstPool constPool = this.getConstPool();
		int pos = this.currentPos;
		boolean index = false;
		int dim = 1;
		String desc;
		byte codeLength;
		int arg14;
		if (this.opcode == 188) {
			arg14 = this.iterator.byteAt(this.currentPos + 1);
			CtPrimitiveType jc = (CtPrimitiveType) this.getPrimitiveType(arg14);
			desc = "[" + jc.getDescriptor();
			codeLength = 2;
		} else if (this.opcode == 189) {
			arg14 = this.iterator.u16bitAt(pos + 1);
			desc = constPool.getClassInfo(arg14);
			if (desc.startsWith("[")) {
				desc = "[" + desc;
			} else {
				desc = "[L" + desc + ";";
			}

			codeLength = 3;
		} else {
			if (this.opcode != 197) {
				throw new RuntimeException("bad opcode: " + this.opcode);
			}

			arg14 = this.iterator.u16bitAt(this.currentPos + 1);
			desc = constPool.getClassInfo(arg14);
			dim = this.iterator.byteAt(this.currentPos + 3);
			codeLength = 4;
		}

		CtClass retType = Descriptor.toCtClass(desc, this.thisClass.getClassPool());
		Javac arg15 = new Javac(this.thisClass);
		CodeAttribute ca = this.iterator.get();
		CtClass[] params = new CtClass[dim];

		int paramVar;
		for (paramVar = 0; paramVar < dim; ++paramVar) {
			params[paramVar] = CtClass.intType;
		}

		paramVar = ca.getMaxLocals();
		arg15.recordParams("java.lang.Object", params, true, paramVar, this.withinStatic());
		checkResultValue(retType, statement);
		int retVar = arg15.recordReturnType(retType, true);
		arg15.recordProceed(new NewArray.ProceedForArray(retType, this.opcode, arg14, dim));
		Bytecode bytecode = arg15.getBytecode();
		storeStack(params, true, paramVar, bytecode);
		arg15.recordLocalVariables(ca, pos);
		bytecode.addOpcode(1);
		bytecode.addAstore(retVar);
		arg15.compileStmnt(statement);
		bytecode.addAload(retVar);
		this.replace0(pos, bytecode, codeLength);
	}

	static class ProceedForArray implements ProceedHandler {
		CtClass arrayType;
		int opcode;
		int index;
		int dimension;

		ProceedForArray(CtClass type, int op, int i, int dim) {
			this.arrayType = type;
			this.opcode = op;
			this.index = i;
			this.dimension = dim;
		}

		public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
			int num = gen.getMethodArgsLength(args);
			if (num != this.dimension) {
				throw new CompileError("$proceed() with a wrong number of parameters");
			} else {
				gen.atMethodArgs(args, new int[num], new int[num], new String[num]);
				bytecode.addOpcode(this.opcode);
				if (this.opcode == 189) {
					bytecode.addIndex(this.index);
				} else if (this.opcode == 188) {
					bytecode.add(this.index);
				} else {
					bytecode.addIndex(this.index);
					bytecode.add(this.dimension);
					bytecode.growStack(1 - this.dimension);
				}

				gen.setType(this.arrayType);
			}
		}

		public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
			c.setType(this.arrayType);
		}
	}
}
