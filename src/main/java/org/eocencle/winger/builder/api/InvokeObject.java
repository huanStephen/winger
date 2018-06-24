package org.eocencle.winger.builder.api;

/**
 * 反射对象
 * @author huan
 *
 */
public class InvokeObject implements ApiObjectGenerate {

	public Object generate(String requiredType)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return this.generate(Class.forName(requiredType));
	}

	public <T> T generate(Class<T> requiredType) throws InstantiationException, IllegalAccessException {
		return requiredType.newInstance();
	}

}
