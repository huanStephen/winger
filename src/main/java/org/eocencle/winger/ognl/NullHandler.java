package org.eocencle.winger.ognl;

import java.util.Map;

public interface NullHandler {
	Object nullMethodResult(Map arg0, Object arg1, String arg2, Object[] arg3);

	Object nullPropertyValue(Map arg0, Object arg1, Object arg2);
}
