package org.eocencle.winger.ognl;

import java.io.StringReader;
import java.lang.reflect.Member;
import java.util.Map;

import org.eocencle.winger.ognl.enhance.ExpressionAccessor;

public abstract class Ognl {
	public static Object parseExpression(String expression) throws OgnlException {
		try {
			OgnlParser e = new OgnlParser(new StringReader(expression));
			return e.topLevelExpression();
		} catch (ParseException arg1) {
			throw new ExpressionSyntaxException(expression, arg1);
		} catch (TokenMgrError arg2) {
			throw new ExpressionSyntaxException(expression, arg2);
		}
	}

	public static Node compileExpression(OgnlContext context, Object root, String expression) throws Exception {
		Node expr = (Node) parseExpression(expression);
		OgnlRuntime.compileExpression(context, expr, root);
		return expr;
	}

	public static Map createDefaultContext(Object root) {
		return addDefaultContext(root, (ClassResolver) null, (TypeConverter) null, (MemberAccess) null,
				new OgnlContext());
	}

	public static Map createDefaultContext(Object root, ClassResolver classResolver) {
		return addDefaultContext(root, classResolver, (TypeConverter) null, (MemberAccess) null, new OgnlContext());
	}

	public static Map createDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter) {
		return addDefaultContext(root, classResolver, converter, (MemberAccess) null, new OgnlContext());
	}

	public static Map createDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter,
			MemberAccess memberAccess) {
		return addDefaultContext(root, classResolver, converter, memberAccess, new OgnlContext());
	}

	public static Map addDefaultContext(Object root, Map context) {
		return addDefaultContext(root, (ClassResolver) null, (TypeConverter) null, (MemberAccess) null, context);
	}

	public static Map addDefaultContext(Object root, ClassResolver classResolver, Map context) {
		return addDefaultContext(root, classResolver, (TypeConverter) null, (MemberAccess) null, context);
	}

	public static Map addDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter,
			Map context) {
		return addDefaultContext(root, classResolver, converter, (MemberAccess) null, context);
	}

	public static Map addDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter,
			MemberAccess memberAccess, Map context) {
		OgnlContext result;
		if (!(context instanceof OgnlContext)) {
			result = new OgnlContext();
			result.setValues(context);
		} else {
			result = (OgnlContext) context;
		}

		if (classResolver != null) {
			result.setClassResolver(classResolver);
		}

		if (converter != null) {
			result.setTypeConverter(converter);
		}

		if (memberAccess != null) {
			result.setMemberAccess(memberAccess);
		}

		result.setRoot(root);
		return result;
	}

	public static void setClassResolver(Map context, ClassResolver classResolver) {
		context.put("_classResolver", classResolver);
	}

	public static ClassResolver getClassResolver(Map context) {
		return (ClassResolver) context.get("_classResolver");
	}

	public static void setTypeConverter(Map context, TypeConverter converter) {
		context.put("_typeConverter", converter);
	}

	public static TypeConverter getTypeConverter(Map context) {
		return (TypeConverter) context.get("_typeConverter");
	}

	public static void setMemberAccess(Map context, MemberAccess memberAccess) {
		context.put("_memberAccess", memberAccess);
	}

	public static MemberAccess getMemberAccess(Map context) {
		return (MemberAccess) context.get("_memberAccess");
	}

	public static void setRoot(Map context, Object root) {
		context.put("root", root);
	}

	public static Object getRoot(Map context) {
		return context.get("root");
	}

	public static Evaluation getLastEvaluation(Map context) {
		return (Evaluation) context.get("_lastEvaluation");
	}

	public static Object getValue(Object tree, Map context, Object root) throws OgnlException {
		return getValue((Object) tree, (Map) context, root, (Class) null);
	}

	public static Object getValue(Object tree, Map context, Object root, Class resultType) throws OgnlException {
		OgnlContext ognlContext = (OgnlContext) addDefaultContext(root, context);
		Node node = (Node) tree;
		Object result;
		if (node.getAccessor() != null) {
			result = node.getAccessor().get(ognlContext, root);
		} else {
			result = node.getValue(ognlContext, root);
		}

		if (resultType != null) {
			result = getTypeConverter(context).convertValue(context, root, (Member) null, (String) null, result,
					resultType);
		}

		return result;
	}

	public static Object getValue(ExpressionAccessor expression, OgnlContext context, Object root) {
		return expression.get(context, root);
	}

	public static Object getValue(ExpressionAccessor expression, OgnlContext context, Object root, Class resultType) {
		return getTypeConverter(context).convertValue(context, root, (Member) null, (String) null,
				expression.get(context, root), resultType);
	}

	public static Object getValue(String expression, Map context, Object root) throws OgnlException {
		return getValue((String) expression, (Map) context, root, (Class) null);
	}

	public static Object getValue(String expression, Map context, Object root, Class resultType) throws OgnlException {
		return getValue(parseExpression(expression), context, root, resultType);
	}

	public static Object getValue(Object tree, Object root) throws OgnlException {
		return getValue((Object) tree, (Object) root, (Class) null);
	}

	public static Object getValue(Object tree, Object root, Class resultType) throws OgnlException {
		return getValue(tree, createDefaultContext(root), root, resultType);
	}

	public static Object getValue(String expression, Object root) throws OgnlException {
		return getValue((String) expression, (Object) root, (Class) null);
	}

	public static Object getValue(String expression, Object root, Class resultType) throws OgnlException {
		return getValue(parseExpression(expression), root, resultType);
	}

	public static void setValue(Object tree, Map context, Object root, Object value) throws OgnlException {
		OgnlContext ognlContext = (OgnlContext) addDefaultContext(root, context);
		Node n = (Node) tree;
		if (n.getAccessor() != null) {
			n.getAccessor().set(ognlContext, root, value);
		} else {
			n.setValue(ognlContext, root, value);
		}
	}

	public static void setValue(ExpressionAccessor expression, OgnlContext context, Object root, Object value) {
		expression.set(context, root, value);
	}

	public static void setValue(String expression, Map context, Object root, Object value) throws OgnlException {
		setValue(parseExpression(expression), context, root, value);
	}

	public static void setValue(Object tree, Object root, Object value) throws OgnlException {
		setValue(tree, createDefaultContext(root), root, value);
	}

	public static void setValue(String expression, Object root, Object value) throws OgnlException {
		setValue(parseExpression(expression), root, value);
	}

	public static boolean isConstant(Object tree, Map context) throws OgnlException {
		return ((SimpleNode) tree).isConstant((OgnlContext) addDefaultContext((Object) null, context));
	}

	public static boolean isConstant(String expression, Map context) throws OgnlException {
		return isConstant(parseExpression(expression), context);
	}

	public static boolean isConstant(Object tree) throws OgnlException {
		return isConstant(tree, createDefaultContext((Object) null));
	}

	public static boolean isConstant(String expression) throws OgnlException {
		return isConstant(parseExpression(expression), createDefaultContext((Object) null));
	}

	public static boolean isSimpleProperty(Object tree, Map context) throws OgnlException {
		return ((SimpleNode) tree).isSimpleProperty((OgnlContext) addDefaultContext((Object) null, context));
	}

	public static boolean isSimpleProperty(String expression, Map context) throws OgnlException {
		return isSimpleProperty(parseExpression(expression), context);
	}

	public static boolean isSimpleProperty(Object tree) throws OgnlException {
		return isSimpleProperty(tree, createDefaultContext((Object) null));
	}

	public static boolean isSimpleProperty(String expression) throws OgnlException {
		return isSimpleProperty(parseExpression(expression), createDefaultContext((Object) null));
	}

	public static boolean isSimpleNavigationChain(Object tree, Map context) throws OgnlException {
		return ((SimpleNode) tree).isSimpleNavigationChain((OgnlContext) addDefaultContext((Object) null, context));
	}

	public static boolean isSimpleNavigationChain(String expression, Map context) throws OgnlException {
		return isSimpleNavigationChain(parseExpression(expression), context);
	}

	public static boolean isSimpleNavigationChain(Object tree) throws OgnlException {
		return isSimpleNavigationChain(tree, createDefaultContext((Object) null));
	}

	public static boolean isSimpleNavigationChain(String expression) throws OgnlException {
		return isSimpleNavigationChain(parseExpression(expression), createDefaultContext((Object) null));
	}
}
