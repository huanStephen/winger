package org.eocencle.winger.session;

import java.util.Map;

public interface JsonSession {

	String request(String action, Map<String, Object> params);
	
}
