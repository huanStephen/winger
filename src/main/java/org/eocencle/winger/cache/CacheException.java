package org.eocencle.winger.cache;

import org.eocencle.winger.exceptions.PersistenceException;

public class CacheException extends PersistenceException {

	private static final long serialVersionUID = -5908608059566999582L;

	public CacheException() {
		super();
	}

	public CacheException(String message) {
		super(message);
	}

	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}
}
