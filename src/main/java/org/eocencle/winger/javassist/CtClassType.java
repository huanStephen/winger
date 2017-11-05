package org.eocencle.winger.javassist;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eocencle.winger.javassist.CtField.Initializer;
import org.eocencle.winger.javassist.CtMember.Cache;
import org.eocencle.winger.javassist.bytecode.AccessFlag;
import org.eocencle.winger.javassist.bytecode.AnnotationsAttribute;
import org.eocencle.winger.javassist.bytecode.AttributeInfo;
import org.eocencle.winger.javassist.bytecode.BadBytecode;
import org.eocencle.winger.javassist.bytecode.Bytecode;
import org.eocencle.winger.javassist.bytecode.ClassFile;
import org.eocencle.winger.javassist.bytecode.CodeAttribute;
import org.eocencle.winger.javassist.bytecode.CodeIterator;
import org.eocencle.winger.javassist.bytecode.ConstPool;
import org.eocencle.winger.javassist.bytecode.ConstantAttribute;
import org.eocencle.winger.javassist.bytecode.Descriptor;
import org.eocencle.winger.javassist.bytecode.EnclosingMethodAttribute;
import org.eocencle.winger.javassist.bytecode.FieldInfo;
import org.eocencle.winger.javassist.bytecode.InnerClassesAttribute;
import org.eocencle.winger.javassist.bytecode.MethodInfo;
import org.eocencle.winger.javassist.bytecode.ParameterAnnotationsAttribute;
import org.eocencle.winger.javassist.bytecode.SignatureAttribute;
import org.eocencle.winger.javassist.bytecode.annotation.Annotation;
import org.eocencle.winger.javassist.bytecode.annotation.AnnotationImpl;
import org.eocencle.winger.javassist.compiler.AccessorMaker;
import org.eocencle.winger.javassist.compiler.CompileError;
import org.eocencle.winger.javassist.compiler.Javac;
import org.eocencle.winger.javassist.expr.ExprEditor;

public class CtClassType extends CtClass {
	ClassPool classPool;
	boolean wasChanged;
	private boolean wasFrozen;
	boolean wasPruned;
	boolean gcConstPool;
	ClassFile classfile;
	byte[] rawClassfile;
	private WeakReference memberCache;
	private AccessorMaker accessors;
	private FieldInitLink fieldInitializers;
	private Hashtable hiddenMethods;
	private int uniqueNumberSeed;
	private boolean doPruning;
	private int getCount;
	private static final int GET_THRESHOLD = 2;

	CtClassType(String name, ClassPool cp) {
		super(name);
		this.doPruning = ClassPool.doPruning;
		this.classPool = cp;
		this.wasChanged = this.wasFrozen = this.wasPruned = this.gcConstPool = false;
		this.classfile = null;
		this.rawClassfile = null;
		this.memberCache = null;
		this.accessors = null;
		this.fieldInitializers = null;
		this.hiddenMethods = null;
		this.uniqueNumberSeed = 0;
		this.getCount = 0;
	}

	CtClassType(InputStream ins, ClassPool cp) throws IOException {
		this((String) null, cp);
		this.classfile = new ClassFile(new DataInputStream(ins));
		this.qualifiedName = this.classfile.getName();
	}

	CtClassType(ClassFile cf, ClassPool cp) {
		this((String) null, cp);
		this.classfile = cf;
		this.qualifiedName = this.classfile.getName();
	}

	protected void extendToString(StringBuffer buffer) {
		if (this.wasChanged) {
			buffer.append("changed ");
		}

		if (this.wasFrozen) {
			buffer.append("frozen ");
		}

		if (this.wasPruned) {
			buffer.append("pruned ");
		}

		buffer.append(Modifier.toString(this.getModifiers()));
		buffer.append(" class ");
		buffer.append(this.getName());

		try {
			CtClass memCache = this.getSuperclass();
			if (memCache != null) {
				String i = memCache.getName();
				if (!i.equals("java.lang.Object")) {
					buffer.append(" extends " + memCache.getName());
				}
			}
		} catch (NotFoundException arg3) {
			buffer.append(" extends ??");
		}

		try {
			CtClass[] arg5 = this.getInterfaces();
			if (arg5.length > 0) {
				buffer.append(" implements ");
			}

			for (int arg7 = 0; arg7 < arg5.length; ++arg7) {
				buffer.append(arg5[arg7].getName());
				buffer.append(", ");
			}
		} catch (NotFoundException arg4) {
			buffer.append(" extends ??");
		}

		Cache arg6 = this.getMembers();
		this.exToString(buffer, " fields=", arg6.fieldHead(), arg6.lastField());
		this.exToString(buffer, " constructors=", arg6.consHead(), arg6.lastCons());
		this.exToString(buffer, " methods=", arg6.methodHead(), arg6.lastMethod());
	}

	private void exToString(StringBuffer buffer, String msg, CtMember head, CtMember tail) {
		buffer.append(msg);

		while (head != tail) {
			head = head.next();
			buffer.append(head);
			buffer.append(", ");
		}

	}

	public AccessorMaker getAccessorMaker() {
		if (this.accessors == null) {
			this.accessors = new AccessorMaker(this);
		}

		return this.accessors;
	}

	public ClassFile getClassFile2() {
		ClassFile cfile = this.classfile;
		if (cfile != null) {
			return cfile;
		} else {
			this.classPool.compress();
			if (this.rawClassfile != null) {
				try {
					this.classfile = new ClassFile(new DataInputStream(new ByteArrayInputStream(this.rawClassfile)));
					this.rawClassfile = null;
					this.getCount = 2;
					return this.classfile;
				} catch (IOException arg14) {
					throw new RuntimeException(arg14.toString(), arg14);
				}
			} else {
				Object fin = null;

				ClassFile arg3;
				try {
					fin = this.classPool.openClassfile(this.getName());
					if (fin == null) {
						throw new NotFoundException(this.getName());
					}

					fin = new BufferedInputStream((InputStream) fin);
					ClassFile e = new ClassFile(new DataInputStream((InputStream) fin));
					if (!e.getName().equals(this.qualifiedName)) {
						throw new RuntimeException("cannot find " + this.qualifiedName + ": " + e.getName()
								+ " found in " + this.qualifiedName.replace('.', '/') + ".class");
					}

					this.classfile = e;
					arg3 = e;
				} catch (NotFoundException arg15) {
					throw new RuntimeException(arg15.toString(), arg15);
				} catch (IOException arg16) {
					throw new RuntimeException(arg16.toString(), arg16);
				} finally {
					if (fin != null) {
						try {
							((InputStream) fin).close();
						} catch (IOException arg13) {
							;
						}
					}

				}

				return arg3;
			}
		}
	}

