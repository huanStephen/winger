package org.eocencle.winger.reflection.wrapper;

import org.eocencle.winger.reflection.MetaObject;

public interface ObjectWrapperFactory {
	boolean hasWrapperFor(Object object);
	
	ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);
}
