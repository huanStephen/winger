package org.eocencle.winger.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eocencle.winger.exceptions.UriNotFoundException;
import org.eocencle.winger.util.StrictMap;

/**
 * 响应缓存
 * @author huan
 *
 */
public class ResponseCache {

	// 是否开启
	private boolean open;
	
	// 缓存
	private StrictMap<Map<Integer, String>> cache = new StrictMap<Map<Integer, String>>("Response Cache");
	
	// 是否懒加载
	private boolean lazy;
	
	public ResponseCache() {
		this.open = true;
		this.lazy = true;
	}
	
	public ResponseCache(boolean close) {
		this.open = false;
	}
	
	public ResponseCache(List<String> uris) {
		this.open = true;
		this.lazy = false;
		if (null != uris && 0 != uris.size()) {
			for (String uri : uris) {
				this.cache.put(uri, new HashMap<Integer, String>());
			}
		}
	}
	
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void push(String uri, Integer paramsHashCode, String json) {
		Map<Integer, String> uriCache = null;
		
		try {
			uriCache = this.cache.get(uri);
		} catch (IllegalArgumentException e) {
			uriCache = new HashMap<Integer, String>();
			this.cache.put(uri, uriCache);
		}
		
		uriCache.put(paramsHashCode, json);
	}
	
	public String get(String uri, Integer paramsHashCode) throws UriNotFoundException {
		try {
			Map<Integer, String> uriCache = this.cache.get(uri);
			return uriCache.get(paramsHashCode);
		} catch (IllegalArgumentException e) {
			throw new UriNotFoundException();
		}
	}

	public boolean isLazy() {
		return lazy;
	}
	
	public void clear() {
		if (this.lazy) {
			this.cache.clear();
		} else {
			for (Entry<String, Map<Integer, String>> entry : this.cache.entrySet()) {
				entry.getValue().clear();
			}
		}
	}
}