	final void incGetCounter() {
		++this.getCount;
	}

	void compress() {
		if (this.getCount < 2) {
			if (!this.isModified() && ClassPool.releaseUnmodifiedClassFile) {
				this.removeClassFile();
			} else if (this.isFrozen() && !this.wasPruned) {
				this.saveClassFile();
			}
		}

		this.getCount = 0;
	}

	private synchronized void saveClassFile() {
		if (this.classfile != null && this.hasMemberCache() == null) {
			ByteArrayOutputStream barray = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(barray);

			try {
				this.classfile.write(out);
				barray.close();
				this.rawClassfile = barray.toByteArray();
				this.classfile = null;
			} catch (IOException arg3) {
				;
			}

		}
	}

	private synchronized void removeClassFile() {
		if (this.classfile != null && !this.isModified() && this.hasMemberCache() == null) {
			this.classfile = null;
		}

	}

	public ClassPool getClassPool() {
		return this.classPool;
	}

	void setClassPool(ClassPool cp) {
		this.classPool = cp;
	}

	public URL getURL() throws NotFoundException {
		URL url = this.classPool.find(this.getName());
		if (url == null) {
			throw new NotFoundException(this.getName());
		} else {
			return url;
		}
	}

	public boolean isModified() {
		return this.wasChanged;
	}

	public boolean isFrozen() {
		return this.wasFrozen;
	}

	public void freeze() {
		this.wasFrozen = true;
	}

	void checkModify() throws RuntimeException {
		if (this.isFrozen()) {
			String msg = this.getName() + " class is frozen";
			if (this.wasPruned) {
				msg = msg + " and pruned";
			}

			throw new RuntimeException(msg);
		} else {
			this.wasChanged = true;
		}
	}

	public void defrost() {
		this.checkPruned("defrost");
		this.wasFrozen = false;
	}

	public boolean subtypeOf(CtClass clazz) throws NotFoundException {
		String cname = clazz.getName();
		if (this != clazz && !this.getName().equals(cname)) {
			ClassFile file = this.getClassFile2();
			String supername = file.getSuperclass();
			if (supername != null && supername.equals(cname)) {
				return true;
			} else {
				String[] ifs = file.getInterfaces();
				int num = ifs.length;

				int i;
				for (i = 0; i < num; ++i) {
					if (ifs[i].equals(cname)) {
						return true;
					}
				}

				if (supername != null && this.classPool.get(supername).subtypeOf(clazz)) {
					return true;
				} else {
					for (i = 0; i < num; ++i) {
						if (this.classPool.get(ifs[i]).subtypeOf(clazz)) {
							return true;
						}
					}

					return false;
				}
			}
		} else {
			return true;
		}
	}

	public void setName(String name) throws RuntimeException {
		String oldname = this.getName();
		if (!name.equals(oldname)) {
			this.classPool.checkNotFrozen(name);
			ClassFile cf = this.getClassFile2();
			super.setName(name);
			cf.setName(name);
			this.nameReplaced();
			this.classPool.classNameChanged(oldname, this);
		}
	}

	public String getGenericSignature() {
		SignatureAttribute sa = (SignatureAttribute) this.getClassFile2().getAttribute("Signature");
		return sa == null ? null : sa.getSignature();
	}

	public void setGenericSignature(String sig) {
		ClassFile cf = this.getClassFile();
		SignatureAttribute sa = new SignatureAttribute(cf.getConstPool(), sig);
		cf.addAttribute(sa);
	}

	public void replaceClassName(ClassMap classnames) throws RuntimeException {
		String oldClassName = this.getName();
		String newClassName = (String) classnames.get(Descriptor.toJvmName(oldClassName));
		if (newClassName != null) {
			newClassName = Descriptor.toJavaName(newClassName);
			this.classPool.checkNotFrozen(newClassName);
		}

		super.replaceClassName(classnames);
		ClassFile cf = this.getClassFile2();
		cf.renameClass(classnames);
		this.nameReplaced();
		if (newClassName != null) {
			super.setName(newClassName);
			this.classPool.classNameChanged(oldClassName, this);
		}

	}

	public void replaceClassName(String oldname, String newname) throws RuntimeException {
		String thisname = this.getName();
		if (thisname.equals(oldname)) {
			this.setName(newname);
		} else {
			super.replaceClassName(oldname, newname);
			this.getClassFile2().renameClass(oldname, newname);
			this.nameReplaced();
		}

	}

	public boolean isInterface() {
		return Modifier.isInterface(this.getModifiers());
	}

	public boolean isAnnotation() {
		return Modifier.isAnnotation(this.getModifiers());
	}

	public boolean isEnum() {
		return Modifier.isEnum(this.getModifiers());
	}

	public int getModifiers() {
		ClassFile cf = this.getClassFile2();
		int acc = cf.getAccessFlags();
		acc = AccessFlag.clear(acc, 32);
		int inner = cf.getInnerAccessFlags();
		if (inner != -1 && (inner & 8) != 0) {
			acc |= 8;
		}

		return AccessFlag.toModifier(acc);
	}

	public CtClass[] getNestedClasses() throws NotFoundException {
		ClassFile cf = this.getClassFile2();
		InnerClassesAttribute ica = (InnerClassesAttribute) cf.getAttribute("InnerClasses");
		if (ica == null) {
			return new CtClass[0];
		} else {
			String thisName = cf.getName() + "$";
			int n = ica.tableLength();
			ArrayList list = new ArrayList(n);

			for (int i = 0; i < n; ++i) {
				String name = ica.innerClass(i);
				if (name != null && name.startsWith(thisName) && name.lastIndexOf(36) < thisName.length()) {
					list.add(this.classPool.get(name));
				}
			}

			return (CtClass[]) ((CtClass[]) list.toArray(new CtClass[list.size()]));
		}
	}

