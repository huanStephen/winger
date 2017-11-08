package org.eocencle.winger.executor;

import org.eocencle.winger.exceptions.PersistenceException;

public class ExecutorException extends PersistenceException {
	private static final long serialVersionUID = -1854835007733445562L;

	public ExecutorException() {
		super();
	}

	public ExecutorException(String message) {
		super(message);
	}

	public ExecutorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecutorException(Throwable cause) {
		super(cause);
	}
}
