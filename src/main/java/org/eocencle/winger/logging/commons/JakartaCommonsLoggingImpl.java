package org.eocencle.winger.logging.commons;

import org.eocencle.winger.logging.Log;
import org.eocencle.winger.logging.LogFactory;

public class JakartaCommonsLoggingImpl implements Log {
	private Log log;

	public JakartaCommonsLoggingImpl(String clazz) {
		log = LogFactory.getLog(clazz);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	public void error(String s, Throwable e) {
		log.error(s, e);
	}

	public void error(String s) {
		log.error(s);
	}

	public void debug(String s) {
		log.debug(s);
	}

	public void trace(String s) {
		log.trace(s);
	}

	public void warn(String s) {
		log.warn(s);
	}
}
