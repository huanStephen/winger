package org.eocencle.winger.javassist;

import org.eocencle.winger.javassist.CtField.Initializer;
import org.eocencle.winger.javassist.bytecode.AccessFlag;
import org.eocencle.winger.javassist.bytecode.AnnotationsAttribute;
import org.eocencle.winger.javassist.bytecode.AttributeInfo;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeAttribute.RuntimeCopyException;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.CodeIterator.Gap;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.Descriptor;
import org.eocencle.winger.javassist.bytecode.ExceptionsAttribute;
import org.eocencle.winger.javassist.bytecode.LineNumberAttribute;
import org.eocencle.winger.javassist.bytecode.LineNumberAttribute.Pc;
import org.eocencle.winger.javassist.bytecode.LocalVariableAttribute;
import org.eocencle.winger.javassist.bytecode.LocalVariableTypeAttribute;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.bytecode.ParameterAnnotationsAttribute;
import org.eocencle.winger.javassist.bytecode.SignatureAttribute;
import org.eocencle.winger.javassist.bytecode.StackMap;
import org.eocencle.winger.javassist.bytecode.StackMapTable;
import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.Javac;
import org.eocencle.winger.javassist.expr.ExprEditor;

public abstract class CtBehavior extends CtMember {
	protected MethodInfo methodInfo;

	protected CtBehavior(CtClass clazz, MethodInfo minfo) {
		super(clazz);
		this.methodInfo = minfo;
	}

	void copy(CtBehavior src, boolean isCons, ClassMap map) throws CannotCompileException {
		CtClass declaring = this.declaringClass;
		MethodInfo srcInfo = src.methodInfo;
		CtClass srcClass = src.getDeclaringClass();
		ConstPool cp = declaring.getClassFile2().getConstPool();
		map = new ClassMap(map);
		map.put(srcClass.getName(), declaring.getName());

		try {
			boolean e = false;
			CtClass srcSuper = srcClass.getSuperclass();
			CtClass destSuper = declaring.getSuperclass();
			String destSuperName = null;
			if (srcSuper != null && destSuper != null) {
				String srcSuperName = srcSuper.getName();
				destSuperName = destSuper.getName();
				if (!srcSuperName.equals(destSuperName)) {
					if (srcSuperName.equals("java.lang.Object")) {
						e = true;
					} else {
						map.putIfNone(srcSuperName, destSuperName);
					}
				}
			}

			this.methodInfo = new MethodInfo(cp, srcInfo.getName(), srcInfo, map);
			if (isCons && e) {
				this.methodInfo.setSuperclass(destSuperName);
			}

		} catch (NotFoundException arg12) {
			throw new CannotCompileException(arg12);
		} catch (BadBytecode arg13) {
			throw new CannotCompileException(arg13);
		}
	}

	protected void extendToString(StringBuffer buffer) {
		buffer.append(' ');
		buffer.append(this.getName());
		buffer.append(' ');
		buffer.append(this.methodInfo.getDescriptor());
	}

	public abstract String getLongName();

	public MethodInfo getMethodInfo() {
		this.declaringClass.checkModify();
		return this.methodInfo;
	}

	public MethodInfo getMethodInfo2() {
		return this.methodInfo;
	}

	public int getModifiers() {
		return AccessFlag.toModifier(this.methodInfo.getAccessFlags());
	}

	public void setModifiers(int mod) {
		this.declaringClass.checkModify();
		this.methodInfo.setAccessFlags(AccessFlag.of(mod));
	}

