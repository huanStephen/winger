package org.eocencle.winger.ognl;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration implements Enumeration {
	private Iterator it;

	public IteratorEnumeration(Iterator it) {
		this.it = it;
	}

	public boolean hasMoreElements() {
		return this.it.hasNext();
	}

	public Object nextElement() {
		return this.it.next();
	}
}
