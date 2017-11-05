package org.eocencle.winger.ognl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eocencle.winger.javassist.CannotCompileException;
import org.eocencle.winger.javassist.ClassPool;
import org.eocencle.winger.javassist.CtClass;
import org.eocencle.winger.javassist.CtField;
import org.eocencle.winger.javassist.CtMethod;
import org.eocencle.winger.javassist.CtNewConstructor;
import org.eocencle.winger.javassist.CtNewMethod;
import org.eocencle.winger.javassist.LoaderClassPath;
import org.eocencle.winger.javassist.NotFoundException;
import org.eocencle.winger.ognl.enhance.ContextClassLoader;
import org.eocencle.winger.ognl.enhance.EnhancedClassLoader;
import org.eocencle.winger.ognl.enhance.ExpressionAccessor;
import org.eocencle.winger.ognl.enhance.LocalReference;
import org.eocencle.winger.ognl.enhance.LocalReferenceImpl;
import org.eocencle.winger.ognl.enhance.UnsupportedCompilationException;

public class ExpressionCompiler implements OgnlExpressionCompiler {
	public static final String PRE_CAST = "_preCast";
	protected Map _loaders = new HashMap();
	protected ClassPool _pool;
	protected int _classCounter = 0;

	public static void addCastString(OgnlContext context, String cast) {
		String value = (String) context.get("_preCast");
		if (value != null) {
			value = cast + value;
		} else {
			value = cast;
		}

		context.put("_preCast", value);
	}

	public static String getCastString(Class type) {
		return type == null ? null : (type.isArray() ? type.getComponentType().getName() + "[]" : type.getName());
	}

	public static String getRootExpression(Node expression, Object root, OgnlContext context) {
		String rootExpr = "";
		if (!shouldCast(expression)) {
			return rootExpr;
		} else {
			if (!ASTList.class.isInstance(expression) && !ASTVarRef.class.isInstance(expression)
					&& !ASTStaticMethod.class.isInstance(expression) && !ASTStaticField.class.isInstance(expression)
					&& !ASTConst.class.isInstance(expression) && !ExpressionNode.class.isInstance(expression)
					&& !ASTCtor.class.isInstance(expression) && !ASTStaticMethod.class.isInstance(expression)
					&& root != null || root != null && ASTRootVarRef.class.isInstance(expression)) {
				Class castClass = OgnlRuntime.getCompiler().getRootExpressionClass(expression, context);
				if (!castClass.isArray() && !ASTRootVarRef.class.isInstance(expression)
						&& !ASTThisVarRef.class.isInstance(expression)) {
					if ((!ASTProperty.class.isInstance(expression) || !((ASTProperty) expression).isIndexedAccess())
							&& !ASTChain.class.isInstance(expression)) {
						rootExpr = "((" + getCastString(castClass) + ")$2).";
					} else {
						rootExpr = "((" + getCastString(castClass) + ")$2)";
					}
				} else {
					rootExpr = "((" + getCastString(castClass) + ")$2)";
					if (ASTProperty.class.isInstance(expression) && !((ASTProperty) expression).isIndexedAccess()) {
						rootExpr = rootExpr + ".";
					}
				}
			}

			return rootExpr;
		}
	}

	public static boolean shouldCast(Node expression) {
		if (ASTChain.class.isInstance(expression)) {
			Node child = expression.jjtGetChild(0);
			if (ASTConst.class.isInstance(child) || ASTStaticMethod.class.isInstance(child)
					|| ASTStaticField.class.isInstance(child)
					|| ASTVarRef.class.isInstance(child) && !ASTRootVarRef.class.isInstance(child)) {
				return false;
			}
		}

		return !ASTConst.class.isInstance(expression);
	}

	public String castExpression(OgnlContext context, Node expression, String body) {
		if (context.getCurrentAccessor() != null && context.getPreviousType() != null
				&& !context.getCurrentAccessor().isAssignableFrom(context.getPreviousType())
				&& (context.getCurrentType() == null || context.getCurrentObject() == null
						|| !context.getCurrentType().isAssignableFrom(context.getCurrentObject().getClass())
						|| !context.getCurrentAccessor().isAssignableFrom(context.getPreviousType()))
				&& body != null && body.trim().length() >= 1
				&& (context.getCurrentType() == null || !context.getCurrentType().isArray()
						|| context.getPreviousType() != null && context.getPreviousType() == Object.class)
				&& !ASTOr.class.isInstance(expression) && !ASTAnd.class.isInstance(expression)
				&& !ASTRootVarRef.class.isInstance(expression) && context.getCurrentAccessor() != Class.class
				&& (context.get("_preCast") == null || !((String) context.get("_preCast")).startsWith("new"))
				&& !ASTStaticField.class.isInstance(expression) && !ASTStaticMethod.class.isInstance(expression)
				&& (!OrderedReturn.class.isInstance(expression)
						|| ((OrderedReturn) expression).getLastExpression() == null)) {
			addCastString(context, "((" + getCastString(context.getCurrentAccessor()) + ")");
			return ")" + body;
		} else {
			return body;
		}
	}

