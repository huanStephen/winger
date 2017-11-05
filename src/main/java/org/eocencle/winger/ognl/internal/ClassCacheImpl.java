package org.eocencle.winger.ognl.internal;

import java.util.Arrays;

import org.eocencle.winger.ognl.ClassCacheInspector;

public class ClassCacheImpl implements ClassCache {
	private static final int TABLE_SIZE = 512;
	private static final int TABLE_SIZE_MASK = 511;
	private Entry[] _table = new Entry[512];
	private ClassCacheInspector _classInspector;
	private int _size = 0;

	public void setClassInspector(ClassCacheInspector inspector) {
		this._classInspector = inspector;
	}

	public void clear() {
		for (int i = 0; i < this._table.length; ++i) {
			this._table[i] = null;
		}

		this._size = 0;
	}

	public int getSize() {
		return this._size;
	}

	public final Object get(Class key) {
		Object result = null;
		int i = key.hashCode() & 511;

		for (Entry entry = this._table[i]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				result = entry.value;
				break;
			}
		}

		return result;
	}

	public final Object put(Class key, Object value) {
		if (this._classInspector != null && !this._classInspector.shouldCache(key)) {
			return value;
		} else {
			Object result = null;
			int i = key.hashCode() & 511;
			Entry entry = this._table[i];
			if (entry == null) {
				this._table[i] = new Entry(key, value);
				++this._size;
			} else if (entry.key == key) {
				result = entry.value;
				entry.value = value;
			} else {
				while (entry.key != key) {
					if (entry.next == null) {
						entry.next = new Entry(key, value);
						return result;
					}

					entry = entry.next;
				}

				result = entry.value;
				entry.value = value;
			}

			return result;
		}
	}

	public String toString() {
		return "ClassCacheImpl[_table=" + (this._table == null ? null : Arrays.asList(this._table)) + '\n'
				+ ", _classInspector=" + this._classInspector + '\n' + ", _size=" + this._size + '\n' + ']';
	}
}
