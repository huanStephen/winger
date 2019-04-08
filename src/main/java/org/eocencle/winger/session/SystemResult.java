package org.eocencle.winger.session;

/**
 * 系统结果
 * @author huan
 *
 */
public class SystemResult {

	// 状态码
	private int code;
	
	public static final int CODE_SUCCESS = 100;
	public static final int CODE_URI_NOT_FOUND = 101;
	public static final int CODE_PARAM_NOT_FOUND = 102;
	public static final int CODE_PARAM_FORMAT_ERROR = 103;
	public static final int CODE_SYSTEM_ERROR = 188;
	
	// 消息
	private String msg;

	public SystemResult() {
		
	}

	public SystemResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
