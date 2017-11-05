package org.eocencle.winger.ognl;

import java.util.Collection;
import java.util.Enumeration;

public class CollectionElementsAccessor implements ElementsAccessor {
	public Enumeration getElements(Object target) {
		return new IteratorEnumeration(((Collection) target).iterator());
	}
}
