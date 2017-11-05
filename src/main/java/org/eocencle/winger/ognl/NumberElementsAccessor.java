package org.eocencle.winger.ognl;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class NumberElementsAccessor implements ElementsAccessor, NumericTypes {
	public Enumeration getElements(final Object target) {
		return new Enumeration() {
			private int type = OgnlOps.getNumericType(target);
			private long next = 0L;
			private long finish = OgnlOps.longValue(target);

			public boolean hasMoreElements() {
				return this.next < this.finish;
			}

			public Object nextElement() {
				if (this.next >= this.finish) {
					throw new NoSuchElementException();
				} else {
					return OgnlOps.newInteger(this.type, (long) (this.next++));
				}
			}
		};
	}
}
