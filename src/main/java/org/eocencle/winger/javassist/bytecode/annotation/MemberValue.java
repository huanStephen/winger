package org.eocencle.winger.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;

import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.Descriptor;

public abstract class MemberValue {
	ConstPool cp;
	char tag;

	MemberValue(char tag, ConstPool cp) {
		this.cp = cp;
		this.tag = tag;
	}

	abstract Object getValue(ClassLoader arg0, ClassPool arg1, Method arg2) throws ClassNotFoundException;

	abstract Class getType(ClassLoader arg0) throws ClassNotFoundException;

	static Class loadClass(ClassLoader cl, String classname) throws ClassNotFoundException, NoSuchClassError {
		try {
			return Class.forName(convertFromArray(classname), true, cl);
		} catch (LinkageError arg2) {
			throw new NoSuchClassError(classname, arg2);
		}
	}

	private static String convertFromArray(String classname) {
		int index = classname.indexOf("[]");
		if (index == -1) {
			return classname;
		} else {
			String rawType = classname.substring(0, index);

			StringBuffer sb;
			for (sb = new StringBuffer(Descriptor.of(rawType)); index != -1; index = classname.indexOf("[]",
					index + 1)) {
				sb.insert(0, "[");
			}

			return sb.toString().replace('/', '.');
		}
	}

	public abstract void accept(MemberValueVisitor arg0);

	public abstract void write(AnnotationsWriter arg0) throws IOException;
}
