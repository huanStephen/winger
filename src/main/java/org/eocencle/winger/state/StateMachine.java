package org.eocencle.winger.state;

import java.util.HashMap;
import java.util.Map;

public final class StateMachine {

	private Map<Integer, Event> events = new HashMap<>();
	
	private Map<String, Object> params;
	
	public StateMachine(Map<String, Object> params) {
		this.params = params;
	}
	
	public void addEvent(Integer state, Event event) {
		this.events.put(state, event);
	}
	
	public void trigger(Integer state) {
		Event event = this.events.get(state);
		if (null == event) {
			return ;
		}
		event.process(this.params);
	}
	
}
