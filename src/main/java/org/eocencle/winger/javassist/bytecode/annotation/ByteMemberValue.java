package org.eocencle.winger.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;

import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.bytecode.ConstPool;

public class ByteMemberValue extends MemberValue {
	int valueIndex;

	public ByteMemberValue(int index, ConstPool cp) {
		super('B', cp);
		this.valueIndex = index;
	}

	public ByteMemberValue(byte b, ConstPool cp) {
		super('B', cp);
		this.setValue(b);
	}

	public ByteMemberValue(ConstPool cp) {
		super('B', cp);
		this.setValue(0);
	}

	Object getValue(ClassLoader cl, ClassPool cp, Method m) {
		return new Integer(this.getValue());
	}

	Class getType(ClassLoader cl) {
		return Byte.TYPE;
	}

	public int getValue() {
		return this.cp.getIntegerInfo(this.valueIndex);
	}

	public void setValue(int newValue) {
		this.valueIndex = this.cp.addIntegerInfo(newValue);
	}

	public String toString() {
		return Integer.toString(this.getValue());
	}

	public void write(AnnotationsWriter writer) throws IOException {
		writer.constValueIndex(this.getValue());
	}

	public void accept(MemberValueVisitor visitor) {
		visitor.visitByteMemberValue(this);
	}
}
