package org.eocencle.winger.javassist.bytecode.analysis;

import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.NotFoundException;

public class MultiArrayType extends Type {
	private MultiType component;
	private int dims;

	public MultiArrayType(MultiType component, int dims) {
		super((CtClass) null);
		this.component = component;
		this.dims = dims;
	}

	public CtClass getCtClass() {
		CtClass clazz = this.component.getCtClass();
		if (clazz == null) {
			return null;
		} else {
			ClassPool pool = clazz.getClassPool();
			if (pool == null) {
				pool = ClassPool.getDefault();
			}

			String name = this.arrayName(clazz.getName(), this.dims);

			try {
				return pool.get(name);
			} catch (NotFoundException arg4) {
				throw new RuntimeException(arg4);
			}
		}
	}

	boolean popChanged() {
		return this.component.popChanged();
	}

	public int getDimensions() {
		return this.dims;
	}

	public Type getComponent() {
		return (Type) (this.dims == 1 ? this.component : new MultiArrayType(this.component, this.dims - 1));
	}

	public int getSize() {
		return 1;
	}

	public boolean isArray() {
		return true;
	}

	public boolean isAssignableFrom(Type type) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean isReference() {
		return true;
	}

	public boolean isAssignableTo(Type type) {
		if (eq(type.getCtClass(), Type.OBJECT.getCtClass())) {
			return true;
		} else if (eq(type.getCtClass(), Type.CLONEABLE.getCtClass())) {
			return true;
		} else if (eq(type.getCtClass(), Type.SERIALIZABLE.getCtClass())) {
			return true;
		} else if (!type.isArray()) {
			return false;
		} else {
			Type typeRoot = this.getRootComponent(type);
			int typeDims = type.getDimensions();
			return typeDims > this.dims ? false
					: (typeDims < this.dims
							? (eq(typeRoot.getCtClass(), Type.OBJECT.getCtClass()) ? true
									: (eq(typeRoot.getCtClass(), Type.CLONEABLE.getCtClass()) ? true
											: eq(typeRoot.getCtClass(), Type.SERIALIZABLE.getCtClass())))
							: this.component.isAssignableTo(typeRoot));
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof MultiArrayType)) {
			return false;
		} else {
			MultiArrayType multi = (MultiArrayType) o;
			return this.component.equals(multi.component) && this.dims == multi.dims;
		}
	}

	public String toString() {
		return this.arrayName(this.component.toString(), this.dims);
	}
}
