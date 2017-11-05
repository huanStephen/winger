package org.eocencle.winger.ognl.internal;

import org.eocencle.winger.ognl.ClassCacheInspector;

public interface ClassCache {
	void setClassInspector(ClassCacheInspector arg0);

	void clear();

	int getSize();

	Object get(Class arg0);

	Object put(Class arg0, Object arg1);
}
