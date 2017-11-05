package org.eocencle.winger.ognl;

import java.lang.reflect.Array;
import java.util.Enumeration;

public class ArrayElementsAccessor implements ElementsAccessor {
	public Enumeration getElements(final Object target) {
		return new Enumeration() {
			private int count = Array.getLength(target);
			private int index = 0;

			public boolean hasMoreElements() {
				return this.index < this.count;
			}

			public Object nextElement() {
				return Array.get(target, this.index++);
			}
		};
	}
}
