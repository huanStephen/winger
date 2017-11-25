package org.eocencle.winger.exceptions;

public class HttpException extends WingerException {

	private static final long serialVersionUID = 7601612227103501749L;

	public HttpException() {
		super();
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException(Throwable cause) {
		super(cause);
	}
}
