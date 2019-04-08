package org.eocencle.winger.exceptions;

/**
 * 系统异常
 * @author huan
 *
 */
public class WingerException extends Exception {
	
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
