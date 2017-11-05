package org.eocencle.winger.javassist.convert;

import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtField;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;

public class TransformWriteField extends TransformReadField {
	public TransformWriteField(Transformer next, CtField field, String methodClassname, String methodName) {
		super(next, field, methodClassname, methodName);
	}

	public int transform(CtClass tclazz, int pos, CodeIterator iterator, ConstPool cp) throws BadBytecode {
		int c = iterator.byteAt(pos);
		if (c == 181 || c == 179) {
			int index = iterator.u16bitAt(pos + 1);
			String typedesc = isField(tclazz.getClassPool(), cp, this.fieldClass, this.fieldname, this.isPrivate,
					index);
			if (typedesc != null) {
				if (c == 179) {
					CodeAttribute mi = iterator.get();
					iterator.move(pos);
					char type = typedesc.charAt(0);
					if (type != 74 && type != 68) {
						pos = iterator.insertGap(2);
						iterator.writeByte(1, pos);
						iterator.writeByte(95, pos + 1);
						mi.setMaxStack(mi.getMaxStack() + 1);
					} else {
						pos = iterator.insertGap(3);
						iterator.writeByte(1, pos);
						iterator.writeByte(91, pos + 1);
						iterator.writeByte(87, pos + 2);
						mi.setMaxStack(mi.getMaxStack() + 2);
					}

					pos = iterator.next();
				}

				int mi1 = cp.addClassInfo(this.methodClassname);
				String type1 = "(Ljava/lang/Object;" + typedesc + ")V";
				int methodref = cp.addMethodrefInfo(mi1, this.methodName, type1);
				iterator.writeByte(184, pos);
				iterator.write16bit(methodref, pos + 1);
			}
		}

		return pos;
	}
}
