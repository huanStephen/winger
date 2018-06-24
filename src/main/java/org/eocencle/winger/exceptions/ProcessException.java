package org.eocencle.winger.exceptions;

public class ProcessException extends WingerException {

	private static final long serialVersionUID = -4757049106502979015L;

	public ProcessException() {
		super();
	}

	public ProcessException(String message) {
		super(message);
	}

	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessException(Throwable cause) {
		super(cause);
	}
}
