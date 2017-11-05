package org.eocencle.winger.javassist;

import org.eocencle.winger.javassist.CtMethod.ConstParameter;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.ClassFile;
import org.eocencle.winger.javassist.bytecode.Descriptor;

public class CtNewWrappedConstructor extends CtNewWrappedMethod {
	private static final int PASS_NONE = 0;
	private static final int PASS_PARAMS = 2;

	public static CtConstructor wrapped(CtClass[] parameterTypes, CtClass[] exceptionTypes, int howToCallSuper,
			CtMethod body, ConstParameter constParam, CtClass declaring) throws CannotCompileException {
		try {
			CtConstructor e = new CtConstructor(parameterTypes, declaring);
			e.setExceptionTypes(exceptionTypes);
			Bytecode code = makeBody(declaring, declaring.getClassFile2(), howToCallSuper, body, parameterTypes,
					constParam);
			e.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
			return e;
		} catch (NotFoundException arg7) {
			throw new CannotCompileException(arg7);
		}
	}

	protected static Bytecode makeBody(CtClass declaring, ClassFile classfile, int howToCallSuper, CtMethod wrappedBody,
			CtClass[] parameters, ConstParameter cparam) throws CannotCompileException {
		int superclazz = classfile.getSuperclassId();
		Bytecode code = new Bytecode(classfile.getConstPool(), 0, 0);
		code.setMaxLocals(false, parameters, 0);
		code.addAload(0);
		int stacksize;
		int stacksize2;
		if (howToCallSuper == 0) {
			stacksize = 1;
			code.addInvokespecial(superclazz, "<init>", "()V");
		} else if (howToCallSuper == 2) {
			stacksize = code.addLoadParameters(parameters, 1) + 1;
			code.addInvokespecial(superclazz, "<init>", Descriptor.ofConstructor(parameters));
		} else {
			stacksize = compileParameterList(code, parameters, 1);
			String desc;
			if (cparam == null) {
				stacksize2 = 2;
				desc = ConstParameter.defaultConstDescriptor();
			} else {
				stacksize2 = cparam.compile(code) + 2;
				desc = cparam.constDescriptor();
			}

			if (stacksize < stacksize2) {
				stacksize = stacksize2;
			}

			code.addInvokespecial(superclazz, "<init>", desc);
		}

		if (wrappedBody == null) {
			code.add(177);
		} else {
			stacksize2 = makeBody0(declaring, classfile, wrappedBody, false, parameters, CtClass.voidType, cparam,
					code);
			if (stacksize < stacksize2) {
				stacksize = stacksize2;
			}
		}

		code.setMaxStack(stacksize);
		return code;
	}
}
