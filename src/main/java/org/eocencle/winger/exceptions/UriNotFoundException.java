package org.eocencle.winger.exceptions;

/**
 * URI未找到异常
 * @author huan
 *
 */
public class UriNotFoundException extends IllegalParamException {

	private static final long serialVersionUID = 5027762405506149798L;
	
	public UriNotFoundException() {
		super();
	}

	public UriNotFoundException(String message) {
		super(message);
	}

	public UriNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UriNotFoundException(Throwable cause) {
		super(cause);
	}

}
