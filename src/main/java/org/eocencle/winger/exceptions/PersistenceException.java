package org.eocencle.winger.exceptions;

public class PersistenceException extends WingerException {
	private static final long serialVersionUID = -2193980294271321492L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceException(Throwable cause) {
		super(cause);
	}
}
