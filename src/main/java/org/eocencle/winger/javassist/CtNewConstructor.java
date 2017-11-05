package org.eocencle.winger.javassist;

import org.eocencle.winger.javassist.CtMethod.ConstParameter;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.Javac;

public class CtNewConstructor {
	public static final int PASS_NONE = 0;
	public static final int PASS_ARRAY = 1;
	public static final int PASS_PARAMS = 2;

	public static CtConstructor make(String src, CtClass declaring) throws CannotCompileException {
		Javac compiler = new Javac(declaring);

		try {
			CtMember e = compiler.compile(src);
			if (e instanceof CtConstructor) {
				return (CtConstructor) e;
			}
		} catch (CompileError arg3) {
			throw new CannotCompileException(arg3);
		}

		throw new CannotCompileException("not a constructor");
	}

	public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring)
			throws CannotCompileException {
		try {
			CtConstructor e = new CtConstructor(parameters, declaring);
			e.setExceptionTypes(exceptions);
			e.setBody(body);
			return e;
		} catch (NotFoundException arg4) {
			throw new CannotCompileException(arg4);
		}
	}

	public static CtConstructor copy(CtConstructor c, CtClass declaring, ClassMap map) throws CannotCompileException {
		return new CtConstructor(c, declaring, map);
	}

	public static CtConstructor defaultConstructor(CtClass declaring) throws CannotCompileException {
		CtConstructor cons = new CtConstructor((CtClass[]) null, declaring);
		ConstPool cp = declaring.getClassFile2().getConstPool();
		Bytecode code = new Bytecode(cp, 1, 1);
		code.addAload(0);

		try {
			code.addInvokespecial(declaring.getSuperclass(), "<init>", "()V");
		} catch (NotFoundException arg4) {
			throw new CannotCompileException(arg4);
		}

		code.add(177);
		cons.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
		return cons;
	}

	public static CtConstructor skeleton(CtClass[] parameters, CtClass[] exceptions, CtClass declaring)
			throws CannotCompileException {
		return make(parameters, exceptions, 0, (CtMethod) null, (ConstParameter) null, declaring);
	}

	public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, CtClass declaring)
			throws CannotCompileException {
		return make(parameters, exceptions, 2, (CtMethod) null, (ConstParameter) null, declaring);
	}

	public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, int howto, CtMethod body,
			ConstParameter cparam, CtClass declaring) throws CannotCompileException {
		return CtNewWrappedConstructor.wrapped(parameters, exceptions, howto, body, cparam, declaring);
	}
}
