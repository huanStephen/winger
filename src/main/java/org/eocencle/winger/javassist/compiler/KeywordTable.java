package org.eocencle.winger.javassist.compiler;

import java.util.HashMap;

public class KeywordTable extends HashMap {
	public int lookup(String name) {
		Object found = this.get(name);
		return found == null ? -1 : ((Integer) found).intValue();
	}

	public void append(String name, int t) {
		this.put(name, new Integer(t));
	}
}
