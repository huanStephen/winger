package org.eocencle.winger.gateway;

import java.util.Map;

public interface ProcessHandler {

	public Object handle(String apiName, Map<String, Object> params);
	
}
