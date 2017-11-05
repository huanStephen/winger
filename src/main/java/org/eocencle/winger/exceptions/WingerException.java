package org.eocencle.winger.exceptions;

public class WingerException extends RuntimeException {
	private static final long serialVersionUID = -2451937513633356300L;

	public WingerException() {
		super();
	}

	public WingerException(String message) {
		super(message);
	}

	public WingerException(String message, Throwable cause) {
		super(message, cause);
	}

	public WingerException(Throwable cause) {
		super(cause);
	}
}
