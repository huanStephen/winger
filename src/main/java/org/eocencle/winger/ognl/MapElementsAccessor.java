package org.eocencle.winger.ognl;

import java.util.Enumeration;
import java.util.Map;

public class MapElementsAccessor implements ElementsAccessor {
	public Enumeration getElements(Object target) {
		return new IteratorEnumeration(((Map) target).values().iterator());
	}
}
