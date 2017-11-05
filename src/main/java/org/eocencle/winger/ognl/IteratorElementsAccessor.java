package org.eocencle.winger.ognl;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorElementsAccessor implements ElementsAccessor {
	public Enumeration getElements(Object target) {
		return new IteratorEnumeration((Iterator) target);
	}
}
