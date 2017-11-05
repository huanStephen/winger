package org.eocencle.winger.javassist.convert;

import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtField;
import org.eocencle.winger.javassist.Modifier;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;

public class TransformFieldAccess extends Transformer {
	private String newClassname;
	private String newFieldname;
	private String fieldname;
	private CtClass fieldClass;
	private boolean isPrivate;
	private int newIndex;
	private ConstPool constPool;

	public TransformFieldAccess(Transformer next, CtField field, String newClassname, String newFieldname) {
		super(next);
		this.fieldClass = field.getDeclaringClass();
		this.fieldname = field.getName();
		this.isPrivate = Modifier.isPrivate(field.getModifiers());
		this.newClassname = newClassname;
		this.newFieldname = newFieldname;
		this.constPool = null;
	}

	public void initialize(ConstPool cp, CodeAttribute attr) {
		if (this.constPool != cp) {
			this.newIndex = 0;
		}

	}

	public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp) {
		int c = iterator.byteAt(pos);
		if (c == 180 || c == 178 || c == 181 || c == 179) {
			int index = iterator.u16bitAt(pos + 1);
			String typedesc = TransformReadField.isField(clazz.getClassPool(), cp, this.fieldClass, this.fieldname,
					this.isPrivate, index);
			if (typedesc != null) {
				if (this.newIndex == 0) {
					int nt = cp.addNameAndTypeInfo(this.newFieldname, typedesc);
					this.newIndex = cp.addFieldrefInfo(cp.addClassInfo(this.newClassname), nt);
					this.constPool = cp;
				}

				iterator.write16bit(this.newIndex, pos + 1);
			}
		}

		return pos;
	}
}
