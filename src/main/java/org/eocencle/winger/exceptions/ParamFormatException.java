package org.eocencle.winger.exceptions;

/**
 * 参数格式化异常
 * @author huan
 *
 */
public class ParamFormatException extends IllegalParamException {

	private static final long serialVersionUID = -6667264768347503443L;

	public ParamFormatException() {
		super();
	}

	public ParamFormatException(String message) {
		super(message);
	}

	public ParamFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParamFormatException(Throwable cause) {
		super(cause);
	}
	
}
