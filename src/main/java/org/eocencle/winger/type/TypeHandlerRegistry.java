package org.eocencle.winger.type;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class TypeHandlerRegistry {
	private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap(JdbcType.class);
	private final Map<Type, Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap();
	//private final TypeHandler<Object> UNKNOWN_TYPE_HANDLER = new UnknownTypeHandler(this);
	private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap();

	public TypeHandlerRegistry() {
		/*this.register((Class) Boolean.class, (TypeHandler) (new BooleanTypeHandler()));
		this.register((Class) Boolean.TYPE, (TypeHandler) (new BooleanTypeHandler()));
		this.register((JdbcType) JdbcType.BOOLEAN, (TypeHandler) (new BooleanTypeHandler()));
		this.register((JdbcType) JdbcType.BIT, (TypeHandler) (new BooleanTypeHandler()));
		this.register((Class) Byte.class, (TypeHandler) (new ByteTypeHandler()));
		this.register((Class) Byte.TYPE, (TypeHandler) (new ByteTypeHandler()));
		this.register((JdbcType) JdbcType.TINYINT, (TypeHandler) (new ByteTypeHandler()));
		this.register((Class) Short.class, (TypeHandler) (new ShortTypeHandler()));
		this.register((Class) Short.TYPE, (TypeHandler) (new ShortTypeHandler()));
		this.register((JdbcType) JdbcType.SMALLINT, (TypeHandler) (new ShortTypeHandler()));
		this.register((Class) Integer.class, (TypeHandler) (new IntegerTypeHandler()));
		this.register((Class) Integer.TYPE, (TypeHandler) (new IntegerTypeHandler()));
		this.register((JdbcType) JdbcType.INTEGER, (TypeHandler) (new IntegerTypeHandler()));
		this.register((Class) Long.class, (TypeHandler) (new LongTypeHandler()));
		this.register((Class) Long.TYPE, (TypeHandler) (new LongTypeHandler()));
		this.register((Class) Float.class, (TypeHandler) (new FloatTypeHandler()));
		this.register((Class) Float.TYPE, (TypeHandler) (new FloatTypeHandler()));
		this.register((JdbcType) JdbcType.FLOAT, (TypeHandler) (new FloatTypeHandler()));
		this.register((Class) Double.class, (TypeHandler) (new DoubleTypeHandler()));
		this.register((Class) Double.TYPE, (TypeHandler) (new DoubleTypeHandler()));
		this.register((JdbcType) JdbcType.DOUBLE, (TypeHandler) (new DoubleTypeHandler()));
		this.register((Class) Reader.class, (TypeHandler) (new ClobReaderTypeHandler()));
		this.register((Class) String.class, (TypeHandler) (new StringTypeHandler()));
		this.register((Class) String.class, JdbcType.CHAR, (TypeHandler) (new StringTypeHandler()));
		this.register((Class) String.class, JdbcType.CLOB, (TypeHandler) (new ClobTypeHandler()));
		this.register((Class) String.class, JdbcType.VARCHAR, (TypeHandler) (new StringTypeHandler()));
		this.register((Class) String.class, JdbcType.LONGVARCHAR, (TypeHandler) (new ClobTypeHandler()));
		this.register((Class) String.class, JdbcType.NVARCHAR, (TypeHandler) (new NStringTypeHandler()));
		this.register((Class) String.class, JdbcType.NCHAR, (TypeHandler) (new NStringTypeHandler()));
		this.register((Class) String.class, JdbcType.NCLOB, (TypeHandler) (new NClobTypeHandler()));
		this.register((JdbcType) JdbcType.CHAR, (TypeHandler) (new StringTypeHandler()));
		this.register((JdbcType) JdbcType.VARCHAR, (TypeHandler) (new StringTypeHandler()));
		this.register((JdbcType) JdbcType.CLOB, (TypeHandler) (new ClobTypeHandler()));
		this.register((JdbcType) JdbcType.LONGVARCHAR, (TypeHandler) (new ClobTypeHandler()));
		this.register((JdbcType) JdbcType.NVARCHAR, (TypeHandler) (new NStringTypeHandler()));
		this.register((JdbcType) JdbcType.NCHAR, (TypeHandler) (new NStringTypeHandler()));
		this.register((JdbcType) JdbcType.NCLOB, (TypeHandler) (new NClobTypeHandler()));
		this.register((Class) Object.class, JdbcType.ARRAY, (TypeHandler) (new ArrayTypeHandler()));
		this.register((JdbcType) JdbcType.ARRAY, (TypeHandler) (new ArrayTypeHandler()));
		this.register((Class) BigInteger.class, (TypeHandler) (new BigIntegerTypeHandler()));
		this.register((JdbcType) JdbcType.BIGINT, (TypeHandler) (new LongTypeHandler()));
		this.register((Class) BigDecimal.class, (TypeHandler) (new BigDecimalTypeHandler()));
		this.register((JdbcType) JdbcType.REAL, (TypeHandler) (new BigDecimalTypeHandler()));
		this.register((JdbcType) JdbcType.DECIMAL, (TypeHandler) (new BigDecimalTypeHandler()));
		this.register((JdbcType) JdbcType.NUMERIC, (TypeHandler) (new BigDecimalTypeHandler()));
		this.register((Class) InputStream.class, (TypeHandler) (new BlobInputStreamTypeHandler()));
		this.register((Class) Byte[].class, (TypeHandler) (new ByteObjectArrayTypeHandler()));
		this.register((Class) Byte[].class, JdbcType.BLOB, (TypeHandler) (new BlobByteObjectArrayTypeHandler()));
		this.register((Class) Byte[].class, JdbcType.LONGVARBINARY,
				(TypeHandler) (new BlobByteObjectArrayTypeHandler()));
		this.register((Class) byte[].class, (TypeHandler) (new ByteArrayTypeHandler()));
		this.register((Class) byte[].class, JdbcType.BLOB, (TypeHandler) (new BlobTypeHandler()));
		this.register((Class) byte[].class, JdbcType.LONGVARBINARY, (TypeHandler) (new BlobTypeHandler()));
		this.register((JdbcType) JdbcType.LONGVARBINARY, (TypeHandler) (new BlobTypeHandler()));
		this.register((JdbcType) JdbcType.BLOB, (TypeHandler) (new BlobTypeHandler()));
		this.register(Object.class, this.UNKNOWN_TYPE_HANDLER);
		this.register(Object.class, JdbcType.OTHER, this.UNKNOWN_TYPE_HANDLER);
		this.register(JdbcType.OTHER, this.UNKNOWN_TYPE_HANDLER);
		this.register((Class) Date.class, (TypeHandler) (new DateTypeHandler()));
		this.register((Class) Date.class, JdbcType.DATE, (TypeHandler) (new DateOnlyTypeHandler()));
		this.register((Class) Date.class, JdbcType.TIME, (TypeHandler) (new TimeOnlyTypeHandler()));
		this.register((JdbcType) JdbcType.TIMESTAMP, (TypeHandler) (new DateTypeHandler()));
		this.register((JdbcType) JdbcType.DATE, (TypeHandler) (new DateOnlyTypeHandler()));
		this.register((JdbcType) JdbcType.TIME, (TypeHandler) (new TimeOnlyTypeHandler()));
		this.register((Class) java.sql.Date.class, (TypeHandler) (new SqlDateTypeHandler()));
		this.register((Class) Time.class, (TypeHandler) (new SqlTimeTypeHandler()));
		this.register((Class) Timestamp.class, (TypeHandler) (new SqlTimestampTypeHandler()));

		try {
			this.register("java.time.Instant", "org.apache.ibatis.type.InstantTypeHandler");
			this.register("java.time.LocalDateTime", "org.apache.ibatis.type.LocalDateTimeTypeHandler");
			this.register("java.time.LocalDate", "org.apache.ibatis.type.LocalDateTypeHandler");
			this.register("java.time.LocalTime", "org.apache.ibatis.type.LocalTimeTypeHandler");
			this.register("java.time.OffsetDateTime", "org.apache.ibatis.type.OffsetDateTimeTypeHandler");
			this.register("java.time.OffsetTime", "org.apache.ibatis.type.OffsetTimeTypeHandler");
			this.register("java.time.ZonedDateTime", "org.apache.ibatis.type.ZonedDateTimeTypeHandler");
			this.register("java.time.Month", "org.apache.ibatis.type.MonthTypeHandler");
			this.register("java.time.Year", "org.apache.ibatis.type.YearTypeHandler");
		} catch (ClassNotFoundException arg1) {
			;
		}

		this.register((Class) Character.class, (TypeHandler) (new CharacterTypeHandler()));
		this.register((Class) Character.TYPE, (TypeHandler) (new CharacterTypeHandler()));*/
	}

	public boolean hasTypeHandler(Class<?> javaType) {
		return this.hasTypeHandler((Class) javaType, (JdbcType) null);
	}

	public boolean hasTypeHandler(TypeReference<?> javaTypeReference) {
		return this.hasTypeHandler((TypeReference) javaTypeReference, (JdbcType) null);
	}

	public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
		return javaType != null && this.getTypeHandler((Type) javaType, jdbcType) != null;
	}

	public boolean hasTypeHandler(TypeReference<?> javaTypeReference, JdbcType jdbcType) {
		return javaTypeReference != null && this.getTypeHandler(javaTypeReference, jdbcType) != null;
	}

	public TypeHandler<?> getMappingTypeHandler(Class<? extends TypeHandler<?>> handlerType) {
		return (TypeHandler) this.ALL_TYPE_HANDLERS_MAP.get(handlerType);
	}

	public <T> TypeHandler<T> getTypeHandler(Class<T> type) {
		return this.getTypeHandler((Type) type, (JdbcType) null);
	}

	public <T> TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference) {
		return this.getTypeHandler((TypeReference) javaTypeReference, (JdbcType) null);
	}

	public TypeHandler<?> getTypeHandler(JdbcType jdbcType) {
		return (TypeHandler) this.JDBC_TYPE_HANDLER_MAP.get(jdbcType);
	}

	public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
		return this.getTypeHandler((Type) type, jdbcType);
	}

	public <T> TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference, JdbcType jdbcType) {
		return this.getTypeHandler(javaTypeReference.getRawType(), jdbcType);
	}

	private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
		Map jdbcHandlerMap = (Map) this.TYPE_HANDLER_MAP.get(type);
		Object handler = null;
		if (jdbcHandlerMap != null) {
			handler = (TypeHandler) jdbcHandlerMap.get(jdbcType);
			if (handler == null) {
				handler = (TypeHandler) jdbcHandlerMap.get((Object) null);
			}

			if (handler == null) {
				handler = this.pickSoleHandler(jdbcHandlerMap);
			}
		}

		if (handler == null && type != null && type instanceof Class && Enum.class.isAssignableFrom((Class) type)) {
			handler = new EnumTypeHandler((Class) type);
		}

		return (TypeHandler) handler;
	}

	private TypeHandler<?> pickSoleHandler(Map<JdbcType, TypeHandler<?>> jdbcHandlerMap) {
		TypeHandler soleHandler = null;
		Iterator i$ = jdbcHandlerMap.values().iterator();

		while (i$.hasNext()) {
			TypeHandler handler = (TypeHandler) i$.next();
			if (soleHandler == null) {
				soleHandler = handler;
			} else if (!handler.getClass().equals(soleHandler.getClass())) {
				return null;
			}
		}

		return soleHandler;
	}

	public TypeHandler<Object> getUnknownTypeHandler() {
		return this.UNKNOWN_TYPE_HANDLER;
	}

	public void register(JdbcType jdbcType, TypeHandler<?> handler) {
		this.JDBC_TYPE_HANDLER_MAP.put(jdbcType, handler);
	}

	public <T> void register(TypeHandler<T> typeHandler) {
		boolean mappedTypeFound = false;
		MappedTypes mappedTypes = (MappedTypes) typeHandler.getClass().getAnnotation(MappedTypes.class);
		if (mappedTypes != null) {
			Class[] typeReference = mappedTypes.value();
			int len$ = typeReference.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Class handledType = typeReference[i$];
				this.register((Type) handledType, (TypeHandler) typeHandler);
				mappedTypeFound = true;
			}
		}

		if (!mappedTypeFound && typeHandler instanceof TypeReference) {
			try {
				TypeReference arg8 = (TypeReference) typeHandler;
				this.register(arg8.getRawType(), typeHandler);
				mappedTypeFound = true;
			} catch (Throwable arg7) {
				;
			}
		}

		if (!mappedTypeFound) {
			this.register((Class) null, typeHandler);
		}

	}

	public <T> void register(Class<T> javaType, TypeHandler<? extends T> typeHandler) {
		this.register((Type) javaType, (TypeHandler) typeHandler);
	}

	private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
		MappedJdbcTypes mappedJdbcTypes = (MappedJdbcTypes) typeHandler.getClass().getAnnotation(MappedJdbcTypes.class);
		if (mappedJdbcTypes != null) {
			JdbcType[] arr$ = mappedJdbcTypes.value();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				JdbcType handledJdbcType = arr$[i$];
				this.register(javaType, handledJdbcType, typeHandler);
			}

			if (mappedJdbcTypes.includeNullJdbcType()) {
				this.register((Type) javaType, (JdbcType) null, (TypeHandler) typeHandler);
			}
		} else {
			this.register((Type) javaType, (JdbcType) null, (TypeHandler) typeHandler);
		}

	}

	public <T> void register(TypeReference<T> javaTypeReference, TypeHandler<? extends T> handler) {
		this.register(javaTypeReference.getRawType(), handler);
	}

	public <T> void register(Class<T> type, JdbcType jdbcType, TypeHandler<? extends T> handler) {
		this.register((Type) type, jdbcType, (TypeHandler) handler);
	}

	private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
		if (javaType != null) {
			Object map = (Map) this.TYPE_HANDLER_MAP.get(javaType);
			if (map == null) {
				map = new HashMap();
				this.TYPE_HANDLER_MAP.put(javaType, map);
			}

			((Map) map).put(jdbcType, handler);
		}

		this.ALL_TYPE_HANDLERS_MAP.put(handler.getClass(), handler);
	}

	public void register(Class<?> typeHandlerClass) {
		boolean mappedTypeFound = false;
		MappedTypes mappedTypes = (MappedTypes) typeHandlerClass.getAnnotation(MappedTypes.class);
		if (mappedTypes != null) {
			Class[] arr$ = mappedTypes.value();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Class javaTypeClass = arr$[i$];
				this.register(javaTypeClass, typeHandlerClass);
				mappedTypeFound = true;
			}
		}

		if (!mappedTypeFound) {
			this.register(this.getInstance((Class) null, typeHandlerClass));
		}

	}

	public void register(String javaTypeClassName, String typeHandlerClassName) throws ClassNotFoundException {
		this.register(Resources.classForName(javaTypeClassName), Resources.classForName(typeHandlerClassName));
	}

	public void register(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
		this.register((Type) javaTypeClass, (TypeHandler) this.getInstance(javaTypeClass, typeHandlerClass));
	}

	public void register(Class<?> javaTypeClass, JdbcType jdbcType, Class<?> typeHandlerClass) {
		this.register((Type) javaTypeClass, jdbcType, (TypeHandler) this.getInstance(javaTypeClass, typeHandlerClass));
	}

	public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
		Constructor e;
		if (javaTypeClass != null) {
			try {
				e = typeHandlerClass.getConstructor(new Class[] { Class.class });
				return (TypeHandler) e.newInstance(new Object[] { javaTypeClass });
			} catch (NoSuchMethodException arg4) {
				;
			} catch (Exception arg5) {
				throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, arg5);
			}
		}

		try {
			e = typeHandlerClass.getConstructor(new Class[0]);
			return (TypeHandler) e.newInstance(new Object[0]);
		} catch (Exception arg3) {
			throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, arg3);
		}
	}

	public void register(String packageName) {
		ResolverUtil resolverUtil = new ResolverUtil();
		resolverUtil.find(new IsA(TypeHandler.class), packageName);
		Set handlerSet = resolverUtil.getClasses();
		Iterator i$ = handlerSet.iterator();

		while (i$.hasNext()) {
			Class type = (Class) i$.next();
			if (!type.isAnonymousClass() && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
				this.register(type);
			}
		}

	}

	public Collection<TypeHandler<?>> getTypeHandlers() {
		return Collections.unmodifiableCollection(this.ALL_TYPE_HANDLERS_MAP.values());
	}
}
