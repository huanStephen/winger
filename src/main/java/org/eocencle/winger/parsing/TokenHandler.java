package org.eocencle.winger.parsing;

import org.eocencle.winger.exceptions.WingerException;

public interface TokenHandler {
	String handleToken(String content) throws WingerException;
}
