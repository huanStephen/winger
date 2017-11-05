package org.eocencle.winger.ognl;

import java.util.Enumeration;

public class EnumerationElementsAccessor implements ElementsAccessor {
	public Enumeration getElements(Object target) {
		return (Enumeration) target;
	}
}
