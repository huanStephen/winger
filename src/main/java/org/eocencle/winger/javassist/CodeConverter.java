package org.eocencle.winger.javassist;

import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.convert.TransformAccessArrayField;
import org.eocencle.winger.javassist.convert.TransformAfter;
import org.eocencle.winger.javassist.convert.TransformBefore;
import org.eocencle.winger.javassist.convert.TransformCall;
import org.eocencle.winger.javassist.convert.TransformFieldAccess;
import org.eocencle.winger.javassist.convert.TransformNew;
import org.eocencle.winger.javassist.convert.TransformNewClass;
import org.eocencle.winger.javassist.convert.TransformReadField;
import org.eocencle.winger.javassist.convert.TransformWriteField;
import org.eocencle.winger.javassist.convert.Transformer;

public class CodeConverter {
	protected Transformer transformers = null;

	public void replaceNew(CtClass newClass, CtClass calledClass, String calledMethod) {
		this.transformers = new TransformNew(this.transformers, newClass.getName(), calledClass.getName(),
				calledMethod);
	}

	public void replaceNew(CtClass oldClass, CtClass newClass) {
		this.transformers = new TransformNewClass(this.transformers, oldClass.getName(), newClass.getName());
	}

	public void redirectFieldAccess(CtField field, CtClass newClass, String newFieldname) {
		this.transformers = new TransformFieldAccess(this.transformers, field, newClass.getName(), newFieldname);
	}

	public void replaceFieldRead(CtField field, CtClass calledClass, String calledMethod) {
		this.transformers = new TransformReadField(this.transformers, field, calledClass.getName(), calledMethod);
	}

	public void replaceFieldWrite(CtField field, CtClass calledClass, String calledMethod) {
		this.transformers = new TransformWriteField(this.transformers, field, calledClass.getName(), calledMethod);
	}

	public void replaceArrayAccess(CtClass calledClass, CodeConverter.ArrayAccessReplacementMethodNames names)
			throws NotFoundException {
		this.transformers = new TransformAccessArrayField(this.transformers, calledClass.getName(), names);
	}

	public void redirectMethodCall(CtMethod origMethod, CtMethod substMethod) throws CannotCompileException {
		String d1 = origMethod.getMethodInfo2().getDescriptor();
		String d2 = substMethod.getMethodInfo2().getDescriptor();
		if (!d1.equals(d2)) {
			throw new CannotCompileException("signature mismatch: " + substMethod.getLongName());
		} else {
			int mod1 = origMethod.getModifiers();
			int mod2 = substMethod.getModifiers();
			if (Modifier.isStatic(mod1) == Modifier.isStatic(mod2)
					&& (!Modifier.isPrivate(mod1) || Modifier.isPrivate(mod2))
					&& origMethod.getDeclaringClass().isInterface() == substMethod.getDeclaringClass().isInterface()) {
				this.transformers = new TransformCall(this.transformers, origMethod, substMethod);
			} else {
				throw new CannotCompileException("invoke-type mismatch " + substMethod.getLongName());
			}
		}
	}

	public void redirectMethodCall(String oldMethodName, CtMethod newMethod) throws CannotCompileException {
		this.transformers = new TransformCall(this.transformers, oldMethodName, newMethod);
	}

	public void insertBeforeMethod(CtMethod origMethod, CtMethod beforeMethod) throws CannotCompileException {
		try {
			this.transformers = new TransformBefore(this.transformers, origMethod, beforeMethod);
		} catch (NotFoundException arg3) {
			throw new CannotCompileException(arg3);
		}
	}

	public void insertAfterMethod(CtMethod origMethod, CtMethod afterMethod) throws CannotCompileException {
		try {
			this.transformers = new TransformAfter(this.transformers, origMethod, afterMethod);
		} catch (NotFoundException arg3) {
			throw new CannotCompileException(arg3);
		}
	}

	protected void doit(CtClass clazz, MethodInfo minfo, ConstPool cp) throws CannotCompileException {
		CodeAttribute codeAttr = minfo.getCodeAttribute();
		if (codeAttr != null && this.transformers != null) {
			Transformer t;
			for (t = this.transformers; t != null; t = t.getNext()) {
				t.initialize(cp, clazz, minfo);
			}

			CodeIterator iterator = codeAttr.iterator();

			int locals;
			while (iterator.hasNext()) {
				try {
					locals = iterator.next();

					for (t = this.transformers; t != null; t = t.getNext()) {
						locals = t.transform(clazz, locals, iterator, cp);
					}
				} catch (BadBytecode arg10) {
					throw new CannotCompileException(arg10);
				}
			}

			locals = 0;
			int stack = 0;

			for (t = this.transformers; t != null; t = t.getNext()) {
				int b = t.extraLocals();
				if (b > locals) {
					locals = b;
				}

				b = t.extraStack();
				if (b > stack) {
					stack = b;
				}
			}

			for (t = this.transformers; t != null; t = t.getNext()) {
				t.clean();
			}

			if (locals > 0) {
				codeAttr.setMaxLocals(codeAttr.getMaxLocals() + locals);
			}

			if (stack > 0) {
				codeAttr.setMaxStack(codeAttr.getMaxStack() + stack);
			}

			try {
				minfo.rebuildStackMapIf6(clazz.getClassPool(), clazz.getClassFile2());
			} catch (BadBytecode arg9) {
				throw new CannotCompileException(arg9.getMessage(), arg9);
			}
		}
	}

	public static class DefaultArrayAccessReplacementMethodNames
			implements CodeConverter.ArrayAccessReplacementMethodNames {
		public String byteOrBooleanRead() {
			return "arrayReadByteOrBoolean";
		}

		public String byteOrBooleanWrite() {
			return "arrayWriteByteOrBoolean";
		}

		public String charRead() {
			return "arrayReadChar";
		}

		public String charWrite() {
			return "arrayWriteChar";
		}

		public String doubleRead() {
			return "arrayReadDouble";
		}

		public String doubleWrite() {
			return "arrayWriteDouble";
		}

		public String floatRead() {
			return "arrayReadFloat";
		}

		public String floatWrite() {
			return "arrayWriteFloat";
		}

		public String intRead() {
			return "arrayReadInt";
		}

		public String intWrite() {
			return "arrayWriteInt";
		}

		public String longRead() {
			return "arrayReadLong";
		}

		public String longWrite() {
			return "arrayWriteLong";
		}

		public String objectRead() {
			return "arrayReadObject";
		}

		public String objectWrite() {
			return "arrayWriteObject";
		}

		public String shortRead() {
			return "arrayReadShort";
		}

		public String shortWrite() {
			return "arrayWriteShort";
		}
	}

	public interface ArrayAccessReplacementMethodNames {
		String byteOrBooleanRead();

		String byteOrBooleanWrite();

		String charRead();

		String charWrite();

		String doubleRead();

		String doubleWrite();

		String floatRead();

		String floatWrite();

		String intRead();

		String intWrite();

		String longRead();

		String longWrite();

		String objectRead();

		String objectWrite();

		String shortRead();

		String shortWrite();
	}
}
