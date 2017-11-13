package org.eocencle.winger.mapping;

import java.util.Map;

public interface JsonSource {
	BoundJson getBoundJson(Map<String, Object> params);
}
