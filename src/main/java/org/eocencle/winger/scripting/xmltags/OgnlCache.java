package org.eocencle.winger.scripting.xmltags;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eocencle.winger.builder.BuilderException;
import org.eocencle.winger.ognl.ExpressionSyntaxException;
import org.eocencle.winger.ognl.Node;
import org.eocencle.winger.ognl.Ognl;
import org.eocencle.winger.ognl.OgnlException;
import org.eocencle.winger.ognl.OgnlParser;
import org.eocencle.winger.ognl.ParseException;
import org.eocencle.winger.ognl.TokenMgrError;

public class OgnlCache {
	private static final Map<String, Node> expressionCache = new ConcurrentHashMap<String, Node>();

	public static Object getValue(String expression, Object root) {
		try {
			return Ognl.getValue(parseExpression(expression), root);
		} catch (OgnlException e) {
			throw new BuilderException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
		}
	}

	private static Object parseExpression(String expression) throws OgnlException {
		try {
			Node node = expressionCache.get(expression);
			if (node == null) {
				node = new OgnlParser(new StringReader(expression)).topLevelExpression();
				expressionCache.put(expression, node);
			}
			return node;
		} catch (ParseException e) {
			throw new ExpressionSyntaxException(expression, e);
		} catch (TokenMgrError e) {
			throw new ExpressionSyntaxException(expression, e);
		}
	}
}
