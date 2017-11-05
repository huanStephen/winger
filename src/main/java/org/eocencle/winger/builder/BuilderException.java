package org.eocencle.winger.builder;

import org.eocencle.winger.exceptions.PersistenceException;

public class BuilderException extends PersistenceException {
	private static final long serialVersionUID = -6985433586506145751L;

	public BuilderException() {
		super();
	}

	public BuilderException(String message) {
		super(message);
	}

	public BuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuilderException(Throwable cause) {
		super(cause);
	}
}
