package org.eocencle.winger.xmltags;

import org.eocencle.winger.exceptions.WingerException;

public interface JsonNode {
	boolean apply(DynamicContext context) throws WingerException;
}