	public void setModifiers(int mod) {
		ClassFile cf = this.getClassFile2();
		if (Modifier.isStatic(mod)) {
			int flags = cf.getInnerAccessFlags();
			if (flags == -1 || (flags & 8) == 0) {
				throw new RuntimeException("cannot change " + this.getName() + " into a static class");
			}

			mod &= -9;
		}

		this.checkModify();
		cf.setAccessFlags(AccessFlag.of(mod));
	}

	public boolean hasAnnotation(Class clz) {
		ClassFile cf = this.getClassFile2();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) cf.getAttribute("RuntimeInvisibleAnnotations");
		AnnotationsAttribute ainfo2 = (AnnotationsAttribute) cf.getAttribute("RuntimeVisibleAnnotations");
		return hasAnnotationType(clz, this.getClassPool(), ainfo, ainfo2);
	}

	static boolean hasAnnotationType(Class clz, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2) {
		Annotation[] anno1;
		if (a1 == null) {
			anno1 = null;
		} else {
			anno1 = a1.getAnnotations();
		}

		Annotation[] anno2;
		if (a2 == null) {
			anno2 = null;
		} else {
			anno2 = a2.getAnnotations();
		}

		String typeName = clz.getName();
		int i;
		if (anno1 != null) {
			for (i = 0; i < anno1.length; ++i) {
				if (anno1[i].getTypeName().equals(typeName)) {
					return true;
				}
			}
		}

		if (anno2 != null) {
			for (i = 0; i < anno2.length; ++i) {
				if (anno2[i].getTypeName().equals(typeName)) {
					return true;
				}
			}
		}

		return false;
	}

	public Object getAnnotation(Class clz) throws ClassNotFoundException {
		ClassFile cf = this.getClassFile2();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) cf.getAttribute("RuntimeInvisibleAnnotations");
		AnnotationsAttribute ainfo2 = (AnnotationsAttribute) cf.getAttribute("RuntimeVisibleAnnotations");
		return getAnnotationType(clz, this.getClassPool(), ainfo, ainfo2);
	}

	static Object getAnnotationType(Class clz, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2)
			throws ClassNotFoundException {
		Annotation[] anno1;
		if (a1 == null) {
			anno1 = null;
		} else {
			anno1 = a1.getAnnotations();
		}

		Annotation[] anno2;
		if (a2 == null) {
			anno2 = null;
		} else {
			anno2 = a2.getAnnotations();
		}

		String typeName = clz.getName();
		int i;
		if (anno1 != null) {
			for (i = 0; i < anno1.length; ++i) {
				if (anno1[i].getTypeName().equals(typeName)) {
					return toAnnoType(anno1[i], cp);
				}
			}
		}

		if (anno2 != null) {
			for (i = 0; i < anno2.length; ++i) {
				if (anno2[i].getTypeName().equals(typeName)) {
					return toAnnoType(anno2[i], cp);
				}
			}
		}

		return null;
	}

	public Object[] getAnnotations() throws ClassNotFoundException {
		return this.getAnnotations(false);
	}

	public Object[] getAvailableAnnotations() {
		try {
			return this.getAnnotations(true);
		} catch (ClassNotFoundException arg1) {
			throw new RuntimeException("Unexpected exception ", arg1);
		}
	}

	private Object[] getAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
		ClassFile cf = this.getClassFile2();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) cf.getAttribute("RuntimeInvisibleAnnotations");
		AnnotationsAttribute ainfo2 = (AnnotationsAttribute) cf.getAttribute("RuntimeVisibleAnnotations");
		return toAnnotationType(ignoreNotFound, this.getClassPool(), ainfo, ainfo2);
	}

	static Object[] toAnnotationType(boolean ignoreNotFound, ClassPool cp, AnnotationsAttribute a1,
			AnnotationsAttribute a2) throws ClassNotFoundException {
		Annotation[] anno1;
		int size1;
		if (a1 == null) {
			anno1 = null;
			size1 = 0;
		} else {
			anno1 = a1.getAnnotations();
			size1 = anno1.length;
		}

		Annotation[] anno2;
		int size2;
		if (a2 == null) {
			anno2 = null;
			size2 = 0;
		} else {
			anno2 = a2.getAnnotations();
			size2 = anno2.length;
		}

		int j;
		if (!ignoreNotFound) {
			Object[] arg12 = new Object[size1 + size2];

			for (j = 0; j < size1; ++j) {
				arg12[j] = toAnnoType(anno1[j], cp);
			}

			for (j = 0; j < size2; ++j) {
				arg12[j + size1] = toAnnoType(anno2[j], cp);
			}

			return arg12;
		} else {
			ArrayList annotations = new ArrayList();

			for (j = 0; j < size1; ++j) {
				try {
					annotations.add(toAnnoType(anno1[j], cp));
				} catch (ClassNotFoundException arg11) {
					;
				}
			}

			for (j = 0; j < size2; ++j) {
				try {
					annotations.add(toAnnoType(anno2[j], cp));
				} catch (ClassNotFoundException arg10) {
					;
				}
			}

			return annotations.toArray();
		}
	}

	static Object[][] toAnnotationType(boolean ignoreNotFound, ClassPool cp, ParameterAnnotationsAttribute a1,
			ParameterAnnotationsAttribute a2, MethodInfo minfo) throws ClassNotFoundException {
		boolean numParameters = false;
		int arg16;
		if (a1 != null) {
			arg16 = a1.numParameters();
		} else if (a2 != null) {
			arg16 = a2.numParameters();
		} else {
			arg16 = Descriptor.numOfParameters(minfo.getDescriptor());
		}

		Object[][] result = new Object[arg16][];

		for (int i = 0; i < arg16; ++i) {
			Annotation[] anno1;
			int size1;
			if (a1 == null) {
				anno1 = null;
				size1 = 0;
			} else {
				anno1 = a1.getAnnotations()[i];
				size1 = anno1.length;
			}

			Annotation[] anno2;
			int size2;
			if (a2 == null) {
				anno2 = null;
				size2 = 0;
			} else {
				anno2 = a2.getAnnotations()[i];
				size2 = anno2.length;
			}

			if (!ignoreNotFound) {
				result[i] = new Object[size1 + size2];

				int arg17;
				for (arg17 = 0; arg17 < size1; ++arg17) {
					result[i][arg17] = toAnnoType(anno1[arg17], cp);
				}

				for (arg17 = 0; arg17 < size2; ++arg17) {
					result[i][arg17 + size1] = toAnnoType(anno2[arg17], cp);
				}
			} else {
				ArrayList annotations = new ArrayList();

				int j;
				for (j = 0; j < size1; ++j) {
					try {
						annotations.add(toAnnoType(anno1[j], cp));
					} catch (ClassNotFoundException arg15) {
						;
					}
				}

				for (j = 0; j < size2; ++j) {
					try {
						annotations.add(toAnnoType(anno2[j], cp));
					} catch (ClassNotFoundException arg14) {
						;
					}
				}

				result[i] = annotations.toArray();
			}
		}

		return result;
	}

	private static Object toAnnoType(Annotation anno, ClassPool cp) throws ClassNotFoundException {
		try {
			ClassLoader e = cp.getClassLoader();
			return anno.toAnnotationType(e, cp);
		} catch (ClassNotFoundException arg7) {
			ClassLoader cl2 = cp.getClass().getClassLoader();

			try {
				return anno.toAnnotationType(cl2, cp);
			} catch (ClassNotFoundException arg6) {
				try {
					Class e3 = cp.get(anno.getTypeName()).toClass();
					return AnnotationImpl.make(e3.getClassLoader(), e3, cp, anno);
				} catch (Throwable arg5) {
					throw new ClassNotFoundException(anno.getTypeName());
				}
			}
		}
	}

	public boolean subclassOf(CtClass superclass) {
		if (superclass == null) {
			return false;
		} else {
			String superName = superclass.getName();
			Object curr = this;

			try {
				while (curr != null) {
					if (((CtClass) curr).getName().equals(superName)) {
						return true;
					}

					curr = ((CtClass) curr).getSuperclass();
				}
			} catch (Exception arg4) {
				;
			}

			return false;
		}
	}

	public CtClass getSuperclass() throws NotFoundException {
		String supername = this.getClassFile2().getSuperclass();
		return supername == null ? null : this.classPool.get(supername);
	}

	public void setSuperclass(CtClass clazz) throws CannotCompileException {
		this.checkModify();
		if (this.isInterface()) {
			this.addInterface(clazz);
		} else {
			this.getClassFile2().setSuperclass(clazz.getName());
		}

	}

	public CtClass[] getInterfaces() throws NotFoundException {
		String[] ifs = this.getClassFile2().getInterfaces();
		int num = ifs.length;
		CtClass[] ifc = new CtClass[num];

		for (int i = 0; i < num; ++i) {
			ifc[i] = this.classPool.get(ifs[i]);
		}

		return ifc;
	}

	public void setInterfaces(CtClass[] list) {
		this.checkModify();
		String[] ifs;
		if (list == null) {
			ifs = new String[0];
		} else {
			int num = list.length;
			ifs = new String[num];

			for (int i = 0; i < num; ++i) {
				ifs[i] = list[i].getName();
			}
		}

		this.getClassFile2().setInterfaces(ifs);
	}

	public void addInterface(CtClass anInterface) {
		this.checkModify();
		if (anInterface != null) {
			this.getClassFile2().addInterface(anInterface.getName());
		}

	}

	public CtClass getDeclaringClass() throws NotFoundException {
		ClassFile cf = this.getClassFile2();
		InnerClassesAttribute ica = (InnerClassesAttribute) cf.getAttribute("InnerClasses");
		if (ica == null) {
			return null;
		} else {
			String name = this.getName();
			int n = ica.tableLength();

			for (int i = 0; i < n; ++i) {
				if (name.equals(ica.innerClass(i))) {
					String outName = ica.outerClass(i);
					if (outName != null) {
						return this.classPool.get(outName);
					}

					EnclosingMethodAttribute ema = (EnclosingMethodAttribute) cf.getAttribute("EnclosingMethod");
					if (ema != null) {
						return this.classPool.get(ema.className());
					}
				}
			}

			return null;
		}
	}

	public CtBehavior getEnclosingBehavior() throws NotFoundException {
		ClassFile cf = this.getClassFile2();
		EnclosingMethodAttribute ema = (EnclosingMethodAttribute) cf.getAttribute("EnclosingMethod");
		if (ema == null) {
			return null;
		} else {
			CtClass enc = this.classPool.get(ema.className());
			String name = ema.methodName();
			return (CtBehavior) ("<init>".equals(name) ? enc.getConstructor(ema.methodDescriptor())
					: ("<clinit>".equals(name) ? enc.getClassInitializer()
							: enc.getMethod(name, ema.methodDescriptor())));
		}
	}

	public CtClass makeNestedClass(String name, boolean isStatic) {
		if (!isStatic) {
			throw new RuntimeException("sorry, only nested static class is supported");
		} else {
			this.checkModify();
			CtClass c = this.classPool.makeNestedClass(this.getName() + "$" + name);
			ClassFile cf = this.getClassFile2();
			ClassFile cf2 = c.getClassFile2();
			InnerClassesAttribute ica = (InnerClassesAttribute) cf.getAttribute("InnerClasses");
			if (ica == null) {
				ica = new InnerClassesAttribute(cf.getConstPool());
				cf.addAttribute(ica);
			}

			ica.append(c.getName(), this.getName(), name, cf2.getAccessFlags() & -33 | 8);
			cf2.addAttribute(ica.copy(cf2.getConstPool(), (Map) null));
			return c;
		}
	}

	private void nameReplaced() {
		Cache cache = this.hasMemberCache();
		if (cache != null) {
			CtMember mth = cache.methodHead();
			CtMember tail = cache.lastMethod();

			while (mth != tail) {
				mth = mth.next();
				mth.nameReplaced();
			}
		}

	}

	protected Cache hasMemberCache() {
		return this.memberCache != null ? (Cache) this.memberCache.get() : null;
	}

	protected synchronized Cache getMembers() {
		Cache cache = null;
		if (this.memberCache == null || (cache = (Cache) this.memberCache.get()) == null) {
			cache = new Cache(this);
			this.makeFieldCache(cache);
			this.makeBehaviorCache(cache);
			this.memberCache = new WeakReference(cache);
		}

		return cache;
	}

	private void makeFieldCache(Cache cache) {
		List list = this.getClassFile2().getFields();
		int n = list.size();

		for (int i = 0; i < n; ++i) {
			FieldInfo finfo = (FieldInfo) list.get(i);
			CtField newField = new CtField(finfo, this);
			cache.addField(newField);
		}

	}

	private void makeBehaviorCache(Cache cache) {
		List list = this.getClassFile2().getMethods();
		int n = list.size();

		for (int i = 0; i < n; ++i) {
			MethodInfo minfo = (MethodInfo) list.get(i);
			if (minfo.isMethod()) {
				CtMethod newCons = new CtMethod(minfo, this);
				cache.addMethod(newCons);
			} else {
				CtConstructor arg6 = new CtConstructor(minfo, this);
				cache.addConstructor(arg6);
			}
		}

	}

	public CtField[] getFields() {
		ArrayList alist = new ArrayList();
		getFields(alist, this);
		return (CtField[]) ((CtField[]) alist.toArray(new CtField[alist.size()]));
	}

	private static void getFields(ArrayList alist, CtClass cc) {
		if (cc != null) {
			try {
				getFields(alist, cc.getSuperclass());
			} catch (NotFoundException arg6) {
				;
			}

			try {
				CtClass[] memCache = cc.getInterfaces();
				int num = memCache.length;

				for (int i = 0; i < num; ++i) {
					getFields(alist, memCache[i]);
				}
			} catch (NotFoundException arg7) {
				;
			}

			Cache arg8 = ((CtClassType) cc).getMembers();
			CtMember field = arg8.fieldHead();
			CtMember tail = arg8.lastField();

			while (field != tail) {
				field = field.next();
				if (!Modifier.isPrivate(field.getModifiers())) {
					alist.add(field);
				}
			}

		}
	}

	public CtField getField(String name, String desc) throws NotFoundException {
		CtField f = this.getField2(name, desc);
		return this.checkGetField(f, name, desc);
	}

	private CtField checkGetField(CtField f, String name, String desc) throws NotFoundException {
		if (f == null) {
			String msg = "field: " + name;
			if (desc != null) {
				msg = msg + " type " + desc;
			}

			throw new NotFoundException(msg + " in " + this.getName());
		} else {
			return f;
		}
	}

	CtField getField2(String name, String desc) {
		CtField df = this.getDeclaredField2(name, desc);
		if (df != null) {
			return df;
		} else {
			try {
				CtClass[] ifs = this.getInterfaces();
				int num = ifs.length;

				for (int s = 0; s < num; ++s) {
					CtField f = ifs[s].getField2(name, desc);
					if (f != null) {
						return f;
					}
				}

				CtClass arg8 = this.getSuperclass();
				if (arg8 != null) {
					return arg8.getField2(name, desc);
				}
			} catch (NotFoundException arg7) {
				;
			}

			return null;
		}
	}

	public CtField[] getDeclaredFields() {
		Cache memCache = this.getMembers();
		CtMember field = memCache.fieldHead();
		CtMember tail = memCache.lastField();
		int num = Cache.count(field, tail);
		CtField[] cfs = new CtField[num];

		for (int i = 0; field != tail; cfs[i++] = (CtField) field) {
			field = field.next();
		}

		return cfs;
	}

	public CtField getDeclaredField(String name) throws NotFoundException {
		return this.getDeclaredField(name, (String) null);
	}

	public CtField getDeclaredField(String name, String desc) throws NotFoundException {
		CtField f = this.getDeclaredField2(name, desc);
		return this.checkGetField(f, name, desc);
	}

	private CtField getDeclaredField2(String name, String desc) {
		Cache memCache = this.getMembers();
		CtMember field = memCache.fieldHead();
		CtMember tail = memCache.lastField();

		do {
			do {
				if (field == tail) {
					return null;
				}

				field = field.next();
			} while (!field.getName().equals(name));
		} while (desc != null && !desc.equals(field.getSignature()));

		return (CtField) field;
	}

	public CtBehavior[] getDeclaredBehaviors() {
		Cache memCache = this.getMembers();
		CtMember cons = memCache.consHead();
		CtMember consTail = memCache.lastCons();
		int cnum = Cache.count(cons, consTail);
		CtMember mth = memCache.methodHead();
		CtMember mthTail = memCache.lastMethod();
		int mnum = Cache.count(mth, mthTail);
		CtBehavior[] cb = new CtBehavior[cnum + mnum];

		int i;
		for (i = 0; cons != consTail; cb[i++] = (CtBehavior) cons) {
			cons = cons.next();
		}

		while (mth != mthTail) {
			mth = mth.next();
			cb[i++] = (CtBehavior) mth;
		}

		return cb;
	}

	public CtConstructor[] getConstructors() {
		Cache memCache = this.getMembers();
		CtMember cons = memCache.consHead();
		CtMember consTail = memCache.lastCons();
		int n = 0;
		CtMember mem = cons;

		while (mem != consTail) {
			mem = mem.next();
			if (isPubCons((CtConstructor) mem)) {
				++n;
			}
		}

		CtConstructor[] result = new CtConstructor[n];
		int i = 0;
		mem = cons;

		while (mem != consTail) {
			mem = mem.next();
			CtConstructor cc = (CtConstructor) mem;
			if (isPubCons(cc)) {
				result[i++] = cc;
			}
		}

		return result;
	}

	private static boolean isPubCons(CtConstructor cons) {
		return !Modifier.isPrivate(cons.getModifiers()) && cons.isConstructor();
	}

	public CtConstructor getConstructor(String desc) throws NotFoundException {
		Cache memCache = this.getMembers();
		CtMember cons = memCache.consHead();
		CtMember consTail = memCache.lastCons();

		CtConstructor cc;
		do {
			if (cons == consTail) {
				return super.getConstructor(desc);
			}

			cons = cons.next();
			cc = (CtConstructor) cons;
		} while (!cc.getMethodInfo2().getDescriptor().equals(desc) || !cc.isConstructor());

		return cc;
	}

	public CtConstructor[] getDeclaredConstructors() {
		Cache memCache = this.getMembers();
		CtMember cons = memCache.consHead();
		CtMember consTail = memCache.lastCons();
		int n = 0;
		CtMember mem = cons;

		while (mem != consTail) {
			mem = mem.next();
			CtConstructor result = (CtConstructor) mem;
			if (result.isConstructor()) {
				++n;
			}
		}

		CtConstructor[] arg8 = new CtConstructor[n];
		int i = 0;
		mem = cons;

		while (mem != consTail) {
			mem = mem.next();
			CtConstructor cc = (CtConstructor) mem;
			if (cc.isConstructor()) {
				arg8[i++] = cc;
			}
		}

		return arg8;
	}

	public CtConstructor getClassInitializer() {
		Cache memCache = this.getMembers();
		CtMember cons = memCache.consHead();
		CtMember consTail = memCache.lastCons();

		CtConstructor cc;
		do {
			if (cons == consTail) {
				return null;
			}

			cons = cons.next();
			cc = (CtConstructor) cons;
		} while (!cc.isClassInitializer());

		return cc;
	}

	public CtMethod[] getMethods() {
		HashMap h = new HashMap();
		getMethods0(h, this);
		return (CtMethod[]) ((CtMethod[]) h.values().toArray(new CtMethod[h.size()]));
	}

	private static void getMethods0(HashMap h, CtClass cc) {
		try {
			CtClass[] memCache = cc.getInterfaces();
			int mth = memCache.length;

			for (int mthTail = 0; mthTail < mth; ++mthTail) {
				getMethods0(h, memCache[mthTail]);
			}
		} catch (NotFoundException arg5) {
			;
		}

		try {
			CtClass arg6 = cc.getSuperclass();
			if (arg6 != null) {
				getMethods0(h, arg6);
			}
		} catch (NotFoundException arg4) {
			;
		}

		if (cc instanceof CtClassType) {
			Cache arg7 = ((CtClassType) cc).getMembers();
			CtMember arg8 = arg7.methodHead();
			CtMember arg9 = arg7.lastMethod();

			while (arg8 != arg9) {
				arg8 = arg8.next();
				if (!Modifier.isPrivate(arg8.getModifiers())) {
					h.put(((CtMethod) arg8).getStringRep(), arg8);
				}
			}
		}

	}

	public CtMethod getMethod(String name, String desc) throws NotFoundException {
		CtMethod m = getMethod0(this, name, desc);
		if (m != null) {
			return m;
		} else {
			throw new NotFoundException(name + "(..) is not found in " + this.getName());
		}
	}

	private static CtMethod getMethod0(CtClass cc, String name, String desc) {
		if (cc instanceof CtClassType) {
			Cache ifs = ((CtClassType) cc).getMembers();
			CtMember size = ifs.methodHead();
			CtMember i = ifs.lastMethod();

			while (size != i) {
				size = size.next();
				if (size.getName().equals(name) && ((CtMethod) size).getMethodInfo2().getDescriptor().equals(desc)) {
					return (CtMethod) size;
				}
			}
		}

		try {
			CtClass arg8 = cc.getSuperclass();
			if (arg8 != null) {
				CtMethod arg10 = getMethod0(arg8, name, desc);
				if (arg10 != null) {
					return arg10;
				}
			}
		} catch (NotFoundException arg6) {
			;
		}

		try {
			CtClass[] arg9 = cc.getInterfaces();
			int arg11 = arg9.length;

			for (int arg12 = 0; arg12 < arg11; ++arg12) {
				CtMethod m = getMethod0(arg9[arg12], name, desc);
				if (m != null) {
					return m;
				}
			}
		} catch (NotFoundException arg7) {
			;
		}

		return null;
	}

	public CtMethod[] getDeclaredMethods() {
		Cache memCache = this.getMembers();
		CtMember mth = memCache.methodHead();
		CtMember mthTail = memCache.lastMethod();
		int num = Cache.count(mth, mthTail);
		CtMethod[] cms = new CtMethod[num];

		for (int i = 0; mth != mthTail; cms[i++] = (CtMethod) mth) {
			mth = mth.next();
		}

		return cms;
	}

	public CtMethod[] getDeclaredMethods(String name) throws NotFoundException {
		Cache memCache = this.getMembers();
		CtMember mth = memCache.methodHead();
		CtMember mthTail = memCache.lastMethod();
		ArrayList methods = new ArrayList();

		while (mth != mthTail) {
			mth = mth.next();
			if (mth.getName().equals(name)) {
				methods.add((CtMethod) mth);
			}
		}

		return (CtMethod[]) methods.toArray(new CtMethod[methods.size()]);
	}

	public CtMethod getDeclaredMethod(String name) throws NotFoundException {
		Cache memCache = this.getMembers();
		CtMember mth = memCache.methodHead();
		CtMember mthTail = memCache.lastMethod();

		do {
			if (mth == mthTail) {
				throw new NotFoundException(name + "(..) is not found in " + this.getName());
			}

			mth = mth.next();
		} while (!mth.getName().equals(name));

		return (CtMethod) mth;
	}

	public CtMethod getDeclaredMethod(String name, CtClass[] params) throws NotFoundException {
		String desc = Descriptor.ofParameters(params);
		Cache memCache = this.getMembers();
		CtMember mth = memCache.methodHead();
		CtMember mthTail = memCache.lastMethod();

		do {
			if (mth == mthTail) {
				throw new NotFoundException(name + "(..) is not found in " + this.getName());
			}

			mth = mth.next();
		} while (!mth.getName().equals(name) || !((CtMethod) mth).getMethodInfo2().getDescriptor().startsWith(desc));

		return (CtMethod) mth;
	}

	public void addField(CtField f, String init) throws CannotCompileException {
		this.addField(f, Initializer.byExpr(init));
	}

	public void addField(CtField f, Initializer init) throws CannotCompileException {
		this.checkModify();
		if (f.getDeclaringClass() != this) {
			throw new CannotCompileException("cannot add");
		} else {
			if (init == null) {
				init = f.getInit();
			}

			if (init != null) {
				init.check(f.getSignature());
				int fil = f.getModifiers();
				if (Modifier.isStatic(fil) && Modifier.isFinal(fil)) {
					try {
						ConstPool link = this.getClassFile2().getConstPool();
						int index = init.getConstantValue(link, f.getType());
						if (index != 0) {
							f.getFieldInfo2().addAttribute(new ConstantAttribute(link, index));
							init = null;
						}
					} catch (NotFoundException arg5) {
						;
					}
				}
			}

			this.getMembers().addField(f);
			this.getClassFile2().addField(f.getFieldInfo2());
			if (init != null) {
				FieldInitLink fil1 = new FieldInitLink(f, init);
				FieldInitLink link1 = this.fieldInitializers;
				if (link1 == null) {
					this.fieldInitializers = fil1;
				} else {
					while (link1.next != null) {
						link1 = link1.next;
					}

					link1.next = fil1;
				}
			}

		}
	}

	public void removeField(CtField f) throws NotFoundException {
		this.checkModify();
		FieldInfo fi = f.getFieldInfo2();
		ClassFile cf = this.getClassFile2();
		if (cf.getFields().remove(fi)) {
			this.getMembers().remove(f);
			this.gcConstPool = true;
		} else {
			throw new NotFoundException(f.toString());
		}
	}

	public CtConstructor makeClassInitializer() throws CannotCompileException {
		CtConstructor clinit = this.getClassInitializer();
		if (clinit != null) {
			return clinit;
		} else {
			this.checkModify();
			ClassFile cf = this.getClassFile2();
			Bytecode code = new Bytecode(cf.getConstPool(), 0, 0);
			this.modifyClassConstructor(cf, code, 0, 0);
			return this.getClassInitializer();
		}
	}

	public void addConstructor(CtConstructor c) throws CannotCompileException {
		this.checkModify();
		if (c.getDeclaringClass() != this) {
			throw new CannotCompileException("cannot add");
		} else {
			this.getMembers().addConstructor(c);
			this.getClassFile2().addMethod(c.getMethodInfo2());
		}
	}

	public void removeConstructor(CtConstructor m) throws NotFoundException {
		this.checkModify();
		MethodInfo mi = m.getMethodInfo2();
		ClassFile cf = this.getClassFile2();
		if (cf.getMethods().remove(mi)) {
			this.getMembers().remove(m);
			this.gcConstPool = true;
		} else {
			throw new NotFoundException(m.toString());
		}
	}

	public void addMethod(CtMethod m) throws CannotCompileException {
		this.checkModify();
		if (m.getDeclaringClass() != this) {
			throw new CannotCompileException("bad declaring class");
		} else {
			int mod = m.getModifiers();
			if ((this.getModifiers() & 512) != 0) {
				m.setModifiers(mod | 1);
				if ((mod & 1024) == 0) {
					throw new CannotCompileException("an interface method must be abstract: " + m.toString());
				}
			}

			this.getMembers().addMethod(m);
			this.getClassFile2().addMethod(m.getMethodInfo2());
			if ((mod & 1024) != 0) {
				this.setModifiers(this.getModifiers() | 1024);
			}

		}
	}

	public void removeMethod(CtMethod m) throws NotFoundException {
		this.checkModify();
		MethodInfo mi = m.getMethodInfo2();
		ClassFile cf = this.getClassFile2();
		if (cf.getMethods().remove(mi)) {
			this.getMembers().remove(m);
			this.gcConstPool = true;
		} else {
			throw new NotFoundException(m.toString());
		}
	}

	public byte[] getAttribute(String name) {
		AttributeInfo ai = this.getClassFile2().getAttribute(name);
		return ai == null ? null : ai.get();
	}

	public void setAttribute(String name, byte[] data) {
		this.checkModify();
		ClassFile cf = this.getClassFile2();
		cf.addAttribute(new AttributeInfo(cf.getConstPool(), name, data));
	}

	public void instrument(CodeConverter converter) throws CannotCompileException {
		this.checkModify();
		ClassFile cf = this.getClassFile2();
		ConstPool cp = cf.getConstPool();
		List list = cf.getMethods();
		int n = list.size();

		for (int i = 0; i < n; ++i) {
			MethodInfo minfo = (MethodInfo) list.get(i);
			converter.doit(this, minfo, cp);
		}

	}

	public void instrument(ExprEditor editor) throws CannotCompileException {
		this.checkModify();
		ClassFile cf = this.getClassFile2();
		List list = cf.getMethods();
		int n = list.size();

		for (int i = 0; i < n; ++i) {
			MethodInfo minfo = (MethodInfo) list.get(i);
			editor.doit(this, minfo);
		}

	}

	public void prune() {
		if (!this.wasPruned) {
			this.wasPruned = this.wasFrozen = true;
			this.getClassFile2().prune();
		}
	}

	public void rebuildClassFile() {
		this.gcConstPool = true;
	}

	public void toBytecode(DataOutputStream out) throws CannotCompileException, IOException {
		try {
			if (this.isModified()) {
				this.checkPruned("toBytecode");
				ClassFile e = this.getClassFile2();
				if (this.gcConstPool) {
					e.compact();
					this.gcConstPool = false;
				}

				this.modifyClassConstructor(e);
				this.modifyConstructors(e);
				if (debugDump != null) {
					this.dumpClassFile(e);
				}

				e.write(out);
				out.flush();
				this.fieldInitializers = null;
				if (this.doPruning) {
					e.prune();
					this.wasPruned = true;
				}
			} else {
				this.classPool.writeClassfile(this.getName(), out);
			}

			this.getCount = 0;
			this.wasFrozen = true;
		} catch (NotFoundException arg2) {
			throw new CannotCompileException(arg2);
		} catch (IOException arg3) {
			throw new CannotCompileException(arg3);
		}
	}

	private void dumpClassFile(ClassFile cf) throws IOException {
		DataOutputStream dump = this.makeFileOutput(debugDump);

		try {
			cf.write(dump);
		} finally {
			dump.close();
		}

	}

	private void checkPruned(String method) {
		if (this.wasPruned) {
			throw new RuntimeException(method + "(): " + this.getName() + " was pruned.");
		}
	}

	public boolean stopPruning(boolean stop) {
		boolean prev = !this.doPruning;
		this.doPruning = !stop;
		return prev;
	}

	private void modifyClassConstructor(ClassFile cf) throws CannotCompileException, NotFoundException {
		if (this.fieldInitializers != null) {
			Bytecode code = new Bytecode(cf.getConstPool(), 0, 0);
			Javac jv = new Javac(code, this);
			int stacksize = 0;
			boolean doInit = false;

			for (FieldInitLink fi = this.fieldInitializers; fi != null; fi = fi.next) {
				CtField f = fi.field;
				if (Modifier.isStatic(f.getModifiers())) {
					doInit = true;
					int s = fi.init.compileIfStatic(f.getType(), f.getName(), code, jv);
					if (stacksize < s) {
						stacksize = s;
					}
				}
			}

			if (doInit) {
				this.modifyClassConstructor(cf, code, stacksize, 0);
			}

		}
	}

	private void modifyClassConstructor(ClassFile cf, Bytecode code, int stacksize, int localsize)
			throws CannotCompileException {
		MethodInfo m = cf.getStaticInitializer();
		if (m == null) {
			code.add(177);
			code.setMaxStack(stacksize);
			code.setMaxLocals(localsize);
			m = new MethodInfo(cf.getConstPool(), "<clinit>", "()V");
			m.setAccessFlags(8);
			m.setCodeAttribute(code.toCodeAttribute());
			cf.addMethod(m);
			Cache e = this.hasMemberCache();
			if (e != null) {
				e.addConstructor(new CtConstructor(m, this));
			}
		} else {
			CodeAttribute e2 = m.getCodeAttribute();
			if (e2 == null) {
				throw new CannotCompileException("empty <clinit>");
			}

			try {
				CodeIterator e1 = e2.iterator();
				int pos = e1.insertEx(code.get());
				e1.insert(code.getExceptionTable(), pos);
				int maxstack = e2.getMaxStack();
				if (maxstack < stacksize) {
					e2.setMaxStack(stacksize);
				}

				int maxlocals = e2.getMaxLocals();
				if (maxlocals < localsize) {
					e2.setMaxLocals(localsize);
				}
			} catch (BadBytecode arg11) {
				throw new CannotCompileException(arg11);
			}
		}

		try {
			m.rebuildStackMapIf6(this.classPool, cf);
		} catch (BadBytecode arg10) {
			throw new CannotCompileException(arg10);
		}
	}

	private void modifyConstructors(ClassFile cf) throws CannotCompileException, NotFoundException {
		if (this.fieldInitializers != null) {
			ConstPool cp = cf.getConstPool();
			List list = cf.getMethods();
			int n = list.size();

			for (int i = 0; i < n; ++i) {
				MethodInfo minfo = (MethodInfo) list.get(i);
				if (minfo.isConstructor()) {
					CodeAttribute codeAttr = minfo.getCodeAttribute();
					if (codeAttr != null) {
						try {
							Bytecode e = new Bytecode(cp, 0, codeAttr.getMaxLocals());
							CtClass[] params = Descriptor.getParameterTypes(minfo.getDescriptor(), this.classPool);
							int stacksize = this.makeFieldInitializer(e, params);
							insertAuxInitializer(codeAttr, e, stacksize);
							minfo.rebuildStackMapIf6(this.classPool, cf);
						} catch (BadBytecode arg10) {
							throw new CannotCompileException(arg10);
						}
					}
				}
			}

		}
	}

	private static void insertAuxInitializer(CodeAttribute codeAttr, Bytecode initializer, int stacksize)
			throws BadBytecode {
		CodeIterator it = codeAttr.iterator();
		int index = it.skipSuperConstructor();
		if (index < 0) {
			index = it.skipThisConstructor();
			if (index >= 0) {
				return;
			}
		}

		int pos = it.insertEx(initializer.get());
		it.insert(initializer.getExceptionTable(), pos);
		int maxstack = codeAttr.getMaxStack();
		if (maxstack < stacksize) {
			codeAttr.setMaxStack(stacksize);
		}

	}

	private int makeFieldInitializer(Bytecode code, CtClass[] parameters)
			throws CannotCompileException, NotFoundException {
		int stacksize = 0;
		Javac jv = new Javac(code, this);

		try {
			jv.recordParams(parameters, false);
		} catch (CompileError arg7) {
			throw new CannotCompileException(arg7);
		}

		for (FieldInitLink fi = this.fieldInitializers; fi != null; fi = fi.next) {
			CtField f = fi.field;
			if (!Modifier.isStatic(f.getModifiers())) {
				int s = fi.init.compile(f.getType(), f.getName(), code, parameters, jv);
				if (stacksize < s) {
					stacksize = s;
				}
			}
		}

		return stacksize;
	}

	Hashtable getHiddenMethods() {
		if (this.hiddenMethods == null) {
			this.hiddenMethods = new Hashtable();
		}

		return this.hiddenMethods;
	}

	int getUniqueNumber() {
		return this.uniqueNumberSeed++;
	}

	public String makeUniqueName(String prefix) {
		HashMap table = new HashMap();
		this.makeMemberList(table);
		Set keys = table.keySet();
		String[] methods = new String[keys.size()];
		keys.toArray(methods);
		if (notFindInArray(prefix, methods)) {
			return prefix;
		} else {
			int i = 100;

			while (i <= 999) {
				String name = prefix + i++;
				if (notFindInArray(name, methods)) {
					return name;
				}
			}

			throw new RuntimeException("too many unique name");
		}
	}

	private static boolean notFindInArray(String prefix, String[] values) {
		int len = values.length;

		for (int i = 0; i < len; ++i) {
			if (values[i].startsWith(prefix)) {
				return false;
			}
		}

		return true;
	}

	private void makeMemberList(HashMap table) {
		int mod = this.getModifiers();
		int n;
		int i;
		if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
			try {
				CtClass[] list = this.getInterfaces();
				n = list.length;

				for (i = 0; i < n; ++i) {
					CtClass finfo = list[i];
					if (finfo != null && finfo instanceof CtClassType) {
						((CtClassType) finfo).makeMemberList(table);
					}
				}
			} catch (NotFoundException arg7) {
				;
			}
		}

		try {
			CtClass arg8 = this.getSuperclass();
			if (arg8 != null && arg8 instanceof CtClassType) {
				((CtClassType) arg8).makeMemberList(table);
			}
		} catch (NotFoundException arg6) {
			;
		}

		List arg9 = this.getClassFile2().getMethods();
		n = arg9.size();

		for (i = 0; i < n; ++i) {
			MethodInfo arg10 = (MethodInfo) arg9.get(i);
			table.put(arg10.getName(), this);
		}

		arg9 = this.getClassFile2().getFields();
		n = arg9.size();

		for (i = 0; i < n; ++i) {
			FieldInfo arg11 = (FieldInfo) arg9.get(i);
			table.put(arg11.getName(), this);
		}

	}
}
