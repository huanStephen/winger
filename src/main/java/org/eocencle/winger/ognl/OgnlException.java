package org.eocencle.winger.ognl;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class OgnlException extends Exception {
	static Method _initCause;
	private Evaluation _evaluation;
	private Throwable _reason;

	public OgnlException() {
		this((String) null, (Throwable) null);
	}

	public OgnlException(String msg) {
		this(msg, (Throwable) null);
	}

	public OgnlException(String msg, Throwable reason) {
		super(msg);
		this._reason = reason;
		if (_initCause != null) {
			try {
				_initCause.invoke(this, new Object[] { reason });
			} catch (Exception arg3) {
				;
			}
		}

	}

	public Throwable getReason() {
		return this._reason;
	}

	public Evaluation getEvaluation() {
		return this._evaluation;
	}

	public void setEvaluation(Evaluation value) {
		this._evaluation = value;
	}

	public String toString() {
		return this._reason == null ? super.toString() : super.toString() + " [" + this._reason + "]";
	}

	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			super.printStackTrace(s);
			if (this._reason != null) {
				s.println("/-- Encapsulated exception ------------\\");
				this._reason.printStackTrace(s);
				s.println("\\--------------------------------------/");
			}

		}
	}

	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			super.printStackTrace(s);
			if (this._reason != null) {
				s.println("/-- Encapsulated exception ------------\\");
				this._reason.printStackTrace(s);
				s.println("\\--------------------------------------/");
			}

		}
	}

	static {
		try {
			_initCause = OgnlException.class.getMethod("initCause", new Class[] { Throwable.class });
		} catch (NoSuchMethodException arg0) {
			;
		}

	}
}