	public String getClassName(Class clazz) {
		return clazz.getName().equals("java.util.AbstractList$Itr") ? Iterator.class.getName()
				: (Modifier.isPublic(clazz.getModifiers()) && clazz.isInterface() ? clazz.getName()
						: this._getClassName(clazz, clazz.getInterfaces()));
	}

	private String _getClassName(Class clazz, Class[] intf) {
		for (int superclazz = 0; superclazz < intf.length; ++superclazz) {
			if (intf[superclazz].getName().indexOf("util.List") > 0) {
				return intf[superclazz].getName();
			}

			if (intf[superclazz].getName().indexOf("Iterator") > 0) {
				return intf[superclazz].getName();
			}
		}

		Class arg4 = clazz.getSuperclass();
		if (arg4 != null) {
			Class[] superclazzIntf = arg4.getInterfaces();
			if (superclazzIntf.length > 0) {
				return this._getClassName(arg4, superclazzIntf);
			}
		}

		return clazz.getName();
	}

	public Class getSuperOrInterfaceClass(Method m, Class clazz) {
		Class[] intfs = clazz.getInterfaces();
		Class superClass;
		if (intfs != null && intfs.length > 0) {
			for (int i = 0; i < intfs.length; ++i) {
				superClass = this.getSuperOrInterfaceClass(m, intfs[i]);
				if (superClass != null) {
					return superClass;
				}

				if (Modifier.isPublic(intfs[i].getModifiers()) && this.containsMethod(m, intfs[i])) {
					return intfs[i];
				}
			}
		}

		if (clazz.getSuperclass() != null) {
			superClass = this.getSuperOrInterfaceClass(m, clazz.getSuperclass());
			if (superClass != null) {
				return superClass;
			}
		}

		return Modifier.isPublic(clazz.getModifiers()) && this.containsMethod(m, clazz) ? clazz : null;
	}

	public boolean containsMethod(Method m, Class clazz) {
		Method[] methods = clazz.getMethods();
		if (methods == null) {
			return false;
		} else {
			for (int i = 0; i < methods.length; ++i) {
				if (methods[i].getName().equals(m.getName()) && methods[i].getReturnType() == m.getReturnType()) {
					Class[] parms = m.getParameterTypes();
					if (parms != null) {
						Class[] mparms = methods[i].getParameterTypes();
						if (mparms != null && mparms.length == parms.length) {
							boolean parmsMatch = true;

							for (int exceptions = 0; exceptions < parms.length; ++exceptions) {
								if (parms[exceptions] != mparms[exceptions]) {
									parmsMatch = false;
									break;
								}
							}

							if (parmsMatch) {
								Class[] arg11 = m.getExceptionTypes();
								if (arg11 != null) {
									Class[] mexceptions = methods[i].getExceptionTypes();
									if (mexceptions != null && mexceptions.length == arg11.length) {
										boolean exceptionsMatch = true;

										for (int e = 0; e < arg11.length; ++e) {
											if (arg11[e] != mexceptions[e]) {
												exceptionsMatch = false;
												break;
											}
										}

										if (exceptionsMatch) {
											return true;
										}
									}
								}
							}
						}
					}
				}
			}

			return false;
		}
	}

	public Class getInterfaceClass(Class clazz) {
		return clazz.getName().equals("java.util.AbstractList$Itr") ? Iterator.class
				: ((!Modifier.isPublic(clazz.getModifiers()) || !clazz.isInterface()) && !clazz.isPrimitive()
						? this._getInterfaceClass(clazz, clazz.getInterfaces()) : clazz);
	}

	private Class _getInterfaceClass(Class clazz, Class[] intf) {
		for (int superclazz = 0; superclazz < intf.length; ++superclazz) {
			if (List.class.isAssignableFrom(intf[superclazz])) {
				return List.class;
			}

			if (Iterator.class.isAssignableFrom(intf[superclazz])) {
				return Iterator.class;
			}

			if (Map.class.isAssignableFrom(intf[superclazz])) {
				return Map.class;
			}

			if (Set.class.isAssignableFrom(intf[superclazz])) {
				return Set.class;
			}

			if (Collection.class.isAssignableFrom(intf[superclazz])) {
				return Collection.class;
			}
		}

		Class arg4 = clazz.getSuperclass();
		if (arg4 != null) {
			Class[] superclazzIntf = arg4.getInterfaces();
			if (superclazzIntf.length > 0) {
				return this._getInterfaceClass(arg4, superclazzIntf);
			}
		}

		return clazz;
	}

