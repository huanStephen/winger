package org.eocencle.winger.builder.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eocencle.winger.builder.IncompleteElementException;
import org.eocencle.winger.builder.MapperBuilderAssistant;
import org.eocencle.winger.builder.MethodResolver;
import org.eocencle.winger.builder.xml.XMLMapperBuilder;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.session.Configuration;

public class MapperAnnotationBuilder {
	private final Set<Class<? extends Annotation>> sqlAnnotationTypes = new HashSet();
	private final Set<Class<? extends Annotation>> sqlProviderAnnotationTypes = new HashSet();
	private Configuration configuration;
	private MapperBuilderAssistant assistant;
	private Class<?> type;

	public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
		String resource = type.getName().replace('.', '/') + ".java (best guess)";
		this.assistant = new MapperBuilderAssistant(configuration, resource);
		this.configuration = configuration;
		this.type = type;
		this.sqlAnnotationTypes.add(Select.class);
		this.sqlAnnotationTypes.add(Insert.class);
		this.sqlAnnotationTypes.add(Update.class);
		this.sqlAnnotationTypes.add(Delete.class);
		this.sqlProviderAnnotationTypes.add(SelectProvider.class);
		this.sqlProviderAnnotationTypes.add(InsertProvider.class);
		this.sqlProviderAnnotationTypes.add(UpdateProvider.class);
		this.sqlProviderAnnotationTypes.add(DeleteProvider.class);
	}

	public void parse() {
		String resource = this.type.toString();
		if (!this.configuration.isResourceLoaded(resource)) {
			this.loadXmlResource();
			this.configuration.addLoadedResource(resource);
			this.assistant.setCurrentNamespace(this.type.getName());
			this.parseCache();
			this.parseCacheRef();
			Method[] methods = this.type.getMethods();
			Method[] arr$ = methods;
			int len$ = methods.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Method method = arr$[i$];

				try {
					if (!method.isBridge()) {
						this.parseStatement(method);
					}
				} catch (IncompleteElementException arg7) {
					this.configuration.addIncompleteMethod(new MethodResolver(this, method));
				}
			}
		}

		this.parsePendingMethods();
	}

	private void parsePendingMethods() {
		Collection incompleteMethods = this.configuration.getIncompleteMethods();
		synchronized (incompleteMethods) {
			Iterator iter = incompleteMethods.iterator();

			while (iter.hasNext()) {
				try {
					((MethodResolver) iter.next()).resolve();
					iter.remove();
				} catch (IncompleteElementException arg5) {
					;
				}
			}

		}
	}

	private void loadXmlResource() {
		if (!this.configuration.isResourceLoaded("namespace:" + this.type.getName())) {
			String xmlResource = this.type.getName().replace('.', '/') + ".xml";
			InputStream inputStream = null;

			try {
				inputStream = Resources.getResourceAsStream(this.type.getClassLoader(), xmlResource);
			} catch (IOException arg3) {
				;
			}

			if (inputStream != null) {
				XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, this.assistant.getConfiguration(),
						xmlResource, this.configuration.getSqlFragments(), this.type.getName());
				xmlParser.parse();
			}
		}

	}

	private void parseCache() {
		CacheNamespace cacheDomain = (CacheNamespace) this.type.getAnnotation(CacheNamespace.class);
		if (cacheDomain != null) {
			Integer size = cacheDomain.size() == 0 ? null : Integer.valueOf(cacheDomain.size());
			Long flushInterval = cacheDomain.flushInterval() == 0L ? null : Long.valueOf(cacheDomain.flushInterval());
			this.assistant.useNewCache(cacheDomain.implementation(), cacheDomain.eviction(), flushInterval, size,
					cacheDomain.readWrite(), cacheDomain.blocking(), (Properties) null);
		}

	}

	private void parseCacheRef() {
		CacheNamespaceRef cacheDomainRef = (CacheNamespaceRef) this.type.getAnnotation(CacheNamespaceRef.class);
		if (cacheDomainRef != null) {
			this.assistant.useCacheRef(cacheDomainRef.value().getName());
		}

	}

	private String parseResultMap(Method method) {
		Class returnType = this.getReturnType(method);
		ConstructorArgs args = (ConstructorArgs) method.getAnnotation(ConstructorArgs.class);
		Results results = (Results) method.getAnnotation(Results.class);
		TypeDiscriminator typeDiscriminator = (TypeDiscriminator) method.getAnnotation(TypeDiscriminator.class);
		String resultMapId = this.generateResultMapName(method);
		this.applyResultMap(resultMapId, returnType, this.argsIf(args), this.resultsIf(results), typeDiscriminator);
		return resultMapId;
	}

	private String generateResultMapName(Method method) {
		Results results = (Results) method.getAnnotation(Results.class);
		if (results != null && !results.id().isEmpty()) {
			return this.type.getName() + "." + results.id();
		} else {
			StringBuilder suffix = new StringBuilder();
			Class[] arr$ = method.getParameterTypes();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Class c = arr$[i$];
				suffix.append("-");
				suffix.append(c.getSimpleName());
			}

			if (suffix.length() < 1) {
				suffix.append("-void");
			}

			return this.type.getName() + "." + method.getName() + suffix;
		}
	}

	private void applyResultMap(String resultMapId, Class<?> returnType, Arg[] args, Result[] results,
			TypeDiscriminator discriminator) {
		ArrayList resultMappings = new ArrayList();
		this.applyConstructorArgs(args, returnType, resultMappings);
		this.applyResults(results, returnType, resultMappings);
		Discriminator disc = this.applyDiscriminator(resultMapId, returnType, discriminator);
		this.assistant.addResultMap(resultMapId, returnType, (String) null, disc, resultMappings, (Boolean) null);
		this.createDiscriminatorResultMaps(resultMapId, returnType, discriminator);
	}

	private void createDiscriminatorResultMaps(String resultMapId, Class<?> resultType,
			TypeDiscriminator discriminator) {
		if (discriminator != null) {
			Case[] arr$ = discriminator.cases();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Case c = arr$[i$];
				String caseResultMapId = resultMapId + "-" + c.value();
				ArrayList resultMappings = new ArrayList();
				this.applyConstructorArgs(c.constructArgs(), resultType, resultMappings);
				this.applyResults(c.results(), resultType, resultMappings);
				this.assistant.addResultMap(caseResultMapId, c.type(), resultMapId, (Discriminator) null,
						resultMappings, (Boolean) null);
			}
		}

	}

	private Discriminator applyDiscriminator(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
		if (discriminator == null) {
			return null;
		} else {
			String column = discriminator.column();
			Class javaType = discriminator.javaType() == Void.TYPE ? String.class : discriminator.javaType();
			JdbcType jdbcType = discriminator.jdbcType() == JdbcType.UNDEFINED ? null : discriminator.jdbcType();
			Class typeHandler = discriminator.typeHandler() == UnknownTypeHandler.class ? null
					: discriminator.typeHandler();
			Case[] cases = discriminator.cases();
			HashMap discriminatorMap = new HashMap();
			Case[] arr$ = cases;
			int len$ = cases.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Case c = arr$[i$];
				String value = c.value();
				String caseResultMapId = resultMapId + "-" + value;
				discriminatorMap.put(value, caseResultMapId);
			}

			return this.assistant.buildDiscriminator(resultType, column, javaType, jdbcType, typeHandler,
					discriminatorMap);
		}
	}

	void parseStatement(Method method) {
		Class parameterTypeClass = this.getParameterType(method);
		LanguageDriver languageDriver = this.getLanguageDriver(method);
		SqlSource sqlSource = this.getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);
		if (sqlSource != null) {
			Options options = (Options) method.getAnnotation(Options.class);
			String mappedStatementId = this.type.getName() + "." + method.getName();
			Integer fetchSize = null;
			Integer timeout = null;
			StatementType statementType = StatementType.PREPARED;
			ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
			SqlCommandType sqlCommandType = this.getSqlCommandType(method);
			boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
			boolean flushCache = !isSelect;
			boolean useCache = isSelect;
			String keyProperty = "id";
			String keyColumn = null;
			Object keyGenerator;
			if (!SqlCommandType.INSERT.equals(sqlCommandType) && !SqlCommandType.UPDATE.equals(sqlCommandType)) {
				keyGenerator = new NoKeyGenerator();
			} else {
				SelectKey resultMapId = (SelectKey) method.getAnnotation(SelectKey.class);
				if (resultMapId != null) {
					keyGenerator = this.handleSelectKeyAnnotation(resultMapId, mappedStatementId,
							this.getParameterType(method), languageDriver);
					keyProperty = resultMapId.keyProperty();
				} else if (options == null) {
					keyGenerator = this.configuration.isUseGeneratedKeys() ? new Jdbc3KeyGenerator()
							: new NoKeyGenerator();
				} else {
					keyGenerator = options.useGeneratedKeys() ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
					keyProperty = options.keyProperty();
					keyColumn = options.keyColumn();
				}
			}

			if (options != null) {
				if (FlushCachePolicy.TRUE.equals(options.flushCache())) {
					flushCache = true;
				} else if (FlushCachePolicy.FALSE.equals(options.flushCache())) {
					flushCache = false;
				}

				useCache = options.useCache();
				fetchSize = options.fetchSize() <= -1 && options.fetchSize() != Integer.MIN_VALUE ? null
						: Integer.valueOf(options.fetchSize());
				timeout = options.timeout() > -1 ? Integer.valueOf(options.timeout()) : null;
				statementType = options.statementType();
				resultSetType = options.resultSetType();
			}

			String arg25 = null;
			ResultMap resultMapAnnotation = (ResultMap) method.getAnnotation(ResultMap.class);
			if (resultMapAnnotation == null) {
				if (isSelect) {
					arg25 = this.parseResultMap(method);
				}
			} else {
				String[] resultMaps = resultMapAnnotation.value();
				StringBuilder sb = new StringBuilder();
				String[] arr$ = resultMaps;
				int len$ = resultMaps.length;

				for (int i$ = 0; i$ < len$; ++i$) {
					String resultMap = arr$[i$];
					if (sb.length() > 0) {
						sb.append(",");
					}

					sb.append(resultMap);
				}

				arg25 = sb.toString();
			}

			this.assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize,
					timeout, (String) null, parameterTypeClass, arg25, this.getReturnType(method), resultSetType,
					flushCache, useCache, false, (KeyGenerator) keyGenerator, keyProperty, keyColumn, (String) null,
					languageDriver, options != null ? this.nullOrEmpty(options.resultSets()) : null);
		}

	}

	private LanguageDriver getLanguageDriver(Method method) {
		Lang lang = (Lang) method.getAnnotation(Lang.class);
		Class langClass = null;
		if (lang != null) {
			langClass = lang.value();
		}

		return this.assistant.getLanguageDriver(langClass);
	}

	private Class<?> getParameterType(Method method) {
		Class parameterType = null;
		Class[] parameterTypes = method.getParameterTypes();
		Class[] arr$ = parameterTypes;
		int len$ = parameterTypes.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Class currentParameterType = arr$[i$];
			if (!RowBounds.class.isAssignableFrom(currentParameterType)
					&& !ResultHandler.class.isAssignableFrom(currentParameterType)) {
				if (parameterType == null) {
					parameterType = currentParameterType;
				} else {
					parameterType = ParamMap.class;
				}
			}
		}

		return parameterType;
	}

	private Class<?> getReturnType(Method method) {
		Class returnType = method.getReturnType();
		Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, this.type);
		if (resolvedReturnType instanceof Class) {
			returnType = (Class) resolvedReturnType;
			if (returnType.isArray()) {
				returnType = returnType.getComponentType();
			}

			if (Void.TYPE.equals(returnType)) {
				ResultType parameterizedType = (ResultType) method.getAnnotation(ResultType.class);
				if (parameterizedType != null) {
					returnType = parameterizedType.value();
				}
			}
		} else if (resolvedReturnType instanceof ParameterizedType) {
			ParameterizedType parameterizedType1 = (ParameterizedType) resolvedReturnType;
			Class rawType = (Class) parameterizedType1.getRawType();
			Type[] actualTypeArguments;
			Type returnTypeParameter;
			if (!Collection.class.isAssignableFrom(rawType) && !Cursor.class.isAssignableFrom(rawType)) {
				if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
					actualTypeArguments = parameterizedType1.getActualTypeArguments();
					if (actualTypeArguments != null && actualTypeArguments.length == 2) {
						returnTypeParameter = actualTypeArguments[1];
						if (returnTypeParameter instanceof Class) {
							returnType = (Class) returnTypeParameter;
						} else if (returnTypeParameter instanceof ParameterizedType) {
							returnType = (Class) ((ParameterizedType) returnTypeParameter).getRawType();
						}
					}
				}
			} else {
				actualTypeArguments = parameterizedType1.getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					returnTypeParameter = actualTypeArguments[0];
					if (returnTypeParameter instanceof Class) {
						returnType = (Class) returnTypeParameter;
					} else if (returnTypeParameter instanceof ParameterizedType) {
						returnType = (Class) ((ParameterizedType) returnTypeParameter).getRawType();
					} else if (returnTypeParameter instanceof GenericArrayType) {
						Class componentType = (Class) ((GenericArrayType) returnTypeParameter)
								.getGenericComponentType();
						returnType = Array.newInstance(componentType, 0).getClass();
					}
				}
			}
		}

		return returnType;
	}

	private SqlSource getSqlSourceFromAnnotations(Method method, Class<?> parameterType,
			LanguageDriver languageDriver) {
		try {
			Class e = this.getSqlAnnotationType(method);
			Class sqlProviderAnnotationType = this.getSqlProviderAnnotationType(method);
			Annotation sqlProviderAnnotation;
			if (e != null) {
				if (sqlProviderAnnotationType != null) {
					throw new BindingException(
							"You cannot supply both a static SQL and SqlProvider to method named " + method.getName());
				} else {
					sqlProviderAnnotation = method.getAnnotation(e);
					String[] strings = (String[]) ((String[]) sqlProviderAnnotation.getClass()
							.getMethod("value", new Class[0]).invoke(sqlProviderAnnotation, new Object[0]));
					return this.buildSqlSourceFromStrings(strings, parameterType, languageDriver);
				}
			} else if (sqlProviderAnnotationType != null) {
				sqlProviderAnnotation = method.getAnnotation(sqlProviderAnnotationType);
				return new ProviderSqlSource(this.assistant.getConfiguration(), sqlProviderAnnotation);
			} else {
				return null;
			}
		} catch (Exception arg7) {
			throw new BuilderException("Could not find value method on SQL annotation.  Cause: " + arg7, arg7);
		}
	}

	private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass,
			LanguageDriver languageDriver) {
		StringBuilder sql = new StringBuilder();
		String[] arr$ = strings;
		int len$ = strings.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			String fragment = arr$[i$];
			sql.append(fragment);
			sql.append(" ");
		}

		return languageDriver.createSqlSource(this.configuration, sql.toString().trim(), parameterTypeClass);
	}

	private SqlCommandType getSqlCommandType(Method method) {
		Class type = this.getSqlAnnotationType(method);
		if (type == null) {
			type = this.getSqlProviderAnnotationType(method);
			if (type == null) {
				return SqlCommandType.UNKNOWN;
			}

			if (type == SelectProvider.class) {
				type = Select.class;
			} else if (type == InsertProvider.class) {
				type = Insert.class;
			} else if (type == UpdateProvider.class) {
				type = Update.class;
			} else if (type == DeleteProvider.class) {
				type = Delete.class;
			}
		}

		return SqlCommandType.valueOf(type.getSimpleName().toUpperCase(Locale.ENGLISH));
	}

	private Class<? extends Annotation> getSqlAnnotationType(Method method) {
		return this.chooseAnnotationType(method, this.sqlAnnotationTypes);
	}

	private Class<? extends Annotation> getSqlProviderAnnotationType(Method method) {
		return this.chooseAnnotationType(method, this.sqlProviderAnnotationTypes);
	}

	private Class<? extends Annotation> chooseAnnotationType(Method method, Set<Class<? extends Annotation>> types) {
		Iterator i$ = types.iterator();

		Class type;
		Annotation annotation;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			type = (Class) i$.next();
			annotation = method.getAnnotation(type);
		} while (annotation == null);

		return type;
	}

	private void applyResults(Result[] results, Class<?> resultType, List<ResultMapping> resultMappings) {
		Result[] arr$ = results;
		int len$ = results.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Result result = arr$[i$];
			ArrayList flags = new ArrayList();
			if (result.id()) {
				flags.add(ResultFlag.ID);
			}

			Class typeHandler = result.typeHandler() == UnknownTypeHandler.class ? null : result.typeHandler();
			ResultMapping resultMapping = this.assistant.buildResultMapping(resultType,
					this.nullOrEmpty(result.property()), this.nullOrEmpty(result.column()),
					result.javaType() == Void.TYPE ? null : result.javaType(),
					result.jdbcType() == JdbcType.UNDEFINED ? null : result.jdbcType(),
					this.hasNestedSelect(result) ? this.nestedSelectId(result) : null, (String) null, (String) null,
					(String) null, typeHandler, flags, (String) null, (String) null, this.isLazy(result));
			resultMappings.add(resultMapping);
		}

	}

	private String nestedSelectId(Result result) {
		String nestedSelect = result.one().select();
		if (nestedSelect.length() < 1) {
			nestedSelect = result.many().select();
		}

		if (!nestedSelect.contains(".")) {
			nestedSelect = this.type.getName() + "." + nestedSelect;
		}

		return nestedSelect;
	}

	private boolean isLazy(Result result) {
		boolean isLazy = this.configuration.isLazyLoadingEnabled();
		if (result.one().select().length() > 0 && FetchType.DEFAULT != result.one().fetchType()) {
			isLazy = result.one().fetchType() == FetchType.LAZY;
		} else if (result.many().select().length() > 0 && FetchType.DEFAULT != result.many().fetchType()) {
			isLazy = result.many().fetchType() == FetchType.LAZY;
		}

		return isLazy;
	}

	private boolean hasNestedSelect(Result result) {
		if (result.one().select().length() > 0 && result.many().select().length() > 0) {
			throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
		} else {
			return result.one().select().length() > 0 || result.many().select().length() > 0;
		}
	}

	private void applyConstructorArgs(Arg[] args, Class<?> resultType, List<ResultMapping> resultMappings) {
		Arg[] arr$ = args;
		int len$ = args.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Arg arg = arr$[i$];
			ArrayList flags = new ArrayList();
			flags.add(ResultFlag.CONSTRUCTOR);
			if (arg.id()) {
				flags.add(ResultFlag.ID);
			}

			Class typeHandler = arg.typeHandler() == UnknownTypeHandler.class ? null : arg.typeHandler();
			ResultMapping resultMapping = this.assistant.buildResultMapping(resultType, (String) null,
					this.nullOrEmpty(arg.column()), arg.javaType() == Void.TYPE ? null : arg.javaType(),
					arg.jdbcType() == JdbcType.UNDEFINED ? null : arg.jdbcType(), this.nullOrEmpty(arg.select()),
					this.nullOrEmpty(arg.resultMap()), (String) null, (String) null, typeHandler, flags, (String) null,
					(String) null, false);
			resultMappings.add(resultMapping);
		}

	}

	private String nullOrEmpty(String value) {
		return value != null && value.trim().length() != 0 ? value : null;
	}

	private Result[] resultsIf(Results results) {
		return results == null ? new Result[0] : results.value();
	}

	private Arg[] argsIf(ConstructorArgs args) {
		return args == null ? new Arg[0] : args.value();
	}

	private KeyGenerator handleSelectKeyAnnotation(SelectKey selectKeyAnnotation, String baseStatementId,
			Class<?> parameterTypeClass, LanguageDriver languageDriver) {
		String id = baseStatementId + "!selectKey";
		Class resultTypeClass = selectKeyAnnotation.resultType();
		StatementType statementType = selectKeyAnnotation.statementType();
		String keyProperty = selectKeyAnnotation.keyProperty();
		String keyColumn = selectKeyAnnotation.keyColumn();
		boolean executeBefore = selectKeyAnnotation.before();
		boolean useCache = false;
		NoKeyGenerator keyGenerator = new NoKeyGenerator();
		Object fetchSize = null;
		Object timeout = null;
		boolean flushCache = false;
		Object parameterMap = null;
		Object resultMap = null;
		Object resultSetTypeEnum = null;
		SqlSource sqlSource = this.buildSqlSourceFromStrings(selectKeyAnnotation.statement(), parameterTypeClass,
				languageDriver);
		SqlCommandType sqlCommandType = SqlCommandType.SELECT;
		this.assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, (Integer) fetchSize,
				(Integer) timeout, (String) parameterMap, parameterTypeClass, (String) resultMap, resultTypeClass,
				(ResultSetType) resultSetTypeEnum, flushCache, useCache, false, keyGenerator, keyProperty, keyColumn,
				(String) null, languageDriver, (String) null);
		id = this.assistant.applyCurrentNamespace(id, false);
		MappedStatement keyStatement = this.configuration.getMappedStatement(id, false);
		SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, executeBefore);
		this.configuration.addKeyGenerator(id, answer);
		return answer;
	}
}
