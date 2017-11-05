package org.eocencle.winger.ognl;

import java.util.Enumeration;

public class ObjectElementsAccessor implements ElementsAccessor {
	public Enumeration getElements(final Object target) {
		return new Enumeration() {
			private boolean seen = false;

			public boolean hasMoreElements() {
				return !this.seen;
			}

			public Object nextElement() {
				Object result = null;
				if (!this.seen) {
					result = target;
					this.seen = true;
				}

				return result;
			}
		};
	}
}
