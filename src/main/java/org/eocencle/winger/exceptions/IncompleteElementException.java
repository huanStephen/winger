package org.eocencle.winger.exceptions;

public class IncompleteElementException extends BuilderException {
	private static final long serialVersionUID = -5429203643303241361L;

	public IncompleteElementException() {
		super();
	}

	public IncompleteElementException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncompleteElementException(String message) {
		super(message);
	}

	public IncompleteElementException(Throwable cause) {
		super(cause);
	}
}
