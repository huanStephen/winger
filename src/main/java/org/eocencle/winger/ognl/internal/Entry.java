package org.eocencle.winger.ognl.internal;

public class Entry {
	Entry next;
	Class key;
	Object value;

	public Entry(Class key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String toString() {
		return "Entry[next=" + this.next + '\n' + ", key=" + this.key + '\n' + ", value=" + this.value + '\n' + ']';
	}
}
