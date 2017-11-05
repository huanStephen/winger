package org.eocencle.winger.ognl;

import java.util.Map;

public interface MethodAccessor {
	Object callStaticMethod(Map arg0, Class arg1, String arg2, Object[] arg3) throws MethodFailedException;

	Object callMethod(Map arg0, Object arg1, String arg2, Object[] arg3) throws MethodFailedException;
}
