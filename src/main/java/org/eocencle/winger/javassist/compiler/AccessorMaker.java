package org.eocencle.winger.javassist.compiler;

import java.util.HashMap;
import java.util.Map;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.ClassFile;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.Descriptor;
import org.eocencle.winger.javassist.bytecode.ExceptionsAttribute;
import org.eocencle.winger.javassist.bytecode.FieldInfo;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.bytecode.SyntheticAttribute;

public class AccessorMaker {
	private CtClass clazz;
	private int uniqueNumber;
	private HashMap accessors;
	static final String lastParamType = "org.eocencle.winger.javassist.runtime.Inner";

	public AccessorMaker(CtClass c) {
		this.clazz = c;
		this.uniqueNumber = 1;
		this.accessors = new HashMap();
	}

	public String getConstructor(CtClass c, String desc, MethodInfo orig) throws CompileError {
		String key = "<init>:" + desc;
		String consDesc = (String) this.accessors.get(key);
		if (consDesc != null) {
			return consDesc;
		} else {
			consDesc = Descriptor.appendParameter("org.eocencle.winger.javassist.runtime.Inner", desc);
			ClassFile cf = this.clazz.getClassFile();

			try {
				ConstPool e = cf.getConstPool();
				ClassPool pool = this.clazz.getClassPool();
				MethodInfo minfo = new MethodInfo(e, "<init>", consDesc);
				minfo.setAccessFlags(0);
				minfo.addAttribute(new SyntheticAttribute(e));
				ExceptionsAttribute ea = orig.getExceptionsAttribute();
				if (ea != null) {
					minfo.addAttribute(ea.copy(e, (Map) null));
				}

				CtClass[] params = Descriptor.getParameterTypes(desc, pool);
				Bytecode code = new Bytecode(e);
				code.addAload(0);
				int regno = 1;
				int i = 0;

				while (true) {
					if (i >= params.length) {
						code.setMaxLocals(regno + 1);
						code.addInvokespecial(this.clazz, "<init>", desc);
						code.addReturn((CtClass) null);
						minfo.setCodeAttribute(code.toCodeAttribute());
						cf.addMethod(minfo);
						break;
					}

					regno += code.addLoad(regno, params[i]);
					++i;
				}
			} catch (CannotCompileException arg14) {
				throw new CompileError(arg14);
			} catch (NotFoundException arg15) {
				throw new CompileError(arg15);
			}

			this.accessors.put(key, consDesc);
			return consDesc;
		}
	}

	public String getMethodAccessor(String name, String desc, String accDesc, MethodInfo orig) throws CompileError {
		String key = name + ":" + desc;
		String accName = (String) this.accessors.get(key);
		if (accName != null) {
			return accName;
		} else {
			ClassFile cf = this.clazz.getClassFile();
			accName = this.findAccessorName(cf);

			try {
				ConstPool e = cf.getConstPool();
				ClassPool pool = this.clazz.getClassPool();
				MethodInfo minfo = new MethodInfo(e, accName, accDesc);
				minfo.setAccessFlags(8);
				minfo.addAttribute(new SyntheticAttribute(e));
				ExceptionsAttribute ea = orig.getExceptionsAttribute();
				if (ea != null) {
					minfo.addAttribute(ea.copy(e, (Map) null));
				}

				CtClass[] params = Descriptor.getParameterTypes(accDesc, pool);
				int regno = 0;
				Bytecode code = new Bytecode(e);

				for (int i = 0; i < params.length; ++i) {
					regno += code.addLoad(regno, params[i]);
				}

				code.setMaxLocals(regno);
				if (desc == accDesc) {
					code.addInvokestatic(this.clazz, name, desc);
				} else {
					code.addInvokevirtual(this.clazz, name, desc);
				}

				code.addReturn(Descriptor.getReturnType(desc, pool));
				minfo.setCodeAttribute(code.toCodeAttribute());
				cf.addMethod(minfo);
			} catch (CannotCompileException arg15) {
				throw new CompileError(arg15);
			} catch (NotFoundException arg16) {
				throw new CompileError(arg16);
			}

			this.accessors.put(key, accName);
			return accName;
		}
	}

