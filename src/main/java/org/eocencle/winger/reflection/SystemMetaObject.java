package org.eocencle.winger.reflection;

import org.eocencle.winger.reflection.factory.DefaultObjectFactory;
import org.eocencle.winger.reflection.factory.ObjectFactory;
import org.eocencle.winger.reflection.wrapper.DefaultObjectWrapperFactory;
import org.eocencle.winger.reflection.wrapper.ObjectWrapperFactory;

public class SystemMetaObject {
	public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);

	private static class NullObject {
	}
	
	public static MetaObject forObject(Object object) {
		return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
	}
}
