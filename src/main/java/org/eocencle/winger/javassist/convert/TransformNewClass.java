package org.eocencle.winger.javassist.convert;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;

public class TransformNewClass extends Transformer {
	private int nested;
	private String classname;
	private String newClassName;
	private int newClassIndex;
	private int newMethodNTIndex;
	private int newMethodIndex;

	public TransformNewClass(Transformer next, String classname, String newClassName) {
		super(next);
		this.classname = classname;
		this.newClassName = newClassName;
	}

	public void initialize(ConstPool cp, CodeAttribute attr) {
		this.nested = 0;
		this.newClassIndex = this.newMethodNTIndex = this.newMethodIndex = 0;
	}

	public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp) throws CannotCompileException {
		int c = iterator.byteAt(pos);
		int index;
		if (c == 187) {
			index = iterator.u16bitAt(pos + 1);
			if (cp.getClassInfo(index).equals(this.classname)) {
				if (iterator.byteAt(pos + 3) != 89) {
					throw new CannotCompileException("NEW followed by no DUP was found");
				}

				if (this.newClassIndex == 0) {
					this.newClassIndex = cp.addClassInfo(this.newClassName);
				}

				iterator.write16bit(this.newClassIndex, pos + 1);
				++this.nested;
			}
		} else if (c == 183) {
			index = iterator.u16bitAt(pos + 1);
			int typedesc = cp.isConstructor(this.classname, index);
			if (typedesc != 0 && this.nested > 0) {
				int nt = cp.getMethodrefNameAndType(index);
				if (this.newMethodNTIndex != nt) {
					this.newMethodNTIndex = nt;
					this.newMethodIndex = cp.addMethodrefInfo(this.newClassIndex, nt);
				}

				iterator.write16bit(this.newMethodIndex, pos + 1);
				--this.nested;
			}
		}

		return pos;
	}
}
