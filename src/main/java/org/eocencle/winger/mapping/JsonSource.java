package org.eocencle.winger.mapping;

import java.util.Map;

import org.eocencle.winger.exceptions.WingerException;

public interface JsonSource {
	BoundJson getBoundJson(Map<String, Object> params) throws WingerException ;
}
