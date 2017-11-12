package org.eocencle.winger.builder;

import org.eocencle.winger.cache.Cache;

public class CacheRefResolver {
	private final ResponseBuilderAssistant assistant;
	private final String cacheRefContextPath;
	public CacheRefResolver(ResponseBuilderAssistant assistant, String cacheRefContextPath) {
		this.assistant = assistant;
		this.cacheRefContextPath = cacheRefContextPath;
	}
	
	public Cache resolveCacheRef() {
		return this.assistant.useCacheRef(this.cacheRefContextPath);
	}
}
