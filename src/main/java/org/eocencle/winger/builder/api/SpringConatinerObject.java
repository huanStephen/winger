package org.eocencle.winger.builder.api;

import org.springframework.context.ApplicationContext;

/**
 * spring容器对象
 * @author huan
 *
 */
public class SpringConatinerObject implements ApiObjectGenerate {

	private ApplicationContext context;
	
	public SpringConatinerObject(ApplicationContext context) {
		this.context = context;
	}
	
	public Object generate(String requiredType)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return this.generate(Class.forName(requiredType));
	}

	public <T> T generate(Class<T> requiredType) throws InstantiationException, IllegalAccessException {
		return this.context.getBean(requiredType);
	}

}