	public Class getRootExpressionClass(Node rootNode, OgnlContext context) {
		if (context.getRoot() == null) {
			return null;
		} else {
			Class ret = context.getRoot().getClass();
			if (context.getFirstAccessor() != null && context.getFirstAccessor().isInstance(context.getRoot())) {
				ret = context.getFirstAccessor();
			}

			return ret;
		}
	}

	public void compileExpression(OgnlContext context, Node expression, Object root) throws Exception {
		if (expression.getAccessor() == null) {
			EnhancedClassLoader loader = this.getClassLoader(context);
			ClassPool pool = this.getClassPool(context, loader);
			CtClass newClass = pool.makeClass(
					expression.getClass().getName() + expression.hashCode() + this._classCounter++ + "Accessor");
			newClass.addInterface(this.getCtClass(ExpressionAccessor.class));
			CtClass ognlClass = this.getCtClass(OgnlContext.class);
			CtClass objClass = this.getCtClass(Object.class);
			CtMethod valueGetter = new CtMethod(objClass, "get", new CtClass[] { ognlClass, objClass }, newClass);
			CtMethod valueSetter = new CtMethod(CtClass.voidType, "set",
					new CtClass[] { ognlClass, objClass, objClass }, newClass);
			CtField nodeMember = null;
			CtClass nodeClass = this.getCtClass(Node.class);
			CtMethod setExpression = null;

			String getBody;
			try {
				getBody = this.generateGetter(context, newClass, objClass, pool, valueGetter, expression, root);
			} catch (UnsupportedCompilationException arg18) {
				nodeMember = new CtField(nodeClass, "_node", newClass);
				newClass.addField(nodeMember);
				getBody = this.generateOgnlGetter(newClass, valueGetter, nodeMember);
				if (setExpression == null) {
					setExpression = CtNewMethod.setter("setExpression", nodeMember);
					newClass.addMethod(setExpression);
				}
			}

			String setBody;
			try {
				setBody = this.generateSetter(context, newClass, objClass, pool, valueSetter, expression, root);
			} catch (UnsupportedCompilationException arg17) {
				if (nodeMember == null) {
					nodeMember = new CtField(nodeClass, "_node", newClass);
					newClass.addField(nodeMember);
				}

				setBody = this.generateOgnlSetter(newClass, valueSetter, nodeMember);
				if (setExpression == null) {
					setExpression = CtNewMethod.setter("setExpression", nodeMember);
					newClass.addMethod(setExpression);
				}
			}

			try {
				newClass.addConstructor(CtNewConstructor.defaultConstructor(newClass));
				Class t = pool.toClass(newClass);
				newClass.detach();
				expression.setAccessor((ExpressionAccessor) t.newInstance());
				if (nodeMember != null) {
					expression.getAccessor().setExpression(expression);
				}

			} catch (Throwable arg16) {
				throw new RuntimeException("Error compiling expression on object " + root + " with expression node "
						+ expression + " getter body: " + getBody + " setter body: " + setBody, arg16);
			}
		}
	}

	protected String generateGetter(OgnlContext context, CtClass newClass, CtClass objClass, ClassPool pool,
			CtMethod valueGetter, Node expression, Object root) throws Exception {
		String pre = "";
		String post = "";
		context.setRoot(root);
		context.remove("_preCast");
		String getterCode = expression.toGetSourceString(context, root);
		if (getterCode == null
				|| getterCode.trim().length() <= 0 && !ASTVarRef.class.isAssignableFrom(expression.getClass())) {
			getterCode = "null";
		}

		String castExpression = (String) context.get("_preCast");
		if (context.getCurrentType() == null || context.getCurrentType().isPrimitive()
				|| Character.class.isAssignableFrom(context.getCurrentType())
				|| Object.class == context.getCurrentType()) {
			pre = pre + " ($w) (";
			post = post + ")";
		}

		String rootExpr = !getterCode.equals("null") ? getRootExpression(expression, root, context) : "";
		String noRoot = (String) context.remove("_noRoot");
		if (noRoot != null) {
			rootExpr = "";
		}

		this.createLocalReferences(context, pool, newClass, objClass, valueGetter.getParameterTypes());
		String body;
		if (OrderedReturn.class.isInstance(expression) && ((OrderedReturn) expression).getLastExpression() != null) {
			body = "{ "
					+ (!ASTMethod.class.isInstance(expression) && !ASTChain.class.isInstance(expression) ? ""
							: rootExpr)
					+ (castExpression != null ? castExpression : "") + ((OrderedReturn) expression).getCoreExpression()
					+ " return " + pre + ((OrderedReturn) expression).getLastExpression() + post + ";}";
		} else {
			body = "{  return " + pre + (castExpression != null ? castExpression : "") + rootExpr + getterCode + post
					+ ";}";
		}

		if (body.indexOf("..") >= 0) {
			body = body.replaceAll("\\.\\.", ".");
		}

		valueGetter.setBody(body);
		newClass.addMethod(valueGetter);
		return body;
	}

