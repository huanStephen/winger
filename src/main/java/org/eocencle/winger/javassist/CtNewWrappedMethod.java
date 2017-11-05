package org.eocencle.winger.javassist;

import java.util.Hashtable;

import org.eocencle.winger.javassist.CtMember.Cache;
import org.eocencle.winger.javassist.CtMethod.ConstParameter;
import org.eocencle.winger.javassist.bytecode.AccessFlag;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.ClassFile;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.bytecode.SyntheticAttribute;
import org.eocencle.winger.javassist.compiler.JvstCodeGen;

public class CtNewWrappedMethod {
	private static final String addedWrappedMethod = "_added_m$";

	public static CtMethod wrapped(CtClass returnType, String mname, CtClass[] parameterTypes, CtClass[] exceptionTypes,
			CtMethod body, ConstParameter constParam, CtClass declaring) throws CannotCompileException {
		CtMethod mt = new CtMethod(returnType, mname, parameterTypes, declaring);
		mt.setModifiers(body.getModifiers());

		try {
			mt.setExceptionTypes(exceptionTypes);
		} catch (NotFoundException arg9) {
			throw new CannotCompileException(arg9);
		}

		Bytecode code = makeBody(declaring, declaring.getClassFile2(), body, parameterTypes, returnType, constParam);
		MethodInfo minfo = mt.getMethodInfo2();
		minfo.setCodeAttribute(code.toCodeAttribute());
		return mt;
	}

	static Bytecode makeBody(CtClass clazz, ClassFile classfile, CtMethod wrappedBody, CtClass[] parameters,
			CtClass returnType, ConstParameter cparam) throws CannotCompileException {
		boolean isStatic = Modifier.isStatic(wrappedBody.getModifiers());
		Bytecode code = new Bytecode(classfile.getConstPool(), 0, 0);
		int stacksize = makeBody0(clazz, classfile, wrappedBody, isStatic, parameters, returnType, cparam, code);
		code.setMaxStack(stacksize);
		code.setMaxLocals(isStatic, parameters, 0);
		return code;
	}

	protected static int makeBody0(CtClass clazz, ClassFile classfile, CtMethod wrappedBody, boolean isStatic,
			CtClass[] parameters, CtClass returnType, ConstParameter cparam, Bytecode code)
			throws CannotCompileException {
		if (!(clazz instanceof CtClassType)) {
			throw new CannotCompileException("bad declaring class" + clazz.getName());
		} else {
			if (!isStatic) {
				code.addAload(0);
			}

			int stacksize = compileParameterList(code, parameters, isStatic ? 0 : 1);
			int stacksize2;
			String desc;
			if (cparam == null) {
				stacksize2 = 0;
				desc = ConstParameter.defaultDescriptor();
			} else {
				stacksize2 = cparam.compile(code);
				desc = cparam.descriptor();
			}

			checkSignature(wrappedBody, desc);

			String bodyname;
			try {
				bodyname = addBodyMethod((CtClassType) clazz, classfile, wrappedBody);
			} catch (BadBytecode arg12) {
				throw new CannotCompileException(arg12);
			}

			if (isStatic) {
				code.addInvokestatic(Bytecode.THIS, bodyname, desc);
			} else {
				code.addInvokespecial(Bytecode.THIS, bodyname, desc);
			}

			compileReturn(code, returnType);
			if (stacksize < stacksize2 + 2) {
				stacksize = stacksize2 + 2;
			}

			return stacksize;
		}
	}

	private static void checkSignature(CtMethod wrappedBody, String descriptor) throws CannotCompileException {
		if (!descriptor.equals(wrappedBody.getMethodInfo2().getDescriptor())) {
			throw new CannotCompileException("wrapped method with a bad signature: "
					+ wrappedBody.getDeclaringClass().getName() + '.' + wrappedBody.getName());
		}
	}

	private static String addBodyMethod(CtClassType clazz, ClassFile classfile, CtMethod src)
			throws BadBytecode, CannotCompileException {
		Hashtable bodies = clazz.getHiddenMethods();
		String bodyname = (String) bodies.get(src);
		if (bodyname == null) {
			do {
				bodyname = "_added_m$" + clazz.getUniqueNumber();
			} while (classfile.getMethod(bodyname) != null);

			ClassMap map = new ClassMap();
			map.put(src.getDeclaringClass().getName(), clazz.getName());
			MethodInfo body = new MethodInfo(classfile.getConstPool(), bodyname, src.getMethodInfo2(), map);
			int acc = body.getAccessFlags();
			body.setAccessFlags(AccessFlag.setPrivate(acc));
			body.addAttribute(new SyntheticAttribute(classfile.getConstPool()));
			classfile.addMethod(body);
			bodies.put(src, bodyname);
			Cache cache = clazz.hasMemberCache();
			if (cache != null) {
				cache.addMethod(new CtMethod(body, clazz));
			}
		}

		return bodyname;
	}

	static int compileParameterList(Bytecode code, CtClass[] params, int regno) {
		return JvstCodeGen.compileParameterList(code, params, regno);
	}

	private static void compileReturn(Bytecode code, CtClass type) {
		if (type.isPrimitive()) {
			CtPrimitiveType pt = (CtPrimitiveType) type;
			if (pt != CtClass.voidType) {
				String wrapper = pt.getWrapperName();
				code.addCheckcast(wrapper);
				code.addInvokevirtual(wrapper, pt.getGetMethodName(), pt.getGetMethodDescriptor());
			}

			code.addOpcode(pt.getReturnOp());
		} else {
			code.addCheckcast(type);
			code.addOpcode(176);
		}

	}
}
