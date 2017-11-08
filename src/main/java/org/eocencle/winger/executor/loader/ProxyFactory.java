package org.eocencle.winger.executor.loader;

import java.util.List;
import java.util.Properties;

import org.eocencle.winger.reflection.factory.ObjectFactory;
import org.eocencle.winger.session.Configuration;

public interface ProxyFactory {
	void setProperties(Properties properties);

	Object createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);
}
