package org.eocencle.winger.gateway;

import java.util.Map;

import org.eocencle.winger.session.JsonSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XmlProcessHandler implements ProcessHandler {

	private JsonSession jsonSession;
	
	@Autowired
	public XmlProcessHandler(JsonSession jsonSession) {
		this.jsonSession = jsonSession;
	}
	
	@Override
	public Object handle(String apiName, Map<String, Object> params) {
		return this.jsonSession.request(apiName, params);
	}

}
