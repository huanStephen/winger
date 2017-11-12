package org.eocencle.winger.type;

import org.eocencle.winger.exceptions.PersistenceException;

public class TypeException extends PersistenceException {

	private static final long serialVersionUID = 1496710079751123528L;

	public TypeException() {
		super();
	}

	public TypeException(String message) {
		super(message);
	}

	public TypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TypeException(Throwable cause) {
		super(cause);
	}
}
