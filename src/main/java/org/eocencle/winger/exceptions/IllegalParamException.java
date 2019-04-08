package org.eocencle.winger.exceptions;

/**
 * 非法参数异常
 * @author huan
 *
 */
public class IllegalParamException extends WingerException {

	private static final long serialVersionUID = 1462873943862689928L;

	public IllegalParamException() {
		super();
	}

	public IllegalParamException(String message) {
		super(message);
	}

	public IllegalParamException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalParamException(Throwable cause) {
		super(cause);
	}
	
}
