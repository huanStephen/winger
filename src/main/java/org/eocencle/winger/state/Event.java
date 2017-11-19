package org.eocencle.winger.state;

import java.util.Map;

public interface Event {
	void process(Map<String, Object> params);
}
