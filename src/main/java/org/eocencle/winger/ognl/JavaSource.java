package org.eocencle.winger.ognl;

public interface JavaSource {
	String toGetSourceString(OgnlContext arg0, Object arg1);

	String toSetSourceString(OgnlContext arg0, Object arg1);
}
