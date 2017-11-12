package org.eocencle.winger.binding;

import org.eocencle.winger.exceptions.PersistenceException;

public class BindingException extends PersistenceException {

	private static final long serialVersionUID = -1770508772274484000L;

	public BindingException() {
		super();
	}

	public BindingException(String message) {
		super(message);
	}

	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}

	public BindingException(Throwable cause) {
		super(cause);
	}
}
