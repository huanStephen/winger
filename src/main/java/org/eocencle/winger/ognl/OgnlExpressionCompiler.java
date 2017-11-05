package org.eocencle.winger.ognl;

import java.lang.reflect.Method;

public interface OgnlExpressionCompiler {
	String ROOT_TYPE = "-ognl-root-type";

	void compileExpression(OgnlContext arg0, Node arg1, Object arg2) throws Exception;

	String getClassName(Class arg0);

	Class getInterfaceClass(Class arg0);

	Class getSuperOrInterfaceClass(Method arg0, Class arg1);

	Class getRootExpressionClass(Node arg0, OgnlContext arg1);

	String castExpression(OgnlContext arg0, Node arg1, String arg2);

	String createLocalReference(OgnlContext arg0, String arg1, Class arg2);
}
