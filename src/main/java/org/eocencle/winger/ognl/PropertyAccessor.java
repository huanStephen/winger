package org.eocencle.winger.ognl;

import java.util.Map;

public interface PropertyAccessor {
	Object getProperty(Map arg0, Object arg1, Object arg2) throws OgnlException;

	void setProperty(Map arg0, Object arg1, Object arg2, Object arg3) throws OgnlException;

	String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2);

	String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2);
}
