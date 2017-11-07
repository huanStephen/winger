package org.eocencle.winger.builder;

public class CacheRefResolver {
	private final MapperBuilderAssistant assistant;
	private final String cacheRefNamespace;
	public CacheRefResolver(MapperBuilderAssistant assistant, String cacheRefNamespace) {
		this.assistant = assistant;
		this.cacheRefNamespace = cacheRefNamespace;
	}
	public Cache resolveCacheRef() {
		return assistant.useCacheRef(cacheRefNamespace);
	}
}
