package org.eocencle.winger.exceptions;

import org.eocencle.winger.executor.ErrorContext;

public class ExceptionFactory {
	public static RuntimeException wrapException(String message, Exception e) {
		return new PersistenceException(ErrorContext.instance().message(message).cause(e).toString(), e);
	}
}