	public boolean hasAnnotation(Class clz) {
		MethodInfo mi = this.getMethodInfo2();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) mi.getAttribute("RuntimeInvisibleAnnotations");
		AnnotationsAttribute ainfo2 = (AnnotationsAttribute) mi.getAttribute("RuntimeVisibleAnnotations");
		return CtClassType.hasAnnotationType(clz, this.getDeclaringClass().getClassPool(), ainfo, ainfo2);
	}

	public Object getAnnotation(Class clz) throws ClassNotFoundException {
		MethodInfo mi = this.getMethodInfo2();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) mi.getAttribute("RuntimeInvisibleAnnotations");
		AnnotationsAttribute ainfo2 = (AnnotationsAttribute) mi.getAttribute("RuntimeVisibleAnnotations");
		return CtClassType.getAnnotationType(clz, this.getDeclaringClass().getClassPool(), ainfo, ainfo2);
	}

	public Object[] getAnnotations() throws ClassNotFoundException {
		return this.getAnnotations(false);
	}

	public Object[] getAvailableAnnotations() {
		try {
			return this.getAnnotations(true);
		} catch (ClassNotFoundException arg1) {
			throw new RuntimeException("Unexpected exception", arg1);
		}
	}

	private Object[] getAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
		MethodInfo mi = this.getMethodInfo2();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) mi.getAttribute("RuntimeInvisibleAnnotations");
		AnnotationsAttribute ainfo2 = (AnnotationsAttribute) mi.getAttribute("RuntimeVisibleAnnotations");
		return CtClassType.toAnnotationType(ignoreNotFound, this.getDeclaringClass().getClassPool(), ainfo, ainfo2);
	}

	public Object[][] getParameterAnnotations() throws ClassNotFoundException {
		return this.getParameterAnnotations(false);
	}

	public Object[][] getAvailableParameterAnnotations() {
		try {
			return this.getParameterAnnotations(true);
		} catch (ClassNotFoundException arg1) {
			throw new RuntimeException("Unexpected exception", arg1);
		}
	}

	Object[][] getParameterAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
		MethodInfo mi = this.getMethodInfo2();
		ParameterAnnotationsAttribute ainfo = (ParameterAnnotationsAttribute) mi
				.getAttribute("RuntimeInvisibleParameterAnnotations");
		ParameterAnnotationsAttribute ainfo2 = (ParameterAnnotationsAttribute) mi
				.getAttribute("RuntimeVisibleParameterAnnotations");
		return CtClassType.toAnnotationType(ignoreNotFound, this.getDeclaringClass().getClassPool(), ainfo, ainfo2, mi);
	}

	public CtClass[] getParameterTypes() throws NotFoundException {
		return Descriptor.getParameterTypes(this.methodInfo.getDescriptor(), this.declaringClass.getClassPool());
	}

	CtClass getReturnType0() throws NotFoundException {
		return Descriptor.getReturnType(this.methodInfo.getDescriptor(), this.declaringClass.getClassPool());
	}

	public String getSignature() {
		return this.methodInfo.getDescriptor();
	}

	public String getGenericSignature() {
		SignatureAttribute sa = (SignatureAttribute) this.methodInfo.getAttribute("Signature");
		return sa == null ? null : sa.getSignature();
	}

	public void setGenericSignature(String sig) {
		this.declaringClass.checkModify();
		this.methodInfo.addAttribute(new SignatureAttribute(this.methodInfo.getConstPool(), sig));
	}

	public CtClass[] getExceptionTypes() throws NotFoundException {
		ExceptionsAttribute ea = this.methodInfo.getExceptionsAttribute();
		String[] exceptions;
		if (ea == null) {
			exceptions = null;
		} else {
			exceptions = ea.getExceptions();
		}

		return this.declaringClass.getClassPool().get(exceptions);
	}

	public void setExceptionTypes(CtClass[] types) throws NotFoundException {
		this.declaringClass.checkModify();
		if (types != null && types.length != 0) {
			String[] names = new String[types.length];

			for (int ea = 0; ea < types.length; ++ea) {
				names[ea] = types[ea].getName();
			}

			ExceptionsAttribute arg3 = this.methodInfo.getExceptionsAttribute();
			if (arg3 == null) {
				arg3 = new ExceptionsAttribute(this.methodInfo.getConstPool());
				this.methodInfo.setExceptionsAttribute(arg3);
			}

			arg3.setExceptions(names);
		} else {
			this.methodInfo.removeExceptionsAttribute();
		}
	}

	public abstract boolean isEmpty();

	public void setBody(String src) throws CannotCompileException {
		this.setBody(src, (String) null, (String) null);
	}

	public void setBody(String src, String delegateObj, String delegateMethod) throws CannotCompileException {
		CtClass cc = this.declaringClass;
		cc.checkModify();

		try {
			Javac e = new Javac(cc);
			if (delegateMethod != null) {
				e.recordProceed(delegateObj, delegateMethod);
			}

			Bytecode b = e.compileBody(this, src);
			this.methodInfo.setCodeAttribute(b.toCodeAttribute());
			this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & -1025);
			this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
			this.declaringClass.rebuildClassFile();
		} catch (CompileError arg6) {
			throw new CannotCompileException(arg6);
		} catch (BadBytecode arg7) {
			throw new CannotCompileException(arg7);
		}
	}

	static void setBody0(CtClass srcClass, MethodInfo srcInfo, CtClass destClass, MethodInfo destInfo, ClassMap map)
			throws CannotCompileException {
		destClass.checkModify();
		map = new ClassMap(map);
		map.put(srcClass.getName(), destClass.getName());

		try {
			CodeAttribute e = srcInfo.getCodeAttribute();
			if (e != null) {
				ConstPool cp = destInfo.getConstPool();
				CodeAttribute ca = (CodeAttribute) e.copy(cp, map);
				destInfo.setCodeAttribute(ca);
			}
		} catch (RuntimeCopyException arg7) {
			throw new CannotCompileException(arg7);
		}

		destInfo.setAccessFlags(destInfo.getAccessFlags() & -1025);
		destClass.rebuildClassFile();
	}

	public byte[] getAttribute(String name) {
		AttributeInfo ai = this.methodInfo.getAttribute(name);
		return ai == null ? null : ai.get();
	}

	public void setAttribute(String name, byte[] data) {
		this.declaringClass.checkModify();
		this.methodInfo.addAttribute(new AttributeInfo(this.methodInfo.getConstPool(), name, data));
	}

	public void useCflow(String name) throws CannotCompileException {
		CtClass cc = this.declaringClass;
		cc.checkModify();
		ClassPool pool = cc.getClassPool();
		int i = 0;

		while (true) {
			String fname = "_cflow$" + i++;

			try {
				cc.getDeclaredField(fname);
			} catch (NotFoundException arg9) {
				pool.recordCflow(name, this.declaringClass.getName(), fname);

				try {
					CtClass e = pool.get("org.eocencle.winger.javassist.runtime.Cflow");
					CtField field = new CtField(e, fname, cc);
					field.setModifiers(9);
					cc.addField(field, Initializer.byNew(e));
					this.insertBefore(fname + ".enter();", false);
					String src = fname + ".exit();";
					this.insertAfter(src, true);
					return;
				} catch (NotFoundException arg8) {
					throw new CannotCompileException(arg8);
				}
			}
		}
	}

	public void addLocalVariable(String name, CtClass type) throws CannotCompileException {
		this.declaringClass.checkModify();
		ConstPool cp = this.methodInfo.getConstPool();
		CodeAttribute ca = this.methodInfo.getCodeAttribute();
		if (ca == null) {
			throw new CannotCompileException("no method body");
		} else {
			LocalVariableAttribute va = (LocalVariableAttribute) ca.getAttribute("LocalVariableTable");
			if (va == null) {
				va = new LocalVariableAttribute(cp);
				ca.getAttributes().add(va);
			}

			int maxLocals = ca.getMaxLocals();
			String desc = Descriptor.of(type);
			va.addEntry(0, ca.getCodeLength(), cp.addUtf8Info(name), cp.addUtf8Info(desc), maxLocals);
			ca.setMaxLocals(maxLocals + Descriptor.dataSize(desc));
		}
	}

	public void insertParameter(CtClass type) throws CannotCompileException {
		this.declaringClass.checkModify();
		String desc = this.methodInfo.getDescriptor();
		String desc2 = Descriptor.insertParameter(type, desc);

		try {
			this.addParameter2(Modifier.isStatic(this.getModifiers()) ? 0 : 1, type, desc);
		} catch (BadBytecode arg4) {
			throw new CannotCompileException(arg4);
		}

		this.methodInfo.setDescriptor(desc2);
	}

	public void addParameter(CtClass type) throws CannotCompileException {
		this.declaringClass.checkModify();
		String desc = this.methodInfo.getDescriptor();
		String desc2 = Descriptor.appendParameter(type, desc);
		int offset = Modifier.isStatic(this.getModifiers()) ? 0 : 1;

		try {
			this.addParameter2(offset + Descriptor.paramSize(desc), type, desc);
		} catch (BadBytecode arg5) {
			throw new CannotCompileException(arg5);
		}

		this.methodInfo.setDescriptor(desc2);
	}

	private void addParameter2(int where, CtClass type, String desc) throws BadBytecode {
		CodeAttribute ca = this.methodInfo.getCodeAttribute();
		if (ca != null) {
			int size = 1;
			char typeDesc = 76;
			int classInfo = 0;
			if (type.isPrimitive()) {
				CtPrimitiveType va = (CtPrimitiveType) type;
				size = va.getDataSize();
				typeDesc = va.getDescriptor();
			} else {
				classInfo = this.methodInfo.getConstPool().addClassInfo(type);
			}

			ca.insertLocalVar(where, size);
			LocalVariableAttribute va1 = (LocalVariableAttribute) ca.getAttribute("LocalVariableTable");
			if (va1 != null) {
				va1.shiftIndex(where, size);
			}

			LocalVariableTypeAttribute lvta = (LocalVariableTypeAttribute) ca.getAttribute("LocalVariableTypeTable");
			if (lvta != null) {
				lvta.shiftIndex(where, size);
			}

			StackMapTable smt = (StackMapTable) ca.getAttribute("StackMapTable");
			if (smt != null) {
				smt.insertLocal(where, StackMapTable.typeTagOf(typeDesc), classInfo);
			}

			StackMap sm = (StackMap) ca.getAttribute("StackMap");
			if (sm != null) {
				sm.insertLocal(where, StackMapTable.typeTagOf(typeDesc), classInfo);
			}
		}

	}

	public void instrument(CodeConverter converter) throws CannotCompileException {
		this.declaringClass.checkModify();
		ConstPool cp = this.methodInfo.getConstPool();
		converter.doit(this.getDeclaringClass(), this.methodInfo, cp);
	}

	public void instrument(ExprEditor editor) throws CannotCompileException {
		if (this.declaringClass.isFrozen()) {
			this.declaringClass.checkModify();
		}

		if (editor.doit(this.declaringClass, this.methodInfo)) {
			this.declaringClass.checkModify();
		}

	}

	public void insertBefore(String src) throws CannotCompileException {
		this.insertBefore(src, true);
	}

	private void insertBefore(String src, boolean rebuild) throws CannotCompileException {
		CtClass cc = this.declaringClass;
		cc.checkModify();
		CodeAttribute ca = this.methodInfo.getCodeAttribute();
		if (ca == null) {
			throw new CannotCompileException("no method body");
		} else {
			CodeIterator iterator = ca.iterator();
			Javac jv = new Javac(cc);

			try {
				int e = jv.recordParams(this.getParameterTypes(), Modifier.isStatic(this.getModifiers()));
				jv.recordParamNames(ca, e);
				jv.recordLocalVariables(ca, 0);
				jv.recordType(this.getReturnType0());
				jv.compileStmnt(src);
				Bytecode b = jv.getBytecode();
				int stack = b.getMaxStack();
				int locals = b.getMaxLocals();
				if (stack > ca.getMaxStack()) {
					ca.setMaxStack(stack);
				}

				if (locals > ca.getMaxLocals()) {
					ca.setMaxLocals(locals);
				}

				int pos = iterator.insertEx(b.get());
				iterator.insert(b.getExceptionTable(), pos);
				if (rebuild) {
					this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
				}

			} catch (NotFoundException arg11) {
				throw new CannotCompileException(arg11);
			} catch (CompileError arg12) {
				throw new CannotCompileException(arg12);
			} catch (BadBytecode arg13) {
				throw new CannotCompileException(arg13);
			}
		}
	}

	public void insertAfter(String src) throws CannotCompileException {
		this.insertAfter(src, false);
	}

	public void insertAfter(String src, boolean asFinally) throws CannotCompileException {
		CtClass cc = this.declaringClass;
		cc.checkModify();
		ConstPool pool = this.methodInfo.getConstPool();
		CodeAttribute ca = this.methodInfo.getCodeAttribute();
		if (ca == null) {
			throw new CannotCompileException("no method body");
		} else {
			CodeIterator iterator = ca.iterator();
			int retAddr = ca.getMaxLocals();
			Bytecode b = new Bytecode(pool, 0, retAddr + 1);
			b.setStackDepth(ca.getMaxStack() + 1);
			Javac jv = new Javac(b, cc);

			try {
				int e = jv.recordParams(this.getParameterTypes(), Modifier.isStatic(this.getModifiers()));
				jv.recordParamNames(ca, e);
				CtClass rtype = this.getReturnType0();
				int varNo = jv.recordReturnType(rtype, true);
				jv.recordLocalVariables(ca, 0);
				int handlerLen = this.insertAfterHandler(asFinally, b, rtype, varNo, jv, src);
				int handlerPos = iterator.getCodeLength();
				if (asFinally) {
					ca.getExceptionTable().add(this.getStartPosOfBody(ca), handlerPos, handlerPos, 0);
				}

				int adviceLen = 0;
				int advicePos = 0;
				boolean noReturn = true;

				while (iterator.hasNext()) {
					int pos = iterator.next();
					if (pos >= handlerPos) {
						break;
					}

					int c = iterator.byteAt(pos);
					if (c == 176 || c == 172 || c == 174 || c == 173 || c == 175 || c == 177) {
						if (noReturn) {
							adviceLen = this.insertAfterAdvice(b, jv, src, pool, rtype, varNo);
							handlerPos = iterator.append(b.get());
							iterator.append(b.getExceptionTable(), handlerPos);
							advicePos = iterator.getCodeLength() - adviceLen;
							handlerLen = advicePos - handlerPos;
							noReturn = false;
						}

						this.insertGoto(iterator, advicePos, pos);
						advicePos = iterator.getCodeLength() - adviceLen;
						handlerPos = advicePos - handlerLen;
					}
				}

				if (noReturn) {
					handlerPos = iterator.append(b.get());
					iterator.append(b.getExceptionTable(), handlerPos);
				}

				ca.setMaxStack(b.getMaxStack());
				ca.setMaxLocals(b.getMaxLocals());
				this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
			} catch (NotFoundException arg19) {
				throw new CannotCompileException(arg19);
			} catch (CompileError arg20) {
				throw new CannotCompileException(arg20);
			} catch (BadBytecode arg21) {
				throw new CannotCompileException(arg21);
			}
		}
	}

	private int insertAfterAdvice(Bytecode code, Javac jv, String src, ConstPool cp, CtClass rtype, int varNo)
			throws CompileError {
		int pc = code.currentPc();
		if (rtype == CtClass.voidType) {
			code.addOpcode(1);
			code.addAstore(varNo);
			jv.compileStmnt(src);
			code.addOpcode(177);
			if (code.getMaxLocals() < 1) {
				code.setMaxLocals(1);
			}
		} else {
			code.addStore(varNo, rtype);
			jv.compileStmnt(src);
			code.addLoad(varNo, rtype);
			if (rtype.isPrimitive()) {
				code.addOpcode(((CtPrimitiveType) rtype).getReturnOp());
			} else {
				code.addOpcode(176);
			}
		}

		return code.currentPc() - pc;
	}

	private void insertGoto(CodeIterator iterator, int subr, int pos) throws BadBytecode {
		iterator.setMark(subr);
		iterator.writeByte(0, pos);
		boolean wide = subr + 2 - pos > 32767;
		int len = wide ? 4 : 2;
		Gap gap = iterator.insertGapAt(pos, len, false);
		pos = gap.position + gap.length - len;
		int offset = iterator.getMark() - pos;
		if (wide) {
			iterator.writeByte(200, pos);
			iterator.write32bit(offset, pos + 1);
		} else if (offset <= 32767) {
			iterator.writeByte(167, pos);
			iterator.write16bit(offset, pos + 1);
		} else {
			if (gap.length < 4) {
				Gap gap2 = iterator.insertGapAt(gap.position, 2, false);
				pos = gap2.position + gap2.length + gap.length - 4;
			}

			iterator.writeByte(200, pos);
			iterator.write32bit(iterator.getMark() - pos, pos + 1);
		}

	}

	private int insertAfterHandler(boolean asFinally, Bytecode b, CtClass rtype, int returnVarNo, Javac javac,
			String src) throws CompileError {
		if (!asFinally) {
			return 0;
		} else {
			int var = b.getMaxLocals();
			b.incMaxLocals(1);
			int pc = b.currentPc();
			b.addAstore(var);
			if (rtype.isPrimitive()) {
				char c = ((CtPrimitiveType) rtype).getDescriptor();
				if (c == 68) {
					b.addDconst(0.0D);
					b.addDstore(returnVarNo);
				} else if (c == 70) {
					b.addFconst(0.0F);
					b.addFstore(returnVarNo);
				} else if (c == 74) {
					b.addLconst(0L);
					b.addLstore(returnVarNo);
				} else if (c == 86) {
					b.addOpcode(1);
					b.addAstore(returnVarNo);
				} else {
					b.addIconst(0);
					b.addIstore(returnVarNo);
				}
			} else {
				b.addOpcode(1);
				b.addAstore(returnVarNo);
			}

			javac.compileStmnt(src);
			b.addAload(var);
			b.addOpcode(191);
			return b.currentPc() - pc;
		}
	}

	public void addCatch(String src, CtClass exceptionType) throws CannotCompileException {
		this.addCatch(src, exceptionType, "$e");
	}

	public void addCatch(String src, CtClass exceptionType, String exceptionName) throws CannotCompileException {
		CtClass cc = this.declaringClass;
		cc.checkModify();
		ConstPool cp = this.methodInfo.getConstPool();
		CodeAttribute ca = this.methodInfo.getCodeAttribute();
		CodeIterator iterator = ca.iterator();
		Bytecode b = new Bytecode(cp, ca.getMaxStack(), ca.getMaxLocals());
		b.setStackDepth(1);
		Javac jv = new Javac(b, cc);

		try {
			jv.recordParams(this.getParameterTypes(), Modifier.isStatic(this.getModifiers()));
			int e = jv.recordVariable(exceptionType, exceptionName);
			b.addAstore(e);
			jv.compileStmnt(src);
			int stack = b.getMaxStack();
			int locals = b.getMaxLocals();
			if (stack > ca.getMaxStack()) {
				ca.setMaxStack(stack);
			}

			if (locals > ca.getMaxLocals()) {
				ca.setMaxLocals(locals);
			}

			int len = iterator.getCodeLength();
			int pos = iterator.append(b.get());
			ca.getExceptionTable().add(this.getStartPosOfBody(ca), len, len, cp.addClassInfo(exceptionType));
			iterator.append(b.getExceptionTable(), pos);
			this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
		} catch (NotFoundException arg14) {
			throw new CannotCompileException(arg14);
		} catch (CompileError arg15) {
			throw new CannotCompileException(arg15);
		} catch (BadBytecode arg16) {
			throw new CannotCompileException(arg16);
		}
	}

	int getStartPosOfBody(CodeAttribute ca) throws CannotCompileException {
		return 0;
	}

	public int insertAt(int lineNum, String src) throws CannotCompileException {
		return this.insertAt(lineNum, true, src);
	}

	public int insertAt(int lineNum, boolean modify, String src) throws CannotCompileException {
		CodeAttribute ca = this.methodInfo.getCodeAttribute();
		if (ca == null) {
			throw new CannotCompileException("no method body");
		} else {
			LineNumberAttribute ainfo = (LineNumberAttribute) ca.getAttribute("LineNumberTable");
			if (ainfo == null) {
				throw new CannotCompileException("no line number info");
			} else {
				Pc pc = ainfo.toNearPc(lineNum);
				lineNum = pc.line;
				int index = pc.index;
				if (!modify) {
					return lineNum;
				} else {
					CtClass cc = this.declaringClass;
					cc.checkModify();
					CodeIterator iterator = ca.iterator();
					Javac jv = new Javac(cc);

					try {
						jv.recordLocalVariables(ca, index);
						jv.recordParams(this.getParameterTypes(), Modifier.isStatic(this.getModifiers()));
						jv.setMaxLocals(ca.getMaxLocals());
						jv.compileStmnt(src);
						Bytecode e = jv.getBytecode();
						int locals = e.getMaxLocals();
						int stack = e.getMaxStack();
						ca.setMaxLocals(locals);
						if (stack > ca.getMaxStack()) {
							ca.setMaxStack(stack);
						}

						index = iterator.insertAt(index, e.get());
						iterator.insert(e.getExceptionTable(), index);
						this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
						return lineNum;
					} catch (NotFoundException arg13) {
						throw new CannotCompileException(arg13);
					} catch (CompileError arg14) {
						throw new CannotCompileException(arg14);
					} catch (BadBytecode arg15) {
						throw new CannotCompileException(arg15);
					}
				}
			}
		}
	}
}
