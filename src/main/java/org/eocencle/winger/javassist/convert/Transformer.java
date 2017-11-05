package org.eocencle.winger.javassist.convert;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.bytecode.Opcode;

public abstract class Transformer implements Opcode {
	private Transformer next;

	public Transformer(Transformer t) {
		this.next = t;
	}

	public Transformer getNext() {
		return this.next;
	}

	public void initialize(ConstPool cp, CodeAttribute attr) {
	}

	public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException {
		this.initialize(cp, minfo.getCodeAttribute());
	}

	public void clean() {
	}

	public abstract int transform(CtClass arg0, int arg1, CodeIterator arg2, ConstPool arg3)
			throws CannotCompileException, BadBytecode;

	public int extraLocals() {
		return 0;
	}

	public int extraStack() {
		return 0;
	}
}
