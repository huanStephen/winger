package org.eocencle.winger.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eocencle.winger.io.ResolverUtil;
import org.eocencle.winger.io.Resources;

public class TypeAliasRegistry {
	private final Map<String, Class<?>> TYPE_ALIASES = new HashMap();

	public TypeAliasRegistry() {
		this.registerAlias("string", String.class);
		this.registerAlias("byte", Byte.class);
		this.registerAlias("long", Long.class);
		this.registerAlias("short", Short.class);
		this.registerAlias("int", Integer.class);
		this.registerAlias("integer", Integer.class);
		this.registerAlias("double", Double.class);
		this.registerAlias("float", Float.class);
		this.registerAlias("boolean", Boolean.class);
		this.registerAlias("byte[]", Byte[].class);
		this.registerAlias("long[]", Long[].class);
		this.registerAlias("short[]", Short[].class);
		this.registerAlias("int[]", Integer[].class);
		this.registerAlias("integer[]", Integer[].class);
		this.registerAlias("double[]", Double[].class);
		this.registerAlias("float[]", Float[].class);
		this.registerAlias("boolean[]", Boolean[].class);
		this.registerAlias("_byte", Byte.TYPE);
		this.registerAlias("_long", Long.TYPE);
		this.registerAlias("_short", Short.TYPE);
		this.registerAlias("_int", Integer.TYPE);
		this.registerAlias("_integer", Integer.TYPE);
		this.registerAlias("_double", Double.TYPE);
		this.registerAlias("_float", Float.TYPE);
		this.registerAlias("_boolean", Boolean.TYPE);
		this.registerAlias("_byte[]", byte[].class);
		this.registerAlias("_long[]", long[].class);
		this.registerAlias("_short[]", short[].class);
		this.registerAlias("_int[]", int[].class);
		this.registerAlias("_integer[]", int[].class);
		this.registerAlias("_double[]", double[].class);
		this.registerAlias("_float[]", float[].class);
		this.registerAlias("_boolean[]", boolean[].class);
		this.registerAlias("date", Date.class);
		this.registerAlias("decimal", BigDecimal.class);
		this.registerAlias("bigdecimal", BigDecimal.class);
		this.registerAlias("biginteger", BigInteger.class);
		this.registerAlias("object", Object.class);
		this.registerAlias("date[]", Date[].class);
		this.registerAlias("decimal[]", BigDecimal[].class);
		this.registerAlias("bigdecimal[]", BigDecimal[].class);
		this.registerAlias("biginteger[]", BigInteger[].class);
		this.registerAlias("object[]", Object[].class);
		this.registerAlias("map", Map.class);
		this.registerAlias("hashmap", HashMap.class);
		this.registerAlias("list", List.class);
		this.registerAlias("arraylist", ArrayList.class);
		this.registerAlias("collection", Collection.class);
		this.registerAlias("iterator", Iterator.class);
		this.registerAlias("ResultSet", ResultSet.class);
	}

	public <T> Class<T> resolveAlias(String string) {
		try {
			if (string == null) {
				return null;
			} else {
				String e = string.toLowerCase(Locale.ENGLISH);
				Class value;
				if (this.TYPE_ALIASES.containsKey(e)) {
					value = (Class) this.TYPE_ALIASES.get(e);
				} else {
					value = Resources.classForName(string);
				}

				return value;
			}
		} catch (ClassNotFoundException arg3) {
			throw new TypeException("Could not resolve type alias \'" + string + "\'.  Cause: " + arg3, arg3);
		}
	}

	public void registerAliases(String packageName) {
		this.registerAliases(packageName, Object.class);
	}

	public void registerAliases(String packageName, Class<?> superType) {
		ResolverUtil resolverUtil = new ResolverUtil();
		resolverUtil.find(new IsA(superType), packageName);
		Set typeSet = resolverUtil.getClasses();
		Iterator i$ = typeSet.iterator();

		while (i$.hasNext()) {
			Class type = (Class) i$.next();
			if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
				this.registerAlias(type);
			}
		}

	}

	public void registerAlias(Class<?> type) {
		String alias = type.getSimpleName();
		Alias aliasAnnotation = (Alias) type.getAnnotation(Alias.class);
		if (aliasAnnotation != null) {
			alias = aliasAnnotation.value();
		}

		this.registerAlias(alias, type);
	}

	public void registerAlias(String alias, Class<?> value) {
		if (alias == null) {
			throw new TypeException("The parameter alias cannot be null");
		} else {
			String key = alias.toLowerCase(Locale.ENGLISH);
			if (this.TYPE_ALIASES.containsKey(key) && this.TYPE_ALIASES.get(key) != null
					&& !((Class) this.TYPE_ALIASES.get(key)).equals(value)) {
				throw new TypeException("The alias \'" + alias + "\' is already mapped to the value \'"
						+ ((Class) this.TYPE_ALIASES.get(key)).getName() + "\'.");
			} else {
				this.TYPE_ALIASES.put(key, value);
			}
		}
	}

	public void registerAlias(String alias, String value) {
		try {
			this.registerAlias(alias, Resources.classForName(value));
		} catch (ClassNotFoundException arg3) {
			throw new TypeException("Error registering type alias " + alias + " for " + value + ". Cause: " + arg3,
					arg3);
		}
	}

	public Map<String, Class<?>> getTypeAliases() {
		return Collections.unmodifiableMap(this.TYPE_ALIASES);
	}
}
