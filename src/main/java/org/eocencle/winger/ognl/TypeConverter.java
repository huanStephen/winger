package org.eocencle.winger.ognl;

import java.lang.reflect.Member;
import java.util.Map;

public interface TypeConverter {
	Object convertValue(Map arg0, Object arg1, Member arg2, String arg3, Object arg4, Class arg5);
}
