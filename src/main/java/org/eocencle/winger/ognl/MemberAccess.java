package org.eocencle.winger.ognl;

import java.lang.reflect.Member;
import java.util.Map;

public interface MemberAccess {
	Object setup(Map arg0, Object arg1, Member arg2, String arg3);

	void restore(Map arg0, Object arg1, Member arg2, String arg3, Object arg4);

	boolean isAccessible(Map arg0, Object arg1, Member arg2, String arg3);
}
