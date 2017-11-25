package org.eocencle.winger.gateway;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class ApiStore {

	private ApplicationContext applicationContext;
	
	private HashMap<String, ApiRunnable> apiMap = new HashMap<String, ApiRunnable>();
	
	public ApiStore() {
		
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext);
		this.applicationContext = applicationContext;
	}
	
	public void loadApiFromSpringBeans() {
		String[] names = this.applicationContext.getBeanDefinitionNames();
		Class<?> type;
		
		for (String name : names) {
			type = this.applicationContext.getType(name);
			for (Method m : type.getDeclaredMethods()) {
				ApiMapping apiMapping = m.getAnnotation(ApiMapping.class);
				if (null != apiMapping) {
					this.addApiItem(apiMapping, name, m);
				}
			}
		}
	}
	
	public ApiRunnable findApiRunnable(String apiName) {
		return this.apiMap.get(apiName);
	}
	
	public ApiRunnable findApiRunnable(String apiName, String version) {
		return (ApiRunnable) this.apiMap.get(apiName + "_" + version);
	}
	
	public List<ApiRunnable> findApiRunnables(String apiName) {
		if (null == apiName) {
			throw new IllegalArgumentException("api name must not null!");
		}
		List<ApiRunnable> list = new ArrayList<ApiRunnable>();
		for (ApiRunnable api : apiMap.values()) {
			if (api.apiName.equals(apiName)) {
				list.add(api);
			}
		}
		
		return list;
	}
	
	public void addApiItem(ApiMapping apiMapping, String beanName, Method method) {
		ApiRunnable apiRun = new ApiRunnable();
		apiRun.apiName = apiMapping.value();
		apiRun.targetMethod = method;
		apiRun.targetName = beanName;
		this.apiMap.put(apiMapping.value(), apiRun);
	}
	
	public class ApiRunnable {
		String apiName;
		
		String targetName;
		
		Object target;
		
		Method targetMethod;
		
		public Object run(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			if (null == this.target) {
				this.target = applicationContext.getBean(this.targetName);
			}
			return this.targetMethod.invoke(this.target, args);
		}
		
		public Class<?>[] getParamTypes() {
			return this.targetMethod.getParameterTypes();
		}
		
		public String getApiName() {
			return this.apiName;
		}
		
		public String getTargetName() {
			return this.targetName;
		}
		
		public Object getTarget() {
			return this.target;
		}
		
		public Method getTargetMethod() {
			return this.targetMethod;
		}
	}
}
