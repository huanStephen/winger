package org.eocencle.winger.intercepter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import org.eocencle.winger.util.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 拦截器数据
 * @author huan
 *
 */
public class IntercepterEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger(IntercepterEntity.class);
	
	private String clazz;
	
	private Class<?> cls;
	
	private StrictMap<Object> params = new StrictMap<Object>("Intercepter params");
	
	public Object getInstance() {
		if (null == this.cls) {
			LOGGER.error("类名不能为空！");
			throw new RuntimeException("类名不能为空！");
		}
		
		try {
			Object obj = cls.newInstance();
			String methodName = null;
			Method method = null;
			for (Entry<String, Object> param : this.params.entrySet()) {
				methodName = param.getKey();
				methodName = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1, methodName.length());
				method = cls.getDeclaredMethod(methodName, param.getValue().getClass());
				method.invoke(obj, param.getValue());
			}
			return obj;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	
	public void push(String key, Object value) {
		this.params.put(key, value);
	}
	
	public Object get(String key) {
		return this.params.get(key);
	}
}
