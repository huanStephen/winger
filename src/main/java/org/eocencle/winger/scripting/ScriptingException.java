package org.eocencle.winger.scripting;

import org.eocencle.winger.exceptions.PersistenceException;

public class ScriptingException extends PersistenceException {
	private static final long serialVersionUID = 3010093545644891377L;

	public ScriptingException() {
		super();
	}

	public ScriptingException(String message) {
		super(message);
	}

	public ScriptingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptingException(Throwable cause) {
		super(cause);
	}
}
