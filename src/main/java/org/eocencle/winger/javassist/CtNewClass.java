package org.eocencle.winger.javassist;

import java.io.DataOutputStream;
import java.io.IOException;

import org.eocencle.winger.javassist.bytecode.ClassFile;

public class CtNewClass extends CtClassType {
	protected boolean hasConstructor;

	CtNewClass(String name, ClassPool cp, boolean isInterface, CtClass superclass) {
		super(name, cp);
		this.wasChanged = true;
		String superName;
		if (!isInterface && superclass != null) {
			superName = superclass.getName();
		} else {
			superName = null;
		}

		this.classfile = new ClassFile(isInterface, name, superName);
		if (isInterface && superclass != null) {
			this.classfile.setInterfaces(new String[] { superclass.getName() });
		}

		this.setModifiers(Modifier.setPublic(this.getModifiers()));
		this.hasConstructor = isInterface;
	}

	protected void extendToString(StringBuffer buffer) {
		if (this.hasConstructor) {
			buffer.append("hasConstructor ");
		}

		super.extendToString(buffer);
	}

	public void addConstructor(CtConstructor c) throws CannotCompileException {
		this.hasConstructor = true;
		super.addConstructor(c);
	}

	public void toBytecode(DataOutputStream out) throws CannotCompileException, IOException {
		if (!this.hasConstructor) {
			try {
				this.inheritAllConstructors();
				this.hasConstructor = true;
			} catch (NotFoundException arg2) {
				throw new CannotCompileException(arg2);
			}
		}

		super.toBytecode(out);
	}

	public void inheritAllConstructors() throws CannotCompileException, NotFoundException {
		CtClass superclazz = this.getSuperclass();
		CtConstructor[] cs = superclazz.getDeclaredConstructors();
		int n = 0;

		for (int i = 0; i < cs.length; ++i) {
			CtConstructor c = cs[i];
			int mod = c.getModifiers();
			if (this.isInheritable(mod, superclazz)) {
				CtConstructor cons = CtNewConstructor.make(c.getParameterTypes(), c.getExceptionTypes(), this);
				cons.setModifiers(mod & 7);
				this.addConstructor(cons);
				++n;
			}
		}

		if (n < 1) {
			throw new CannotCompileException("no inheritable constructor in " + superclazz.getName());
		}
	}

	private boolean isInheritable(int mod, CtClass superclazz) {
		if (Modifier.isPrivate(mod)) {
			return false;
		} else if (Modifier.isPackage(mod)) {
			String pname = this.getPackageName();
			String pname2 = superclazz.getPackageName();
			return pname == null ? pname2 == null : pname.equals(pname2);
		} else {
			return true;
		}
	}
}
