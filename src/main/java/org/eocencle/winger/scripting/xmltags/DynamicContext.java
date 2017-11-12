package org.eocencle.winger.scripting.xmltags;

import java.util.HashMap;
import java.util.Map;

import org.eocencle.winger.ognl.OgnlException;
import org.eocencle.winger.ognl.OgnlRuntime;
import org.eocencle.winger.reflection.MetaObject;
import org.eocencle.winger.session.Configuration;

public class DynamicContext {
	public static final String PARAMETER_OBJECT_KEY = "_parameter";
	public static final String DATABASE_ID_KEY = "_databaseId";

	static {
		OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
	}

	private final ContextMap bindings;
	private final StringBuilder jsonBuilder = new StringBuilder();
	private int uniqueNumber = 0;

	public DynamicContext(Configuration configuration, Object parameterObject) {
		if (parameterObject != null && !(parameterObject instanceof Map)) {
			MetaObject metaObject = configuration.newMetaObject(parameterObject);
			this.bindings = new ContextMap(metaObject);
		} else {
			this.bindings = new ContextMap(null);
		}
		bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
		bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
	}

	public Map<String, Object> getBindings() {
		return this.bindings;
	}

	public void bind(String name, Object value) {
		this.bindings.put(name, value);
	}

	public void appendJson(String json) {
		this.jsonBuilder.append(json);
	}

	public String getJson() {
		return this.jsonBuilder.toString().trim();
	}

	public int getUniqueNumber() {
		return uniqueNumber++;
	}

	static class ContextMap extends HashMap<String, Object> {
		private static final long serialVersionUID = 2977601501966151582L;

		private MetaObject parameterMetaObject;
		public ContextMap(MetaObject parameterMetaObject) {
		this.parameterMetaObject = parameterMetaObject;
		}

		@Override
		public Object get(Object key) {
		String strKey = (String) key;
		if (super.containsKey(strKey)) {
			return super.get(strKey);
		}

		if (parameterMetaObject != null) {
			Object object = parameterMetaObject.getValue(strKey);
			if (object != null) {
			super.put(strKey, object);
			}

			return object;
		}

		return null;
		}
	}

	static class ContextAccessor implements PropertyAccessor {

		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			Map map = (Map) target;
	
			Object result = map.get(name);
			if (result != null) {
				return result;
			}
	
			Object parameterObject = map.get(PARAMETER_OBJECT_KEY);
			if (parameterObject instanceof Map) {
				return ((Map)parameterObject).get(name);
			}
	
			return null;
		}

		public void setProperty(Map context, Object target, Object name, Object value)
			throws OgnlException {
			Map map = (Map) target;
			map.put(name, value);
		}
	}
}
