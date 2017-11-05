package org.eocencle.winger.ognl;

import java.util.Map;

public interface ClassResolver {
	Class classForName(String arg0, Map arg1) throws ClassNotFoundException;
}
