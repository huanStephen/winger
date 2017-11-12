package org.eocencle.winger.logging;

import org.eocencle.winger.exceptions.PersistenceException;

public class LogException extends PersistenceException {

	private static final long serialVersionUID = -4175798814216098367L;

	public LogException() {
		super();
	}

	public LogException(String message) {
		super(message);
	}

	public LogException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogException(Throwable cause) {
		super(cause);
	}
}