	public MethodInfo getFieldGetter(FieldInfo finfo, boolean is_static) throws CompileError {
		String fieldName = finfo.getName();
		String key = fieldName + ":getter";
		Object res = this.accessors.get(key);
		if (res != null) {
			return (MethodInfo) res;
		} else {
			ClassFile cf = this.clazz.getClassFile();
			String accName = this.findAccessorName(cf);

			try {
				ConstPool e = cf.getConstPool();
				ClassPool pool = this.clazz.getClassPool();
				String fieldType = finfo.getDescriptor();
				String accDesc;
				if (is_static) {
					accDesc = "()" + fieldType;
				} else {
					accDesc = "(" + Descriptor.of(this.clazz) + ")" + fieldType;
				}

				MethodInfo minfo = new MethodInfo(e, accName, accDesc);
				minfo.setAccessFlags(8);
				minfo.addAttribute(new SyntheticAttribute(e));
				Bytecode code = new Bytecode(e);
				if (is_static) {
					code.addGetstatic(Bytecode.THIS, fieldName, fieldType);
				} else {
					code.addAload(0);
					code.addGetfield(Bytecode.THIS, fieldName, fieldType);
					code.setMaxLocals(1);
				}

				code.addReturn(Descriptor.toCtClass(fieldType, pool));
				minfo.setCodeAttribute(code.toCodeAttribute());
				cf.addMethod(minfo);
				this.accessors.put(key, minfo);
				return minfo;
			} catch (CannotCompileException arg13) {
				throw new CompileError(arg13);
			} catch (NotFoundException arg14) {
				throw new CompileError(arg14);
			}
		}
	}

	public MethodInfo getFieldSetter(FieldInfo finfo, boolean is_static) throws CompileError {
		String fieldName = finfo.getName();
		String key = fieldName + ":setter";
		Object res = this.accessors.get(key);
		if (res != null) {
			return (MethodInfo) res;
		} else {
			ClassFile cf = this.clazz.getClassFile();
			String accName = this.findAccessorName(cf);

			try {
				ConstPool e = cf.getConstPool();
				ClassPool pool = this.clazz.getClassPool();
				String fieldType = finfo.getDescriptor();
				String accDesc;
				if (is_static) {
					accDesc = "(" + fieldType + ")V";
				} else {
					accDesc = "(" + Descriptor.of(this.clazz) + fieldType + ")V";
				}

				MethodInfo minfo = new MethodInfo(e, accName, accDesc);
				minfo.setAccessFlags(8);
				minfo.addAttribute(new SyntheticAttribute(e));
				Bytecode code = new Bytecode(e);
				int reg;
				if (is_static) {
					reg = code.addLoad(0, Descriptor.toCtClass(fieldType, pool));
					code.addPutstatic(Bytecode.THIS, fieldName, fieldType);
				} else {
					code.addAload(0);
					reg = code.addLoad(1, Descriptor.toCtClass(fieldType, pool)) + 1;
					code.addPutfield(Bytecode.THIS, fieldName, fieldType);
				}

				code.addReturn((CtClass) null);
				code.setMaxLocals(reg);
				minfo.setCodeAttribute(code.toCodeAttribute());
				cf.addMethod(minfo);
				this.accessors.put(key, minfo);
				return minfo;
			} catch (CannotCompileException arg14) {
				throw new CompileError(arg14);
			} catch (NotFoundException arg15) {
				throw new CompileError(arg15);
			}
		}
	}

	private String findAccessorName(ClassFile cf) {
		String accName;
		do {
			accName = "access$" + this.uniqueNumber++;
		} while (cf.getMethod(accName) != null);

		return accName;
	}
}
