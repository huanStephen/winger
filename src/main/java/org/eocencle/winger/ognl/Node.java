package org.eocencle.winger.ognl;

import org.eocencle.winger.ognl.enhance.ExpressionAccessor;

public interface Node extends JavaSource {
	void jjtOpen();

	void jjtClose();

	void jjtSetParent(Node arg0);

	Node jjtGetParent();

	void jjtAddChild(Node arg0, int arg1);

	Node jjtGetChild(int arg0);

	int jjtGetNumChildren();

	Object getValue(OgnlContext arg0, Object arg1) throws OgnlException;

	void setValue(OgnlContext arg0, Object arg1, Object arg2) throws OgnlException;

	ExpressionAccessor getAccessor();

	void setAccessor(ExpressionAccessor arg0);
}
