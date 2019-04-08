package org.eocencle.winger.exceptions;

/**
 * 参数未找到异常
 * @author huan
 *
 */
public class ParamNotFoundException extends IllegalParamException {

	private static final long serialVersionUID = -1862801361068767140L;

	public ParamNotFoundException() {
		super();
	}

	public ParamNotFoundException(String message) {
		super(message);
	}

	public ParamNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParamNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
