package org.eocencle.winger.logging.slf4j;

import org.eocencle.winger.logging.Log;
import org.slf4j.Logger;

public class Slf4jLoggerImpl implements Log {
	private Logger log;

	public Slf4jLoggerImpl(Logger logger) {
		log = logger;
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
