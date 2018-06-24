package org.eocencle.winger.builder.api;

/**
 * api对象生成
 * @author huan
 *
 */
public interface ApiObjectGenerate {

	Object generate(String requiredType) throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	
	<T> T generate(Class<T> requiredType) throws InstantiationException, IllegalAccessException;
}
