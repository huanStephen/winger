package org.eocencle.winger.ognl.enhance;

import org.eocencle.winger.ognl.Node;
import org.eocencle.winger.ognl.OgnlContext;

public interface ExpressionAccessor {
	Object get(OgnlContext arg0, Object arg1);

	void set(OgnlContext arg0, Object arg1, Object arg2);

	void setExpression(Node arg0);
}