	public String createLocalReference(OgnlContext context, String expression, Class type) {
		String referenceName = "ref" + context.incrementLocalReferenceCounter();
		context.addLocalReference(referenceName, new LocalReferenceImpl(referenceName, expression, type));
		String castString = "";
		if (!type.isPrimitive()) {
			castString = "(" + getCastString(type) + ") ";
		}

		return castString + referenceName + "($$)";
	}

	void createLocalReferences(OgnlContext context, ClassPool pool, CtClass clazz, CtClass objClass, CtClass[] params)
			throws CannotCompileException, NotFoundException {
		Map referenceMap = context.getLocalReferences();
		if (referenceMap != null && referenceMap.size() >= 1) {
			Iterator it = referenceMap.values().iterator();

			while (it.hasNext()) {
				LocalReference ref = (LocalReference) it.next();
				String widener = ref.getType().isPrimitive() ? " " : " ($w) ";
				String body = "{";
				body = body + " return  " + widener + ref.getExpression() + ";";
				body = body + "}";
				if (body.indexOf("..") >= 0) {
					body = body.replaceAll("\\.\\.", ".");
				}

				CtMethod method = new CtMethod(pool.get(getCastString(ref.getType())), ref.getName(), params, clazz);
				method.setBody(body);
				clazz.addMethod(method);
				it.remove();
			}

		}
	}

	protected String generateSetter(OgnlContext context, CtClass newClass, CtClass objClass, ClassPool pool,
			CtMethod valueSetter, Node expression, Object root) throws Exception {
		if (!ExpressionNode.class.isInstance(expression) && !ASTConst.class.isInstance(expression)) {
			context.setRoot(root);
			context.remove("_preCast");
			String setterCode = expression.toSetSourceString(context, root);
			String castExpression = (String) context.get("_preCast");
			if (setterCode != null && setterCode.trim().length() >= 1) {
				if (root == null) {
					throw new UnsupportedCompilationException("Can\'t compile setters with a null root object.");
				} else {
					String pre = getRootExpression(expression, root, context);
					String noRoot = (String) context.remove("_noRoot");
					if (noRoot != null) {
						pre = "";
					}

					this.createLocalReferences(context, pool, newClass, objClass, valueSetter.getParameterTypes());
					String body = "{" + (castExpression != null ? castExpression : "") + pre + setterCode + ";}";
					if (body.indexOf("..") >= 0) {
						body = body.replaceAll("\\.\\.", ".");
					}

					valueSetter.setBody(body);
					newClass.addMethod(valueSetter);
					return body;
				}
			} else {
				throw new UnsupportedCompilationException("Can\'t compile null setter body.");
			}
		} else {
			throw new UnsupportedCompilationException("Can\'t compile expression/constant setters.");
		}
	}

	protected String generateOgnlGetter(CtClass clazz, CtMethod valueGetter, CtField node) throws Exception {
		String body = "return " + node.getName() + ".getValue($1, $2);";
		valueGetter.setBody(body);
		clazz.addMethod(valueGetter);
		return body;
	}

	protected String generateOgnlSetter(CtClass clazz, CtMethod valueSetter, CtField node) throws Exception {
		String body = node.getName() + ".setValue($1, $2, $3);";
		valueSetter.setBody(body);
		clazz.addMethod(valueSetter);
		return body;
	}

	protected EnhancedClassLoader getClassLoader(OgnlContext context) {
		EnhancedClassLoader ret = (EnhancedClassLoader) this._loaders.get(context.getClassResolver());
		if (ret != null) {
			return ret;
		} else {
			ContextClassLoader classLoader = new ContextClassLoader(OgnlContext.class.getClassLoader(), context);
			ret = new EnhancedClassLoader(classLoader);
			this._loaders.put(context.getClassResolver(), ret);
			return ret;
		}
	}

	protected CtClass getCtClass(Class searchClass) throws NotFoundException {
		return this._pool.get(searchClass.getName());
	}

	protected ClassPool getClassPool(OgnlContext context, EnhancedClassLoader loader) {
		if (this._pool != null) {
			return this._pool;
		} else {
			this._pool = ClassPool.getDefault();
			this._pool.insertClassPath(new LoaderClassPath(loader.getParent()));
			return this._pool;
		}
	}
}
